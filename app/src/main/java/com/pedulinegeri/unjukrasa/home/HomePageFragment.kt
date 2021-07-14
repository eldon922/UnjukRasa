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

class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvTrending.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = DemonstrationListAdapter(
                arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"),
                DemonstrationListAdapter.ViewType.TRENDING,
                requireActivity().findNavController(
                    R.id.nav_host_container_main
                )
            )
        }

        binding.rvMostActiveToday.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = DemonstrationListAdapter(
                arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"),
                DemonstrationListAdapter.ViewType.MOST_ACTIVE,
                requireActivity().findNavController(
                    R.id.nav_host_container_main
                )
            )
        }

        binding.rvRecommended.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = DemonstrationListAdapter(
                arrayListOf("abcde", "abcde", "abcde", "abcde", "abcde", "abcde"),
                DemonstrationListAdapter.ViewType.RECOMMENDED,
                requireActivity().findNavController(
                    R.id.nav_host_container_main
                )
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}