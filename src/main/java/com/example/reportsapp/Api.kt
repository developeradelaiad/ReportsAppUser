package com.example.reportsapp

import com.example.reportsapp.data.Reports
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object Api {

    private val user = OkHttpClient()

    fun login(name: String, password: String, callback: (List<Reports>?) -> Unit) {

        val url = "https://youre_link/a/login_reports.php"
        val retrofit = FormBody.Builder()
            .add("name", name)
            .add("password", password)
            .build()

        val request = Request.Builder().url(url)
            .post(retrofit)
            .build()

        user.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback(null)
                        return
                    }
                    val responseData = response.body()?.string()
                    handleResponse(responseData,callback)

                }
            }

            private fun handleResponse(string: String?, callBack: (List<Reports>?) -> Unit) {
                if (string!=null){
                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    try{
                        val list=Types.newParameterizedType(List::class.java, Reports::class.java)
                        val listAdapter=moshi.adapter<List<Reports>>(list)
                        val users : List<Reports>? =listAdapter.fromJson(string)
                        if (users!=null){
                            callBack(users)
                            return
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    callBack(null)
                }else{
                    callBack(null)
                }
            }
        })
    }
}