package com.ucsm.tylersai.amsteacher.ui.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ucsm.tylersai.amsteacher.R
import com.ucsm.tylersai.amsteacher.adapter.AddTeacherListViewAdapter
import com.ucsm.tylersai.amsteacher.model.Teacher
import com.ucsm.tylersai.amsteacher.ui.activity.AddTeacherDeanActivity
import com.ucsm.tylersai.amsteacher.ui.activity.Main2Activity

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



lateinit var teacherListAry: ArrayList<Teacher>

class AddTeacherFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    lateinit var btAddTeacher: Button
    lateinit var listViewTeacherList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_teacher_dean, container, false)
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Učitavanje...")
        progressDialog.show()

        val activity = activity as Main2Activity
        activity.supportActionBar!!.title = "Dodavanje profesora"

        btAddTeacher = view.findViewById(R.id.bt_add_add_teacher)
        listViewTeacherList = view.findViewById(R.id.list_view_teacher_list_add_teacher)

        teacherListAry = ArrayList<Teacher>()
        teacherListAry.clear()

        //get teacher data from firebase
        val teacherTable = FirebaseDatabase.getInstance().reference.child("ams").child("teacher")
        teacherTable.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, "Dogodila se greška ${p0.message}", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                teacherListAry.clear()
                dataSnapshot.children.forEach {
                    val teacherObj = it.getValue(Teacher::class.java)
                    teacherListAry.add(teacherObj!!)

                    //set list view adapter and update ui
                    listViewTeacherList.adapter = AddTeacherListViewAdapter(context!!, teacherListAry)
                    progressDialog.dismiss()
                }
            }

        })


        btAddTeacher.setOnClickListener {
            val intent = Intent(context, AddTeacherDeanActivity::class.java)
            startActivity(intent)
        }

        return view
    }


    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddTeacherFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
