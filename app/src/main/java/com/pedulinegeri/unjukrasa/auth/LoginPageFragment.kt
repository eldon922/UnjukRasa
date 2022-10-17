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
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentLoginPageBinding
import com.pedulinegeri.unjukrasa.profile.User


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
                        if (findNavController().previousBackStackEntry?.destination?.id == R.id.demonstrationPageFragment) {
                            requireActivity().findNavController(R.id.navHostContainerMain)
                                .navigate(R.id.actionLoginPageFragmentToSignUpPageFragment)
                        } else {
                            requireActivity().findNavController(R.id.navHostContainerMain)
                                .navigate(R.id.actionMainScreenToSignUpPageFragment)
                        }
                    } else {
                        authViewModel.signedIn(Firebase.auth.currentUser!!.uid)
                        authViewModel.saveName(it?.toObject<User>()!!.name)
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.unknown_error_message, it),
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

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnLogin.setOnClickListener { setupAuth() }

        if (!authLaunched) {
            setupAuth()
        }

        if (findNavController().previousBackStackEntry?.destination?.id == R.id.demonstrationPageFragment) {
            binding.appbar.visibility = View.VISIBLE
        }

        authViewModel.isSignedIn.observe(viewLifecycleOwner) {
            if (it) {
                requireActivity().onBackPressed()
            }
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