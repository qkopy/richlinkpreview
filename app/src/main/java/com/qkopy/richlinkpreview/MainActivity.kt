package com.qkopy.richlinkpreview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.qkopy.richlink.ViewListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val youtube = "https://youtu.be/-g7yxxTpF2o"
        val instagram = "https://www.instagram.com/p/Bt51GkHBBq9/?utm_source=ig_share_sheet&igshid=s2egm9xcr80v"
        val google = "https://www.google.com"
        val test = "http://kmccelection.com"

        richLink.setLink(test, this, object : ViewListener {
            override fun onSuccess(status: Boolean) {

            }

            override fun onError(e: Exception) {

            }
        })


    }
}
