package com.example.adminbuyify.api

import com.example.adminbuyify.model.PushNotify
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

        @Headers("Content-Type: application/json")
        @POST("v1/projects/buyify-fe90b/messages:send")
        fun sendNotification(
            @Body notification: PushNotify
        ): Call<Void>
    }


//@Headers(
//    "Content-Type: application/json",
//    "Accept: application/json"
//    )
//@POST("/v1/projects/buyify-fe90b/messages:send")
//fun sendNotification(
//    @Body message: PushNotify,
//    @Header("Authorization") accessToken: String = "Bearer ${AccessToken.getAccessToken()}"
//) : Call<PushNotify>

//}