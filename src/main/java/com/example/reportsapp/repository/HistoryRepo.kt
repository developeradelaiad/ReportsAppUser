package com.example.reportsapp.repository

import android.content.Context
import com.example.reportsapp.callback.ReportsCallable
import com.example.reportsapp.data.DataReports
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HistoryRepo(context: Context) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://youre_link/") // عنوان السيرفر
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val callBack = retrofit.create(ReportsCallable::class.java)
private val shared = context.getSharedPreferences("Save_Client", Context.MODE_PRIVATE)
    private val user_id = shared.getInt("id",-1)
    fun getReports(callback: Callback<DataReports>) {
        callBack.getMainReports(user_id).enqueue(callback)
    }
}