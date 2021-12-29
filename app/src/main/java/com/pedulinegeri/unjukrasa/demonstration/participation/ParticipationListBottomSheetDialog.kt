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
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.pedulinegeri.unjukrasa.databinding.PersonListBottomSheetLayoutBinding
import com.pedulinegeri.unjukrasa.databinding.PersonListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.person.Person
import com.pedulinegeri.unjukrasa.demonstration.person.PersonListAdapter
import com.pedulinegeri.unjukrasa.profile.User
import android.R.string.no
import android.view.View.OnFocusChangeListener
import io.reactivex.rxjava3.exceptions.UndeliverableException

import io.reactivex.rxjava3.plugins.RxJavaPlugins

import android.R.string.no
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.net.SocketException


class ParticipationListBottomSheetDialog : BottomSheetDialogFragment() {

    enum class TypeList {
        PARTICIPANT, UPVOTE, DOWNVOTE, SHARE
    }

    private var _binding: PersonListBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    private val args: ParticipationListBottomSheetDialogArgs by navArgs()

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

        var baseQuery = usersRef.whereArrayContains(searchTypeQuery, args.demonstrationId)
        val config = PagingConfig(10, 5, true)
        var options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(baseQuery, config) {
                val user = it.toObject<User>()!!
                user.id = it.id
                return@setQuery user
            }
            .build()

        val adapter = object : FirestorePagingAdapter<User, PersonListAdapter.ViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): PersonListAdapter.ViewHolder {
                val binding =
                    PersonListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                return PersonListAdapter.ViewHolder(binding, findNavController())
            }

            override fun onBindViewHolder(
                holder: PersonListAdapter.ViewHolder,
                position: Int,
                model: User
            ) {
                holder.bind(Person(model.id, model.name))
            }
        }

        binding.rvPerson.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    baseQuery =
                        usersRef.whereArrayContains(searchTypeQuery, args.demonstrationId)
                            .whereEqualTo("name", query)
                    options = FirestorePagingOptions.Builder<User>()
                        .setLifecycleOwner(viewLifecycleOwner)
                        .setQuery(baseQuery, config) {
                            val user = it.toObject<User>()!!
                            user.id = it.id
                            return@setQuery user
                        }
                        .build()

                    val adapter = object :
                        FirestorePagingAdapter<User, PersonListAdapter.ViewHolder>(options) {
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): PersonListAdapter.ViewHolder {
                            val binding =
                                PersonListItemBinding.inflate(
                                    LayoutInflater.from(parent.context),
                                    parent,
                                    false
                                )

                            return PersonListAdapter.ViewHolder(binding, findNavController())
                        }

                        override fun onBindViewHolder(
                            holder: PersonListAdapter.ViewHolder,
                            position: Int,
                            model: User
                        ) {
                            holder.bind(Person(model.id, model.name))
                        }
                    }

                    binding.rvPerson.adapter = adapter

                    return true
                } else {
                    return false
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.ivClose.setOnClickListener {
            dismiss()
        }
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