package com.pedulinegeri.unjukrasa.demonstration.participation

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.databinding.ParticipateBottomSheetLayoutBinding

class ParticipateBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: ParticipateBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    private val args: ParticipateBottomSheetDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ParticipateBottomSheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupContent()

        binding.btnViewPolicePermitDoc.setOnClickListener {
            val imageRef =
                Firebase.storage.reference.child("/police_permit_image/${args.demonstrationId}")

            imageRef.listAll().addOnSuccessListener {
                it.items[0].downloadUrl.addOnSuccessListener {
                    findNavController().navigate(
                        ParticipateBottomSheetDialogDirections.actionGlobalImageZoomBottomSheetDialog(
                            it.toString()
                        )
                    )
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireActivity().applicationContext,
                    "Gagal memuat gambar. ($it)",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setupContent() {
        binding.tvDescription.text = Html.fromHtml(
            "<ul style='padding-left: 1.2em'>\n" +
                    "<li>Silahkan koordinasi dengan koordinator dan peserta lain. Unjuk rasa ini akan diadakan pada:\n" +
                    "<ul>\n" +
                    "<li>Tanggal : ${args.date}</li>\n" +
                    "<li>Pukul : ${args.time}</li>\n" +
                    "<li>Tempat : ${args.location}</li>\n" +
                    "</ul>\n" +
                    "</li>\n" +
                    "<li>Waktu dan tempat dapat berubah sewaktu-waktu apabila koordinator menggantinya. Pastikan lagi kepada koordinator saat dekat hari h dan sesuaikan dengan surat ijin dari kepolisian.</li>\n" +
                    "</ul>"
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}