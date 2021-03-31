package com.pedulinegeri.unjukrasa.demonstration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pedulinegeri.unjukrasa.databinding.ActivityDemonstrationBinding

class DemonstrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemonstrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemonstrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
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