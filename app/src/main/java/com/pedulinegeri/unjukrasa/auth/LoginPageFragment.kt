package com.pedulinegeri.unjukrasa.auth

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.processphoenix.ProcessPhoenix
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentLoginPageBinding


class LoginPageFragment : Fragment() {

    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private var authLaunched = false

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { authResult ->
        if (authResult.resultCode == Activity.RESULT_OK) {
            val db = Firebase.firestore
            val docRef = db.collection("users").document(Firebase.auth.currentUser!!.uid)
            docRef.get()
                .addOnSuccessListener {
                    if (!it.exists()) {
                        requireActivity().findNavController(R.id.nav_host_container_main)
                            .navigate(R.id.action_main_screen_to_signUpPageFragment)
                    } else {
                        authViewModel.signedIn(Firebase.auth.currentUser!!.uid)
                        ProcessPhoenix.triggerRebirth(requireContext())
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Ada kesalahan, silahkan coba lagi. ($it)",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener { setupAuth() }

        if (!authLaunched) {
            setupAuth()
        }
    }

    private fun setupAuth() {
        val providers =
            arrayListOf(AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("id").build())
        signInLauncher.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher) // Set logo drawable
                .setTheme(R.style.Theme_UnjukRasa) // Set theme
                .build()
        )

        authLaunched = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}