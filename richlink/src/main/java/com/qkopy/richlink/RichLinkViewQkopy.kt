package com.qkopy.richlink

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.qkopy.richlink.data.database.MetaDatabase
import com.qkopy.richlink.data.model.MetaData
import kotlinx.android.synthetic.main.qkopy_link_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


open class RichLinkViewQkopy : RelativeLayout {

    private var view: View? = null
    var metaData: MetaData? = null
    internal var context: Context
    private var mainUrl: String? = null
    private var isDefaultClick = false
    private var richLinkListener: RichLinkListener? = null
    private var DB_LIMIT = 50
    private lateinit var coroutineScope: CoroutineScope


    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.context = context
    }

    fun setCoroutineScope(lifecycle: Lifecycle) {
        this.coroutineScope = lifecycle.coroutineScope
    }

    fun initView() {
        if (findLinearLayoutChild() != null) {
            this.view = findLinearLayoutChild()
        } else {
            this.view = this
            View.inflate(context, R.layout.qkopy_link_layout, this)
        }

        Glide
            .with(context).load(metaData?.image)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    imageViewBanner.visibility = View.GONE
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    imageViewBanner.setImageDrawable(resource)
                    imageViewBanner.alpha = 1.0f
                    imageViewBanner.visibility = View.VISIBLE
                    imageViewBanner.animation =
                        AnimationUtils.loadAnimation(context, R.anim.fade_in)
                    imageViewBanner.animation.start()
                }
            })

        textViewTitle.text = metaData?.title
        if (metaData?.description?.isEmpty() == true || metaData?.description == "") {
            textViewDescription.visibility = View.GONE
        } else {
            textViewDescription.visibility = View.VISIBLE
            textViewDescription.text = metaData?.description
        }
        //textViewDescription.text = metaData?.url ?: mainUrl

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
        try {
            customTabsIntent.launchUrl(context, Uri.parse(mainUrl))
        } catch (exception: Exception) {
            Log.e("RICLNK","fail to launch url",exception)
        }

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

    //Set No.of DB items to store. default 50
    fun setDBCacheLimit(limit: Int) {
        this.DB_LIMIT = limit
    }

    // Initialize RichLinkView
    fun setLink(url: String, context: Context, viewListener: ViewListener) {
        this.context = context
        mainUrl = url

        if (!mainUrl.isNullOrEmpty()) {
            val metaDataBase = MetaDatabase.getInstance(context)
            val scope = if (this::coroutineScope.isInitialized) coroutineScope else GlobalScope
            scope.launch(Dispatchers.IO) {
                val meta = metaDataBase.metaDataDao().getMetaDataUrl(url)
                scope.launch(Dispatchers.Main) {
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
                                    scope.launch(Dispatchers.IO) {
                                        metaDataBase.metaDataDao().insert(metaData!!)
                                        metaDataBase.metaDataDao().delete(DB_LIMIT)
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
