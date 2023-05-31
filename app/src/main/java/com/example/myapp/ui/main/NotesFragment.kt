package com.example.myapp.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapp.MainActivity
import com.example.myapp.R
import com.example.myapp.databinding.FragmentNotesBinding
import com.example.myapp.ui.main.firebase.FirebaseModel
import com.example.myapp.ui.main.firebase.NoteViewHolder
import com.example.myapp.ui.settings.SettingsFragment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class NotesFragment : Fragment(R.layout.fragment_notes) {

    private lateinit var binding: FragmentNotesBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var noteAdapter: FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>
    private var currentSortOption: String = "creationDate" // Початкова опція сортування (creationDate)
    private var currentUserNotes:  FirestoreRecyclerOptions<FirebaseModel>? = null/////////////
    private var startDate: Date? = null
    private var endDate: Date? = null

    @SuppressLint("NotifyDataSetChanged")
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

            filterMenu.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.creation_date -> {
                        handleSortOptionSelected("creationDate") // Запустити обробник сортування за полем "creationDate"
                    }
                    R.id.complete_date -> {
                        handleSortOptionSelected("completeDate") // Запустити обробник сортування за полем "executionDate"
                    }
                    R.id.priority -> {
                        handleSortOptionSelected("priority") // Запустити обробник сортування за полем "priority"
                    }
//                    R.id.difficulty -> {
//                        handleSortOptionSelected("difficulty") // Запустити обробник сортування за полем "difficulty"
//                    }

                }
                drawerLayout.closeDrawer(filterMenu)
                true
            }

            navBt.setOnClickListener {

                if (drawerLayout.isDrawerOpen(leftSideMenu)) {
                    drawerLayout.closeDrawer(leftSideMenu)
                } else {
                    drawerLayout.openDrawer(leftSideMenu)
                }

            }

            leftSideMenu.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.dayBt -> {
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val startOfDay = calendar.time

                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                        val endOfDay = calendar.time

                        startDate = startOfDay
                        endDate = endOfDay

                        updateQuery(startOfDay, endOfDay)
                    }
                    R.id.weekBt -> {
                        val calendar = Calendar.getInstance()
                        calendar.firstDayOfWeek = Calendar.MONDAY
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val startOfWeek = calendar.time

                        calendar.add(Calendar.WEEK_OF_YEAR, 1)
                        val endOfWeek = calendar.time

                        startDate = startOfWeek
                        endDate = endOfWeek

                        updateQuery(startOfWeek, endOfWeek)

                    }
                    R.id.monthBt -> {
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val startOfMonth = calendar.time

                        calendar.add(Calendar.MONTH, 1)
                        val endOfMonth = calendar.time

                        startDate = startOfMonth
                        endDate = endOfMonth

                        updateQuery(startOfMonth, endOfMonth)

                    }
                    R.id.yearBt -> {
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.DAY_OF_YEAR, 1)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val startOfYear = calendar.time

                        calendar.add(Calendar.YEAR, 1)
                        val endOfYear = calendar.time

                        startDate = startOfYear
                        endDate = endOfYear

                        updateQuery(startOfYear, endOfYear)

                    }
                    R.id.allBt -> {
                        val query = firestore.collection("Notes").document(user.uid).collection("UserNotes")
                            .orderBy(currentSortOption, Query.Direction.ASCENDING)

                        val userNotes = FirestoreRecyclerOptions.Builder<FirebaseModel>()
                            .setQuery(query, FirebaseModel::class.java)
                            .build()

                        currentUserNotes = userNotes
                        startDate = null
                        endDate = null
                        noteAdapter.updateOptions(userNotes)
                        binding.recyclerView.adapter?.notifyDataSetChanged()
                    }
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
            .orderBy(currentSortOption, Query.Direction.ASCENDING)

        val userNotes = FirestoreRecyclerOptions.Builder<FirebaseModel>()
            .setQuery(query, FirebaseModel::class.java)
            .build()

        //qetQuery(startDate, endDate)

        if (currentUserNotes  == null || (startDate == null && endDate == null)) {
            currentUserNotes = userNotes

            noteAdapter =
                object : FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(currentUserNotes!!) {
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
        } else {
            updateQuery(startDate!!, endDate!!)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(context, 1)
        binding.recyclerView.adapter = noteAdapter
        noteAdapter.startListening()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateQuery(startDate: Date, endDate: Date) {

        val query = firestore.collection("Notes")
            .document(user.uid)
            .collection("UserNotes")
            .whereGreaterThanOrEqualTo("completeDate", startDate)
            .whereLessThan("completeDate", endDate)
            .orderBy("completeDate")
            .orderBy("priority")

        val userNotes = FirestoreRecyclerOptions.Builder<FirebaseModel>()
            .setQuery(query, FirebaseModel::class.java)
            .build()

        currentUserNotes = userNotes

        noteAdapter.updateOptions(userNotes)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun handleSortOptionSelected(sortOption: String) {
        currentSortOption = sortOption
        reloadRecyclerViewData()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun reloadRecyclerViewData() {
        noteAdapter.stopListening()

        val query = firestore.collection("Notes").document(user.uid).collection("UserNotes")
            .orderBy(currentSortOption, Query.Direction.ASCENDING)

        val userNotes = FirestoreRecyclerOptions.Builder<FirebaseModel>()
            .setQuery(query, FirebaseModel::class.java).build()


        noteAdapter.updateOptions(userNotes)

        binding.recyclerView.adapter?.notifyDataSetChanged()
        noteAdapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

}