package com.qkopy.richlink

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.qkopy.richlink.data.database.MetaDatabase
import com.qkopy.richlink.data.model.MetaData
import kotlinx.android.synthetic.main.qkopy_link_layout.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


open class RichLinkViewQkopy : RelativeLayout {


    private var view: View?=null
    var metaData: MetaData? = null
    internal var context: Context



    private var mainUrl: String? = null

    private var isDefaultClick = false

    private var richLinkListener: RichLinkListener? = null

    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.context = context
    }

    fun initView() {

        if (findLinearLayoutChild() != null) {
            this.view = findLinearLayoutChild()
        } else {
            this.view = this
            View.inflate(context, R.layout.qkopy_link_layout, this)
        }





        imageViewBanner.visibility = View.VISIBLE

        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.centerRadius = 25.0f
        circularProgressDrawable.start()

        Glide.with(context).load(metaData?.image)
            .placeholder(circularProgressDrawable)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .error(R.drawable.notfound).into(imageViewBanner)

        textViewTitle.text = metaData?.title

        if (metaData?.description?.isEmpty() == true || metaData?.description == "") {
            textViewDescription.visibility = View.GONE
        } else {
            textViewDescription.visibility = View.VISIBLE
            textViewDescription.text = metaData?.description
        }
        textViewDescription.text = metaData?.url ?: mainUrl






        rootLayout.setOnClickListener { view ->
            if (isDefaultClick) {
                richLinkClicked()
            } else {
                if (richLinkListener != null) {
                    richLinkListener?.onClicked(view, metaData)
                } else {
                    richLinkClicked()
                }
            }
        }

    }

    //Default clickListener function

    private fun richLinkClicked() {

     val builder = CustomTabsIntent.Builder()
     val customTabsIntent = builder.build()
     customTabsIntent.launchUrl(context, Uri.parse(mainUrl))

    }


    private fun findLinearLayoutChild(): LinearLayout? {
        return if (childCount > 0 && getChildAt(0) is LinearLayout) {
            getChildAt(0) as LinearLayout
        } else null
    }


    // Set this to false when using  Custom ClickListener

    fun setDefaultClickListener(isDefault: Boolean) {
        isDefaultClick = isDefault
    }


    //  Set a Custom ClickListener

    fun setClickListener(richLinkListener1: RichLinkListener) {
        richLinkListener = richLinkListener1
    }

    // Initialize RichLinkView
    fun setLink(url: String, context: Context, viewListener: ViewListener) {
        this.context = context
        mainUrl = url

        if (!mainUrl.isNullOrEmpty()) {

            val metaDataBase = MetaDatabase.getInstance(context)
            doAsync {
                val meta = metaDataBase.metaDataDao().getMetaDataUrl(url)
                uiThread {

                    if (meta != null) {

                        metaData = meta
                        viewListener.onSuccess(true)
                        initView()

                    } else {

                        val richPreview = RichPreview(object : ResponseListener {
                            override fun onData(meta: MetaData?) {
                                metaData = meta
                                if (metaData?.title?.isEmpty() == false || metaData?.title == "") {

                                    viewListener.onSuccess(true)
                                    doAsync {
                                        metaDataBase.metaDataDao().insert(metaData!!)
                                        metaDataBase.metaDataDao().delete()
                                    }

                                    initView()
                                }
                            }

                            override fun onError(e: Exception) {
                                initView()
                                viewListener.onError(e)
                            }
                        })

                        richPreview.getPreview(url)
                    }
                }
            }

        }

    }


}
