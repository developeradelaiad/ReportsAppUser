@file:Suppress("DEPRECATION")

package com.example.reportsapp.bottom_fragment

import android.R
import android.app.Activity.RESULT_OK
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.reportsapp.databinding.FragmentSendOrderBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.io.copyTo


class SendOrderFragment : Fragment() {
    private lateinit var binding: FragmentSendOrderBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var fileUri: Uri? = null
    private val buttonHideKey = "hide_btn"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendOrderBinding.inflate(inflater, container, false)
        //Button
        binding.reportBtn.setOnClickListener {
            submitReport()
        }
        //animation
        val label = getString(com.example.reportsapp.R.string.reports_app___)
        val stringBuilder = StringBuilder()
        Thread {
            for (letter in label) {
                stringBuilder.append(letter)
                Thread.sleep(300)
                activity?.runOnUiThread {
                    binding.reportToContinueTxt.text = stringBuilder.toString()
                }
            }
        }.start()
///Spinner
        val reportTypes =
            arrayOf("Excel", "Word", "Power Point") // استبدلها بأنواع التقارير الفعلية
        val adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, reportTypes)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.reportTypeSpin.adapter = adapter
//attach_file
        binding.reportAttachFileImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // يمكنك تحديد نوع الملف إذا كنت ترغب في ذلك
            startActivityForResult(intent, REQUEST_CODE_SELECT_FILE)
        }
        //hide Button With Time
        sharedPreferences = requireActivity().getSharedPreferences("hide_btn", MODE_PRIVATE)
        checkIfButtonShouldBeHidden()


        return binding.root
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_FILE && resultCode == RESULT_OK) {
            fileUri = data?.data
            binding.attachFileTxt.text = fileUri?.let { getFileName(it) } ?: "Attach File"
            fileUri?.let { changeImageBasedOnFileType(it) }

        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst()) {
                    result = it.getString(nameIndex)
                }
            }
        }
        result?.let {
            val cut = it.lastIndexOf('/')
            if (cut != -1) {
                result = it.substring(cut + 1)
            }
        }
        return result ?: getString(com.example.reportsapp.R.string.unknown_file)

    }

    private fun getFileFromUri(uri: Uri): File? {
        val fileName = getFileName(uri)
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, fileName) // أو استخدم مسارًا مختلفًا حسب الحاجة

        inputStream.use { input ->
            if (input != null) {
                val outputStream = FileOutputStream(file)
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }
        return file
    }

    private fun changeImageBasedOnFileType(uri: Uri) {
        val fileExtension = getFileExtension(uri)

        when (fileExtension) {
            "docx" -> {
                Glide.with(this)
                    .load(com.example.reportsapp.R.drawable.word) // استخدم أيقونة PDF
                    .into(binding.reportAttachFileImg)
            }

            "xls" -> {
                Glide.with(this)
                    .load(com.example.reportsapp.R.drawable.excel) // تحميل صورة JPG
                    .into(binding.reportAttachFileImg)
            }

            "pptx" -> {
                Glide.with(this)
                    .load(com.example.reportsapp.R.drawable.power_point) // تحميل صورة PNG
                    .into(binding.reportAttachFileImg)
            }
            "pdf" -> {
                Glide.with(this)
                    .load(com.example.reportsapp.R.drawable.pdf) // تحميل صورة PNG
                    .into(binding.reportAttachFileImg)
            }
            "jpg" ->{
                Glide.with(this)
                    .load(com.example.reportsapp.R.drawable.image_icon) // تحميل صورة PNG
                    .into(binding.reportAttachFileImg)
            }
            "png" ->{
                Glide.with(this)
                    .load(com.example.reportsapp.R.drawable.image_icon) // تحميل صورة PNG
                    .into(binding.reportAttachFileImg)
        }


        else -> {
                // يمكن إضافة أيقونة افتراضية
                Glide.with(this)
                    .load(com.example.reportsapp.R.drawable.baseline_attach_file_24) // أيقونة ملف افتراضية
                    .into(binding.reportAttachFileImg)
            }
        }
    }

    private fun getFileExtension(uri: Uri): String {
        var extension = ""
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst()) {
                    val fileName = it.getString(nameIndex)
                    extension = fileName.substringAfterLast(".", "").toLowerCase()
                }
            }
        }
        return extension
    }

    //connect to api
    private fun submitReport() {
        val report_name = binding.reportNameEd.text.toString()
        val report_type = binding.reportTypeSpin.selectedItem.toString()
        val report_address = binding.reportAddressEd.text.toString()
        val report_details = binding.reportDetailsEd.text.toString()
        val report_page_numper = binding.reportPageNumperEd.text.toString()

        val get = requireActivity().getSharedPreferences("Save_Client", MODE_PRIVATE)
        val client_id = get.getInt("id", -1)
        val client_name = get.getString("username", "")
        val client_email = get.getString("email", "")
        // تحقق من صحة البيانات المدخلة
        if (report_name.isEmpty() || report_type.isEmpty() || report_address.isEmpty() || report_details.isEmpty()  || report_page_numper.isEmpty()) {
            Toast.makeText(activity,
                getString(com.example.reportsapp.R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }
        // الحصول على الملف من URI
        val file = getFileFromUri(fileUri!!)
        if (file == null || !file.exists()) {
            Toast.makeText(activity, getString(com.example.reportsapp.R.string.error_file), Toast.LENGTH_SHORT).show()
            return
        }
        // إرسال البيانات إلى الخادم
        val url = "https://youre_link/a/main_reports.php" // تأكد من أن هذا هو عنوان URL الصحيح
        val client = OkHttpClient()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("report_type", report_type)
            .addFormDataPart("report_name", report_name)
            .addFormDataPart("report_address", report_address)
            .addFormDataPart("report_details", report_details)
            .addFormDataPart("report_attach_file", file.name, RequestBody.create(MediaType.parse("application/excel,application/word,application/powerpoint,application/pdf"), file))
            .addFormDataPart("report_page_numper", report_page_numper)
            .addFormDataPart("report_date", currentDate)
            .addFormDataPart("report_time", currentTime)
            .addFormDataPart("id", client_id.toString())
            .addFormDataPart("username", client_name)
            .addFormDataPart("email", client_email)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Log.e("ReportsError", e.message.toString())
                     AlertDialog.Builder(requireContext())
                        .setTitle(getString(com.example.reportsapp.R.string.no_internet))
                        .setMessage(getString(com.example.reportsapp.R.string.no_internet))
                        .setCancelable(false)
                        .setPositiveButton(getString(com.example.reportsapp.R.string.retry)) { dialog, _ ->
                            dialog.dismiss()
                            submitReport() // إعادة المحاولة عند الضغط على زر "إعادة المحاولة"
                        }
                        .setNegativeButton(getString(com.example.reportsapp.R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    val responseData = response.body()?.string()
                    val jsonResponse = JSONObject(responseData ?: "")

                    activity?.runOnUiThread {
                        if (jsonResponse.has("error")) {
                            Toast.makeText(
                                requireActivity(),
                                jsonResponse.getString("error"),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(activity, getString(com.example.reportsapp.R.string.field), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val responseData = response.body()?.string()
                    Log.d("ReportsSuccess", "Response successful: $responseData")
                    activity?.let { activity ->
                        activity.runOnUiThread {
                            Toast.makeText(activity, getString(com.example.reportsapp.R.string.send_success), Toast.LENGTH_SHORT).show()
                            onButtonClicked()
                            binding.timeRemainingText.visibility = View.VISIBLE
                            val num = "https://wa.me/+2011111111111"
                            AlertDialog.Builder(requireContext())
                                .setTitle(getString(com.example.reportsapp.R.string.attention))
                                .setMessage(getString(com.example.reportsapp.R.string.we_will))
                                .setCancelable(false)
                                .setIcon(com.example.reportsapp.R.drawable.baseline_error_outline_24)
                                .setNegativeButton(getString(com.example.reportsapp.R.string.cancel)) { dialog, _ -> dialog.cancel() }
                                .setPositiveButton(getString(com.example.reportsapp.R.string.whatsApp)) { _, _ ->
                                 " NUMBER:  ${startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(num)))}"
                                }
                                .show()
                        }
                    }
                }
            }
        })
    }

    companion object {
        private const val REQUEST_CODE_SELECT_FILE = 1
    }

    // دالة للتحقق من الوقت وإخفاء أو إظهار الزر بناءً على مرور 3 أيام
    private fun checkIfButtonShouldBeHidden() {
        val lastClickedTime = sharedPreferences.getLong(buttonHideKey, 0L)
        val currentTime = System.currentTimeMillis()

        // إذا مر 3 أيام (72 ساعة = 3 * 24 * 60 * 60 * 1000)
        if (lastClickedTime != 0L && currentTime - lastClickedTime < 3 * 24 * 60 * 60 * 1000) {
            // إذا مر أقل من 3 أيام، أخفي الزر
            binding.reportBtn.visibility = View.INVISIBLE

            // حساب الوقت المتبقي
            val timeRemaining = 3 * 24 * 60 * 60 * 1000 - (currentTime - lastClickedTime)
            startCountdown(timeRemaining)

        } else {
            // إذا مر 3 أيام أو أكثر، أظهر الزر
            binding.reportBtn.visibility = View.VISIBLE
            binding.timeRemainingText.visibility = View.INVISIBLE
        }
    }

    // دالة لبدء العد التنازلي وعرض الوقت المتبقي
    private fun startCountdown(timeRemaining: Long) {
        val countdownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // تحديث الوقت المتبقي في الـ TextView
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val minutes =
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        hours
                    )
                val seconds =
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )

                binding.timeRemainingText.text = String.format(getString(com.example.reportsapp.R.string.you_cant), hours, minutes, seconds)
                if (isAdded) {
                    binding.timeRemainingText.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.example.reportsapp.R.color.black
                        )
                    )
                }
            }

            override fun onFinish() {
                // عندما ينتهي العد التنازلي، أظهر الزر مجددًا
                binding.reportBtn.visibility = View.VISIBLE
                binding.timeRemainingText.text = getString(com.example.reportsapp.R.string.time_over)
                binding.timeRemainingText.visibility = View.INVISIBLE
            }
        }
        countdownTimer.start()
    }

    // دالة يمكن استخدامها لتخزين الوقت الحالي عند الضغط على الزر
    private fun onButtonClicked() {
        // حفظ الوقت الحالي في SharedPreferences عند الضغط على الزر
        val editor = sharedPreferences.edit()
        editor.putLong(buttonHideKey, System.currentTimeMillis())
        editor.apply()

        // إخفاء الزر بعد الضغط عليه
        binding.reportBtn.visibility = View.INVISIBLE

        // إظهار رسالة للمستخدم
        Toast.makeText(activity, getString(com.example.reportsapp.R.string.the_button), Toast.LENGTH_SHORT).show()

        // بدء العد التنازلي
        val timeRemaining = 3 * 24 * 60 * 60 * 1000 // 3 أيام بالميلي ثانية
        startCountdown(timeRemaining.toString().toLong())
    }
}