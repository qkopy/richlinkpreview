package com.museon.richlink

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.style.URLSpan
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.squareup.picasso.Picasso


class RichLinkViewTelegram : RelativeLayout {

    private var view: View? = null
    internal var context: Context
    var metaData: MetaData? = null
        private set

    lateinit var linearLayout: LinearLayout
    lateinit var imageView: ImageView
    lateinit var textViewTitle: TextView
    lateinit var textViewDesp: TextView
    lateinit var textViewUrl: TextView
    lateinit var textViewOriginalUrl: TextView

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
            View.inflate(context, R.layout.telegram_link_layout, this)
        }

        linearLayout = findViewById<View>(R.id.rich_link_card) as LinearLayout
        imageView = findViewById<View>(R.id.rich_link_image) as ImageView
        textViewTitle = findViewById<View>(R.id.rich_link_title) as TextView
        textViewDesp = findViewById<View>(R.id.rich_link_desp) as TextView
        textViewUrl = findViewById<View>(R.id.rich_link_url) as TextView

        textViewOriginalUrl = findViewById<View>(R.id.rich_link_original_url) as TextView

        textViewOriginalUrl.text = main_url
        removeUnderlines(textViewOriginalUrl.text as Spannable)

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

    private fun removeUnderlines(p_Text: Spannable) {
        val spans = p_Text.getSpans(0, p_Text.length, URLSpan::class.java)

        for (span in spans) {
            val start = p_Text.getSpanStart(span)
            val end = p_Text.getSpanEnd(span)
            p_Text.removeSpan(span)
            var span = URLSpanNoUnderline(span.url)
            p_Text.setSpan(span, start, end, 0)
        }
    }

}
