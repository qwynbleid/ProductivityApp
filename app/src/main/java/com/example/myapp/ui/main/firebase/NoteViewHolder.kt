package com.example.myapp.ui.main.firebase

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.example.myapp.databinding.NoteItemBinding
import com.example.myapp.ui.main.firebase.FirebaseModel
import com.example.myapp.utils.Utility

class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = NoteItemBinding.bind(view)

    fun bind(note: FirebaseModel) = with(binding) {
        title.text = note.title
        text.text = note.text
        creationDate.text = Utility.timestampToString(note.creationDate)
        completeDate.text = Utility.timestampToString(note.completeDate)

        val priorityColor = when (note.priority) {
            1 -> R.color.red
            2 -> R.color.yellow
            3 -> R.color.green
            else -> {R.color.green}
        }
        ivViewPriorityIndicator.setColorFilter(ContextCompat.getColor(itemView.context, priorityColor))
    }
}
