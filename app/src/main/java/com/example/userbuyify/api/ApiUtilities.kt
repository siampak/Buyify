package com.example.userbuyify.api

import com.example.userbuyify.AccessToken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {

    private const val BASE_URL = "https://fcm.googleapis.com/"

    private fun getRetrofit(): Retrofit {
        // Interceptor to add Bearer token
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val token =
                    AccessToken.getAccessToken()  // Get access token dynamically- for i use retrofit(okHttpClient)
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getApiInterface(): ApiInterface {
        return getRetrofit().create(ApiInterface::class.java)
    }
}

//    private var retrofit : Retrofit ? = null
//
//    public fun getApiInterface(): ApiInterface {
//        if (retrofit == null){
//            retrofit = Retrofit
//                .Builder()
//                .baseUrl("https://fcm.googleapis.com")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//        }
//        return retrofit!!.create(ApiInterface::class.java)
//    }
