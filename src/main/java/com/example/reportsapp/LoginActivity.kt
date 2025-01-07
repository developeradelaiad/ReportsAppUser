package com.example.reportsapp

import android.content.Intent
import android.content.SharedPreferences
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
import com.example.reportsapp.callback.ReportsCallable
import com.example.reportsapp.data.Reports
import com.example.reportsapp.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.loginBtn.setOnClickListener {
            val email = binding.loginNameEd.text.toString()
            val password = binding.loginPassEd.text.toString()
            retrofit(email, password)
        }
        binding.dontHaveAccountBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.forgetPasswordTxt.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
        val animBlink = AnimationUtils.loadAnimation(applicationContext, R.anim.blink_text_view)
        binding.loginToContinueTxt.startAnimation(animBlink)

        val label = "Forget Password"
        val stringBuilder = StringBuilder()
        Thread {
            for (letter in label) {
                stringBuilder.append(letter)
                Thread.sleep(300)
                runOnUiThread {
                    binding.forgetPasswordTxt.text = stringBuilder.toString()
                }
            }
        }.start()
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
// تحقق إذا كان المستخدم قد سجل الدخول مسبقًا
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // الانتقال إلى الصفحة الرئيسية
            startActivity(Intent(this, MainActivity::class.java))
            finish() // إنهاء Activity تسجيل الدخول
        }
    }
    private fun retrofit(email: String, password: String) {
        val retrofit = Retrofit.Builder().baseUrl("http://youre_link/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
        val c = retrofit.create(ReportsCallable::class.java)
        c.getLoginReports(email, password).enqueue(object : Callback<Reports> {
            override fun onResponse(p0: Call<Reports?>, p1: Response<Reports?>) {
                if (p1.isSuccessful) {
                    val login = p1.body()!!
                    if (email == login.email && password == login.password) {
                        // تخزين حالة تسجيل الدخول
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                        Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        val save = getSharedPreferences("Save_Client", MODE_PRIVATE).edit()
                        save.putInt("id",login.id)
                        save.putString("username", login.username)
                        save.putString("email", login.email)
                        save.putString("address", login.address)
                        save.putString("phone", login.Phone)
                        save.putString("password", login.password)
                        save.putString("confirmpassword", login.confirm_password)
                        save.putString("locale_used", login.last_used).apply()
                        Log.e("loginSuccess", login.toString())
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                        Log.e("loginField", login.toString())
                    }
                } else {
                    Log.e("loginError", "Response was not successful: ${p1.errorBody()?.string()}")
                }
            }

            override fun onFailure(p0: Call<Reports?>, p1: Throwable) {
                Log.e("loginError", p1.message.toString())
                AlertDialog.Builder(this@LoginActivity)
                    .setTitle(getString(R.string.no_internet)).setMessage(getString(R.string.no_internet))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.cancel)) { dialod, _ -> dialod.cancel() }
                    .show()
            }
        })
    }
//private fun login(email: String, password: String) {
//        // تحقق من المدخلات
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "يرجى إدخال البريد الإلكتروني وكلمة المرور", Toast.LENGTH_SHORT)
//                .show()
//            return
//        }
//
//        val client = OkHttpClient()
//        val requestBody = FormBody.Builder()
//            .add("email", email)
//            .add("password", password)
//            .build()
//
//        val request = Request.Builder()
//            .url("http://youre_link/a/login_reports.php")  // استبدل بمسار السكريبت الخاص بك
//            .post(requestBody)
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                runOnUiThread {
//                    Toast.makeText(this@LoginActivity, "فشل الاتصال بالخادم", Toast.LENGTH_SHORT)
//                        .show()
//                    Log.e("LoginError", e.message ?: "Unknown error")
//                }
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (!response.isSuccessful) {
//                        runOnUiThread {
//                            Toast.makeText(
//                                this@LoginActivity,
//                                "فشل تسجيل الدخول",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                        return
//                    }
//
//                    val responseData = response.body()?.string()
//                    runOnUiThread {
//                        val gson = Gson()
//                        val userType = object : TypeToken<List<Reports>>() {}.type
//                        val userData: List<Reports> = gson.fromJson(responseData, userType)
//
//                        // التعامل مع بيانات المستخدم هنا
//                        if (userData.isNotEmpty() &&
//                            binding.loginNameEd.text.toString() == userData[0].email &&
//                            binding.loginPassEd.text.toString() == userData[0].password
//                        ) {
//                            Toast.makeText(
//                                this@LoginActivity,
//                                "تم تسجيل الدخول بنجاح",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                        }
//                    }
//                }
//            }
//        })
//    }
}