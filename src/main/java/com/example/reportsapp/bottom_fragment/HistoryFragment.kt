package com.example.reportsapp.bottom_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reportsapp.adapter.HistoryAdapter
import com.example.reportsapp.data.DataHistory
import com.example.reportsapp.databinding.FragmentHistoryBinding
import com.example.reportsapp.viewmodel.HistoryVM
import kotlin.getValue
import androidx.fragment.app.viewModels

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val mainViewModel: HistoryVM by viewModels() // الحصول على ViewModel
    private val list = ArrayList<DataHistory>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        mainViewModel.listData.observe(requireActivity(), Observer { data ->
            if (data.isNotEmpty()) {
                list.clear()
                list.addAll(data)
                updateRecyclerView()
            } else {
                Toast.makeText(activity, getString(com.example.reportsapp.R.string.no_data_avaible ), Toast.LENGTH_SHORT).show()
            }
        })

        // الاستماع لحالة التحميل (loading)
        mainViewModel.loading.observe(requireActivity(), Observer { isLoading ->
            if (isLoading) {
                binding.progress.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.INVISIBLE
            }
        })

        // جلب البيانات عند بدء النشاط
        mainViewModel.fetchData()

        // إعداد الـ RecyclerView
        binding.recyclerHistory.layoutManager = LinearLayoutManager(activity)
        binding.recyclerHistory.adapter = HistoryAdapter(requireActivity(), list)
        return binding.root
    }

    private fun updateRecyclerView() {
        // تحديث الـ RecyclerView بناءً على البيانات الجديدة
        binding.recyclerHistory.adapter?.notifyDataSetChanged()
    }
}