package com.qkopy.richlink

import com.qkopy.richlink.data.model.MetaData

interface ResponseListener {

    fun onData(metaData: MetaData?)

    fun onError(e: Exception)
}
