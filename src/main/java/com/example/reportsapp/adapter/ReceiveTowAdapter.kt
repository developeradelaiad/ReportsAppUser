package com.example.reportsapp.adapter

import android.app.Activity
import android.os.Build
import android.os.Environment
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.reportsapp.data.DataZero
import com.example.reportsapp.databinding.ListReceiveTwoBinding
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.io.File

class ReceiveTowAdapter(private val a: Activity, private val list: List<DataZero>) :
    RecyclerView.Adapter<ReceiveTowAdapter.TwoViewHolder>() {
    class TwoViewHolder(val binding: ListReceiveTwoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TwoViewHolder {
        val a = ListReceiveTwoBinding.inflate(a.layoutInflater, parent, false)
        return TwoViewHolder(a)
    }

    override fun onBindViewHolder(holder: TwoViewHolder, position: Int) {
        holder.binding.receiveTwoId.setText(list[position].report_id.toInt().toString())
        holder.binding.receiveTwoName.setText(list[position].report_name)
        holder.binding.receiveTwoDetails.setText(list[position].report_details)
        holder.binding.receiveTwoType.setText(list[position].report_type)
        holder.binding.receiveTwoAddress.setText(list[position].report_address)
        holder.binding.receiveTwoAttachFile.setText(list[position].report_attach_file)
        holder.binding.receiveTwoDate.setText(list[position].report_date)
        holder.binding.receiveTwoTime.setText(list[position].report_time)
        holder.binding.receiveTwoPageNumper.setText(list[position].report_page_numper)
        holder.binding.receiveTwoStatus.setText(list[position].report_status)
        holder.binding.downloadButton.setOnClickListener {
            downloadFile(list[position].report_attach_file)
        }
    }

    override fun getItemCount() = list.size
    private fun downloadFile(fileName: String) {
        // Start a coroutine in the background
        CoroutineScope(Dispatchers.IO).launch {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val client = HttpClient(CIO) {
                        install(ContentNegotiation) {
                            json(Json { ignoreUnknownKeys = true })
                        }
                    }

                    val downloadUrl = "http://youre_link/a/download.php?file=$fileName"

                    val response: HttpResponse = client.get(downloadUrl)

                    val fileDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // For Android 10 and above, use app-specific external storage
                        a.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    } else {
                        // For older versions, use public external storage
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    }

                    // Ensure the directory exists
                    fileDir?.mkdirs()

                    val file = File(fileDir, fileName)
                    file.writeBytes(response.readBytes())

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            a,
                            "File downloaded to: ${file.absolutePath}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            a,
                            "Error: ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}