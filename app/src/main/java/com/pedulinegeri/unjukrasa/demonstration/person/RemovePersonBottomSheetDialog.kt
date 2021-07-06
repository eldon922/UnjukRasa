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
import com.pedulinegeri.unjukrasa.databinding.RemovePersonBottomSheetLayoutBinding


class RemovePersonBottomSheetDialog: BottomSheetDialogFragment() {

    private var fragmentBinding: RemovePersonBottomSheetLayoutBinding? = null

    private val removePersonListAdapter = RemovePersonListAdapter(arrayListOf("Inisiator", "Koordinator", "Dukung", "Ikut", "Koordinator", "Koordinator"))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = RemovePersonBottomSheetLayoutBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        binding.rvRemovePerson.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = removePersonListAdapter
        }

        binding.btnRemove.setOnClickListener {
            Toast.makeText(requireContext(), "Orang yang dipilih telah berhasil dihapus!", Toast.LENGTH_SHORT).show()
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
        layoutParams.height = bottomSheet.layoutParams.height
        bottomSheet.layoutParams = layoutParams
    }
}