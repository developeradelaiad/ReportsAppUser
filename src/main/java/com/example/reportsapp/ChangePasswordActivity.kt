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
import com.example.reportsapp.databinding.ActivityChangePasswordBinding
import com.google.android.material.textfield.TextInputEditText
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //animation
        val animBlink = AnimationUtils.loadAnimation(applicationContext, R.anim.blink_text_view)
        binding.forgetPasswordToContinueTxt.startAnimation(animBlink)
        //button
        binding.changePasswordBtn.setOnClickListener{
            val newPassword = binding.passwordForgetEd.text.toString()
            val confirmPassword = binding.confirmPasswordForgetEd.text.toString()
            sendRatingToServer(newPassword,confirmPassword)
//            val retrofit = Retrofit.Builder().baseUrl("http://youre_link/")
//                .addConverterFactory(GsonConverterFactory.create()).build()
//            val c = retrofit.create(ReportsCallable::class.java)
//            c.getNewPasswordReports(newPassword,confirmPassword).enqueue(object : Callback<Reports>{
//                override fun onResponse(p0: Call<Reports?>, p1: Response<Reports?>) {
//                    if (p1.isSuccessful){
//                        val newPasswordF = p1.body()!!
//                        var intent = intent.getIntExtra("id",-1)
//                        if (newPassword==confirmPassword&&newPassword.length<=10){
//                            newPasswordF.id=intent
//                            newPasswordF.password=newPassword
//                            newPasswordF.confirm_password=confirmPassword
//                            Toast.makeText(this@ChangePasswordActivity, "Done", Toast.LENGTH_SHORT).show()
//                            Log.e("NewPassword","${newPasswordF.password},${newPasswordF.confirm_password}")
//                        }else{
//                            Toast.makeText(this@ChangePasswordActivity, "Please Check Your ", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//
//                override fun onFailure(p0: Call<Reports?>, p1: Throwable) {
//                    Log.e("NewPassword",p1.message.toString())
//                }
//            })
        }
    }
    private fun sendRatingToServer(password: String, confirmPassword: String) {
        val url = "http://youre_link/a/update_password.php"
        var intent = intent.getIntExtra("id",-1)

        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("id", intent.toString())
            .add("password", password)
            .add("confirm_password", confirmPassword)// إضافة المفتاح إلى الطلب
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        Log.d("RatingActivity", "Sending data: id=$intent, password=$password,confirm_password=$confirmPassword")

        Thread {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body()?.string()
                Log.d("RatingActivity", "Response: $responseBody")

                runOnUiThread {
                    if (response.isSuccessful) {
                        if (password == confirmPassword && password.length >= 10) {
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                            startActivity(
                                Intent(
                                    this@ChangePasswordActivity,
                                    LoginActivity::class.java
                                )
                            )
                        } else {
                            Toast.makeText(this, "password is not equal confirmPassword Or password is less than 10", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "field", Toast.LENGTH_SHORT).show()

                    }
                }
            } catch (e: IOException) {
                Log.e("RatingActivity", "Error: ${e.localizedMessage}")
                runOnUiThread {
                    AlertDialog.Builder(this@ChangePasswordActivity)
                        .setTitle("Server Error").setMessage("${e.message}")
                        .setCancelable(false)
                        .setNegativeButton("cancel"){dialod,_->dialod.cancel()}
                        .show()
                }
            }
        }.start()
    }
}