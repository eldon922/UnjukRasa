package com.pedulinegeri.unjukrasa.demonstration

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
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentEditDemonstrationPageBinding
import com.squareup.picasso.Picasso
import java.util.*


class EditDemonstrationPageFragment : Fragment() {

    private var _binding: FragmentEditDemonstrationPageBinding? = null
    private val binding get() = _binding!!

    private val args: EditDemonstrationPageFragmentArgs by navArgs()

    private lateinit var toast: Toast

    private val authViewModel: AuthViewModel by activityViewModels()

    private val POLICE_PERMIT_MEDIA_PICKER_CODE = 2

    private val datePicker by lazy {
        SingleDateAndTimePickerDialog.Builder(requireContext())
            .bottomSheet()
            .curved()
            .listener {
                binding.etTime.setText(it.toString())
                chosenDate = it
            }
    }

    private lateinit var chosenDate: Date
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private var lastClickTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDemonstrationPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

        setupToolbar()
        setupRoadProtests()
        setupDescriptionEditor()

        val db = Firebase.firestore
        val docRef = db.collection("demonstrations").document(args.id)
        docRef.get().addOnSuccessListener {
            val demonstration = it?.toObject<Demonstration>()!!

            binding.etTitle.setText(demonstration.title)
            binding.etTo.setText(demonstration.to)
            binding.cbRoadProtests.isChecked = demonstration.road_protests
            binding.etLocation.setText(demonstration.location)
            binding.etTime.setText(demonstration.datetime.toString())
            binding.reDescription.html = demonstration.description

            chosenDate = demonstration.datetime
        }.addOnFailureListener {
            toast.setText(getString(R.string.unknown_error_message, it))
            toast.show()
        }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.exit)
                .setMessage(getString(R.string.exit_new_page_confirmation_message))
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
                POLICE_PERMIT_MEDIA_PICKER_CODE -> {
                    binding.etPolicePermit.setText(uri.toString())
                }
            }
        }
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
            datePicker.display()
        }

        binding.etLocation.setOnClickListener {
            autocompleteFragment.view?.findViewById<View>(R.id.places_autocomplete_search_input)
                ?.performClick()
        }

        setupPlacePicker()

        binding.btnUploadPolicePermit.setOnClickListener {
            ImagePicker.with(this).compress(1024).crop().start(POLICE_PERMIT_MEDIA_PICKER_CODE)
        }

        val imageRef =
            Firebase.storage.reference.child("/police_permit_image/${args.id}")

        imageRef.listAll().addOnSuccessListener {
            if (it.items.size > 0) {
                it.items[0].downloadUrl.addOnSuccessListener {
                    binding.etPolicePermit.setText(it.toString())
                    Picasso.get().load(it).into(binding.ivPolicePermit)
                }
            }
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

            if (it.itemId == R.id.actionEdit) {
                binding.etTitle.error = null
                binding.etTo.error = null
                binding.etTime.error = null
                binding.etLocation.error = null

                if (binding.etTitle.text.isBlank()) {
                    binding.etTitle.error = getString(R.string.title_input_empty)
                    binding.etTitle.requestFocus()
                    return@setOnMenuItemClickListener false
                } else if (binding.etTo.text.isBlank()) {
                    binding.etTo.error = getString(R.string.destination_input_empty)
                    binding.etTo.requestFocus()
                    return@setOnMenuItemClickListener false
                } else if (binding.cbRoadProtests.isChecked) {
                    if (binding.etTime.text.isBlank()) {
                        binding.etTime.error = getString(R.string.time_input_empty)
                        binding.etTime.requestFocus()
                        return@setOnMenuItemClickListener false
                    } else if (binding.etLocation.text.isBlank()) {
                        binding.etLocation.error = getString(R.string.location_input_empty)
                        binding.etLocation.requestFocus()
                        return@setOnMenuItemClickListener false
                    }
                } else if (binding.reDescription.html == null) {
                    toast.setText(getString(R.string.description_input_empty))
                    toast.show()
                    return@setOnMenuItemClickListener false
                }

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.edit_demonstration))
                    .setMessage(
                        getString(R.string.edit_demonstration_confirmation_message)
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

    private fun submit() {
        val db = Firebase.firestore

        val demonstrationData = hashMapOf(
            "title" to binding.etTitle.text.toString(),
            "to" to binding.etTo.text.toString(),
            "description" to binding.reDescription.html,
            "road_protests" to binding.cbRoadProtests.isChecked,
            "datetime" to chosenDate,
            "location" to binding.etLocation.text.toString()
        )

        db.collection("demonstrations").document(args.id)
            .update(demonstrationData as Map<String, Any>)
            .addOnSuccessListener {
                toast.setText(getString(R.string.edit_demonstration_success_message))
                toast.show()

                if (binding.cbRoadProtests.isChecked && binding.etPolicePermit.text.isNotBlank()) {
                    val imageRef =
                        Firebase.storage.reference.child("/police_permit_image/${args.id}/${authViewModel.uid}.png")
                    val uploadTask =
                        imageRef.putFile(binding.etPolicePermit.text.toString().toUri())

                    uploadTask.addOnFailureListener {
                        toast.setText(
                            getString(
                                R.string.upload_police_permit_image_failed_message,
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
                    toast.setText(getString(R.string.unknown_error_message, status))
                    toast.show()
                }
            }
        })
    }

    private fun setupDescriptionEditor() {
        binding.reDescription.setEditorHeight(200)
        binding.reDescription.setEditorFontSize(16)
        binding.reDescription.setEditorFontColor(binding.etTitle.currentTextColor)
        binding.reDescription.setEditorBackgroundColor((binding.etTitle.background as MaterialShapeDrawable).fillColor!!.defaultColor)
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