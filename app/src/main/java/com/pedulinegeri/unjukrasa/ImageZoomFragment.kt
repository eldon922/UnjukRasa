package com.pedulinegeri.unjukrasa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.pedulinegeri.unjukrasa.databinding.FragmentImageZoomBinding
import com.pedulinegeri.unjukrasa.databinding.FragmentNotificationPageBinding
import com.pedulinegeri.unjukrasa.message.MessagePageFragmentArgs
import com.squareup.picasso.Picasso


class ImageZoomFragment : Fragment() {

    private var _binding: FragmentImageZoomBinding? = null
    private val binding get() = _binding!!

    private val args: ImageZoomFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageZoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.get().load(args.uri.toUri()).into(binding.photoView)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}