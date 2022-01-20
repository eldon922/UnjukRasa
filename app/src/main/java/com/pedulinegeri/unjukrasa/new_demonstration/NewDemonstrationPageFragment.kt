package com.pedulinegeri.unjukrasa.new_demonstration

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentNewDemonstrationPageBinding
import java.util.*
import java.util.regex.Pattern


class NewDemonstrationPageFragment : Fragment() {

    private var _binding: FragmentNewDemonstrationPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var toast: Toast

    private val authViewModel: AuthViewModel by activityViewModels()

    private val DEMONSTRATION_MEDIA_PICKER_CODE = 1
    private val POLICE_PERMIT_MEDIA_PICKER_CODE = 2

    private lateinit var datePicker: SingleDateAndTimePickerDialog.Builder
    private lateinit var chosenDate: Date

    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private lateinit var imageAdapter: NewDemonstrationImageAdapter

    private var lastClickTime = 0L

    private lateinit var imagePicker: ImagePicker.Builder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewDemonstrationPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

        imagePicker = ImagePicker.with(this)
            .setImageProviderInterceptor { binding.groupLoading.visibility = View.VISIBLE }
            .compress(1024).crop()

        setupToolbar()
        setupImageVideoUpload()
        setupRoadProtests()
        setupDescriptionEditor()
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Apakah kamu yakin ingin keluar? Data yang telah dimasukkan akan hilang.")
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

