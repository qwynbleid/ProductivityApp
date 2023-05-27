package com.example.myapp.utils

import android.annotation.SuppressLint
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

class Utility {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun timestampToString(timestamp: Timestamp): String {
            return SimpleDateFormat("dd/MM/yyyy").format(timestamp.toDate())
        }
    }
}