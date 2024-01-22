package com.task.imageuploadsoft.network

import com.task.imageuploadsoft.util.Resource
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ImageRepository {
    suspend fun uploadImage(file: MultipartBody.Part, preset:RequestBody): Resource<Any>

}
