package com.example.myapp.ui.main

import com.google.firebase.Timestamp
import java.util.*

class Note(
    var title: String = "",
    var text: String = "",
    var creationDate: Timestamp = Timestamp.now(),
    var completeDate: Timestamp = Timestamp.now(),
    var priority: Int = 3,
    var noteId : String = ""
)
