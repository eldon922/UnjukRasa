package com.pedulinegeri.unjukrasa.auth

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentLoginPageBinding


class LoginPageFragment : Fragment() {

    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (it.idpResponse!!.isNewUser) {
                requireActivity().findNavController(R.id.nav_host_container_main)
                    .navigate(R.id.action_main_screen_to_signUpPageFragment)
            }

            authViewModel.signedIn()
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

        setupAuth()
    }

    private fun setupAuth() {
        if (FirebaseAuth.getInstance().currentUser == null) {
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
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}