package com.pedulinegeri.unjukrasa.demonstration.participation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pedulinegeri.unjukrasa.databinding.PersonListBottomSheetLayoutBinding
import com.pedulinegeri.unjukrasa.demonstration.person.Person
import com.pedulinegeri.unjukrasa.demonstration.person.PersonListAdapter

class ParticipationListBottomSheetDialog : BottomSheetDialogFragment() {

    enum class TypeList {
        PARTICIPANT, UPVOTE, DOWNVOTE, SHARE
    }

    private var _binding: PersonListBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    private val args: ParticipationListBottomSheetDialogArgs by navArgs()

    private lateinit var rvPersonListAdapter: PersonListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PersonListBottomSheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPersonListAdapter = PersonListAdapter(
            findNavController()
        )

        binding.rvPerson.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = rvPersonListAdapter
        }

        rvPersonListAdapter.initPersonList(
            arrayListOf(
                Person(),
                Person(),
                Person(),
                Person(),
                Person()
            )
        )

        when (args.typeList) {
            TypeList.PARTICIPANT -> {
                binding.tvTitle.text = "Partisipan Unjuk Rasa"
            }
            TypeList.UPVOTE -> {
                binding.tvTitle.text = "Mendukung Unjuk Rasa"
            }
            TypeList.DOWNVOTE -> {
                binding.tvTitle.text = "Menolak Unjuk Rasa"
            }
            TypeList.SHARE -> {
                binding.tvTitle.text = "Membagikan Unjuk Rasa"
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotBlank()) {
                    rvPersonListAdapter.addPerson(Person("", newText))
                    return true
                } else {
                    return false
                }
            }
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}