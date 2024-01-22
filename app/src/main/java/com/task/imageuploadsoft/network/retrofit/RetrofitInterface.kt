package com.task.imageuploadsoft.network.retrofit

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInterface {


    companion object {

        var instance: Retrofit? = null
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        private fun setHeaders(): OkHttpClient {
            val httpLoggingInterceptor = HttpLoggingInterceptor { message -> Log.d("HttpsLogs", message) }
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(httpLoggingInterceptor)
            return httpClient.build()
        }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        fun getRetrofitInstance(): Retrofit {

            if (instance == null) {

                instance = Retrofit.Builder()
                    .baseUrl("https://api.cloudinary.com/v1_1/dcypzty0c/").client(client)
                    .addConverterFactory(GsonConverterFactory.create()).
                    client(setHeaders()).build()

            }

            return instance!!


        }

    }
}
