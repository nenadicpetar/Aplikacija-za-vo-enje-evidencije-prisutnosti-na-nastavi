package com.example.feritep.models

data class Notifications(
    val title: String,
    val body: String,
    val date: String,
    val key: String,
    var targetUser: String
){
    constructor() : this("", "", "", "", "")
}
