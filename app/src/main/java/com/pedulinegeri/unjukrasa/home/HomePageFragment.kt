package com.pedulinegeri.unjukrasa.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pedulinegeri.unjukrasa.MainFragmentDirections
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentHomePageBinding
import com.pedulinegeri.unjukrasa.home.adapter.MostRecentCreatedDemonstrationAdapter
import com.pedulinegeri.unjukrasa.home.adapter.MostUpvotedDemonstrationAdapter
import com.pedulinegeri.unjukrasa.home.adapter.TrendingDemonstrationAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    private val homePageViewModel: HomePageViewModel by viewModels()

    private lateinit var demonstrationOnClickListener: DemonstrationOnClickListener

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

        demonstrationOnClickListener = object : DemonstrationOnClickListener {
            override fun onClick(demonstrationId: String) {
                requireActivity().findNavController(R.id.navHostContainerMain).navigate(
                    MainFragmentDirections.actionGlobalDemonstrationPageFragment(demonstrationId)
                )
            }
        }

        setupTrendingDemonstrationList()
        setupMostUpvotedDemonstrationList()
        setupMostRecentCreatedDemonstrationList()
    }

    override fun onPause() {
        super.onPause()

        homePageViewModel.nsvScrollPosition = binding.nsv.scrollY
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTrendingDemonstrationList() {
        val adapter = TrendingDemonstrationAdapter(demonstrationOnClickListener)

        binding.rvTrending.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }

        lifecycleScope.launch {
            homePageViewModel.trendingDemonstrationFlow.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun setupMostUpvotedDemonstrationList() {
        val adapter = MostUpvotedDemonstrationAdapter(demonstrationOnClickListener)

        binding.rvMostUpvoted.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }

        lifecycleScope.launch {
            homePageViewModel.mostUpvotedDemonstrationFlow.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun setupMostRecentCreatedDemonstrationList() {
        val adapter = MostRecentCreatedDemonstrationAdapter(demonstrationOnClickListener)

        binding.rvMostRecentCreated.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }

        lifecycleScope.launch {
            homePageViewModel.mostRecentCreatedDemonstrationFlow.collect {
                adapter.submitData(it)
            }
        }
    }
}