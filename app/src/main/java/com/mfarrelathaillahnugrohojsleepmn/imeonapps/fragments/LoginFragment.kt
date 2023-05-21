package com.mfarrelathaillahnugrohojsleepmn.imeonapps.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.R
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.databinding.FragmentLoginBinding
import kotlin.system.exitProcess


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentLoginBinding
    private var pressedTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        disableBackPress()
        registerEvents()
    }

    private fun init(view:View){
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun disableBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (pressedTime + 2000 > System.currentTimeMillis()) {
                    exitProcess(0)
                } else {
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show();
                }
                pressedTime = System.currentTimeMillis();
            }
        })
    }

    private fun registerEvents(){

        binding.signinSignup.setOnClickListener {
            navControl.navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.signinButton.setOnClickListener{
            val email = binding.signinEmail.text.toString().trim()
            val pass = binding.signinPassword.text.toString().trim()

            if(email.isNotEmpty() && pass.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(
                        OnCompleteListener{
                        if (it.isSuccessful) {
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            navControl.navigate(R.id.action_loginFragment_to_homeFragment)
                        }else{
                            Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    )
                }
            }


        }
    }
