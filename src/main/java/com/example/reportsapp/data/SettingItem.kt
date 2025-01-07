package com.example.reportsapp.data

data class SettingItem(    val title: String,
                           val type: SettingType)
enum class SettingType {
    NOTIFICATIONS,
    CHANGE_PASSWORD,
    THEME,
    HELP,
    ABOUT,
    BACKUP,
    RESTORE
}
