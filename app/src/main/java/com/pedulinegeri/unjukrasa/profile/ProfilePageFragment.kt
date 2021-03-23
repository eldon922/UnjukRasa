package com.pedulinegeri.unjukrasa.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.auth.SignUpActivity
import com.pedulinegeri.unjukrasa.databinding.FragmentProfilePageBinding


class ProfilePageFragment : Fragment() {

    private val RC_SIGN_IN = 1
    private var fragmentBinding: FragmentProfilePageBinding? = null

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = FragmentProfilePageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

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
        }

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    resources.getString(R.string.mendukung) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(arrayListOf("1111", "2222"))
                    resources.getString(R.string.membuat) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(arrayListOf("1111"))
                    resources.getString(R.string.ditandai) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(arrayListOf())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.rvDemonstration.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = DemonstrationListAdapter(arrayListOf("1111", "2222"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (response!!.isNewUser) {
                    val intent = Intent(requireContext(), SignUpActivity::class.java)
                    startActivity(intent)
                }
                authViewModel.signedIn()
            } else {
                activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.selectedItemId =
                    R.id.action_home_page
            }
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}