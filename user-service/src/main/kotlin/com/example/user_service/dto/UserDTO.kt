package com.example.user_service.dto

data class UserDTO(
    var id:Long = 0L,
    var name:String="",
    var surname:String="",
    var email:String="",
    var address:String="",
    var alerting: Boolean= false,
    var energyAlertingThreshold: Double = 0.0
)
