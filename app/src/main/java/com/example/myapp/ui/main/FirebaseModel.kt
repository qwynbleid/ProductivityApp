package com.example.myapp.ui.main

import com.google.firebase.Timestamp
import java.util.*

class FirebaseModel {
    var title: String = ""
    var text: String = ""
    var creationDate: Timestamp = Timestamp.now()
    //var creationDate: String = ""
    var completeDate: Timestamp = Timestamp.now()
    var priority: Int = 3


//    constructor(
//        title: String,
//        text: String,
//        creationDate: String,
//        completeDate: String,
//        priority: Int
//    ) : this() {
//        this.title = title
//        this.text = text
//        this.creationDate = creationDate
//        this.completeDate = completeDate
//        this.priority = priority
//    }

}