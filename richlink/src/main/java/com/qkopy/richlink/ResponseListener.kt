package com.qkopy.richlink

interface ResponseListener {

    fun onData(metaData: MetaData?)

    fun onError(e: Exception)
}
