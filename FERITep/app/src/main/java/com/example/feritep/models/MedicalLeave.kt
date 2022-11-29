package com.example.feritep.models

data class MedicalLeave(
    var askingDate: String,
    var mkpt: String,
    var imgurl: String,
    var progress: String,
    var key: String
){
    constructor() : this("", "", "", "", "")
}
