package com.chriseldon.unjukrasa.topic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chriseldon.unjukrasa.databinding.FragmentTopicPageBinding

class TopicPageFragment : Fragment() {

    private var fragmentBinding: FragmentTopicPageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentTopicPageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}