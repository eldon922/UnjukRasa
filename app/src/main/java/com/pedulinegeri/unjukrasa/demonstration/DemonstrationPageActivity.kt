package com.pedulinegeri.unjukrasa.demonstration

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.ActivityDemonstrationPageBinding

class DemonstrationPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemonstrationPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemonstrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        binding.vpImages.adapter = DemonstrationImageAdapter(listOf(R.drawable.cat_caviar, R.drawable.cat_caviar, R.drawable.cat_caviar, R.drawable.cat_caviar))
        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ ->}.attach()

        binding.rvDiscussion.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = DiscussionListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"))
        }

        binding.nsv.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.fabUpvote.hide()
                binding.fabDownvote.hide()
                binding.fabShare.hide()
                binding.fabParticipate.hide()
            } else if (scrollY < oldScrollY || scrollY <= 0) {
                binding.fabUpvote.show()
                binding.fabDownvote.show()
                binding.fabShare.show()
                binding.fabParticipate.show()
            }
        }

        binding.rvProgress.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = ProgressListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"))
        }

        binding.rvPerson.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = PersonListAdapter(arrayListOf("Inisiator", "Koordinator", "Dukung", "Ikut", "Koordinator", "Koordinator"))
        }
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