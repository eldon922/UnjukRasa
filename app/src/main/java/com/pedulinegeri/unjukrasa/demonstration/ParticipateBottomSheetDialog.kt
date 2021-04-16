package com.pedulinegeri.unjukrasa.demonstration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pedulinegeri.unjukrasa.databinding.DiscussionReplyBottomSheetLayoutBinding
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


    }
}