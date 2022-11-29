package com.example.feritep.models

data class Subject(
    var subjectCode: String,
    var name: String,
    var day: String,
    var teacherId: String,
    var room: String,
    var time: String
){
    constructor() : this("", "", "", "", "", "")
}
