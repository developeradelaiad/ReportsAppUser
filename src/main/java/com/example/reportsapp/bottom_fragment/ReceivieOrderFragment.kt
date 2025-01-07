package com.example.reportsapp.bottom_fragment

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.reportsapp.adapter.ReceiveTowAdapter
import com.example.reportsapp.adapter.Receive_Zero_adapter
import com.example.reportsapp.api.API
import com.example.reportsapp.callback.ReportsCallable
import com.example.reportsapp.data.DataRecycler
import com.example.reportsapp.data.DataZero
import com.example.reportsapp.databinding.FragmentReceivieOrderBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReceivieOrderFragment : Fragment() {
    private lateinit var binding: FragmentReceivieOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReceivieOrderBinding.inflate(inflater, container, false)
        val shared = requireActivity().getSharedPreferences("Save_Client", MODE_PRIVATE)
        val id = shared.getInt("id", -1)

        if (id != -1) {
            retrofitZero(id)
        } else {
            Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.id), Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun retrofitZero(id: Int) {
        val retrofitZero = API().retrofit
        val c = retrofitZero.create(ReportsCallable::class.java)

        c.getMainOne(id).enqueue(object : Callback<DataRecycler> {
            override fun onResponse(p0: Call<DataRecycler?>, p1: Response<DataRecycler?>) {
                try {
                    if (p1.isSuccessful && p1.body() != null) {
                        val b = p1.body()!!.zero
                        if (b != null) {
                            recyclerOne(b)
                            Log.e("OneSuccess", b.toString())
                            Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.success), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.response_body_is_null_or_malformed), Toast.LENGTH_SHORT).show()                        }
                    } else {
                        Toast.makeText(requireActivity(), "${getString(com.example.reportsapp.R.string.field)}${p1.errorBody()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireActivity(), e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<DataRecycler?>, p1: Throwable) {
                Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.no_internet), Toast.LENGTH_SHORT).show()            }
        })

        c.getMainZero(id).enqueue(object : Callback<DataRecycler> {
            override fun onResponse(p0: Call<DataRecycler?>, p1: Response<DataRecycler?>) {
                try {
                    if (p1.isSuccessful && p1.body() != null) {
                        val b = p1.body()!!.zero
                        if (b != null) {
                            recyclerZero(b)
                            Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.success), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.response_body_is_null_or_malformed), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireActivity(), "${getString(com.example.reportsapp.R.string.field)}${p1.errorBody()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireActivity(), e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<DataRecycler?>, p1: Throwable) {
                Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
        })

        c.getMainTwo(id).enqueue(object : Callback<DataRecycler> {
            override fun onResponse(p0: Call<DataRecycler?>, p1: Response<DataRecycler?>) {
                try {
                    if (p1.isSuccessful && p1.body() != null) {
                        val b = p1.body()!!.zero
                        if (b != null) {
                            recyclerTow(b)
                            Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.success), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.response_body_is_null_or_malformed), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireActivity(), "${getString(com.example.reportsapp.R.string.field)}${p1.errorBody()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireActivity(), e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<DataRecycler?>, p1: Throwable) {
                Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun recyclerZero(reports: List<DataZero>) {
        if (reports.isNotEmpty()) {
            binding.recyclerZero.adapter = Receive_Zero_adapter(requireActivity(), reports)
        } else {
            Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.empty_data), Toast.LENGTH_SHORT).show()
        }
    }

    private fun recyclerOne(reports: List<DataZero>) {
        if (reports.isNotEmpty()) {
            binding.recyclerOne.adapter = Receive_Zero_adapter(requireActivity(), reports)
        } else {
            Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.empty_data), Toast.LENGTH_SHORT).show()
        }
    }

    private fun recyclerTow(reports: List<DataZero>) {
        if (reports.isNotEmpty()) {
            binding.recyclerTwo.adapter = ReceiveTowAdapter(requireActivity(), reports)
        } else {
            Toast.makeText(requireActivity(), getString(com.example.reportsapp.R.string.empty_data), Toast.LENGTH_SHORT).show()
        }
    }
}