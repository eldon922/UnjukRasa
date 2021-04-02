package com.pedulinegeri.unjukrasa.demonstration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.ActivityDemonstrationBinding

class DemonstrationPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemonstrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemonstrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        binding.vpImages.adapter = DemonstrationImagesAdapter(listOf(R.drawable.cat_caviar, R.drawable.cat_caviar, R.drawable.cat_caviar, R.drawable.cat_caviar))
        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ ->}.attach()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Unjuk Rasa"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}