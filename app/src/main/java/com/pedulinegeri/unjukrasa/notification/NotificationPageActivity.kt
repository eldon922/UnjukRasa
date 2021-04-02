package com.pedulinegeri.unjukrasa.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pedulinegeri.unjukrasa.databinding.ActivityNotificationPageBinding

class NotificationPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvNotification.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = NotificationListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"))
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notifikasi"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}