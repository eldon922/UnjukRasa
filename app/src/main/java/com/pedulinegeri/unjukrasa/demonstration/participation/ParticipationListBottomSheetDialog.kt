package com.pedulinegeri.unjukrasa.demonstration.participation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.pedulinegeri.unjukrasa.databinding.PersonListBottomSheetLayoutBinding
import com.pedulinegeri.unjukrasa.demonstration.person.Person
import com.pedulinegeri.unjukrasa.demonstration.person.PersonListAdapter
import com.pedulinegeri.unjukrasa.profile.User

class ParticipationListBottomSheetDialog : BottomSheetDialogFragment() {

    enum class TypeList {
        PARTICIPANT, UPVOTE, DOWNVOTE, SHARE
    }

    private var _binding: PersonListBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    private val args: ParticipationListBottomSheetDialogArgs by navArgs()

    private lateinit var rvPersonListAdapter: PersonListAdapter

    private lateinit var searchTypeQuery: String

    private val usersRef = Firebase.firestore.collection("users")

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

        rvPersonListAdapter = PersonListAdapter(findNavController())

        binding.rvPerson.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = rvPersonListAdapter
        }

        when (args.typeList) {
            TypeList.PARTICIPANT -> {
                binding.tvTitle.text = "Partisipan Unjuk Rasa"
                searchTypeQuery = "participation"
            }
            TypeList.UPVOTE -> {
                binding.tvTitle.text = "Pendukung Unjuk Rasa"
                searchTypeQuery = "upvote"
            }
            TypeList.DOWNVOTE -> {
                binding.tvTitle.text = "Menolak Unjuk Rasa"
                searchTypeQuery = "downvote"
            }
            TypeList.SHARE -> {
                binding.tvTitle.text = "Membagikan Unjuk Rasa"
                searchTypeQuery = "share"
            }
        }

        usersRef.whereArrayContains(searchTypeQuery, args.demonstrationId).limit(10).get()
            .addOnSuccessListener {
                for (document in it!!.documents) {
                    val user = document?.toObject<User>()!!
                    rvPersonListAdapter.addPerson(Person(document.id, user.name))
                }
            }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                rvPersonListAdapter.clearPersonList()

                usersRef.whereArrayContains(searchTypeQuery, args.demonstrationId)
                    .whereEqualTo("name", query).get().addOnSuccessListener {
                        for (document in it!!.documents) {
                            val user = document?.toObject<User>()!!
                            rvPersonListAdapter.addPerson(Person(document.id, user.name))
                        }

                        if (rvPersonListAdapter.itemCount == 0) {
                            Toast.makeText(
                                requireActivity().applicationContext,
                                "Nama yang dicari tidak ada",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
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