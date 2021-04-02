package com.pedulinegeri.unjukrasa.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.auth.SignUpPageActivity
import com.pedulinegeri.unjukrasa.databinding.FragmentProfilePageBinding
import com.pedulinegeri.unjukrasa.home.DemonstrationListAdapter
import com.pedulinegeri.unjukrasa.home.ViewType


class ProfilePageFragment : Fragment() {

    private val RC_SIGN_IN = 1
    private var fragmentBinding: FragmentProfilePageBinding? = null

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentProfilePageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        setupAuth()
        setupTabLayout()

        binding.rvDemonstration.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = DemonstrationListAdapter(arrayListOf("1111", "2222", "2222", "2222", "2222"), ViewType.PROFILE)
        }

        binding.fabAdd.setOnClickListener {
            Toast.makeText(requireContext(), "fab clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.rvDemonstration.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) binding.fabAdd.hide() else if (dy < 0) binding.fabAdd.show()
            }
        })
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
        }
    }

    private fun setupTabLayout() {
        val binding = fragmentBinding!!

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.fabAdd.show()
                when (tab.text) {
                    resources.getString(R.string.mendukung) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(arrayListOf("1111", "2222", "2222", "2222", "2222"), ViewType.PROFILE)
                    resources.getString(R.string.membuat) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(arrayListOf("1111"), ViewType.PROFILE)
                    resources.getString(R.string.ditandai) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(arrayListOf(), ViewType.PROFILE)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (response!!.isNewUser) {
                    val intent = Intent(requireContext(), SignUpPageActivity::class.java)
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