package com.pedulinegeri.unjukrasa.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentSearchPageBinding
import com.pedulinegeri.unjukrasa.demonstration.Demonstration


class SearchPageFragment : Fragment() {

    private var _binding: FragmentSearchPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_searchPageFragment_to_newDemonstrationPageFragment)
        }

//        setupSearchEngine()
//        setupResult()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun setupResult() {
//        resultDemonstrationListAdapter = MostRecentCreatedDemonstrationListAdapter(
//            findNavController()
//        )
//
//        binding.rvResult.apply {
//            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//            this.adapter = resultDemonstrationListAdapter
//        }
//    }
//
//    private fun setupSearchEngine() {
//        binding.etSearch.addTextChangedListener {
//            resultDemonstrationListAdapter.addDemonstration(Demonstration())
//        }
//
//        binding.etSearch.requestFocus()
//        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
//    }
}