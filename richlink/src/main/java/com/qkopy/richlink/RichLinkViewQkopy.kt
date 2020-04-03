package com.qkopy.richlink

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.transition.Fade
import android.transition.Slide
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.browser.customtabs.CustomTabsIntent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.qkopy.richlink.data.database.MetaDatabase
import com.qkopy.richlink.data.model.MetaData
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


open class RichLinkViewQkopy : RelativeLayout {


    private var view: View?=null
    var metaData: MetaData? = null
    internal var context: Context


    private lateinit var linearLayout: LinearLayout
    private lateinit var imageView: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var progreeBar: ProgressBar

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

        linearLayout = findViewById(R.id.linearLayout)
        imageView = findViewById(R.id.imageViewBanner)
        tvTitle = findViewById(R.id.textViewTitle)
        tvDescription = findViewById(R.id.textViewDescription)
        progreeBar = findViewById(R.id.progress)

        linearLayout.visibility = View.VISIBLE
        linearLayout.alpha = 0.0f

        imageView.visibility = View.VISIBLE
        progreeBar.visibility = View.VISIBLE

        Glide.with(context).load(metaData?.image).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {

                progreeBar.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                progreeBar.visibility = View.GONE
                return false
            }
        }).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .error(R.drawable.notfound).into(imageView)

        tvTitle.text = metaData?.title

        if (metaData?.description?.isEmpty() == true || metaData?.description == "") {
            tvDescription.visibility = View.GONE
        } else {
            tvDescription.visibility = View.VISIBLE
            tvDescription.text = metaData?.description
        }
        tvDescription.text = metaData?.url ?: mainUrl

        linearLayout.animate().alpha(1.0f).setDuration(1000)



        linearLayout.setOnClickListener { view ->
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

    fun setLinkFromMeta(metaData: MetaData) {
        this.metaData = metaData
//        initView()
    }

    fun setDefaultClickListener(isDefault: Boolean) {
        isDefaultClick = isDefault
    }

    fun setClickListener(richLinkListener1: RichLinkListener) {
        richLinkListener = richLinkListener1
    }

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
