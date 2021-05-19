package com.pedulinegeri.unjukrasa.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentHomePageBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationListAdapter
import com.pedulinegeri.unjukrasa.demonstration.ViewType

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
            this.adapter = DemonstrationListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"), ViewType.TRENDING, requireActivity().findNavController(
                R.id.nav_host_container_main))
        }

        binding.rvMostActiveToday.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = DemonstrationListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"), ViewType.MOST_ACTIVE, requireActivity().findNavController(
                R.id.nav_host_container_main))
        }

        binding.rvRecommended.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = DemonstrationListAdapter(arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"), ViewType.RECOMMENDED, requireActivity().findNavController(
                R.id.nav_host_container_main))
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}