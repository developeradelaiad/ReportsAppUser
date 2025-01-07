package com.example.reportsapp.nav_fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import com.example.reportsapp.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reportsapp.ForgetPasswordActivity
import com.example.reportsapp.adapter.SettingsAdapter
import com.example.reportsapp.data.SettingItem
import com.example.reportsapp.data.SettingType
import com.example.reportsapp.databinding.FragmentSettingsBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val settingsList = listOf(
            SettingItem(getString(R.string.open_close_notification), SettingType.NOTIFICATIONS),
            SettingItem(getString(R.string.change_password), SettingType.CHANGE_PASSWORD),
            SettingItem(getString(R.string.dark_light), SettingType.THEME),
            SettingItem(getString(R.string.contact_support), SettingType.HELP),
            SettingItem(getString(R.string.about_the_app), SettingType.ABOUT),
            SettingItem(getString(R.string.backup_settings), SettingType.BACKUP),
            SettingItem(getString(R.string.restore_the_original), SettingType.RESTORE)
        )

        binding.settingsRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.settingsRecyclerView.adapter = SettingsAdapter(settingsList) { settingItem ->
            onSettingClicked(settingItem)
        }
        return binding.root
    }

    private fun onSettingClicked(settingItem: SettingItem) {
        when (settingItem.type) {
            SettingType.NOTIFICATIONS -> toggleNotifications()
            SettingType.CHANGE_PASSWORD -> changePassword()
            SettingType.THEME -> toggleTheme()
            SettingType.HELP -> contactSupport()
            SettingType.ABOUT -> showAbout()
            SettingType.BACKUP -> backupSettings()
            SettingType.RESTORE -> restoreSettings()
        }
    }

    private fun changePassword() {
        startActivity(Intent(requireActivity(), ForgetPasswordActivity::class.java))
    }

    private fun toggleNotifications() {
        val preferences = requireActivity().getSharedPreferences("app_preferences", MODE_PRIVATE)
        val editor = preferences.edit()
        val currentSetting = preferences.getBoolean("notifications_enabled", true)
        editor.putBoolean("notifications_enabled", !currentSetting)
        editor.apply()

        val message = if (currentSetting) getString(R.string.notification_turn_off) else getString(R.string.notification_turn_on)
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun toggleTheme() {
        val preferences = requireActivity().getSharedPreferences("app_preferences", MODE_PRIVATE)
        val editor = preferences.edit()
        val currentTheme = preferences.getBoolean("dark_theme", false)

        // تغيير الثيم
        if (currentTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // الوضع الفاتح
            editor.putBoolean("dark_theme", false)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // الوضع الداكن
            editor.putBoolean("dark_theme", true)
        }

        editor.apply()

        // إعادة تشغيل الـ Activity لتطبيق التغييرات
        requireActivity().recreate()
    }

    private fun contactSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("your_email") // استبدل بعنوان البريد الإلكتروني الفعلي
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.application_support))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.i_need_help))
        }
        if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(emailIntent)
        } else {
            Toast.makeText(activity, getString(R.string.no_mail), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAbout() {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.about_the_app))
            .setMessage(getString(R.string.this_app))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun backupSettings() {
        val preferences = requireActivity().getSharedPreferences("app_preferences", MODE_PRIVATE)
        val notificationsEnabled = preferences.getBoolean("notifications_enabled", true)
        val selectedLanguage = preferences.getString("selected_language", "ar")
        val darkTheme = preferences.getBoolean("dark_theme", false)

        // إنشاء ملف نصي للنسخ الاحتياطي
        val backupFile = File(requireActivity().getExternalFilesDir(null), "settings_backup.txt")
        try {
            FileOutputStream(backupFile).use { outputStream ->
                val data = """
                    Notifications Enabled: $notificationsEnabled
                    Selected Language: $selectedLanguage
                    Dark Theme: $darkTheme
                """.trimIndent()

                outputStream.write(data.toByteArray())
                Toast.makeText(activity, "${getString(R.string.backup_success)} :${backupFile.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "${getString(R.string.backup_failed)} :${e.message}", Toast.LENGTH_LONG).show()
        }
        requireActivity().recreate()
    }

    private fun restoreSettings() {
        val backupFile = File(requireActivity().getExternalFilesDir(null), "settings_backup.txt")
        if (backupFile.exists()) {
            try {
                val inputStream = FileInputStream(backupFile)
                val data = inputStream.bufferedReader().use { it.readText() }

                val lines = data.split("\n")
                val preferences = requireActivity().getSharedPreferences("app_preferences", MODE_PRIVATE)
                val editor = preferences.edit()

                for (line in lines) {
                    when {
                        line.startsWith("Notifications Enabled: ") -> {
                            val value = line.split(": ")[1].toBoolean()
                            editor.putBoolean("notifications_enabled", value)
                        }
                        line.startsWith("Selected Language: ") -> {
                            val value = line.split(": ")[1]
                            editor.putString("selected_language", value)
                        }
                        line.startsWith("Dark Theme: ") -> {
                            val value = line.split(": ")[1].toBoolean()
                            editor.putBoolean("dark_theme", value)
                        }
                    }
                }
                editor.apply()
                Toast.makeText(activity, getString(R.string.setting_restore_success), Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(activity, "${getString(R.string.settings_restore_faield)} ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(activity, getString(R.string.no_backup), Toast.LENGTH_LONG).show()
        }
        requireActivity().recreate()
    }
}