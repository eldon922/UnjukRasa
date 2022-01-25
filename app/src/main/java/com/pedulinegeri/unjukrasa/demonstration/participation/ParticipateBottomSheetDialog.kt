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
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.ParticipateBottomSheetLayoutBinding

class ParticipateBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: ParticipateBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    private val args: ParticipateBottomSheetDialogArgs by navArgs()

    private lateinit var toast: Toast

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

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

        setupContent()

        binding.btnViewPolicePermit.setOnClickListener {
            val imageRef =
                Firebase.storage.reference.child("/police_permit_image/${args.demonstrationId}")

            imageRef.listAll().addOnSuccessListener {
                if (it.items.size > 0) {
                    it.items[0].downloadUrl.addOnSuccessListener {
                        findNavController().navigate(
                            ParticipateBottomSheetDialogDirections.actionGlobalImageZoomBottomSheetDialog(
                                it.toString()
                            )
                        )
                    }
                } else {
                    toast.setText(getString(R.string.police_permit_not_exist))
                    toast.show()
                }
            }.addOnFailureListener {
                toast.setText(getString(R.string.image_load_failed, it))
                toast.show()
            }
        }
    }

    private fun setupContent() {
        binding.tvDescription.text = Html.fromHtml(
            "<b>Silahkan koordinasi dengan koordinator dan peserta lain. Unjuk rasa ini akan diadakan pada:</b>" +
                    "<br/>" +
                    "<br/>&#8226; Tanggal : ${args.date}" +
                    "<br/>&#8226; Pukul : ${args.time}" +
                    "<br/>&#8226; Tempat : ${args.location}" +
                    "<br/>" +
                    "<br/><b>Waktu dan tempat dapat berubah sewaktu-waktu apabila koordinator menggantinya. Pastikan lagi kepada koordinator saat dekat hari h dan sesuaikan dengan surat ijin dari kepolisian</b>"
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