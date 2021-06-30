package com.pedulinegeri.unjukrasa.demonstration.participation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pedulinegeri.unjukrasa.databinding.ParticipateBottomSheetLayoutBinding

class ParticipateBottomSheetDialog: BottomSheetDialogFragment() {

    private var fragmentBinding: ParticipateBottomSheetLayoutBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = ParticipateBottomSheetLayoutBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        binding.reDescription.setEditorFontSize(16)
        binding.reDescription.setEditorFontColor(binding.tvTitle.currentTextColor)
        binding.reDescription.setEditorBackgroundColor(binding.root.solidColor)
        binding.reDescription.setInputEnabled(false)
        binding.reDescription.html = "<ul style='padding-left: 1.2em'>\n" +
                "<li>Anda sudah dimasukkan ke grup obrolan unjuk rasa ini. Silahkan klik tombol di bawah untuk menuju halaman obrolan.</li>\n" +
                "<li>Silahkan koordinasi dengan koordinator dan peserta lain. Unjuk rasa ini akan diadakan pada:\n" +
                "<ul>\n" +
                "<li>Tanggal : 26 Februari 2021</li>\n" +
                "<li>Pukul : 11.00 - 15.00 WIB</li>\n" +
                "<li>Tempat : Gedung DPR, Jl. Gatot Subroto No.1, RT.1/RW.3</li>\n" +
                "</ul>\n" +
                "</li>\n" +
                "<li>Waktu dan tempat dapat berubah sewaktu-waktu apabila koordinator menggantinya. Pastikan lagi kepada koordinator saat dekat hari h dan sesuaikan dengan surat ijin dari kepolisian.</li>\n" +
                "</ul>"
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