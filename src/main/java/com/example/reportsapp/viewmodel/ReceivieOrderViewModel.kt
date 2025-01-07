package com.example.reportsapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reportsapp.api.API
import com.example.reportsapp.callback.ReportsCallable
import com.example.reportsapp.data.DataRecycler
import com.example.reportsapp.data.DataZero
import com.example.reportsapp.repository.ReceivieOrderRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReceivieOrderViewModel(application: Application) : AndroidViewModel(application) {

    private val _reportsZero = MutableLiveData<List<DataZero>>()
    val reportsZero: LiveData<List<DataZero>> get() = _reportsZero

    private val _reportsOne = MutableLiveData<List<DataZero>>()
    val reportsOne: LiveData<List<DataZero>> get() = _reportsOne

    private val _reportsTwo = MutableLiveData<List<DataZero>>()
    val reportsTwo: LiveData<List<DataZero>> get() = _reportsTwo

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val repository: ReceivieOrderRepository

    init {
        val api = API().retrofit.create(ReportsCallable::class.java)
        repository = ReceivieOrderRepository(api)
    }

    fun fetchReports(id: Int) {
        fetchReportZero(id)
        fetchReportOne(id)
        fetchReportTwo(id)
    }

    private fun fetchReportZero(id: Int) {
        repository.getMainZero(id).enqueue(object : Callback<DataRecycler> {
            override fun onResponse(p0: Call<DataRecycler?>, p1: Response<DataRecycler?>) {
                if (p1.isSuccessful && p1.body() != null) {
                    _reportsZero.value = p1.body()!!.zero
                } else {
                    _errorMessage.value = "Error fetching Zero data: ${p1.errorBody()?.string()}"
                }            }

            override fun onFailure(call: Call<DataRecycler>, t: Throwable) {
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    private fun fetchReportOne(id: Int) {
        repository.getMainOne(id).enqueue(object : Callback<DataRecycler> {
            override fun onResponse(call: Call<DataRecycler>, response: Response<DataRecycler>) {
                if (response.isSuccessful && response.body() != null) {
                    _reportsOne.value = response.body()!!.zero
                } else {
                    _errorMessage.value = "Error fetching One data: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<DataRecycler>, t: Throwable) {
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }

    private fun fetchReportTwo(id: Int) {
        repository.getMainTwo(id).enqueue(object : Callback<DataRecycler> {
            override fun onResponse(call: Call<DataRecycler>, response: Response<DataRecycler>) {
                if (response.isSuccessful && response.body() != null) {
                    _reportsTwo.value = response.body()!!.zero
                } else {
                    _errorMessage.value = "Error fetching Two data: ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<DataRecycler>, t: Throwable) {
                _errorMessage.value = "Error: ${t.message}"
            }
        })
    }
}