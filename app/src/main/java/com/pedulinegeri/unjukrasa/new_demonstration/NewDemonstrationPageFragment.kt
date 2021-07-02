package com.pedulinegeri.unjukrasa.new_demonstration

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.tabs.TabLayoutMediator
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentNewDemonstrationPageBinding


class NewDemonstrationPageFragment : Fragment() {

    private var fragmentBinding: FragmentNewDemonstrationPageBinding? = null
    private val DEMONSTRATION_MEDIA_PICKER_CODE = 1
    private val POLICE_PERMIT_MEDIA_PICKER_CODE = 2

    private lateinit var imageAdapter: NewDemonstrationImageAdapter
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private lateinit var datePicker: SingleDateAndTimePickerDialog.Builder
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private val args: NewDemonstrationPageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentNewDemonstrationPageBinding.inflate(inflater, container, false)
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
                .setRequestCode(DEMONSTRATION_MEDIA_PICKER_CODE) //Request code for activity results
                .setCount(1) //Number of images to restict selection count
                .setFrontfacing(false) //Front Facing camera on start
                .setMode(Options.Mode.Picture) //Option to select only pictures or videos or both
                .setScreenOrientation(Options.SCREEN_ORIENTATION_SENSOR) //Orientaion

            Pix.start(this, options)
        }

        setupDescriptionEditor()

        imageAdapter = NewDemonstrationImageAdapter(childFragmentManager)
        binding.vpImages.adapter = imageAdapter
        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ -> }.attach()
        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {
                    binding.intoTabLayout.visibility = View.GONE
                } else {
                    binding.intoTabLayout.visibility = View.VISIBLE
                }
            }
        })

        imageAdapter.onVideoLoaded = {
            binding.vpImages.setBackgroundResource(0)
        }

        imageAdapter.onVideoRemoved = {
            binding.etYoutubeVideo.text.clear()
            binding.vpImages.setBackgroundResource(R.drawable.video_placeholder)
        }

        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_start) {
                AlertDialog.Builder(requireContext())
                    .setTitle(if (args.editMode) "Ubah Unjuk Rasa" else "Mulai Unjuk Rasa")
                    .setMessage(
                        "Apakah kamu yakin ingin ".plus(if (args.editMode) "mengubah" else "memulai")
                            .plus(" unjuk rasa ini? Tekan cancel untuk mengubah data kembali")
                    )
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        requireActivity().findNavController(R.id.nav_host_container_main)
                            .navigateUp()
                    }
                    .setNegativeButton(android.R.string.cancel, null).show()
            }

            return@setOnMenuItemClickListener true
        }

        binding.etYoutubeVideo.addTextChangedListener {
            binding.vpImages.setCurrentItem(0, false)
            imageAdapter.changeYoutubeVideo(binding.etYoutubeVideo.text.takeLast(11).toString())
        }

        binding.cbDemonstration.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                binding.groupDemonstration.visibility = View.VISIBLE
            } else {
                binding.groupDemonstration.visibility = View.GONE
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
            val options: Options = Options.init()
                .setRequestCode(POLICE_PERMIT_MEDIA_PICKER_CODE) //Request code for activity results
                .setCount(1) //Number of images to restict selection count
                .setFrontfacing(false) //Front Facing camera on start
                .setMode(Options.Mode.Picture) //Option to select only pictures or videos or both
                .setScreenOrientation(Options.SCREEN_ORIENTATION_SENSOR) //Orientaion

            Pix.start(this, options)
        }

        if (args.editMode) {
            binding.toolbar.title = "Ubah Unjuk Rasa"
            binding.toolbar.menu.findItem(R.id.action_start).title = "Ubah"
        }
    }

    override fun onResume() {
        super.onResume()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

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
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        onBackPressedCallback.remove()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DEMONSTRATION_MEDIA_PICKER_CODE -> {
                if (resultCode == Pix.RESULT_OK) {
                    val returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                    imageAdapter.addImage(returnValue!![0])

                    val binding = fragmentBinding!!
                    binding.vpImages.setCurrentItem(imageAdapter.itemCount - 1, true)
                }
            }
            POLICE_PERMIT_MEDIA_PICKER_CODE -> {
                if (resultCode == Pix.RESULT_OK) {
                    val returnValue = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                    val binding = fragmentBinding!!
                    binding.etPolicePermit.setText(returnValue!![0])
                }
            }
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
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
                val binding = fragmentBinding!!

                binding.etLocation.setText(place.name)
            }

            override fun onError(status: Status) {
                Toast.makeText(requireContext(), "An error occurred: $status", Toast.LENGTH_SHORT)
                    .show()
            }
        })
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