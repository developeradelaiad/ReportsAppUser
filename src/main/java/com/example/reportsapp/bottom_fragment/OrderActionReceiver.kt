package com.example.reportsapp.bottom_fragment

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.reportsapp.data.DataZero
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class OrderActionReceiver : BroadcastReceiver() {
    var alertDialog: AlertDialog? = null
    override fun onReceive(context: Context, intent: Intent) {
        val orderId = intent.getIntExtra("order_id", -1)
        val action = intent.getStringExtra("action")
        if (orderId != -1) {
            when (action) {
                "Download File" -> {
                    showToast(context, "Order Accepted: $orderId")
                    cancelNotification(context, orderId)
                    val file = ArrayList<DataZero>()
                    downloadFile(context,file[0].report_attach_file)
                }
            }
        } else {
            showToast(context, "Invalid order ID")
        }
    }
    private fun showToast(context: Context, message: String) {
        // Display toast on the main thread
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun cancelNotification(context: Context, orderId: Int) {
        // Cancelling the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("NotificationGone", "Cancelling notification for Order ID: $orderId")
        notificationManager.cancel(orderId.hashCode())  // Cancel notification using the same ID
    }
    private fun downloadFile(context:Context,fileName: String) {
        // Start a coroutine in the background
        CoroutineScope(Dispatchers.IO).launch {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val client = HttpClient(CIO) {
                        install(ContentNegotiation) {
                            json(Json { ignoreUnknownKeys = true })
                        }
                    }

                    val downloadUrl = "https://youre_link/a/download.php?file=$fileName"

                    val response: HttpResponse = client.get(downloadUrl)

                    val fileDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // For Android 10 and above, use app-specific external storage
                        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
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
                            context,
                            "File downloaded to: ${file.absolutePath}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Error: ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
    }