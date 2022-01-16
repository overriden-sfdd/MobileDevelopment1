package com.edu.mobileapponeassignment.data.remote

import com.edu.mobileapponeassignment.common.Constants
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy

object ServiceBuilder {

    private val client: OkHttpClient by lazy {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient
            .Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .addInterceptor(interceptor)
            .build()
    }

    private val retrofit: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
    }

    val apiService: ApiService by lazy {
        retrofit
            .build()
            .create(ApiService::class.java)
    }
}