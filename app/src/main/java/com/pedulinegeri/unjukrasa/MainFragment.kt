package com.pedulinegeri.unjukrasa

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var addOnBackPressedCallbackJob: Job

    private var defaultStatusBarColor: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupBottomNavigation()
        setupNavigationDrawer()

        if (mainViewModel.bottomNavState != -1) {
            binding.bottomNavigation.selectedItemId = mainViewModel.bottomNavState
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = mainViewModel.bottomNavState

        defaultStatusBarColor = requireActivity().window.statusBarColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().window.statusBarColor = Color.TRANSPARENT
        }

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                requireActivity().window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        addOnBackPressedCallback()
    }

    private fun addOnBackPressedCallback() {
        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback {
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            } else if (binding.bottomNavigation.selectedItemId == R.id.navigation_home_page) {
                remove()
                Toast.makeText(
                    requireContext(),
                    "Tekan sekali lagi untuk keluar",
                    Toast.LENGTH_SHORT
                ).show()
                addOnBackPressedCallbackJob = lifecycleScope.launch {
                    delay(3000L)
                    addOnBackPressedCallback()
                }
            } else {
                remove()
                requireActivity().onBackPressed()
                addOnBackPressedCallback()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.bottomNavState = binding.bottomNavigation.selectedItemId

        onBackPressedCallback.remove()

        requireActivity().window.statusBarColor = defaultStatusBarColor
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)

        binding.hamIcon.setOnClickListener {
            binding.drawer.open()
        }

        binding.etSearch.setOnClickListener {
            findNavController().navigate(R.id.action_main_screen_to_searchPageFragment)
        }
    }

    private fun setupNavigationDrawer() {
        val drawerToggle = object : ActionBarDrawerToggle(
            requireActivity(),
            binding.drawer,
            R.string.open,
            R.string.close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                if (this@MainFragment::addOnBackPressedCallbackJob.isInitialized) {
                    addOnBackPressedCallbackJob.cancel()
                }

                onBackPressedCallback.remove()
                addOnBackPressedCallback()
            }
        }
        binding.drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener {
            when (it.title) {
                resources.getString(R.string.inisiasi_unjuk_rasa) -> {
                    findNavController().navigate(R.id.action_main_screen_to_navigation_new_demonstration_page)
                }
                resources.getString(R.string.masuk) -> binding.bottomNavigation.selectedItemId =
                    R.id.navigation_login_page
                resources.getString(R.string.keluar) -> {
                    AuthUI.getInstance().signOut(requireContext()).addOnSuccessListener {
                        authViewModel.signedOut()
                        binding.bottomNavigation.selectedItemId =
                            R.id.navigation_home_page
                        Toast.makeText(
                            requireContext(),
                            "Kamu sudah keluar dari akunmu.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                resources.getString(R.string.pengaturan) -> {
                    findNavController().navigate(R.id.action_main_screen_to_editProfilePageFragment)
                }
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
                drawerMenu.setGroupVisible(R.id.signed_in_menu, true)

                bottomNavigationMenu.findItem(R.id.navigation_login_page).isVisible = false
                bottomNavigationMenu.findItem(R.id.navigation_profile_page).isVisible = true
            } else {
                drawerMenu.findItem(R.id.action_login).isVisible = true
                drawerMenu.setGroupVisible(R.id.signed_in_menu, false)

                bottomNavigationMenu.findItem(R.id.navigation_login_page).isVisible = true
                bottomNavigationMenu.findItem(R.id.navigation_profile_page).isVisible = false
            }
        })
    }

    private fun setupBottomNavigation() {
        val navGraphIds = listOf(
            R.navigation.home_page,
            R.navigation.profile_page,
            R.navigation.login_page
        )

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
                binding.drawer.open()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}