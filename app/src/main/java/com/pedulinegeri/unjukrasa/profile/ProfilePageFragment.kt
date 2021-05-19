package com.pedulinegeri.unjukrasa.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentProfilePageBinding
import com.pedulinegeri.unjukrasa.home.DemonstrationListAdapter
import com.pedulinegeri.unjukrasa.home.ViewType


class ProfilePageFragment : Fragment() {

    private var fragmentBinding: FragmentProfilePageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentProfilePageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        setupTabLayout()

        binding.rvDemonstration.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = DemonstrationListAdapter(arrayListOf("1111", "2222", "2222", "2222", "2222"), ViewType.PROFILE, requireActivity().findNavController(R.id.nav_host_container_main))
        }

        binding.fabAdd.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_container_main).navigate(R.id.action_main_screen_to_navigation_new_demonstration_page)
        }

        binding.rvDemonstration.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) binding.fabAdd.hide() else if (dy < 0) binding.fabAdd.show()
            }
        })
    }

    private fun setupTabLayout() {
        val binding = fragmentBinding!!

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.fabAdd.show()
                when (tab.text) {
                    resources.getString(R.string.mendukung) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(arrayListOf("1111", "2222", "2222", "2222", "2222"), ViewType.PROFILE, requireActivity().findNavController(
                            R.id.nav_host_container_main))
                    resources.getString(R.string.membuat) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(arrayListOf("1111"), ViewType.PROFILE, requireActivity().findNavController(
                            R.id.nav_host_container_main))
                    resources.getString(R.string.ditandai) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(arrayListOf(), ViewType.PROFILE, requireActivity().findNavController(
                            R.id.nav_host_container_main))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}