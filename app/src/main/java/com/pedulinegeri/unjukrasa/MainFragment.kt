package com.pedulinegeri.unjukrasa

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var fragmentBinding: FragmentMainBinding? = null

    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentMainBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        setupToolbar()

        setupBottomNavigation()

        setupNavigationDrawer()

        if (mainViewModel.bottomNavState != -1){
            binding.bottomNavigation.selectedItemId = mainViewModel.bottomNavState
        }

        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback {
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        val binding = fragmentBinding!!
        mainViewModel.bottomNavState = binding.bottomNavigation.selectedItemId

        onBackPressedCallback.remove()
    }

    private fun setupToolbar() {
        val binding = fragmentBinding!!
        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)

        binding.notificationButton.setOnClickListener {
            findNavController().navigate(R.id.action_main_screen_to_navigation_notification_page)
        }

        binding.hamIcon.setOnClickListener {
            binding.drawer.open()
        }

        findNavController().addOnDestinationChangedListener { _, _, _ ->
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
    }

    private fun setupNavigationDrawer() {
        val binding = fragmentBinding!!

        val drawerToggle =
            ActionBarDrawerToggle(requireActivity(), binding.drawer, R.string.open, R.string.close)
        binding.drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener {
            when (it.title) {
                resources.getString(R.string.inisiasi_unjuk_rasa) -> {
                    findNavController().navigate(R.id.action_main_screen_to_navigation_new_demonstration_page)
                }
                resources.getString(R.string.masuk) -> binding.bottomNavigation.selectedItemId =
                    R.id.navigation_profile_page
                resources.getString(R.string.keluar) -> {
                    AuthUI.getInstance().signOut(requireContext()).addOnSuccessListener {
                        authViewModel.signedOut()
                    }
                    binding.bottomNavigation.selectedItemId = R.id.navigation_home_page

                    // TODO DEV
                    authViewModel.signedOut()

                }
                resources.getString(R.string.pengaturan) -> false
                resources.getString(R.string.bantuan) -> false
                resources.getString(R.string.tentang) -> false
                else -> false
            }

            binding.drawer.close()

            true
        }

        authViewModel.isSignedIn.observe(viewLifecycleOwner, { signedIn ->
            val drawerMenu = binding.navigationDrawer.menu
            val bottomNavigationMenu = binding.bottomNavigation.menu

            if (signedIn) {
                drawerMenu.findItem(R.id.action_login).isVisible = false
                drawerMenu.findItem(R.id.action_logout).isVisible = true

                bottomNavigationMenu.findItem(R.id.navigation_login_page).isVisible = false
                bottomNavigationMenu.findItem(R.id.navigation_message_page).isVisible = true
                bottomNavigationMenu.findItem(R.id.navigation_profile_page).isVisible = true
            } else {
                drawerMenu.findItem(R.id.action_login).isVisible = true
                drawerMenu.findItem(R.id.action_logout).isVisible = false

                bottomNavigationMenu.findItem(R.id.navigation_login_page).isVisible = true
                bottomNavigationMenu.findItem(R.id.navigation_message_page).isVisible = false
                bottomNavigationMenu.findItem(R.id.navigation_profile_page).isVisible = false
            }

            binding.bottomNavigation.selectedItemId = R.id.navigation_home_page
        })
    }

    private fun setupBottomNavigation() {
        val binding = fragmentBinding!!

        val navGraphIds = listOf(
            R.navigation.home_page,
            R.navigation.message_page,
            R.navigation.profile_page,
            R.navigation.login_page
        )

        // Setup the bottom navigation view with a list of navigation graphs
        binding.bottomNavigation.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = childFragmentManager,
            containerId = R.id.nav_host_container,
            intent = requireActivity().intent
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val binding = fragmentBinding!!
                binding.drawer.open()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}