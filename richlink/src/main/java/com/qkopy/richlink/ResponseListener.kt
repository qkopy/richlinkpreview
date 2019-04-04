package com.museon.richlink


interface ResponseListener {

    fun onData(metaData: MetaData)

    fun onError(e: Exception)
}
