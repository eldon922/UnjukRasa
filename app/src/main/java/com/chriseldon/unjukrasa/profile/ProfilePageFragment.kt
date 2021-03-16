package com.chriseldon.unjukrasa.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chriseldon.unjukrasa.R
import com.chriseldon.unjukrasa.databinding.FragmentProfilePageBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


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

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.text) {
                    resources.getString(R.string.mendukung) -> binding.rvDemonstration.adapter = DemonstrationListAdapter(arrayListOf("1111", "2222"))
                    resources.getString(R.string.membuat) -> binding.rvDemonstration.adapter = DemonstrationListAdapter(arrayListOf("1111"))
                    resources.getString(R.string.ditandai) -> binding.rvDemonstration.adapter = DemonstrationListAdapter(arrayListOf())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.rvDemonstration.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = DemonstrationListAdapter(arrayListOf("1111", "2222"))
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}