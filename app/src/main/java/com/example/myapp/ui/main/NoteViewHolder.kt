package com.example.myapp.ui.main

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.databinding.NoteItemBinding

class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = NoteItemBinding.bind(view)

    fun bind(note: FirebaseModel) = with(binding) {
        title.text = note.title
        text.text = note.text
        creationDate.text = note.creationDate
        completeDate.text= note.completeDate
    }
}
