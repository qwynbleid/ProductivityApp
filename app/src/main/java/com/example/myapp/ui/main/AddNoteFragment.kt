package com.example.myapp.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myapp.MainActivity
import com.example.myapp.R
import com.example.myapp.databinding.FragmentAddNoteBinding
import com.example.myapp.utils.Utility
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddNoteFragment(val editMode: Boolean = false, val note: Note = Note("","", Timestamp.now(),Timestamp.now(),3,"")) : Fragment(R.layout.fragment_add_note) {

    private lateinit var binding : FragmentAddNoteBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var docRef: DocumentReference

    private var noteCompleteDate: Timestamp = note.completeDate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddNoteBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        firestore = FirebaseFirestore.getInstance()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)
            noteCompleteDate = Timestamp(selectedDate)
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            binding.completeDate.text = format.format(selectedDate)
            binding.completeDate.visibility = View.VISIBLE
        }


        val activity = requireActivity() as MainActivity
        activity.showMenu(false)

        binding.apply {

            if (editMode) {
                noteTitle.setText(note.title)
                noteText.setText(note.text)
                completeDate.text = note.completeDate.let { Utility.timestampToString(it) }
                completeDate.visibility = View.VISIBLE
                deleteBt.visibility = View.VISIBLE
                topTextView.text = getString(R.string.edit_note)

                when(note.priority) {
                    1 -> {priorityGroup.check(highPriority.id)}
                    2 -> {priorityGroup.check(mediumPriority.id)}
                    3 -> {priorityGroup.check(lowPriority.id)}
                }
            }

            deleteBt.setOnClickListener {

                val alertDialog = AlertDialog.Builder(requireContext())
                    .setMessage(R.string.sure_want_delete)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        noteProgressBar.visibility = View.VISIBLE

                        docRef = firestore.collection("Notes").document(user.uid).collection("UserNotes").document(note.noteId)

                        docRef.delete().addOnSuccessListener {

                            Toast.makeText(requireContext(), R.string.deleted_note, Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)


                        }.addOnFailureListener {

                            Toast.makeText(requireContext(), R.string.cannot_deleted_note, Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)

                        }
                    }

                    .setNegativeButton(R.string.no, null)
                    .create()
                alertDialog.show()


            }



            var priority: Int = note.priority
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
                        Toast.makeText(requireContext(),R.string.all_fields_should_be_filled, Toast.LENGTH_SHORT).show()
                    } else {

                        noteProgressBar.visibility = View.VISIBLE

                        docRef = firestore.collection("Notes").document(user.uid).collection("UserNotes").document(note.noteId)

                        val noteMap = hashMapOf<String, Any>()

                        noteMap["title"] = title
                        noteMap["text"] = text
                        noteMap["creationDate"] = Timestamp.now()
                        noteMap["completeDate"] = noteCompleteDate
                        noteMap["priority"] = priority

                        docRef.set(noteMap).addOnCompleteListener {
                            Toast.makeText(requireContext(),R.string.note_updated, Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)


                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),R.string.failed_to_update, Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)

                        }

                    }

                } else {

                    val title = noteTitle.text.toString()
                    val text = noteText.text.toString()

                    if (title.isEmpty() || text.isEmpty() || completeDate.text == "noDate") {
                        Toast.makeText(requireContext(),R.string.all_fields_should_be_filled, Toast.LENGTH_SHORT).show()
                    } else {

                        noteProgressBar.visibility = View.VISIBLE


                        docRef = firestore.collection("Notes").document(user.uid).collection("UserNotes").document()
                        val noteMap = hashMapOf<String, Any>()
                        noteMap["title"] = title
                        noteMap["text"] = text
                        noteMap["creationDate"] = Timestamp.now()
                        noteMap["completeDate"] = noteCompleteDate
                        noteMap["priority"] = priority

                        docRef.set(noteMap).addOnCompleteListener {
                            Toast.makeText(requireContext(),R.string.note_created, Toast.LENGTH_SHORT).show()

                            requireActivity().supportFragmentManager
                                .popBackStack("NotesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)


                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),R.string.failed_to_create, Toast.LENGTH_SHORT).show()

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
}

