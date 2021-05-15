package com.pedulinegeri.unjukrasa.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pedulinegeri.unjukrasa.databinding.FragmentHomePageBinding

class HomePageFragment : Fragment() {

    private var fragmentBinding: FragmentHomePageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentHomePageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        binding.rvTrending.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = DemonstrationListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"), ViewType.TRENDING)
        }

        binding.rvMostActiveToday.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = DemonstrationListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"), ViewType.MOST_ACTIVE)
        }

        binding.rvRecommended.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = DemonstrationListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"), ViewType.RECOMMENDED)
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}