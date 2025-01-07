package com.example.reportsapp.repository

import com.example.reportsapp.callback.ReportsCallable
import com.example.reportsapp.data.DataRecycler
import retrofit2.Call

class ReceivieOrderRepository(private val api: ReportsCallable) {

    fun getMainOne(id: Int): Call<DataRecycler> {
        return api.getMainOne(id)
    }

    fun getMainZero(id: Int): Call<DataRecycler> {
        return api.getMainZero(id)
    }

    fun getMainTwo(id: Int): Call<DataRecycler> {
        return api.getMainTwo(id)
    }
}