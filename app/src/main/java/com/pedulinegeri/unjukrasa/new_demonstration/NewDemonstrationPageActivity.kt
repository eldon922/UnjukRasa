package com.pedulinegeri.unjukrasa.new_demonstration

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.ActivityNewDemonstrationPageBinding


class NewDemonstrationPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewDemonstrationPageBinding
    private val MEDIA_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewDemonstrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        binding.btnImage.setOnClickListener {
            val options: Options = Options.init()
                .setRequestCode(MEDIA_CODE) //Request code for activity results
                .setCount(3) //Number of images to restict selection count
                .setFrontfacing(false) //Front Facing camera on start
                .setSpanCount(4) //Span count for gallery min 1 & max 5
                .setMode(Options.Mode.All) //Option to select only pictures or videos or both
                .setVideoDurationLimitinSeconds(30) //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT) //Orientaion
                .setPath("/pix/images") //Custom Path For media Storage

            Pix.start(this, options)
        }

        binding.reDescription.setEditorHeight(200)
        binding.reDescription.setEditorFontSize(16)
        binding.reDescription.setEditorFontColor(Color.WHITE)
        binding.reDescription.setEditorBackgroundColor(Color.parseColor("#2E2E2E"))
        binding.reDescription.setPadding(15, 15, 15, 15)
        binding.reDescription.setPlaceholder("Deskripsikan suaramu...")

        binding.reDescription.setOnTextChangeListener { text ->
            binding.tvPreview.text = text

            if (text.isNotEmpty()) {
                binding.scrollView.post {
                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
                }
            } else {
                binding.scrollView.post {
                    binding.scrollView.fullScroll(View.FOCUS_UP)
                }
            }
        }

        binding.actionUndo.setOnClickListener {
            binding.reDescription.undo()
        }

        binding.actionRedo.setOnClickListener {
            binding.reDescription.redo()
        }

        binding.actionBold.setOnClickListener {
            binding.reDescription.setBold()
        }

        binding.actionItalic.setOnClickListener {
            binding.reDescription.setItalic()
        }

        binding.reDescription.setOnFocusChangeListener { _, focused ->
            binding.hsvEditor.visibility = if (focused) View.VISIBLE else View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && resultCode == MEDIA_CODE) {
            val returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.new_demonstration_page_items, menu)
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Inisiasi Unjuk Rasa"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}