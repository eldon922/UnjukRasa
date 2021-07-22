package com.pedulinegeri.unjukrasa.demonstration

import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentDemonstrationPageBinding
import com.pedulinegeri.unjukrasa.demonstration.discussion.DiscussionListAdapter
import com.pedulinegeri.unjukrasa.demonstration.participation.ParticipationListBottomSheetDialog
import com.pedulinegeri.unjukrasa.demonstration.person.PersonListAdapter
import com.pedulinegeri.unjukrasa.demonstration.progress.ProgressListAdapter


class DemonstrationPageFragment : Fragment() {

    private var _binding: FragmentDemonstrationPageBinding? = null
    private val binding get() = _binding!!

    //    TODO dev
    private var editMode = false

    private lateinit var discussionListAdapter: DiscussionListAdapter
    private lateinit var personListAdapter: PersonListAdapter
    private lateinit var demonstrationImageAdapter: DemonstrationImageAdapter
    private lateinit var progressListAdapter: ProgressListAdapter

    private var progressInitialized = false
    private var discussionInitialized = false

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

        setupToolbar()
        setupImages()
        setupChips()
        setupPerson()
        setupDescription()
        setupFab()
        setupEditMode()

        binding.nsv.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (isVisible(binding.rvProgress) && !progressInitialized) {
                    setupProgress()
                } else if (isVisible(binding.rvDiscussion) && !discussionInitialized) {
                    setupDiscussion()
                }

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
            }

            fun isVisible(view: View?): Boolean {
                if (view == null) {
                    return false
                }
                if (!view.isShown) {
                    return false
                }
                val actualPosition = Rect()
                view.getGlobalVisibleRect(actualPosition)
                val screen = Rect(
                    0,
                    0,
                    Resources.getSystem().displayMetrics.widthPixels,
                    Resources.getSystem().displayMetrics.heightPixels
                )
                return actualPosition.intersect(screen)
            }
        })
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
            arrayListOf(
                "Inisiator",
                "Koordinator",
                "Dukung",
                "Ikut",
                "Koordinator",
                "Koordinator"
            )
        )
    }

    private fun setupImages() {
        demonstrationImageAdapter = DemonstrationImageAdapter()

        binding.vpImages.adapter = demonstrationImageAdapter

        demonstrationImageAdapter.initDemonstrationImageList(
            arrayListOf(
                R.drawable.indonesian_flag,
                R.drawable.indonesian_flag,
                R.drawable.indonesian_flag,
                R.drawable.indonesian_flag
            )
        )

        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ -> }.attach()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.toolbar.setOnMenuItemClickListener {
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
                    Toast.makeText(
                        requireContext(),
                        "Anda telah batal mengikuti unjuk rasa ini.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.action_cancel_upvote -> {
                    Toast.makeText(
                        requireContext(),
                        "Anda telah batal mendukung unjuk rasa ini.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.action_cancel_downvote -> {
                    Toast.makeText(
                        requireContext(),
                        "Anda telah batal menolak unjuk rasa ini.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.action_self_remove -> {
                    Toast.makeText(
                        requireContext(),
                        "Anda telah menghapus anda sendiri dari unjuk rasa ini.",
                        Toast.LENGTH_SHORT
                    ).show()
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

        progressListAdapter = ProgressListAdapter()

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
            Toast.makeText(
                requireContext(),
                "Terima kasih telah mendukung! Silahkan berikan pendapatmu.",
                Toast.LENGTH_LONG
            ).show()
            binding.nsv.smoothScrollTo(0, binding.tvDiscuss.top, 1500)
        }

        binding.fabDownvote.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Anda sudah menolak. Silahkan berikan pendapatmu.",
                Toast.LENGTH_LONG
            ).show()
            binding.nsv.smoothScrollTo(0, binding.tvDiscuss.top, 1500)
        }
    }

    private fun setupChips() {
        binding.chipParticipant.setOnClickListener {
            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.PARTICIPANT
                )
            )
        }

        binding.chipUpvote.setOnClickListener {
            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.UPVOTE
                )
            )
        }

        binding.chipDownvote.setOnClickListener {
            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.DOWNVOTE
                )
            )
        }

        binding.chipShare.setOnClickListener {
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
        binding.reDescription.html =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur? At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat."
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}