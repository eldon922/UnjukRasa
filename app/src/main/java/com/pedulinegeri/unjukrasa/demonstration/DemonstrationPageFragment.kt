package com.pedulinegeri.unjukrasa.demonstration

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.Tasks
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentDemonstrationPageBinding
import com.pedulinegeri.unjukrasa.demonstration.person.PersonListAdapter
import com.pedulinegeri.unjukrasa.demonstration.person.PersonListBottomSheetDialog
import com.pedulinegeri.unjukrasa.demonstration.progress.ProgressListAdapter
import com.pedulinegeri.unjukrasa.profile.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

@AndroidEntryPoint
class DemonstrationPageFragment : Fragment() {

    private var _binding: FragmentDemonstrationPageBinding? = null
    private val binding get() = _binding!!

    private val args: DemonstrationPageFragmentArgs by navArgs()

    private lateinit var toast: Toast

    private val authViewModel: AuthViewModel by activityViewModels()
    private val demonstrationPageViewModel: DemonstrationPageViewModel by viewModels()

    private var editMode = false
    private var hasAction = true
    private var showParticipate = false

    private lateinit var personListAdapter: PersonListAdapter
    private lateinit var demonstrationImageAdapter: DemonstrationImageAdapter
    private lateinit var progressListAdapter: ProgressListAdapter

    private var progressInitialized = false

    private var lastClickTime = 0L

    private lateinit var demonstration: Demonstration

    private lateinit var userSnapshotListener: ListenerRegistration

    private lateinit var chipDefaultColor: ColorStateList

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDemonstrationPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chipDefaultColor = binding.chipUpvote.chipBackgroundColor!!

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

        demonstrationPageViewModel.getDemonstration(args.id).observe(viewLifecycleOwner) {
            if (it.data != null) {
                demonstration = it.data
                binding.tvTitle.text = demonstration.title
                binding.tvTo.text = getString(R.string.demonstration_destination, demonstration.to)

                showParticipate = demonstration.roadProtest

                setupFab()
                setupEditMode()
                authViewModel.isSignedIn.observe(viewLifecycleOwner) {
                    if (it) {
                        updateUserData()
                    } else {
                        setupFabSignedOut()
                    }
                }
                setupImages()
                setupChips()
                setupPerson()
                binding.tvDescription.text = Html.fromHtml(demonstration.description)
                setupProgress()

                binding.nsv.post {
                    binding.nsv.scrollY = demonstrationPageViewModel.nsvScrollPosition
                }
            } else {
                toast.setText(getString(R.string.unknown_error_message))
                toast.show()
            }
        }