        val v = requireActivity().currentFocus
        if (v != null) {
            v.clearFocus()
            val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            val uri: Uri = data?.data!!

            when (requestCode) {
                DEMONSTRATION_MEDIA_PICKER_CODE -> {
                    imageAdapter.addImage(uri)
                    binding.vpImages.setCurrentItem(imageAdapter.itemCount - 1, true)
                }
                POLICE_PERMIT_MEDIA_PICKER_CODE -> {
                    binding.ivPolicePermit.setImageURI(uri)
                    binding.etPolicePermit.setText(uri.toString())
                }
            }
        }
        binding.groupLoading.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRoadProtests() {
        binding.cbRoadProtests.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                binding.groupRoadProtests.visibility = View.VISIBLE
            } else {
                binding.groupRoadProtests.visibility = View.GONE
            }
        }

        binding.etTime.setOnClickListener {
            if (!this::datePicker.isInitialized) {
                datePicker = SingleDateAndTimePickerDialog.Builder(requireContext())
                    .bottomSheet()
                    .curved()
                    .listener {
                        binding.etTime.setText(it.toString())
                        chosenDate = it
                    }
            }

            datePicker.display()
        }

        binding.etLocation.setOnClickListener {
            autocompleteFragment.view?.findViewById<View>(R.id.places_autocomplete_search_input)
                ?.performClick()
        }

        setupPlacePicker()

        binding.btnUploadPolicePermit.setOnClickListener {
            imagePicker.provider(ImageProvider.BOTH).start(POLICE_PERMIT_MEDIA_PICKER_CODE)
        }
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

            if (it.itemId == R.id.actionStart) {
                binding.etTitle.error = null
                binding.etTo.error = null
                binding.etTime.error = null
                binding.etLocation.error = null

                if (imageAdapter.itemCount == 0 && binding.etYoutubeVideo.text.isBlank()) {
                    toast.setText("Sertakan minimal 1 gambar atau video youtube.")
                    toast.show()
                    return@setOnMenuItemClickListener false
                } else if (binding.etYoutubeVideo.text.isNotBlank() && getYoutubeVideoID().length != 11) {
                    toast.setText("Tautan video youtube tidak valid.")
                    toast.show()
                    return@setOnMenuItemClickListener false
                } else if (binding.etTitle.text.isBlank()) {
                    binding.etTitle.error = "Judul wajib diisi!"
                    return@setOnMenuItemClickListener false
                } else if (binding.etTo.text.isBlank()) {
                    binding.etTo.error = "Tujuan wajib diisi!"
                    return@setOnMenuItemClickListener false
                } else if (binding.reDescription.html == null) {
                    toast.setText("Deskripsi wajib diisi.")
                    toast.show()
                    return@setOnMenuItemClickListener false
                } else if (binding.cbRoadProtests.isChecked) {
                    if (binding.etTime.text.isBlank()) {
                        binding.etTime.error = "Waktu wajib diisi!"
                        return@setOnMenuItemClickListener false
                    } else if (binding.etLocation.text.isBlank()) {
                        binding.etLocation.error = "Lokasi wajib diisi!"
                        return@setOnMenuItemClickListener false
                    }
                }

                AlertDialog.Builder(requireContext())
                    .setTitle("Mulai Unjuk Rasa")
                    .setMessage(
                        "Apakah kamu yakin ingin memulai unjuk rasa ini? Tekan cancel untuk mengubah data kembali"
                    )
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        submit()
                    }
                    .setNegativeButton(android.R.string.cancel, null).show()
            }

            return@setOnMenuItemClickListener true
        }
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

    private fun submit() {
        val db = Firebase.firestore

        val demonstrationData = DemonstrationData(
            authViewModel.uid,
            binding.etTitle.text.toString(),
            binding.etTo.text.toString(),
            binding.reDescription.html
        )

        if (getYoutubeVideoID().isNotBlank()) {
            demonstrationData.youtube_video = getYoutubeVideoID()
        }

        if (binding.cbRoadProtests.isChecked) {
            demonstrationData.road_protests = binding.cbRoadProtests.isChecked
            demonstrationData.datetime = chosenDate
            demonstrationData.location = binding.etLocation.text.toString()
        }

        db.collection("demonstrations").add(demonstrationData)
            .addOnSuccessListener {
                toast.setText("Unjuk rasa berhasil dibuat. Terima kasih.")
                toast.show()

                imageAdapter.imagesUri.forEachIndexed { index, uri ->
                    val imageRef =
                        Firebase.storage.reference.child("demonstration_image/${it.id}/${authViewModel.uid}/$index.png")
                    val uploadTask = imageRef.putFile(uri)

                    uploadTask.addOnFailureListener {
                        toast.setText("Unggah gambar ke-${index + 1} gagal. Silahkan coba lagi dengan mengubah unjuk rasa yang sudah dibuat. ($it)")
                        toast.show()
                    }
                }

                if (binding.cbRoadProtests.isChecked) {
                    val imageRef =
                        Firebase.storage.reference.child("/police_permit_image/${it.id}/${authViewModel.uid}.png")
                    val uploadTask =
                        imageRef.putFile(binding.etPolicePermit.text.toString().toUri())

                    uploadTask.addOnFailureListener {
                        toast.setText("Unggah foto ijin kepolisian gagal. Silahkan coba lagi dengan mengubah unjuk rasa yang sudah dibuat. ($it)")
                        toast.show()
                    }
                }
            }
            .addOnFailureListener {
                toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                toast.show()
            }

        requireActivity().findNavController(R.id.navHostContainerMain)
            .navigateUp()
    }

    private fun setupImageVideoUpload() {
        binding.btnImage.setOnClickListener {
            imagePicker.provider(ImageProvider.BOTH).start(DEMONSTRATION_MEDIA_PICKER_CODE)
        }

        imageAdapter = NewDemonstrationImageAdapter()
        binding.vpImages.adapter = imageAdapter
        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ -> }.attach()
    }

    private fun setupPlacePicker() {
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocompleteFragment)
                as AutocompleteSupportFragment
        Places.initialize(requireContext(), getString(R.string.google_api_key))
        Places.createClient(requireContext())

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                binding.etLocation.setText(place.name)

            }

            override fun onError(status: Status) {
                if (!status.isCanceled) {
                    toast.setText("An error occurred: $status")
                    toast.show()
                }
            }
        })
    }

    private fun setupDescriptionEditor() {
        binding.reDescription.setEditorHeight(200)
        binding.reDescription.setEditorFontSize(16)
        binding.reDescription.setEditorFontColor(binding.etYoutubeVideo.currentTextColor)
        binding.reDescription.setEditorBackgroundColor((binding.etYoutubeVideo.background as MaterialShapeDrawable).fillColor!!.defaultColor)
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