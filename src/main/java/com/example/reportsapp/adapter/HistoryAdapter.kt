package com.example.reportsapp.adapter

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.reportsapp.data.DataHistory
import com.example.reportsapp.databinding.ListHistoryBinding
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread

class HistoryAdapter(val a: Activity, val list: List<DataHistory>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(val binding: ListHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val v = ListHistoryBinding.inflate(a.layoutInflater, parent, false)
        return HistoryViewHolder(v)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.binding.reportName.setText(list[position].report_name)
        holder.binding.reportType.setText(list[position].report_type)
        holder.binding.reportAddress.setText(list[position].report_address)
        holder.binding.reportDetails.setText(list[position].report_details)
        holder.binding.reportAttachFile.setText(list[position].report_attach_file)
        holder.binding.reportPageNumber.setText(list[position].report_page_numper)
        holder.binding.reportDate.setText(list[position].report_date)
        holder.binding.reportTime.setText(list[position].report_time)
        holder.binding.reportStatus.setText(list[position].report_status)
        holder.binding.reportId.setText(list[position].report_id.toInt().toString())
        if (list[position].report_status == "Completed") {
            holder.binding.downloadButton.visibility = View.VISIBLE
            holder.binding.downloadButton.setOnClickListener {
                downloadFile(list[position].report_attach_file,a)
            }
        } else {
            holder.binding.downloadButton.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount() = list.size
    fun downloadFile(filename: String, context: Context) {
        val encodedFilename = URLEncoder.encode(filename, "UTF-8")
        val url = URL("http://youre_link/a/download.php?file=$encodedFilename")  // Your PHP file download URL
        thread {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream: InputStream = connection.inputStream

                    // Create content values for the file metadata
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)  // The filename you want to save as
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")  // MIME type (can be updated based on file type)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) // Save to Downloads folder
                    }

                    // Insert the file metadata into MediaStore
                    val resolver = context.contentResolver
                    val uri: Uri? = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

                    if (uri != null) {
                        val outputStream: OutputStream? = resolver.openOutputStream(uri)

                        if (outputStream != null) {
                            val buffer = ByteArray(1024)
                            var bytesRead: Int

                            // Write data from input stream to output stream
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                            }

                            // Close the streams
                            outputStream.flush()
                            outputStream.close()
                            inputStream.close()

                            a.runOnUiThread {
                                Toast.makeText(context, a.getString(com.example.reportsapp.R.string.file_download_success ), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            a.runOnUiThread {
                                Toast.makeText(context, a.getString(com.example.reportsapp.R.string.error_stream), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        a.runOnUiThread {
                            Toast.makeText(context, a.getString(com.example.reportsapp.R.string.error_media_stream), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    a.runOnUiThread {
                        Toast.makeText(context, "${a.getString(com.example.reportsapp.R.string.failed_to_fetch_file)} ${connection.responseCode}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                a.runOnUiThread {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    }