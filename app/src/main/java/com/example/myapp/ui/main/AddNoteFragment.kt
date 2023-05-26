package com.example.myapp.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myapp.MainActivity
import com.example.myapp.R
import com.example.myapp.databinding.FragmentAddNoteBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddNoteFragment(val editMode: Boolean = false, val note: Note = Note("","","","",0,"")) : Fragment(R.layout.fragment_add_note) {

    private lateinit var binding : FragmentAddNoteBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var docRef: DocumentReference


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddNoteBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        firestore = FirebaseFirestore.getInstance()

        //picker для дати виконання завдання
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            binding.completeDate.text = format.format(it)
            binding.completeDate.visibility = View.VISIBLE
        }


        val activity = requireActivity() as MainActivity
        activity.showMenu(false)

        binding.apply {

            if (editMode) {
                noteTitle.setText(note.title)
                noteText.setText(note.text)
                completeDate.text = note.completeDate
                completeDate.visibility = View.VISIBLE
                deleteBt.visibility = View.VISIBLE
                topTextView.text = "Edit note"

                when(note.priority) {
                    1 -> {priorityGroup.check(highPriority.id)}
                    2 -> {priorityGroup.check(mediumPriority.id)}
                    3 -> {priorityGroup.check(lowPriority.id)}
                }
            }

            deleteBt.setOnClickListener {

                val alertDialog = AlertDialog.Builder(requireContext())
                    .setMessage("Ви впевнені, що хочете видалити нотатку?")
                    .setPositiveButton("Так") { _, _ ->
                        noteProgressBar.visibility = View.VISIBLE

                        //noteText.setTextColor(R.id)

                        docRef = firestore.collection("Notes").document(user.uid).collection("UserNotes").document(note.noteId)

                        docRef.delete().addOnSuccessListener {

                            Toast.makeText(requireContext(), "Note ${note.title} deleted", Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)


                        }.addOnFailureListener {

                            Toast.makeText(requireContext(), "Can not delete the note", Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)

                        }
                    }

                    .setNegativeButton("Ні", null)
                    .create()
                alertDialog.show()


            }

            var priority: Int = 3
            priorityGroup.setOnCheckedChangeListener { _, checkedId ->
                priority = when (checkedId) {
                    R.id.lowPriority -> 3
                    R.id.mediumPriority -> 2
                    R.id.highPriority -> 1
                    else -> 3
                }
            }

            saveBt.setOnClickListener {


                if (editMode) {

                    val title = noteTitle.text.toString()
                    val text = noteText.text.toString()


                    if (title.isEmpty() || text.isEmpty() || completeDate.text == "noDate") {
                        Toast.makeText(requireContext(),"All fields should be filled", Toast.LENGTH_SHORT).show()
                    } else {

                        noteProgressBar.visibility = View.VISIBLE

                        docRef = firestore.collection("Notes").document(user.uid).collection("UserNotes").document(note.noteId)

                        val noteMap = hashMapOf<String, Any>()

                        noteMap["title"] = title
                        noteMap["text"] = text



                        val currentDate = Calendar.getInstance().time

                        // Встановіть формат дати
                        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                        //val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        // Отримайте рядок зі зформатованою датою
                        val formattedDate = dateFormat.format(currentDate)

                        // Встановіть отриманий рядок у TextView
                        noteMap["creationDate"] =  formattedDate
                        noteMap["completeDate"] = completeDate.text


                        noteMap["priority"] = priority

                        docRef.set(noteMap).addOnCompleteListener {
                            Toast.makeText(requireContext(),"Note is updated", Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)


                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),"Failed to update note", Toast.LENGTH_SHORT).show()



                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)



                        }



                    }

                } else {

                    val title = noteTitle.text.toString()
                    val text = noteText.text.toString()


                    if (title.isEmpty() || text.isEmpty() || completeDate.text == "noDate") {
                        Toast.makeText(requireContext(),"All fields should be filled", Toast.LENGTH_SHORT).show()
                    } else {

                        noteProgressBar.visibility = View.VISIBLE


                        docRef = firestore.collection("Notes").document(user.uid).collection("UserNotes").document()
                        val noteMap = hashMapOf<String, Any>()
                        noteMap["title"] = title
                        noteMap["text"] = text

                        val currentDate = Calendar.getInstance().time

                        // Встановіть формат дати
                        //val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
                        // Отримайте рядок зі зформатованою датою
                        val formattedDate = dateFormat.format(currentDate)

                        // Встановіть отриманий рядок у TextView
                        noteMap["creationDate"] =  formattedDate
                        noteMap["completeDate"] = completeDate.text


                        noteMap["priority"] = priority

                        docRef.set(noteMap).addOnCompleteListener {
                            Toast.makeText(requireContext(),"Note created successfully", Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)


                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),"Failed to create note", Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)

                        }
                    }
                }
            }

            pickDateBt.setOnClickListener {
                datePicker.show(requireActivity().supportFragmentManager, "picker")
            }

            returnBt.setOnClickListener {
                requireActivity().supportFragmentManager
                    .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}

