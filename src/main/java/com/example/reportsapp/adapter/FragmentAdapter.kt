package com.example.reportsapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.reportsapp.bottom_fragment.AllOrderFragment
import com.example.reportsapp.bottom_fragment.ReceivieOrderFragment
import com.example.reportsapp.bottom_fragment.SendOrderFragment

class FragmentAdapter(a: FragmentActivity): FragmentStateAdapter(a) {
    override fun createFragment(position: Int): Fragment {
        return when (position){
            0-> SendOrderFragment()
            1-> ReceivieOrderFragment()
            else -> AllOrderFragment()
        }
    }

    override fun getItemCount(): Int {
return 3
    }
}