package com.mfarrelathaillahnugrohojsleepmn.imeonapps.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.R
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {

    private lateinit var auth:FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun init(view:View){
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun registerEvents(){

        binding.signupSignin.setOnClickListener {
            navControl.navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.signupButton.setOnClickListener{
            val nama = binding.signupName.text.toString().trim()
            val status = binding.signupStatus.text.toString().trim()
            val email = binding.signupEmail.text.toString().trim()
            val pass = binding.signupPassword.text.toString().trim()
            val confpass = binding.signupRepassword.text.toString().trim()

            if(email.isNotEmpty() && pass.isNotEmpty() && confpass.isNotEmpty() && nama.isNotEmpty() && status.isNotEmpty()) {
                if (pass == confpass){
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                        OnCompleteListener{
                            if (it.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                val user = hashMapOf(
                                    "nama" to nama,
                                    "status" to status
                                )
                                if (userId != null) {
                                    FirebaseDatabase.getInstance("https://imeonapps-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(userId).setValue(user)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Account Created Successfuly, Please Log In", Toast.LENGTH_SHORT).show()
                                            navControl.navigate(R.id.action_signUpFragment_to_loginFragment)
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }else{
                                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                    )
                }
            }
        }
    }
}