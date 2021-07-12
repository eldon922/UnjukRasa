package com.pedulinegeri.unjukrasa.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentSearchPageBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationListAdapter


class SearchPageFragment : Fragment() {

    private var fragmentBinding: FragmentSearchPageBinding? = null

    private lateinit var resultDemonstrationListAdapter: DemonstrationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentSearchPageBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = fragmentBinding!!

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_searchPageFragment_to_newDemonstrationPageFragment)
        }

        resultDemonstrationListAdapter = DemonstrationListAdapter(arrayListOf(), DemonstrationListAdapter.ViewType.RECOMMENDED, findNavController())

        binding.rvResult.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = resultDemonstrationListAdapter
        }

        binding.etSearch.addTextChangedListener {
            resultDemonstrationListAdapter.addDemonstration("abcde")
        }

        binding.etSearch.requestFocus()
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}