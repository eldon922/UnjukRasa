package com.pedulinegeri.unjukrasa.demonstration.person

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pedulinegeri.unjukrasa.databinding.AddPersonBottomSheetLayoutBinding


class AddPersonBottomSheetDialog: BottomSheetDialogFragment() {

    private var fragmentBinding: AddPersonBottomSheetLayoutBinding? = null

    private val addPersonListAdapter = AddPersonListAdapter(arrayListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = AddPersonBottomSheetLayoutBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        binding.rvAddPerson.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = addPersonListAdapter
        }

        addPersonListAdapter.onItemClick = {
            binding.group.visibility = View.VISIBLE
            binding.rvAddPerson.visibility = View.GONE
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.group.visibility = View.GONE
                binding.rvAddPerson.visibility = View.VISIBLE

                if (newText != null && newText.isNotEmpty()) {
                    addPersonListAdapter.addPerson(newText.last().toString())
                    return true
                } else {
                    addPersonListAdapter.clear()
                    return false
                }
            }
        })

        binding.btnAdd.setOnClickListener {
            Toast.makeText(requireContext(), "Invitasi telah dikirimkan ke orang yang dituju!", Toast.LENGTH_SHORT).show()
            this.dismiss()
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
}