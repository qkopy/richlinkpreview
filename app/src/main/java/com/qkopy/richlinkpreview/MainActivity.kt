package com.qkopy.richlinkpreview

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.webkit.URLUtil
import androidx.appcompat.app.AppCompatActivity
import com.qkopy.richlink.RichLinkListener
import com.qkopy.richlink.ViewListener
import com.qkopy.richlink.data.model.MetaData
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val youtube = "https://youtu.be/-g7yxxTpF2o"
        val instagram = "https://www.instagram.com/p/Bt51GkHBBq9/?utm_source=ig_share_sheet&igshid=s2egm9xcr80v"
        val google = "https://www.google.com"
        val test = "http://kmccelection.com"

        richLink.setDBCacheLimit(1)
        edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val ss = s?.split("\\s+")
                ss?.forEach {
                    if (URLUtil.isValidUrl(it)){
                        richLink.setLink(it,this@MainActivity, object : ViewListener {
                            override fun onSuccess(status: Boolean) {

                            }

                            override fun onError(e: Exception) {

                            }
                        })
                    }
                }
            }
        })




    }
}
