package com.pedulinegeri.unjukrasa.demonstration

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentDemonstrationPageBinding
import com.pedulinegeri.unjukrasa.demonstration.discussion.DiscussionListAdapter
import com.pedulinegeri.unjukrasa.demonstration.participation.ParticipateBottomSheetDialog
import com.pedulinegeri.unjukrasa.demonstration.progress.ProgressListAdapter
import com.pedulinegeri.unjukrasa.demonstration.person.AddPersonBottomSheetDialog
import com.pedulinegeri.unjukrasa.demonstration.person.PersonListAdapter
import com.pedulinegeri.unjukrasa.demonstration.participation.PersonListBottomSheetDialog


class DemonstrationPageFragment : Fragment() {

    private var fragmentBinding: FragmentDemonstrationPageBinding? = null

    private lateinit var participateBottomSheetDialog: ParticipateBottomSheetDialog

    private lateinit var addPersonBottomSheetDialog: AddPersonBottomSheetDialog

    private lateinit var participantListBottomSheetDialog: PersonListBottomSheetDialog
    private lateinit var upvoteListBottomSheetDialog: PersonListBottomSheetDialog
    private lateinit var downvoteListBottomSheetDialog: PersonListBottomSheetDialog
    private lateinit var shareListBottomSheetDialog: PersonListBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentDemonstrationPageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.vpImages.adapter = DemonstrationImageAdapter(listOf(R.drawable.indonesian_flag, R.drawable.indonesian_flag, R.drawable.indonesian_flag, R.drawable.indonesian_flag))
        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ ->}.attach()

        binding.rvDiscussion.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = DiscussionListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"))
        }

        binding.nsv.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.fabUpvote.hide()
                binding.fabDownvote.hide()
                binding.fabShare.hide()
                binding.fabParticipate.hide()
            } else if (scrollY < oldScrollY || scrollY <= 0) {
                binding.fabUpvote.show()
                binding.fabDownvote.show()
                binding.fabShare.show()
                binding.fabParticipate.show()
            }
        }

        binding.rvProgress.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = ProgressListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"))
        }

        binding.rvPerson.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = PersonListAdapter(arrayListOf("Inisiator", "Koordinator", "Dukung", "Ikut", "Koordinator", "Koordinator"))
        }

        binding.fabParticipate.setOnClickListener {
            if (!this::participateBottomSheetDialog.isInitialized) {
                participateBottomSheetDialog = ParticipateBottomSheetDialog()
            }

            participateBottomSheetDialog.show(parentFragmentManager, "ParticipateBottomSheet")
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
            Toast.makeText(requireContext(), "Terima kasih telah mendukung! Silahkan berikan pendapatmu.", Toast.LENGTH_LONG).show()
            binding.nsv.smoothScrollTo(0, binding.tvDiscuss.top, 1500)
        }

        binding.fabDownvote.setOnClickListener {
            Toast.makeText(requireContext(), "Anda sudah menolak. Silahkan berikan pendapatmu.", Toast.LENGTH_LONG).show()
            binding.nsv.smoothScrollTo(0, binding.tvDiscuss.top, 1500)
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_edit -> {
                    findNavController().navigate(R.id.action_demonstrationPageFragment_to_newDemonstrationPageFragment)
                }
                R.id.action_add_person -> {
                    addPersonBottomSheetDialog = AddPersonBottomSheetDialog()
                    addPersonBottomSheetDialog.show(parentFragmentManager, "AddPersonBottomSheet")
                }
            }

            return@setOnMenuItemClickListener true
        }

        binding.cvAddProgress.setOnClickListener {
            findNavController().navigate(R.id.action_demonstrationPageFragment_to_addProgressPageFragment)
        }

        binding.reDescription.setEditorFontSize(16)
        binding.reDescription.setEditorFontColor(binding.tvTitle.currentTextColor)
        binding.reDescription.setEditorBackgroundColor(binding.root.solidColor)
        binding.reDescription.setInputEnabled(false)
        binding.reDescription.html = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur? At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat."

        binding.chipParticipant.setOnClickListener {
            if (!this::participantListBottomSheetDialog.isInitialized) {
                participantListBottomSheetDialog = PersonListBottomSheetDialog()
            }

            participantListBottomSheetDialog.show(parentFragmentManager, "ParticipantListBottomSheet")
        }

        binding.chipUpvote.setOnClickListener {
            if (!this::upvoteListBottomSheetDialog.isInitialized) {
                upvoteListBottomSheetDialog = PersonListBottomSheetDialog()
            }

            upvoteListBottomSheetDialog.show(parentFragmentManager, "UpvoteListBottomSheet")
        }

        binding.chipDownvote.setOnClickListener {
            if (!this::downvoteListBottomSheetDialog.isInitialized) {
                downvoteListBottomSheetDialog = PersonListBottomSheetDialog()
            }

            downvoteListBottomSheetDialog.show(parentFragmentManager, "DownvoteListBottomSheet")
        }

        binding.chipShare.setOnClickListener {
            if (!this::shareListBottomSheetDialog.isInitialized) {
                shareListBottomSheetDialog = PersonListBottomSheetDialog()
            }

            shareListBottomSheetDialog.show(parentFragmentManager, "ShareListBottomSheet")
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}