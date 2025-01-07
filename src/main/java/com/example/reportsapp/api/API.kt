package com.example.reportsapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class API {
       val retrofit=Retrofit.Builder().baseUrl("https://youre_link/").addConverterFactory(
            GsonConverterFactory.create()).build()
}