package com.pedulinegeri.unjukrasa.demonstration.progress

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.google.android.material.tabs.TabLayoutMediator
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentAddProgressPageBinding
import com.pedulinegeri.unjukrasa.new_demonstration.NewDemonstrationImageAdapter


class AddProgressPageFragment : Fragment() {

    private var fragmentBinding: FragmentAddProgressPageBinding? = null
    private val MEDIA_CODE = 1

    private lateinit var imageAdapter: NewDemonstrationImageAdapter
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentAddProgressPageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnImage.setOnClickListener {
            val options: Options = Options.init()
                .setRequestCode(MEDIA_CODE) //Request code for activity results
                .setCount(1) //Number of images to restict selection count
                .setFrontfacing(false) //Front Facing camera on start
                .setMode(Options.Mode.Picture) //Option to select only pictures or videos or both
                .setScreenOrientation(Options.SCREEN_ORIENTATION_SENSOR) //Orientaion

            Pix.start(this, options)
        }

        setupDescriptionEditor()

        imageAdapter = NewDemonstrationImageAdapter(childFragmentManager)
        binding.vpImages.adapter = imageAdapter
        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ ->}.attach()
        binding.vpImages.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {
                    binding.intoTabLayout.visibility = View.GONE
                } else {
                    binding.intoTabLayout.visibility = View.VISIBLE
                }
            }
        })

        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_add) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Tambah Perkembangan")
                    .setMessage("Apakah kamu yakin ingin menambahkan perkembangan ini? Tekan cancel untuk mengubah data kembali")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        requireActivity().findNavController(R.id.nav_host_container_main).navigateUp()
                    }
                    .setNegativeButton(android.R.string.cancel, null).show()
            }

            return@setOnMenuItemClickListener true
        }

        binding.etYoutubeVideo.addTextChangedListener {
            binding.vpImages.setCurrentItem(0, false)
            imageAdapter.changeYoutubeVideo(binding.etYoutubeVideo.text.takeLast(11).toString())
        }
    }

    override fun onResume() {
        super.onResume()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback {
            AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Apakah kamu yakin ingin keluar?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requireActivity().findNavController(R.id.nav_host_container_main).navigateUp()
                }
                .setNegativeButton(android.R.string.cancel, null).show()
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        onBackPressedCallback.remove()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == MEDIA_CODE) {
            val returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)

            imageAdapter.addImage(returnValue!![0])
            val binding = fragmentBinding!!
            binding.vpImages.setCurrentItem(imageAdapter.itemCount - 1, true)
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }

    private fun setupDescriptionEditor() {
        val binding = fragmentBinding!!

        binding.reDescription.setEditorHeight(200)
        binding.reDescription.setEditorFontSize(16)
        binding.reDescription.setEditorFontColor(Color.WHITE)
        binding.reDescription.setEditorBackgroundColor(Color.parseColor("#2E2E2E"))
        binding.reDescription.setPadding(15, 15, 15, 15)
        binding.reDescription.setPlaceholder("Deskripsikan suaramu...")

        binding.reDescription.setOnTextChangeListener { text ->
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
}