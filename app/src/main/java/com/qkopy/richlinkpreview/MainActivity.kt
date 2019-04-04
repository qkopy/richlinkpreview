package com.qkopy.richlinkpreview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.qkopy.richlink.ViewListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        richLink.setLink("https://medium.com/zopper-app/zopper-optimizes-android-screen-render-e41ca41ae1b8", object : ViewListener {
            override fun onSuccess(status: Boolean) {

            }

            override fun onError(e: Exception) {

            }
        })


    }
}
