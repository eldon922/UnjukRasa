package com.pedulinegeri.unjukrasa.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.attributesToRetrieve
import com.algolia.search.dsl.query
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentSearchPageBinding
import com.pedulinegeri.unjukrasa.profile.DemonstrationTitle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


class SearchPageFragment : Fragment() {

    private var _binding: FragmentSearchPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var imm: InputMethodManager

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

        imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_searchPageFragment_to_newDemonstrationPageFragment)
        }

        setupSearchEngine()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSearchEngine() {
        val appID = ApplicationID("SCYCUB9LCD")
        val apiKey = APIKey("b3d99bfd89a019f83cef2fec55bcaffb")

        val client = ClientSearch(appID, apiKey)

        val indexName = IndexName("demonstrations")
        val index = client.initIndex(indexName)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(queryText: String?): Boolean {
                imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)

                return if (queryText != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val result = index.search(query {
                                query = queryText
                                attributesToRetrieve {
                                    +"color"
                                    +"category"
                                }
                            })

                            val demonstrationTitleList = arrayListOf<DemonstrationTitle>()
                            for (hit in result.hits) {
                                val jsonObject = hit.json["_highlightResult"]!!
                                demonstrationTitleList.add(
                                    DemonstrationTitle(
                                        hit.json["objectID"]!!.jsonPrimitive.content,
                                        jsonObject.jsonObject["title"]!!.jsonObject["value"]!!.jsonPrimitive.content,
                                        jsonObject.jsonObject["description"]!!.jsonObject["value"]!!.jsonPrimitive.content,
                                        jsonObject.jsonObject["youtubeThumbnailUrl"]!!.jsonObject["value"]!!.jsonPrimitive.content
                                    )
                                )
                            }

                            binding.rvResult.apply {
                                this.layoutManager =
                                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                                this.adapter = SearchPageDemonstrationListAdapter(
                                    demonstrationTitleList,
                                    findNavController()
                                )
                            }
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    true
                } else {
                    false
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()

        binding.searchView.requestFocus()
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onPause() {
        super.onPause()

        imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
    }
}