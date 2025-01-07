package com.example.reportsapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reportsapp.data.DataHistory
import com.example.reportsapp.data.DataReports
import com.example.reportsapp.repository.HistoryRepo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryVM(application: Application):AndroidViewModel(application) {
    // LiveData لتخزين البيانات
    private val _listData = MutableLiveData<List<DataHistory>>()
    val listData: LiveData<List<DataHistory>> get() = _listData

    // LiveData لحالة التقدم (ProgressBar)
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val repository = HistoryRepo(application)

    fun fetchData() {
        _loading.value = true
        repository.getReports(object : Callback<DataReports> {
            override fun onResponse(call: Call<DataReports>, response: Response<DataReports>) {
                if (response.isSuccessful) {
                    val data = response.body()?.reported ?: emptyList()
                    _listData.value = data
                } else {
                    // التعامل مع الاستجابة الغير ناجحة
                    _listData.value = emptyList()
                }
                _loading.value = false
            }

            override fun onFailure(call: Call<DataReports>, t: Throwable) {
                _loading.value = false
                // التعامل مع الفشل في الاتصال
                _listData.value = emptyList()
            }
        })
    }
}