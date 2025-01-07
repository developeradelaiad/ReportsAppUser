package com.example.reportsapp.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reportsapp.data.DataZero
import com.example.reportsapp.databinding.ListReceiveZeroBinding

class Receive_Zero_adapter(val a:Activity,val list: List<DataZero>): RecyclerView.Adapter<Receive_Zero_adapter.ReceiveViewHolder>() {
    class ReceiveViewHolder(val binding: ListReceiveZeroBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiveViewHolder {
val v = ListReceiveZeroBinding.inflate(a.layoutInflater,parent,false)
        return ReceiveViewHolder(v)
    }

    override fun onBindViewHolder(holder: ReceiveViewHolder, position: Int) {
        holder.binding.receiveZeroId.setText(list[position].report_id.toInt().toString())
        holder.binding.receiveZeroName.setText(list[position].report_name)
        holder.binding.receiveZeroDetails.setText(list[position].report_details)
        holder.binding.receiveZeroType.setText(list[position].report_type)
        holder.binding.receiveZeroAddress.setText(list[position].report_address)
        holder.binding.receiveZeroAttachFile.setText(list[position].report_attach_file)
        holder.binding.receiveZeroDate.setText(list[position].report_date)
        holder.binding.receiveZeroTime.setText(list[position].report_time)
        holder.binding.receiveZeroPageNumper.setText(list[position].report_page_numper)
        holder.binding.receiveZeroStatus.setText(list[position].report_status)
    }

    override fun getItemCount()=list.size
}