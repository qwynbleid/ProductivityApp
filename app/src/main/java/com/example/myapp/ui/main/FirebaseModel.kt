package com.example.myapp.ui.main

class FirebaseModel() {

    var title: String = ""
    var text: String = ""
    var creationDate: String = ""

    constructor(title: String, text: String, creationDate: String) : this() {
        this.title = title
        this.text = text
        this.creationDate = creationDate
    }
}