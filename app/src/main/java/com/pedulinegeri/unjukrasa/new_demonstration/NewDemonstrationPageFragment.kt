package com.pedulinegeri.unjukrasa.new_demonstration

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentNewDemonstrationPageBinding
import com.squareup.picasso.Picasso
import java.util.regex.Pattern


class NewDemonstrationPageFragment : Fragment() {

    private var _binding: FragmentNewDemonstrationPageBinding? = null
    private val binding get() = _binding!!

    private val args: NewDemonstrationPageFragmentArgs by navArgs()

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private lateinit var toast: Toast

    private val DEMONSTRATION_MEDIA_PICKER_CODE = 1
    private val POLICE_PERMIT_MEDIA_PICKER_CODE = 2

    private lateinit var datePicker: SingleDateAndTimePickerDialog.Builder
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private lateinit var imageAdapter: NewDemonstrationImageAdapter

    private var lastClickTime = 0L

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

        setupToolbar()
        setupImageVideoUpload()
        setupRoadProtests()
        setupDescriptionEditor()

        if (args.editMode) {
            binding.toolbar.title = "Ubah Unjuk Rasa"
            binding.toolbar.menu.findItem(R.id.action_start).title = "Ubah"
        }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback {
            AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Apakah kamu yakin ingin keluar? ".plus(if (args.editMode) "Perubahan yang telah dilakukan akan hilang" else "Draft akan disimpan dan bisa diakses kembali di lain waktu"))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requireActivity().findNavController(R.id.nav_host_container_main).navigateUp()
                }
                .setNegativeButton(android.R.string.cancel, null).show()
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        onBackPressedCallback.remove()

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
                    binding.etPolicePermit.setText(uri.toString())
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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
            ImagePicker.with(this).start(POLICE_PERMIT_MEDIA_PICKER_CODE)
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

            if (it.itemId == R.id.action_start) {
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
                } else if (binding.cbRoadProtests.isChecked) {
                    if (binding.etTime.text.isBlank()) {
                        binding.etTime.error = "Waktu wajib diisi!"
                        return@setOnMenuItemClickListener false
                    } else if (binding.etLocation.text.isBlank()) {
                        binding.etLocation.error = "Lokasi wajib diisi!"
                        return@setOnMenuItemClickListener false
                    }
                } else if (binding.reDescription.html == null) {
                    toast.setText("Deskripsi wajib diisi.")
                    toast.show()
                    return@setOnMenuItemClickListener false
                }

                AlertDialog.Builder(requireContext())
                    .setTitle(if (args.editMode) "Ubah Unjuk Rasa" else "Mulai Unjuk Rasa")
                    .setMessage(
                        "Apakah kamu yakin ingin ".plus(if (args.editMode) "mengubah" else "memulai")
                            .plus(" unjuk rasa ini? Tekan cancel untuk mengubah data kembali")
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
        val pattern = "^.*(?:(?:youtu\\.be\\/|v\\/|vi\\/|u\\/\\w\\/|embed\\/)|(?:(?:watch)?\\?v(?:i)?=|\\&v(?:i)?=))([^#\\&\\?]+).*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(binding.etYoutubeVideo.text.toString())
        return if (matcher.find()) {
            matcher.group(1)!!
        } else ""
    }

    private fun submit() {
        val db = Firebase.firestore

        val demonstrationData = hashMapOf(
            "title" to binding.etTitle.text.toString(),
            "to" to binding.etTo.text.toString(),
            "description" to binding.reDescription.html,
            "youtube_video" to getYoutubeVideoID(),
            "images" to listOf(1, 2, 3),
            "road_protests" to binding.cbRoadProtests.isChecked,
            "datetime" to binding.etTime.text.toString(),
            "location" to binding.etLocation.text.toString(),
            "uidFrom" to Firebase.auth.currentUser!!.uid
        )

        db.collection("demonstrations").add(demonstrationData)
            .addOnSuccessListener {
                toast.setText("Unjuk rasa berhasil dibuat. Terima kasih.")
                toast.show()

                imageAdapter.imagesUri.forEachIndexed { index, uri ->
                    val imageRef =
                        Firebase.storage.reference.child("demonstration_image/${Firebase.auth.currentUser!!.uid}/${it.id}/$index.png")
                    val uploadTask = imageRef.putFile(uri)

                    uploadTask.addOnFailureListener {
                        toast.setText("Unggah gambar ke-${index+1} gagal. Silahkan coba lagi dengan mengubah unjuk rasa yang sudah dibuat. ($it)")
                        toast.show()
                    }
                }
            }
            .addOnFailureListener {
                toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                toast.show()
            }

        requireActivity().findNavController(R.id.nav_host_container_main)
            .navigateUp()
    }

    private fun setupImageVideoUpload() {
        binding.btnImage.setOnClickListener {
            ImagePicker.with(this).start(DEMONSTRATION_MEDIA_PICKER_CODE)
        }

        imageAdapter = NewDemonstrationImageAdapter()
        binding.vpImages.adapter = imageAdapter
        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ -> }.attach()

        binding.etYoutubeVideo.addTextChangedListener {

        }
    }

    private fun setupPlacePicker() {
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment

        // Initialize the SDK
        Places.initialize(requireContext(), R.string.google_api_key.toString())

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(requireContext())

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {


                binding.etLocation.setText(place.name)
            }

            override fun onError(status: Status) {
                toast.setText("An error occurred: $status")
                toast.show()
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