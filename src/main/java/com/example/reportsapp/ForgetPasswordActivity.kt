package com.example.reportsapp

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.reportsapp.RegisterActivity
import com.example.reportsapp.callback.ReportsCallable
import com.example.reportsapp.data.Reports
import com.example.reportsapp.databinding.ActivityForgetPasswordBinding
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val animBlink = AnimationUtils.loadAnimation(applicationContext, R.anim.blink_text_view)
        binding.forgetPasswordToContinueTxt.startAnimation(animBlink)
        binding.continueBtn.setOnClickListener{
           val emailCheck =binding.emailForgetEd.text.toString()
            email(emailCheck)
        }
    }

    private fun email(email:String) {
        val retrofit = Retrofit.Builder().baseUrl("http://youre_link/") // تأكد من استخدام عنوان السيرفر الصحيح
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiService = retrofit.create(ReportsCallable::class.java)
        apiService.getForgetPasswordReports(email).enqueue(object : Callback<Reports> {
            override fun onResponse(call: Call<Reports>, p1: Response<Reports>) {
                if (p1.isSuccessful) {
                    val forget = p1.body()!!
                    if (email == forget.email){

                        Toast.makeText(this@ForgetPasswordActivity, "Success", Toast.LENGTH_SHORT).show()
                        Log.e("forgetPasswordSuccess",forget.toString())
                    }else {
                        Toast.makeText(this@ForgetPasswordActivity, "Field", Toast.LENGTH_SHORT).show()
                        Log.e("forgetPasswordField", forget.toString())

                    }
                }else {
                    Log.e("forgetPasswordError", p1.errorBody()?.string().toString())
                }

            }

            override fun onFailure(p0: Call<Reports?>, p1: Throwable) {
                Log.e("forgetPasswordError",p1.message.toString())
                AlertDialog.Builder(this@ForgetPasswordActivity)
                    .setTitle("Server Error").setMessage("${p1.message}")
                    .setCancelable(false)
                    .setNegativeButton("cancel"){dialod,_->dialod.cancel()}
                    .show()
            }
        })
    }
}