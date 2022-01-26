package com.pedulinegeri.unjukrasa.demonstration.progress

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentAddProgressPageBinding
import com.pedulinegeri.unjukrasa.new_demonstration.NewDemonstrationImageAdapter
import java.util.*
import java.util.regex.Pattern


class AddProgressPageFragment : Fragment() {

    private var _binding: FragmentAddProgressPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var toast: Toast

    private val args: AddProgressPageFragmentArgs by navArgs()

    private val authViewModel: AuthViewModel by activityViewModels()

    private lateinit var imageAdapter: NewDemonstrationImageAdapter

    private var lastClickTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProgressPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

        setupToolbar()
        setupImageVideoUpload()
        setupDescriptionEditor()
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.exit))
                .setMessage(getString(R.string.exit_edit_page_confirmation_message))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requireActivity().findNavController(R.id.navHostContainerMain).navigateUp()
                }
                .setNegativeButton(android.R.string.cancel, null).show()
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            val uri: Uri = data?.data!!

            imageAdapter.addImage(uri)

            binding.vpImages.setCurrentItem(imageAdapter.itemCount - 1, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.toolbar.setOnMenuItemClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnMenuItemClickListener false
            }
            lastClickTime = SystemClock.elapsedRealtime()

            if (binding.reDescription.html == null) {
                toast.setText(getString(R.string.description_input_empty))
                toast.show()
                return@setOnMenuItemClickListener false
            }

            if (it.itemId == R.id.actionAdd) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.add_progress))
                    .setMessage(getString(R.string.add_progress_confirmation_message))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        submit()
                    }
                    .setNegativeButton(android.R.string.cancel, null).show()
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun submit() {
        val db = Firebase.firestore

        val progressData = Progress(binding.reDescription.html)

        if (getYoutubeVideoID().isNotBlank()) {
            progressData.youtube_video = getYoutubeVideoID()
        }

        db.collection("demonstrations").document(args.demonstrationId)
            .update("progress", FieldValue.arrayUnion(progressData))
            .addOnSuccessListener {
                toast.setText(getString(R.string.add_progress_success_message))
                toast.show()

                imageAdapter.imagesUri.forEachIndexed { index, uri ->
                    val imageRef =
                        Firebase.storage.reference.child("progress_image/${args.demonstrationId}/${args.progressSize}/${authViewModel.uid}/$index.png")
                    val uploadTask = imageRef.putFile(uri)

                    uploadTask.addOnFailureListener {
                        toast.setText(
                            getString(
                                R.string.upload_progress_image_failed_message,
                                index + 1,
                                it
                            )
                        )
                        toast.show()
                    }
                }
            }
            .addOnFailureListener {
                toast.setText(getString(R.string.unknown_error_message, it))
                toast.show()
            }

        requireActivity().findNavController(R.id.navHostContainerMain)
            .navigateUp()
    }

    private fun getYoutubeVideoID(): String {
        val pattern =
            "^.*(?:(?:youtu\\.be\\/|v\\/|vi\\/|u\\/\\w\\/|embed\\/)|(?:(?:watch)?\\?v(?:i)?=|\\&v(?:i)?=))([^#\\&\\?]+).*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(binding.etYoutubeVideo.text.toString())
        return if (matcher.find()) {
            matcher.group(1)!!
        } else ""
    }

    private fun setupImageVideoUpload() {
        binding.btnImage.setOnClickListener {
            ImagePicker.with(this).compress(1024)
                .crop().start()
        }

        imageAdapter = NewDemonstrationImageAdapter()
        binding.vpImages.adapter = imageAdapter
        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ -> }.attach()
    }

    private fun setupDescriptionEditor() {
        binding.reDescription.setEditorHeight(200)
        binding.reDescription.setEditorFontSize(16)
        binding.reDescription.setEditorFontColor(binding.etYoutubeVideo.currentTextColor)
        binding.reDescription.setEditorBackgroundColor((binding.etYoutubeVideo.background as MaterialShapeDrawable).fillColor!!.defaultColor)
        binding.reDescription.setPadding(15, 15, 15, 15)
        binding.reDescription.setPlaceholder(getString(R.string.description_input_placeholder))

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