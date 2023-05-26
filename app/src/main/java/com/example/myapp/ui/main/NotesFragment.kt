package com.example.myapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapp.MainActivity
import com.example.myapp.R
import com.example.myapp.databinding.FragmentNotesBinding
import com.example.myapp.ui.settings.SettingsFragment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotesFragment : Fragment(R.layout.fragment_notes) {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var noteAdapter: FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNotesBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            user = FirebaseAuth.getInstance().currentUser!!
        }

        firestore = FirebaseFirestore.getInstance()

        val activity = requireActivity() as MainActivity
        activity.showMenu(true)

        binding.apply {
            createNoteBt.setOnClickListener {

                val addNoteFragment = AddNoteFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, addNoteFragment)
                    .addToBackStack("NotesFragment")
                    .commit()
            }

            filterBt.setOnClickListener {
                if (drawerLayout.isDrawerOpen(filterMenu)) {
                    drawerLayout.closeDrawer(filterMenu)
                } else {
                    drawerLayout.openDrawer(filterMenu)
                }
            }

//            filterMenu.setNavigationItemSelectedListener {
//                when (it.itemId) {
//                    R.id.priority -> {
//
//                    }
//                    R.id.difficulty -> {
//
//                    }
//                    R.id.creation_date -> {
//
//                    }
//                    R.id.execution_date -> {
//
//                    }
//                }
//                drawerLayout.closeDrawer(filterMenu)
//                true
//            }

            navBt.setOnClickListener {

                if (drawerLayout.isDrawerOpen(leftSideMenu)) {
                    drawerLayout.closeDrawer(leftSideMenu)
                } else {
                    drawerLayout.openDrawer(leftSideMenu)
                }

            }

            leftSideMenu.setNavigationItemSelectedListener {
                when (it.itemId) {
//                    R.id.dayBt -> {
//                        TODO()
//                    }
//                    R.id.weekBt -> {
//                        TODO()
//                    }
//                    R.id.monthBt -> {
//                        TODO()
//                    }
//                    R.id.yearBt -> {
//                        TODO()
//                    }
                    R.id.settingsBt -> {

                        val fragmentSettings = SettingsFragment()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, fragmentSettings)
                            .addToBackStack("NotesFragment")
                            .commit()
                    }
                }
                drawerLayout.closeDrawer(leftSideMenu)
                true
            }

        }

        val query = firestore.collection("Notes").document(user.uid).collection("UserNotes")
            .orderBy("creationDate", Query.Direction.ASCENDING)

        val userNotes = FirestoreRecyclerOptions.Builder<FirebaseModel>()
            .setQuery(query, FirebaseModel::class.java).build()


        noteAdapter =
            object : FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(userNotes) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): NoteViewHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.note_item, parent, false)
                    return NoteViewHolder(itemView)
                }

                override fun onBindViewHolder(
                    holder: NoteViewHolder,
                    position: Int,
                    model: FirebaseModel
                ) {

                    val noteId = noteAdapter.snapshots.getSnapshot(position).id

                    holder.bind(model)

                    holder.itemView.setOnClickListener {

                        val note = Note(model.title, model.text, model.creationDate, model.completeDate, model.priority, noteId)

                        val addNoteFragment = AddNoteFragment(true, note)

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, addNoteFragment)
                            .addToBackStack("NotesFragment")
                            .commit()

                    }

                }

            }

        binding.recyclerView.layoutManager = GridLayoutManager(context, 1)
        binding.recyclerView.adapter = noteAdapter
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

}