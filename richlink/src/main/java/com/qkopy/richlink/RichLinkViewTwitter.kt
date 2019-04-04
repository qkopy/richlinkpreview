package com.museon.richlink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi

import com.squareup.picasso.Picasso


class RichLinkViewTwitter : RelativeLayout {

    private var view: View? = null
    internal var context: Context
    var metaData: MetaData? = null
        private set

    internal lateinit var linearLayout: LinearLayout
    internal lateinit var imageView: ImageView
    internal lateinit var textViewTitle: TextView
    internal lateinit var textViewDesp: TextView
    internal var textViewUrl: TextView? = null

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        this.context = context
    }

    fun initView() {

        if (findLinearLayoutChild() != null) {
            this.view = findLinearLayoutChild()
        } else {
            this.view = this
            View.inflate(context, R.layout.twitter_link_layout, this)
        }




        linearLayout = findViewById<View>(R.id.rich_link_card) as LinearLayout
        imageView = findViewById<View>(R.id.rich_link_image) as ImageView
        textViewTitle = findViewById<View>(R.id.rich_link_title) as TextView
        textViewDesp = findViewById<View>(R.id.rich_link_desp) as TextView


        if (metaData!!.imageurl == "" || metaData!!.imageurl.isEmpty()) {
            imageView.visibility = View.GONE
        } else {
            imageView.visibility = View.VISIBLE
            Picasso.get()
                .load(metaData!!.imageurl)
                .into(imageView)
        }

        if (metaData!!.title.isEmpty() || metaData!!.title == "") {
            textViewTitle.visibility = View.GONE
        } else {
            textViewTitle.visibility = View.VISIBLE
            textViewTitle.text = metaData!!.title
        }

        if (metaData!!.description.isEmpty() || metaData!!.description == "") {
            textViewDesp.visibility = View.GONE
        } else {
            textViewDesp.visibility = View.VISIBLE
            textViewDesp.text = metaData!!.description
        }


        linearLayout.setOnClickListener { view ->
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

    protected fun findLinearLayoutChild(): LinearLayout? {
        return if (childCount > 0 && getChildAt(0) is LinearLayout) {
            getChildAt(0) as LinearLayout
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
            override fun onData(meta: MetaData) {
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
