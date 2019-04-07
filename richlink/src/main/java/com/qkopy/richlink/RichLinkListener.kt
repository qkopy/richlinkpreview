package com.qkopy.richlink

import android.view.View
import com.qkopy.richlink.data.model.MetaData

interface RichLinkListener {
    fun onClicked(view: View, meta: MetaData)
}
