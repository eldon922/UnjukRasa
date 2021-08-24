package com.pedulinegeri.unjukrasa.demonstration

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentDemonstrationPageBinding
import com.pedulinegeri.unjukrasa.demonstration.discussion.DiscussionListAdapter
import com.pedulinegeri.unjukrasa.demonstration.participation.ParticipationListBottomSheetDialog
import com.pedulinegeri.unjukrasa.demonstration.person.Person
import com.pedulinegeri.unjukrasa.demonstration.person.PersonListAdapter
import com.pedulinegeri.unjukrasa.demonstration.progress.ProgressListAdapter
import com.pedulinegeri.unjukrasa.new_demonstration.Demonstration


class DemonstrationPageFragment : Fragment() {

    private var _binding: FragmentDemonstrationPageBinding? = null
    private val binding get() = _binding!!

    private val args: DemonstrationPageFragmentArgs by navArgs()

    private lateinit var toast: Toast

    private val authViewModel: AuthViewModel by activityViewModels()

    //    TODO dev
    private var editMode = true

    private lateinit var discussionListAdapter: DiscussionListAdapter
    private lateinit var personListAdapter: PersonListAdapter
    private lateinit var demonstrationImageAdapter: DemonstrationImageAdapter
    private lateinit var progressListAdapter: ProgressListAdapter

    private var progressInitialized = false
    private var discussionInitialized = false

    private var lastClickTime = 0L

    private lateinit var demonstration: Demonstration

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

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

        val db = Firebase.firestore
        val docRef = db.collection("demonstrations").document(args.id)
        docRef.addSnapshotListener { snapshot, e ->
            demonstration = snapshot?.toObject<Demonstration>()!!

            editMode = demonstration.persons[0].uid == authViewModel.uid

            binding.tvTitle.text = demonstration.title

            setupFab()
            setupEditMode()
            setupImages()
//            setupChips()
            setupPerson()
            setupDescription()
//            setupProgress()
//            setupDiscussion()
        }


        setupToolbar()
    }

    private fun setupDiscussion() {
        discussionInitialized = true

        discussionListAdapter = DiscussionListAdapter(findNavController())

        binding.rvDiscussion.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = discussionListAdapter
        }

        discussionListAdapter.initDiscussionList(
            arrayListOf(
                "abcde",
                "abcde",
                "abcde",
                "abcde",
                "abcde",
                "abcde"
            )
        )
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

        if (demonstration.youtube_video.isNotBlank()) {
            demonstrationImageAdapter.addImageOrVideo(demonstration.youtube_video)
        }

        val imageRef =
            Firebase.storage.reference.child("demonstration_image/${demonstration.persons[0].uid}/${args.id}")

        imageRef.listAll().addOnSuccessListener {
            it.items.forEach {
                it.downloadUrl.addOnSuccessListener { demonstrationImageAdapter.addImageOrVideo(it.toString())}
            }
        }.addOnFailureListener {
            toast.setText("Gagal memuat gambar. ($it)")
            toast.show()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.toolbar.setOnMenuItemClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnMenuItemClickListener false
            }
            lastClickTime = SystemClock.elapsedRealtime()

            when (it.itemId) {
                R.id.action_edit -> {
                    findNavController().navigate(
                        DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToNewDemonstrationPageFragment(
                            true
                        )
                    )
                }
                R.id.action_add_person -> {
                    findNavController().navigate(R.id.action_demonstrationPageFragment_to_addPersonBottomSheetDialog)
                }
                R.id.action_remove_person -> {
                    findNavController().navigate(R.id.action_demonstrationPageFragment_to_removePersonBottomSheetDialog)
                }
                R.id.action_cancel_participate -> {
                    toast.setText("Anda telah batal mengikuti unjuk rasa ini.")
                    toast.show()
                }
                R.id.action_cancel_upvote -> {
                    toast.setText("Anda telah batal mendukung unjuk rasa ini.")
                    toast.show()
                }
                R.id.action_cancel_downvote -> {
                    toast.setText("Anda telah batal menolak unjuk rasa ini.")
                    toast.show()
                }
                R.id.action_self_remove -> {
                    toast.setText("Anda telah menghapus anda sendiri dari unjuk rasa ini.")
                    toast.show()
                }
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun setupEditMode() {
        if (editMode) {
            binding.fabUpvote.hide()
            binding.fabDownvote.hide()
            binding.fabParticipate.hide()
            binding.toolbar.menu.setGroupVisible(R.id.view_mode, false)
        } else {
            binding.toolbar.menu.setGroupVisible(R.id.edit_mode, false)
            binding.cvAddProgress.visibility = View.GONE
        }
    }

    private fun setupProgress() {
        progressInitialized = true

        progressListAdapter = ProgressListAdapter(childFragmentManager, findNavController())

        binding.rvProgress.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = progressListAdapter
        }

        progressListAdapter.initProgressList(
            arrayListOf(
                "abcde",
                "abcde",
                "abcde",
                "abcde",
                "abcde",
                "abcde"
            )
        )

        binding.cvAddProgress.setOnClickListener {
            findNavController().navigate(R.id.action_demonstrationPageFragment_to_addProgressPageFragment)
        }
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
                    binding.fabUpvote.show()
                    binding.fabDownvote.show()
                    binding.fabParticipate.show()
                }
                binding.fabShare.show()
            }
        })

        binding.fabParticipate.setOnClickListener {
            findNavController().navigate(R.id.action_demonstrationPageFragment_to_participateBottomSheetDialog)
        }

        binding.fabShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.fabUpvote.setOnClickListener {
            toast.setText("Terima kasih telah mendukung! Silahkan berikan pendapatmu.")
            toast.show()
            binding.nsv.smoothScrollTo(0, binding.tvDiscuss.top, 1500)
        }

        binding.fabDownvote.setOnClickListener {
            toast.setText("Anda sudah menolak. Silahkan berikan pendapatmu.")
            toast.show()
            binding.nsv.smoothScrollTo(0, binding.tvDiscuss.top, 1500)
        }
    }

    private fun setupChips() {
        binding.chipParticipant.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.PARTICIPANT
                )
            )
        }

        binding.chipUpvote.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.UPVOTE
                )
            )
        }

        binding.chipDownvote.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.DOWNVOTE
                )
            )
        }

        binding.chipShare.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.SHARE
                )
            )
        }
    }

    private fun setupDescription() {
        binding.reDescription.setEditorFontSize(16)
        binding.reDescription.setEditorFontColor(binding.tvTitle.currentTextColor)
        binding.reDescription.setEditorBackgroundColor(binding.root.solidColor)
        binding.reDescription.setInputEnabled(false)
        binding.reDescription.html = demonstration.description
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}