        setupToolbar()
    }

    private fun setupFabSignedOut() {
        val onClickListenerToSignIn = View.OnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@OnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToNavigationLoginPage()
            )
        }

        binding.fabParticipate.setOnClickListener(onClickListenerToSignIn)
        binding.fabUpvote.setOnClickListener(onClickListenerToSignIn)
        binding.fabDownvote.setOnClickListener(onClickListenerToSignIn)
    }

    private fun setupPerson() {
        personListAdapter = PersonListAdapter(findNavController())

        binding.rvPerson.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = personListAdapter
        }

        personListAdapter.initPersonList(
            demonstration.persons
        )
    }

    private fun setupImages() {
        demonstrationImageAdapter = DemonstrationImageAdapter(findNavController())
        binding.vpImages.adapter = demonstrationImageAdapter

        if (demonstration.youtubeVideo.isNotBlank()) {
            demonstrationImageAdapter.addImageOrVideo(demonstration.youtubeVideo)
        }

        val imageRef =
            Firebase.storage.reference.child("demonstration_image/${demonstration.id}/${demonstration.initiatorUid}")

        imageRef.listAll().addOnSuccessListener {
            lifecycleScope.launch(Dispatchers.IO) {
                it.items.forEach {
                    val downloadUri = Tasks.await(it.downloadUrl)
                    withContext(Dispatchers.Main) {
                        demonstrationImageAdapter.addImageOrVideo(downloadUri.toString())
                        binding.vpImages.currentItem =
                            demonstrationPageViewModel.vpImagesCurrentItem
                    }
                }
            }

            if (it.items.size > 1 && demonstration.youtubeVideo.isBlank()) {
                binding.intoTabLayout.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
            toast.setText(getString(R.string.image_load_failed, it))
            toast.show()
        }

        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ -> }.attach()
        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (demonstrationImageAdapter.itemCount > 1 || (demonstrationImageAdapter.itemCount > 0 && demonstration.youtubeVideo.isNotBlank())) {
                    if (position == 0 && demonstration.youtubeVideo.isNotBlank()) {
                        binding.intoTabLayout.visibility = View.GONE
                    } else {
                        binding.intoTabLayout.visibility = View.VISIBLE
                    }
                }
            }
        })
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

            when (it.itemId) {
                R.id.actionEdit -> {
                    findNavController().navigate(
                        DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToEditDemonstrationPageFragment(
                            demonstration.id
                        )
                    )
                }
                R.id.actionCancelParticipate -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.cancel_participate))
                        .setMessage(
                            getString(R.string.cancel_participate_confirmation_message)
                        )
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            toast.setText(getString(R.string.cancel_participate_success_message))
                            toast.show()
                            binding.chipParticipate.text = getString(
                                R.string.participate_count,
                                binding.chipParticipate.text.split(" ")[0].toLong() - 1
                            )
                            binding.chipParticipate.chipBackgroundColor = chipDefaultColor

                            cancelDemonstrationAction("participate")
                        }
                        .setNegativeButton(android.R.string.cancel, null).show()
                }
                R.id.actionCancelUpvote -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.cancel_upvote))
                        .setMessage(getString(R.string.cancel_upvote_confirmation_message))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            toast.setText(getString(R.string.cancel_upvote_success_message))
                            toast.show()
                            binding.chipUpvote.text = getString(
                                R.string.upvote_count,
                                binding.chipUpvote.text.split(" ")[0].toLong() - 1
                            )
                            binding.chipUpvote.chipBackgroundColor = chipDefaultColor

                            cancelDemonstrationAction("upvote")
                        }
                        .setNegativeButton(android.R.string.cancel, null).show()
                }
                R.id.actionCancelDownvote -> {
                    toast.setText(getString(R.string.cancel_downvote_success_message))
                    toast.show()
                    binding.chipDownvote.text = getString(
                        R.string.downvote_count,
                        binding.chipDownvote.text.split(" ")[0].toLong() - 1
                    )
                    binding.chipDownvote.chipBackgroundColor = chipDefaultColor

                    cancelDemonstrationAction("downvote")
                }
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun cancelDemonstrationAction(action: String) {
        val data = hashMapOf(
            "action" to action,
            "demonstrationId" to demonstration.id
        )

        Firebase.functions("asia-southeast2")
            .getHttpsCallable("cancelDemonstrationAction").call(data)
            .addOnSuccessListener {
                if (!((it.data as HashMap<String, Any>)["success"] as Boolean)) {
                    toast.setText(getString(R.string.cancel_action_not_exist))
                    toast.show()
                }
            }.addOnFailureListener {
                toast.setText(getString(R.string.unknown_error_message, it))
                toast.show()
            }
    }

    private fun setupEditMode() {
        editMode = demonstration.initiatorUid == authViewModel.uid

        if (editMode) {
            binding.fabUpvote.hide()
            binding.fabDownvote.hide()
            binding.fabParticipate.hide()
            binding.toolbar.menu.setGroupVisible(R.id.editMode, true)
            binding.cvAddProgress.visibility = View.VISIBLE
            binding.toolbar.menu.setGroupVisible(R.id.viewMode, false)
        } else {
            if (hasAction) {
                binding.fabUpvote.show()
                binding.fabDownvote.show()
            } else {
                binding.fabUpvote.hide()
                binding.fabDownvote.hide()
            }

            if (showParticipate) {
                binding.fabParticipate.show()
            } else {
                binding.fabParticipate.hide()
            }
        }
        binding.fabShare.show()
    }

    private fun updateUserData() {
        val db = Firebase.firestore
        val docRef = db.collection("users").document(authViewModel.uid)
        userSnapshotListener = docRef.addSnapshotListener { document, e ->
            val user = document?.toObject<User>()!!
            hasAction = !(demonstration.id in user.upvote || demonstration.id in user.downvote)
            showParticipate =
                demonstration.id !in user.participate && demonstration.roadProtest && demonstration.id !in user.downvote

            binding.toolbar.menu.findItem(R.id.actionCancelParticipate).isVisible = false
            binding.toolbar.menu.findItem(R.id.actionCancelUpvote).isVisible = false
            binding.toolbar.menu.findItem(R.id.actionCancelDownvote).isVisible = false
            if (demonstration.id in user.participate && demonstration.roadProtest) {
                binding.toolbar.menu.findItem(R.id.actionCancelParticipate).isVisible = true
                binding.chipParticipate.setChipBackgroundColorResource(R.color.green)
            }
            if (demonstration.id in user.upvote) {
                if (demonstration.id !in user.participate) {
                    binding.toolbar.menu.findItem(R.id.actionCancelUpvote).isVisible = true
                }
                binding.chipUpvote.setChipBackgroundColorResource(R.color.light_green)
            }
            if (demonstration.id in user.downvote) {
                binding.toolbar.menu.findItem(R.id.actionCancelDownvote).isVisible = true
                binding.chipDownvote.setChipBackgroundColorResource(R.color.light_red)
            }

            setupEditMode()
        }
    }

    override fun onResume() {
        super.onResume()

        if (this::userSnapshotListener.isInitialized) {
            updateUserData()
        }
    }

    override fun onPause() {
        super.onPause()

        if (this::userSnapshotListener.isInitialized) {
            userSnapshotListener.remove()
        }

        demonstrationPageViewModel.nsvScrollPosition = binding.nsv.scrollY
        demonstrationPageViewModel.vpImagesCurrentItem = binding.vpImages.currentItem
    }

    private fun setupProgress() {
        progressInitialized = true

        progressListAdapter = ProgressListAdapter(
            findNavController(),
            demonstration.id,
            demonstration.persons[0].uid,
            demonstration.persons[0].name
        )

        binding.rvProgress.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = progressListAdapter
        }

        progressListAdapter.initProgressList(
            demonstration.progress
        )

        binding.cvAddProgress.setOnClickListener {
            if (demonstration.progress.size < 6) {
                findNavController().navigate(
                    DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToAddProgressPageFragment(
                        demonstration.id,
                        progressListAdapter.itemCount
                    )
                )
            } else {
                toast.setText(getString(R.string.add_progress_limit_message))
                toast.show()
            }
        }

        if (!editMode && progressListAdapter.itemCount == 0) binding.tvProgress.visibility =
            View.GONE
    }

    private fun setupFab() {
        binding.nsv.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.fabUpvote.hide()
                binding.fabDownvote.hide()
                binding.fabShare.hide()
                binding.fabParticipate.hide()
            } else if (scrollY < oldScrollY || scrollY <= 0) {
                if (!editMode) {
                    if (hasAction) {
                        binding.fabUpvote.show()
                        binding.fabDownvote.show()
                    }

                    if (showParticipate) binding.fabParticipate.show()
                }
                binding.fabShare.show()
            }
        })

        binding.fabParticipate.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            binding.fabParticipate.hide()
            binding.fabUpvote.hide()
            binding.fabDownvote.hide()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipateBottomSheetDialog(
                    SimpleDateFormat("dd MMMM yyyy").format(demonstration.datetime),
                    SimpleDateFormat("hh:mm aa").format(demonstration.datetime),
                    demonstration.location,
                    demonstration.id
                )
            )

            binding.chipParticipate.text = getString(
                R.string.participate_count,
                binding.chipParticipate.text.split(" ")[0].toLong() + 1
            )
            if (hasAction) binding.chipUpvote.text =
                getString(R.string.upvote_count, binding.chipUpvote.text.split(" ")[0].toLong() + 1)
            binding.chipParticipate.setChipBackgroundColorResource(R.color.green)

            hasAction = false
            showParticipate = false

            val data = hashMapOf(
                "action" to "participate",
                "demonstrationId" to demonstration.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
                .addOnSuccessListener {
                    if (!((it.data as HashMap<String, Any>)["success"] as Boolean)) {
                        toast.setText(getString(R.string.action_already_done_message))
                        toast.show()
                    }
                }.addOnFailureListener {
                    toast.setText(getString(R.string.unknown_error_message, it))
                    toast.show()
                }
        }

        binding.fabShare.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "unjukrasa.com/${demonstration.id}")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

            val data = hashMapOf(
                "action" to "share",
                "demonstrationId" to demonstration.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
        }

        binding.fabUpvote.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            binding.fabUpvote.hide()
            binding.fabDownvote.hide()

            toast.setText(getString(R.string.upvote_success_message))
            toast.show()

            binding.chipUpvote.text =
                getString(R.string.upvote_count, binding.chipUpvote.text.split(" ")[0].toLong() + 1)
            binding.chipUpvote.setChipBackgroundColorResource(R.color.light_green)

            hasAction = false

            val data = hashMapOf(
                "action" to "upvote",
                "demonstrationId" to demonstration.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
                .addOnSuccessListener {
                    if (!((it.data as HashMap<String, Any>)["success"] as Boolean)) {
                        toast.setText(getString(R.string.action_already_done_message))
                        toast.show()
                    }
                }.addOnFailureListener {
                    toast.setText(getString(R.string.unknown_error_message, it))
                    toast.show()
                }
        }

        binding.fabDownvote.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            binding.fabDownvote.hide()
            binding.fabUpvote.hide()
            binding.fabParticipate.hide()

            toast.setText(getString(R.string.downvote_success_message))
            toast.show()

            binding.chipDownvote.text = getString(
                R.string.downvote_count,
                binding.chipDownvote.text.split(" ")[0].toLong() + 1
            )
            binding.chipDownvote.setChipBackgroundColorResource(R.color.light_red)

            hasAction = false
            showParticipate = false

            val data = hashMapOf(
                "action" to "downvote",
                "demonstrationId" to demonstration.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
                .addOnSuccessListener {
                    if (!((it.data as HashMap<String, Any>)["success"] as Boolean)) {
                        toast.setText(getString(R.string.action_already_done_message))
                        toast.show()
                    }
                }.addOnFailureListener {
                    toast.setText(getString(R.string.unknown_error_message, it))
                    toast.show()
                }
        }
    }

    private fun setupChips() {
        if (showParticipate) binding.chipParticipate.visibility = View.VISIBLE

        binding.chipParticipate.text =
            getString(R.string.participate_count, demonstration.participate)
        binding.chipUpvote.text = getString(R.string.upvote_count, demonstration.upvote)
        binding.chipDownvote.text = getString(R.string.downvote_count, demonstration.downvote)
        binding.chipShare.text = getString(R.string.share_count, demonstration.share)

        binding.chipParticipate.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToPersonListBottomSheetDialog(
                    PersonListBottomSheetDialog.TypeList.PARTICIPATE,
                    demonstration.id
                )
            )
        }

        binding.chipUpvote.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToPersonListBottomSheetDialog(
                    PersonListBottomSheetDialog.TypeList.UPVOTE,
                    demonstration.id
                )
            )
        }

        binding.chipDownvote.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToPersonListBottomSheetDialog(
                    PersonListBottomSheetDialog.TypeList.DOWNVOTE,
                    demonstration.id
                )
            )
        }

        binding.chipShare.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToPersonListBottomSheetDialog(
                    PersonListBottomSheetDialog.TypeList.SHARE,
                    demonstration.id
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}