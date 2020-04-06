package com.qkopy.richlink

import android.webkit.URLUtil
import com.qkopy.richlink.data.model.MetaData
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

class RichPreview(internal var responseListener: ResponseListener) {

    internal var mainUrl: String = ""

    var isError = false
    var errorMessage = ""
    fun getPreview(url: String) {
        this.mainUrl = url
        getHtmlData()
    }

    var metaData = MetaData(0, "", mainUrl, "", "", "", "", "", "")


    //getting meta data of the html page
    private fun getHtmlData()
    {
        doAsync {
            try {
                val doc: Document = Jsoup.connect(mainUrl).timeout(30 * 1000).get()

                if (doc != null) {

                    val metaTags = doc.getElementsByTag("meta")

                    // getTitle doc.select("meta[property=og:title]")
                    var title: String = doc.select("meta[property=og:title]").attr("content") ?: ""

                    if (title.isEmpty()) {
                        title = doc.title()
                    }
                    metaData.title = title

                    //getDescription
                    var description: String? = doc.select("meta[name=description]").attr("content") ?: ""
                    if (description!!.isEmpty()) {
                        description = doc.select("meta[name=Description]").attr("content")
                    }
                    if (description!!.isEmpty()) {
                        description = doc.select("meta[property=og:description]").attr("content")
                    }
                    if (description!!.isEmpty()) {
                        description = ""
                    }
                    metaData.description = description


                    // getMediaType
                    val mediaTypes = doc.select("meta[name=medium]")

                    val type = if (mediaTypes.size > 0) {
                        val media = mediaTypes.attr("content")

                        if (media == "image") "photo" else media
                    } else {
                        doc.select("meta[property=og:type]").attr("content")
                    }
                    metaData.media_type = type



                    //getImages
                    val imageElements = doc.select("meta[property=og:image]")
                    if (imageElements.size > 0) {
                        val image = imageElements.attr("content")
                        if (!image.isEmpty()) {
                            metaData.image = resolveURL(mainUrl, image)
                        }
                    }
                    if (metaData.image.isEmpty()) {
                        var src = doc.select("link[rel=image_src]").attr("href")
                        if (!src.isEmpty()) {
                            metaData.image = resolveURL(mainUrl, src)
                        } else {
                            src = doc.select("link[rel=apple-touch-icon]").attr("href")
                            if (!src.isEmpty()) {
                                metaData.image = resolveURL(mainUrl, src)
                                metaData.favicon = resolveURL(mainUrl, src)
                            } else {
                                src = doc.select("link[rel=icon]").attr("href")
                                if (!src.isEmpty()) {
                                    metaData.image = resolveURL(mainUrl, src)
                                    metaData.favicon = resolveURL(mainUrl, src)
                                } else {
                                    src = doc.selectFirst("img").absUrl("src")
                                    if (!src.isEmpty()) {
                                        metaData.image = resolveURL(mainUrl, src)

                                    }
                                }
                            }
                        }
                    }

                    //Favicon
                    var src = doc.select("link[rel=apple-touch-icon]").attr("href")
                    if (!src.isEmpty()) {
                        metaData.favicon = resolveURL(mainUrl, src)
                    } else {
                        src = doc.select("link[rel=icon]").attr("href")
                        if (!src.isEmpty()) {
                            metaData.favicon = resolveURL(mainUrl, src)
                        }
                    }

                    if (metaTags != null) {
                        for (element in metaTags) {
                            if (element.hasAttr("property")) {
                                val property = element.attr("property").toString().trim { it <= ' ' }
                                if (property == "og:url") {
                                    metaData.url = element.attr("content").toString()
                                }
                                if (property == "og:site_name") {
                                    metaData.site = element.attr("content").toString()
                                }
                            }
                        }
                    }

                    if (metaData.url == "" || metaData.url.isEmpty()) {
                        var uri: URI? = null
                        try {
                            if (uri != null) {
                                uri = URI(mainUrl)
                            }
                        } catch (e: URISyntaxException) {
                            e.printStackTrace()
                        }

                        if (mainUrl.isNotEmpty()) {
                            metaData.url = mainUrl
                        } else {
                            metaData.url = uri!!.host
                        }
                    } else {
                        metaData.url = mainUrl
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
                errorMessage = e.localizedMessage
                isError = true
            }

            uiThread {

                if (isError) {
                    responseListener.onError(Exception("No Html Received from $mainUrl Check your Internet $errorMessage"))
                } else {
                    responseListener.onData(metaData)
                }
            }
        }
    }

    private fun resolveURL(url: String?, part: String): String = if (URLUtil.isValidUrl(part)) {
        part
    } else {
        var uri: URI? = null
        try {
            if (uri != null) {
                uri = URI(url)
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        uri = uri?.resolve(part)
        uri.toString()
    }

}
