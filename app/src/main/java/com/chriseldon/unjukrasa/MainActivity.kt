package com.chriseldon.unjukrasa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.chriseldon.unjukrasa.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        if (savedInstanceState == null) {
            setupNavigation()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        val drawerToggle = ActionBarDrawerToggle(this, binding.drawer, R.string.open, R.string.close)
        binding.drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupNavigation()
    }

    private fun setupNavigation() {
        val navGraphIds = listOf(
            R.navigation.home_page,
            R.navigation.topic_page,
            R.navigation.message_page,
            R.navigation.profile_page
        )

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.navigation.home_page,
                R.navigation.topic_page,
                R.navigation.message_page,
                R.navigation.profile_page
            )
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
                binding.drawer.openDrawer(GravityCompat.START)
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

}