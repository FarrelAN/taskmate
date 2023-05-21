package com.mfarrelathaillahnugrohojsleepmn.imeonapps.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.*
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.R
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.databinding.FragmentHomeBinding
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.databinding.FragmentSignUpBinding
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.utils.ToDoAdapter
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.utils.ToDoData


class HomeFragment : Fragment(), PopupAddTaskFragment.DialogNextBtnClickListener,
    ToDoAdapter.ToDoAdapterClicksInterface {

    private lateinit var auth : FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private lateinit var PopupFragment : PopupAddTaskFragment
    private lateinit var adapter : ToDoAdapter
    private lateinit var mList : MutableList<ToDoData>

    private lateinit var signUpBinding: FragmentSignUpBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disableBackPress()
        init(view)
        getDataFromFirebase()
        registerEvents()
    }


    private fun registerEvents(){
        binding.buttonAdd.setOnClickListener{
            PopupFragment = PopupAddTaskFragment()
            PopupFragment.setListener(this)
            PopupFragment.show(childFragmentManager, "PopupAddTaskFragment")
        }


        binding.HomeLogout.setOnClickListener{
            val auth: FirebaseAuth
            val authStateListener =
                AuthStateListener { auth ->
                    if (auth.currentUser == null) {
                        navController.popBackStack(R.id.homeFragment, false)
                        navController.navigate(R.id.action_homeFragment_to_loginFragment)
                    } else {

                    }
                }
            auth = FirebaseAuth.getInstance()
            auth.addAuthStateListener(authStateListener)
            auth.signOut()
        }

    }


    private fun disableBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // do nothing for disable back button
            }
        })
    }

    private fun init(view: View) {

        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance("https://imeonapps-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("Tasks").child(auth.currentUser?.uid.toString())

        binding.recycleviewtask.setHasFixedSize(true)
        binding.recycleviewtask.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.recycleviewtask.adapter = adapter

        binding.HomeName.setSelected(true);
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance("https://imeonapps-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users").child(userId)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("nama").value?.toString()
                    val status = snapshot.child("status").value?.toString()
                    binding.HomeName.text = name
                    binding.HomeStatus.text = status
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    private fun getDataFromFirebase() {

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for(taskSnapshot in snapshot.children) {
                    val taskname = taskSnapshot.child("taskName").value?.toString()
                    val taskdate = taskSnapshot.child("taskDate").value?.toString()
                    if (taskname != null && taskdate != null){
                        val todoData = ToDoData(taskSnapshot.key!!, taskname, taskdate)
                        mList.add(todoData)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        }
        )
    }

    class Task(val taskName: String, val taskDate: String) {
        constructor() : this("", "")
    }


    override fun onSaveTask(nametask: String, taskinput: EditText, taskdate : String, taskdateinput : EditText) {
        println("Nyampe awalonSaveTask")
        val task = Task(nametask, taskdate)
        databaseRef.push().setValue(task).addOnCompleteListener() {
            println("Nyampe dalem dbref onSaveTask")
            if (it.isSuccessful){
                Toast.makeText(context, "Task successfuly saved", Toast.LENGTH_SHORT).show()
                println("Kesave tetep bang")
                taskinput.text = null
                taskdateinput.text = null
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                println("gakesave tetep bang")
            }
            PopupFragment.dismissNow()
            println("Nyampe selesai dbref onSaveTask")
        }
        println("Nyampe selesai full onSaveTask")
    }

    override fun onDeleteTaskBtnClick(toDoData: ToDoData) {
        databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClick(toDoData: ToDoData) {
        TODO("Not yet implemented")
    }

}