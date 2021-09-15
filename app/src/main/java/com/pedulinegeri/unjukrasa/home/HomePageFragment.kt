package com.pedulinegeri.unjukrasa.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentHomePageBinding
import com.pedulinegeri.unjukrasa.demonstration.Demonstration

class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    private lateinit var trendingDemonstrationListAdapter: TrendingDemonstrationListAdapter
    private lateinit var mostUpvotedDemonstrationListAdapter: MostUpvotedDemonstrationListAdapter
    private lateinit var mostRecentCreatedDemonstrationListAdapter: MostRecentCreatedDemonstrationListAdapter

    private val collectionRef = Firebase.firestore.collection("demonstrations")

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

        setupTrendingListDemonstration()
        setupMostVotedListDemonstration()
        setupMostRecentCreatedListDemonstration()
    }

    private fun setupTrendingListDemonstration() {
        trendingDemonstrationListAdapter = TrendingDemonstrationListAdapter(
            requireActivity().findNavController(
                R.id.nav_host_container_main
            )
        )

        binding.rvTrending.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = trendingDemonstrationListAdapter
        }

        collectionRef.orderBy("numberOfAction", Query.Direction.DESCENDING).limit(10).get()
            .addOnSuccessListener {
                for (document in it!!.documents) {
                    val demonstration = document?.toObject<Demonstration>()!!
                    demonstration.id = document.id

                    trendingDemonstrationListAdapter.addDemonstration(demonstration)
                }
            }
    }

    private fun setupMostVotedListDemonstration() {
        mostUpvotedDemonstrationListAdapter = MostUpvotedDemonstrationListAdapter(
            requireActivity().findNavController(
                R.id.nav_host_container_main
            )
        )

        binding.rvMostUpvoted.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = mostUpvotedDemonstrationListAdapter
        }

        collectionRef.orderBy("upvote", Query.Direction.DESCENDING).limit(10).get()
            .addOnSuccessListener {
                for (document in it!!.documents) {
                    val demonstration = document?.toObject<Demonstration>()!!
                    demonstration.id = document.id

                    mostUpvotedDemonstrationListAdapter.addDemonstration(demonstration)
                }
            }
    }

    private fun setupMostRecentCreatedListDemonstration() {
        mostRecentCreatedDemonstrationListAdapter = MostRecentCreatedDemonstrationListAdapter(
            requireActivity().findNavController(
                R.id.nav_host_container_main
            )
        )

        binding.rvMostRecentCreated.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = mostRecentCreatedDemonstrationListAdapter
        }

        collectionRef.orderBy("creationDate", Query.Direction.DESCENDING).limit(10).get()
            .addOnSuccessListener {
                for (document in it!!.documents) {
                    val demonstration = document?.toObject<Demonstration>()!!
                    demonstration.id = document.id

                    mostRecentCreatedDemonstrationListAdapter.addDemonstration(demonstration)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}