package com.qkopy.richlink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide


class RichLinkViewQkopy : RelativeLayout {

    private var view: View? = null
    internal var context: Context
    var metaData: MetaData? = null
        private set

    lateinit var relativeLayout: RelativeLayout
    lateinit var imageView: ImageView
    lateinit var imageViewFavIcon: ImageView
    lateinit var textViewTitle: TextView
    lateinit var textViewDesp: TextView
    lateinit var textViewUrl: TextView

    private var main_url: String? = null

    private var isDefaultClick = true

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

        if (findRelativeLayoutChild() != null) {
            this.view = findRelativeLayoutChild()
        } else {
            this.view = this
            View.inflate(context, R.layout.qkopy_link_layout, this)
        }

        relativeLayout = findViewById<View>(R.id.rich_link_card) as RelativeLayout
        imageView = findViewById<View>(R.id.rich_link_image) as ImageView
        imageViewFavIcon = findViewById<View>(R.id.rich_link_favicon) as ImageView
        textViewTitle = findViewById<View>(R.id.rich_link_title) as TextView
        textViewDesp = findViewById<View>(R.id.rich_link_desp) as TextView
        textViewUrl = findViewById<View>(R.id.rich_link_url) as TextView


        if (metaData!!.imageurl == "" || metaData!!.imageurl.isEmpty()) {
            imageView.visibility = View.GONE
        } else {
            imageView.visibility = View.VISIBLE
            Glide.with(context).load(metaData!!.imageurl).into(imageView)
        }
        println("metadata image ${metaData!!.imageurl}")

        if (metaData!!.favicon == "" || metaData!!.favicon.isEmpty()) {
            imageViewFavIcon.visibility = View.GONE
        } else {
            imageViewFavIcon.visibility = View.VISIBLE
            Glide.with(context).load(metaData!!.favicon).into(imageViewFavIcon)
        }

        println("metadata image ${metaData!!.favicon}")

        if (metaData!!.title.isEmpty() || metaData!!.title == "") {
            textViewTitle.visibility = View.GONE
        } else {
            textViewTitle.visibility = View.VISIBLE
            textViewTitle.text = metaData!!.title
        }
        if (metaData!!.url.isEmpty() || metaData!!.url == "") {
            textViewUrl.visibility = View.GONE
        } else {
            textViewUrl.visibility = View.VISIBLE
            textViewUrl.text = metaData!!.url
        }
        if (metaData!!.description.isEmpty() || metaData!!.description == "") {
            textViewDesp.visibility = View.GONE
        } else {
            textViewDesp.visibility = View.VISIBLE
            textViewDesp.text = metaData!!.description
        }


        relativeLayout.setOnClickListener { view ->
            if (isDefaultClick) {
                richLinkClicked()
            } else {
                if (richLinkListener != null) {
                    richLinkListener!!.onClicked(view, metaData!!)
                } else {
                    richLinkClicked()
                }
            }
        }

    }

    private fun richLinkClicked() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(main_url))
        context.startActivity(intent)
    }

    protected fun findRelativeLayoutChild(): RelativeLayout? {
        return if (childCount > 0 && getChildAt(0) is LinearLayout) {
            getChildAt(0) as RelativeLayout
        } else null
    }

    fun setLinkFromMeta(metaData: MetaData) {
        this.metaData = metaData
        initView()
    }


    fun setDefaultClickListener(isDefault: Boolean) {
        isDefaultClick = isDefault
    }

    fun setClickListener(richLinkListener1: RichLinkListener) {
        richLinkListener = richLinkListener1
    }


    fun setLink(url: String, viewListener: ViewListener) {
        main_url = url
        val richPreview = RichPreview(object : ResponseListener {
            override fun onData(meta: MetaData?) {
                metaData = meta

                if (metaData!!.title.isEmpty() || metaData!!.title == "") {
                    viewListener.onSuccess(true)
                }

                initView()
            }

            override fun onError(e: Exception) {
                viewListener.onError(e)
            }
        })
        richPreview.getPreview(url)
    }


}
