package com.example.myapp.ui.main

class FirebaseModel() {

    var title: String = ""
    var text: String = ""
    var creationDate: String = ""
    var completeDate: String = ""
    var priority: Int = 3

    constructor(
        title: String,
        text: String,
        creationDate: String,
        completeDate: String,
        priority: Int
    ) : this() {
        this.title = title
        this.text = text
        this.creationDate = creationDate
        this.completeDate = completeDate
        this.priority = priority
    }

}