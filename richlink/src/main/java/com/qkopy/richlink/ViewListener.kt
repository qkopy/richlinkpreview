package com.museon.richlink


interface ViewListener {

    fun onSuccess(status: Boolean)

    fun onError(e: Exception)
}
