package com.example.reportsapp.data


data class Reports(var id:Int, val username:String, val email:String, val address:String, var password:String, var confirm_password:String, val Phone:String,val last_used: String)
data class DataReports(val reported: ArrayList<DataHistory>)
data class DataHistory(
    val report_id: Int,
    val report_type: String,
    val report_name: String,
    val report_address: String,
    val report_details: String,
    val report_attach_file: String,
    val report_page_numper: String,
    val report_date: String,
    val report_time: String,
    val report_status: String
)
data class DataRecycler(val zero : ArrayList<DataZero>)
data class DataZero(
    val report_id: Int,
    val report_type: String,
    val report_name: String,
    val report_address: String,
    val report_details: String,
    val report_attach_file: String,
    val report_page_numper: String,
    val report_date: String,
    val report_time: String,
    val report_status: String,
    val group_id: Int,
    val id: Int,
    val username: String,
    val email: String
)
data class UserUpdateRequest(
    val username: String,
    val address: String,
    val email: String,
    val password: String,
    val confirm_password: String,
    val id: Int,
    val Phone: String
)

data class UserUpdateResponse(
    val status: String,
    val message: String
)
data class DeleteUserRequest(val id: Int)

data class DeleteUserResponse(val status: String, val message: String)