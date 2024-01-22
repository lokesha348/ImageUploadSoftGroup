package com.task.imageuploadsoft.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

object ConnectionUtil {
    const val UPLOAD_OP_PHOTO = "upload/base/image"
    fun isNetworkAvailable(context: Context?): Boolean {
        try {
            if (context != null) {
                val connectivityManager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                @SuppressLint("MissingPermission") val activeNetworkInfo =
                    connectivityManager.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
