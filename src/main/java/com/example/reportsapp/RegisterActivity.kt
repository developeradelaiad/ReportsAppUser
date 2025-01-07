package com.example.reportsapp

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.reportsapp.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputEditText
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val animBlink = AnimationUtils.loadAnimation(applicationContext, R.anim.blink_text_view)
        binding.registerToContinueTxt.startAnimation(animBlink)

        binding.registerBtn.setOnClickListener {
            val username = binding.registerNameEd.text.toString()
            val address = binding.registerAddressEd.text.toString()
            val email = binding.registerEmailEd.text.toString()
            val password = binding.registerPasswordEd.text.toString()
            val confirmPassword = binding.registerConfirmPasswordEd.text.toString()
            val phone = binding.registerPhoneEd.text.toString()


            sendRatingToServer(username,address, email, password, confirmPassword,phone)
        }
    }

        private fun sendRatingToServer(username: String,address:String,email: String, password: String, confirmPassword: String,Phone:String) {
        val url = "http://youre_link/a/register.php"
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("email",email)
            .add("address", address)
            .add("password", password)
            .add("confirm_password", confirmPassword)
            .add("Phone", Phone)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        Log.d("RegisterActivity", "Sending data: username=$username,email=$email,address=$address, password=$password,confirm_password=$confirmPassword,phone=$Phone")

        Thread {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body()?.string()
                Log.d("RatingActivity", "Response: $responseBody")

                runOnUiThread {
                    if (response.isSuccessful) {
                        if (password == confirmPassword&&password.length>=10) {
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                            binding.registerBtn.isVisible = false
                            finish()
                        } else {
                            Toast.makeText(this, "password is not equal confirmPassword Or password is less than 10", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "field", Toast.LENGTH_SHORT).show()

                    }
                }
            } catch (e: IOException) {
                runOnUiThread {
                    AlertDialog.Builder(this@RegisterActivity)
                        .setTitle("Server Error").setMessage("Server Offline Or ${getString(R.string.no_internet)}")
                        .setCancelable(false)
                        .setNegativeButton(getString(R.string.cancel)){dialog,_->dialog.cancel()}
                        .show()
                }
            }
        }.start()
    }
//    private fun register(username: String,address:String,
//                         email: String, password: String, confirmPassword: String,Phone:String){
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://youre_link/") // استبدل بـ IP أو الدومين الخاص بك
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apiService = retrofit.create(ReportsCallable::class.java)
//        apiService.registerUser(username,address, email, password, confirmPassword,Phone)
//            .enqueue(object : Callback<Reports> {
//            override fun onResponse(call: Call<Reports>, response: Response<Reports>) {
//                if (response.isSuccessful) {
//                    val registerResponse = response.body()
//                    if (registerResponse!=null) {
//                        Toast.makeText(this@RegisterActivity, "success", Toast.LENGTH_SHORT).show()
//                        Log.e("RegisterSuccess",registerResponse.toString())
//                    } else {
//                        Toast.makeText(this@RegisterActivity,"Unknown error", Toast.LENGTH_SHORT).show()
//                        Log.e("RegisterField",registerResponse.toString())
//
//                    }
//                } else {
//                    Toast.makeText(this@RegisterActivity, "Error", Toast.LENGTH_SHORT).show()
//                    Log.e("RegisterError","${response.errorBody()?.string()}")
//
//                }
//            }
//
//            override fun onFailure(call: Call<Reports>, t: Throwable) {
//                Log.e("RegisterError","${t.message}")
//            }
//        })
//    }
}