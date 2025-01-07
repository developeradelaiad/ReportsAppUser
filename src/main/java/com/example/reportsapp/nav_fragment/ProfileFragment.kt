package com.example.reportsapp.nav_fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reportsapp.LoginActivity
import com.example.reportsapp.callback.ReportsCallable
import com.example.reportsapp.data.DeleteUserRequest
import com.example.reportsapp.data.DeleteUserResponse
import com.example.reportsapp.data.UserUpdateRequest
import com.example.reportsapp.data.UserUpdateResponse
import com.example.reportsapp.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val get = requireActivity().getSharedPreferences("Save_Client",MODE_PRIVATE)
        val clientName=get.getString("username","")
        val clientEmail = get.getString("email","")
        val clientAddress = get.getString("address","")
        val clientPhone = get.getString("phone","")
        val clientPassword =get.getString("password","")
        val clientLastUsed = get.getString("locale_used","")
        val id = get.getInt("id",-1)
        binding.textViewName.setText(clientName)
        binding.textViewEmail.setText (clientEmail)
        binding.textViewAddress.setText (clientAddress)
        binding.textViewPhone.setText (clientPhone)
        binding.textViewPassword.setText (clientPassword)
        binding.textViewLastUsed.setText (clientLastUsed)
        binding.updateBtn.setOnClickListener{
            updateUserData(binding.textViewName.text.toString(),
                binding.textViewAddress.text.toString(),
                binding.textViewEmail.text.toString(),
                binding.textViewPassword.text.toString(),
                binding.textViewPassword.text.toString(),
                id.toInt(),
                binding.textViewPhone.text.toString())
            val shared = requireActivity().getSharedPreferences("Save_Client" , MODE_PRIVATE).edit()
            shared.putString("username",binding.textViewName.text.toString())
            shared.putString("address",binding.textViewAddress.text.toString())
            shared.putString("email",binding.textViewEmail.text.toString())
            shared.putString("phone",binding.textViewPhone.text.toString())
            shared.putString("password",binding.textViewPassword.text.toString())
            shared.putString("confirm_password",binding.textViewPassword.text.toString())
            shared.apply()
            Toast.makeText(requireContext(), getString(com.example.reportsapp.R.string.update), Toast.LENGTH_SHORT).show()

        }
        binding.deleteBtn.setOnClickListener{
            deleteUser(id.toInt())
            val shared = requireActivity().getSharedPreferences("Save_Client",MODE_PRIVATE).edit()
            shared.clear()
            shared.apply()
            Toast.makeText(requireContext(), getString(com.example.reportsapp.R.string.deleted), Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
        }
        return binding.root
    }
    fun updateUserData(username: String, address: String, email: String, password: String, confirmPassword: String, id: Int, phone: String) {
        val retrofit = Retrofit.Builder().baseUrl("https://youre_link/").addConverterFactory(
            GsonConverterFactory.create()).build()
        val c = retrofit.create(ReportsCallable::class.java)

        val request = UserUpdateRequest(username, address, email, password, confirmPassword, id.toInt(), phone)

        c.updateUser(request).enqueue(object : Callback<UserUpdateResponse> {
            override fun onResponse(call: Call<UserUpdateResponse>, response: Response<UserUpdateResponse>) {
                if (response.isSuccessful) {
                    // الحصول على الاستجابة من الخادم
                    val updateResponse = response.body()
                    updateResponse?.let {
                        // يمكن الوصول إلى status و message هنا
                        println("Status: ${it.status}")
                        println("Message: ${it.message}")
                    }
                } else {
                    println("Request failed with response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserUpdateResponse>, t: Throwable) {
                // في حالة فشل الاتصال
                println("Error: ${t.message}")
            }
        })
    }
    fun deleteUser(id: Int) {
        val retrofit = Retrofit.Builder().baseUrl("https://youre_link/").addConverterFactory(
            GsonConverterFactory.create()).build()
        val c = retrofit.create(ReportsCallable::class.java)
        val request = DeleteUserRequest(id)
        c.deleteUser(request).enqueue(object : Callback<DeleteUserResponse> {
            override fun onResponse(call: Call<DeleteUserResponse>, response: Response<DeleteUserResponse>) {
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    deleteResponse?.let {
                        println("Status: ${it.status}")
                        println("Message: ${it.message}")
                    }
                } else {
                    println("Request failed with response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DeleteUserResponse>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }
}