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
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            } else if (binding.bottomNavigation.selectedItemId == R.id.navigationHomePage) {
                remove()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.exit_app_confirmation),
                    Toast.LENGTH_SHORT
                ).show()
                lifecycleScope.launch {
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
            findNavController().navigate(R.id.actionMainScreenToSearchPageFragment)
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

                addOnBackPressedCallback()
            }
        }
        binding.drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener {
            when (it.title) {
                resources.getString(R.string.initiate_demonstration) -> {
                    findNavController().navigate(R.id.actionMainScreenToNavigationNewDemonstrationPage)
                }
                resources.getString(R.string.login) -> binding.bottomNavigation.selectedItemId =
                    R.id.navigationLoginPage
                resources.getString(R.string.logout) -> {
                    AuthUI.getInstance().signOut(requireContext()).addOnSuccessListener {
                        authViewModel.signedOut()
                        binding.bottomNavigation.selectedItemId =
                            R.id.navigationHomePage
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.logout_success_message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                resources.getString(R.string.settings) -> {
                    findNavController().navigate(R.id.actionMainScreenToEditProfilePageFragment)
                }
                resources.getString(R.string.help) -> false
                resources.getString(R.string.about) -> false
                else -> false
            }

            binding.drawer.close()

            true
        }

        authViewModel.isSignedIn.observe(viewLifecycleOwner, { signedIn ->
            val drawerMenu = binding.navigationDrawer.menu
            val bottomNavigationMenu = binding.bottomNavigation.menu

            if (signedIn) {
                drawerMenu.findItem(R.id.actionLogin).isVisible = false
                drawerMenu.setGroupVisible(R.id.signed_in_menu, true)

                bottomNavigationMenu.findItem(R.id.navigationLoginPage).isVisible = false
                bottomNavigationMenu.findItem(R.id.navigationProfilePage).isVisible = true
            } else {
                drawerMenu.findItem(R.id.actionLogin).isVisible = true
                drawerMenu.setGroupVisible(R.id.signed_in_menu, false)

                bottomNavigationMenu.findItem(R.id.navigationLoginPage).isVisible = true
                bottomNavigationMenu.findItem(R.id.navigationProfilePage).isVisible = false
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
            containerId = R.id.navHostContainer,
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