package com.example.myapp.ui.main

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.databinding.NoteItemBinding

class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = NoteItemBinding.bind(view)

    fun bind(note: FirebaseModel) = with(binding) {
        title.text = note.title
        text.text = note.text
        creationDate.text = note.creationDate
        completeDate.text = note.completeDate


        val priorityColor = when (note.priority) {
            1 -> R.color.red
            2 -> R.color.yellow
            3 -> R.color.green

            else -> {R.color.green}
        }
        ivViewPriorityIndicator.setColorFilter(ContextCompat.getColor(itemView.context, priorityColor))
       // ivViewPriorityIndicator.setColorFilter(ContextCompat.getColor(itemView.context, priorityColor))

    }
}
