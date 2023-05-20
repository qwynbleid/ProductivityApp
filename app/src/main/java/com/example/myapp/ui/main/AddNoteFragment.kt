package com.example.myapp.ui.main

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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddNoteFragment(val editMode: Boolean = false, val note: Note = Note("","","")) : Fragment(R.layout.fragment_add_note) {

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

        val activity = requireActivity() as MainActivity
        activity.showMenu(false)

        binding.apply {

            if (editMode) {
                noteTitle.setText(note.title)
                noteText.setText(note.text)
                deleteBt.visibility = View.VISIBLE
                topTextView.text = "Edit note"
            }

            deleteBt.setOnClickListener {

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

            saveBt.setOnClickListener {

                if (editMode) {

                    val title = noteTitle.text.toString()
                    val text = noteText.text.toString()

                    if (title.isEmpty() || text.isEmpty()) {
                        Toast.makeText(requireContext(),"All fields should be filled", Toast.LENGTH_SHORT).show()
                    } else {

                        docRef = firestore.collection("Notes").document(user.uid).collection("UserNotes").document(note.noteId)

                        val noteMap = hashMapOf<String, Any>()

                        noteMap["title"] = title
                        noteMap["text"] = text

                        val currentDate = Calendar.getInstance().time

                        // Встановіть формат дати
                        //val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        // Отримайте рядок зі зформатованою датою
                        val formattedDate = dateFormat.format(currentDate)

                        // Встановіть отриманий рядок у TextView
                        noteMap["creationDate"] =  formattedDate

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

                    if (title.isEmpty() || text.isEmpty()) {
                        Toast.makeText(requireContext(),"All fields should be filled", Toast.LENGTH_SHORT).show()
                    } else {

                        docRef = firestore.collection("Notes").document(user.uid).collection("UserNotes").document()
                        val noteMap = hashMapOf<String, Any>()
                        noteMap["title"] = title
                        noteMap["text"] = text

                        val currentDate = Calendar.getInstance().time

                        // Встановіть формат дати
                        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                        // Отримайте рядок зі зформатованою датою
                        val formattedDate = dateFormat.format(currentDate)

                        // Встановіть отриманий рядок у TextView
                        noteMap["creationDate"] =  formattedDate


                        noteMap["creationDate"] =
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