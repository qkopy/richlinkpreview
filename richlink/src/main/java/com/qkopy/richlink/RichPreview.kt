package com.qkopy.richlink

import android.os.AsyncTask
import android.webkit.URLUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

class RichPreview(internal var responseListener: ResponseListener) {


    internal var url: String? = null


    fun getPreview(url: String) {
        this.url = url
        getData().execute()
    }

    var metaData = MetaData()

    private inner class getData : AsyncTask<Void, Void, Void>() {


        override fun doInBackground(vararg params: Void): Void? {
            var doc: Document? = null
            try {
                doc = Jsoup.connect(url)
                    .timeout(30 * 1000)
                    .get()

                val elements = doc!!.getElementsByTag("meta")

                // getTitle doc.select("meta[property=og:title]")
                var title: String? = doc.select("meta[property=og:title]").attr("content")

                if (title == null || title.isEmpty()) {
                    title = doc.title()
                }
                metaData.title = title!!

                //getDescription
                var description: String? = doc.select("meta[name=description]").attr("content")
                if (description!!.isEmpty() || description == null) {
                    description = doc.select("meta[name=Description]").attr("content")
                }
                if (description!!.isEmpty() || description == null) {
                    description = doc.select("meta[property=og:description]").attr("content")
                }
                if (description!!.isEmpty() || description == null) {
                    description = ""
                }
                metaData.description = description


                // getMediaType
                val mediaTypes = doc.select("meta[name=medium]")
                var type = ""
                if (mediaTypes.size > 0) {
                    val media = mediaTypes.attr("content")

                    type = if (media == "image") "photo" else media
                } else {
                    type = doc.select("meta[property=og:type]").attr("content")
                }
                metaData.mediatype = type


                //getImages
                val imageElements = doc.select("meta[property=og:image]")
                if (imageElements.size > 0) {
                    val image = imageElements.attr("content")
                    if (!image.isEmpty()) {
                        metaData.imageurl = resolveURL(url, image)
                    }
                }
                if (metaData.imageurl.isEmpty()) {
                    var src = doc.select("link[rel=image_src]").attr("href")
                    if (!src.isEmpty()) {
                        metaData.imageurl = resolveURL(url, src)
                    } else {
                        src = doc.select("link[rel=apple-touch-icon]").attr("href")
                        if (!src.isEmpty()) {
                            metaData.imageurl = resolveURL(url, src)
                            metaData.favicon = resolveURL(url, src)
                        } else {
                            src = doc.select("link[rel=icon]").attr("href")
                            if (!src.isEmpty()) {
                                metaData.imageurl = resolveURL(url, src)
                                metaData.favicon = resolveURL(url, src)
                            }
                        }
                    }
                }

                //Favicon
                var src = doc.select("link[rel=apple-touch-icon]").attr("href")
                if (!src.isEmpty()) {
                    metaData.favicon = resolveURL(url, src)
                } else {
                    src = doc.select("link[rel=icon]").attr("href")
                    if (!src.isEmpty()) {
                        metaData.favicon = resolveURL(url, src)
                    }
                }

                for (element in elements) {
                    if (element.hasAttr("property")) {
                        val str_property = element.attr("property").toString().trim { it <= ' ' }
                        if (str_property == "og:url") {
                            metaData.url = element.attr("content").toString()
                        }
                        if (str_property == "og:site_name") {
                            metaData.sitename = element.attr("content").toString()
                        }
                    }
                }

                if (metaData.url == "" || metaData.url.isEmpty()) {
                    var uri: URI? = null
                    try {
                        uri = URI(url!!)
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }

                    if (url == null) {
                        metaData.url = url!!
                    } else {
                        metaData.url = uri!!.host
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
                responseListener.onError(Exception("No Html Received from " + url + " Check your Internet " + e.localizedMessage))
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            responseListener.onData(metaData)
        }
    }

    private fun resolveURL(url: String?, part: String): String {
        if (URLUtil.isValidUrl(part)) {
            return part
        } else {
            var base_uri: URI? = null
            try {
                base_uri = URI(url!!)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }

            base_uri = base_uri!!.resolve(part)
            return base_uri!!.toString()
        }
    }

}
