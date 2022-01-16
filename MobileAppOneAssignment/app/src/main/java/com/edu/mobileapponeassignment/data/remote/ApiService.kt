package com.edu.mobileapponeassignment.data.remote

import com.edu.mobileapponeassignment.common.Constants
import com.edu.mobileapponeassignment.data.remote.dto.login.User
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    @Headers("app-id: ${Constants.APP_ID}")
    @GET("/data/v1/user/${Constants.RANDOM_USER_ID}")
    suspend fun auth(): User
}