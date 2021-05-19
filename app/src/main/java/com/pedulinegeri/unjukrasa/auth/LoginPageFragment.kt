package com.pedulinegeri.unjukrasa.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentLoginPageBinding


class LoginPageFragment : Fragment() {

    private val RC_SIGN_IN = 1
    private var fragmentBinding: FragmentLoginPageBinding? = null

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentLoginPageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        binding.btnLogin.setOnClickListener { setupAuth() }

//        setupAuth()
    }

    private fun setupAuth() {
        // Checking user directly to Firebase, just in case account signed out in Firebase server
        if (FirebaseAuth.getInstance().currentUser == null) {
            // TODO DEV
//            val providers = arrayListOf(AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("id").build())
//            startActivityForResult(
//                AuthUI.getInstance()
//                    .createSignInIntentBuilder()
//                    .setAvailableProviders(providers)
//                    .build(),
//                RC_SIGN_IN
//            )

            // TODO DEV
            authViewModel.signedIn()
            requireActivity().findNavController(R.id.nav_host_container_main).navigate(R.id.action_main_screen_to_signUpPageFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (response!!.isNewUser) {
                    requireActivity().findNavController(R.id.nav_host_container_main).navigate(R.id.action_main_screen_to_signUpPageFragment)
                } else {
                    authViewModel.signedIn()
                }
            } else {
                activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.selectedItemId =
                    R.id.navigation_home_page
            }
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}