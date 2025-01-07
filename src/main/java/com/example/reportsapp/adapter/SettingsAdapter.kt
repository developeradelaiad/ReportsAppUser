package com.example.reportsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reportsapp.data.SettingItem

class SettingsAdapter (private val settingsList: List<SettingItem>, private val listener: (SettingItem) -> Unit) :
    RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(settingItem: SettingItem) {
            title.text = settingItem.title
            itemView.setOnClickListener { listener(settingItem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(settingsList[position])
    }

    override fun getItemCount(): Int = settingsList.size
}