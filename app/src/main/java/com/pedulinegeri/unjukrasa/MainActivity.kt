package com.pedulinegeri.unjukrasa

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.firebase.ui.auth.AuthUI
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.ActivityMainBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationPageActivity
import com.pedulinegeri.unjukrasa.notification.NotificationPageActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        if (savedInstanceState == null) {
            setupBottomNavigation()
        }

        setupNavigationDrawer()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        binding.notificationButton.setOnClickListener {
            val intent = Intent(this, NotificationPageActivity::class.java)
            startActivity(intent)
        }

        val drawerToggle =
            ActionBarDrawerToggle(this, binding.drawer, R.string.open, R.string.close)
        binding.drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupNavigationDrawer() {
        binding.navigationDrawer.setNavigationItemSelectedListener {
            when (it.title) {
                resources.getString(R.string.masuk) -> binding.bottomNavigation.selectedItemId =
                    R.id.action_profile_page
                resources.getString(R.string.keluar) -> {
                    AuthUI.getInstance().signOut(this).addOnSuccessListener {
                        authViewModel.signedOut()
                    }
                }
                resources.getString(R.string.pengaturan) -> false
                resources.getString(R.string.bantuan) -> false
                resources.getString(R.string.tentang) -> false
                else -> false
            }

            binding.drawer.close()

            true
        }

        authViewModel.isSignedIn.observe(this, { signedIn ->
            val drawerMenu = binding.navigationDrawer.menu

            if (signedIn) {
                drawerMenu.findItem(R.id.action_login).isVisible = false
                drawerMenu.findItem(R.id.action_logout).isVisible = true
            } else {
                drawerMenu.findItem(R.id.action_login).isVisible = true
                drawerMenu.findItem(R.id.action_logout).isVisible = false
//                TODO DEV
//                binding.bottomNavigation.selectedItemId = R.id.action_home_page
            }
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navGraphIds = listOf(
            R.navigation.home_page,
            R.navigation.message_page,
            R.navigation.profile_page
        )

        // Setup the bottom navigation view with a list of navigation graphs
        binding.bottomNavigation.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
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

    override fun onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}