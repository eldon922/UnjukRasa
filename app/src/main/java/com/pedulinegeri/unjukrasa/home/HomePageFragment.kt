package com.pedulinegeri.unjukrasa.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentHomePageBinding
import com.pedulinegeri.unjukrasa.databinding.MostRecentCreatedDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.MostUpvotedDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.TrendingDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.Demonstration

class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

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
        val baseQuery = collectionRef.orderBy("numberOfAction", Query.Direction.DESCENDING)
        val config = PagingConfig(10, 5, true)
        val options = FirestorePagingOptions.Builder<Demonstration>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(baseQuery, config) {
                val demonstration = it.toObject<Demonstration>()!!
                demonstration.id = it.id
                return@setQuery demonstration
            }
            .build()

        val adapter = object :
            FirestorePagingAdapter<Demonstration, TrendingDemonstrationViewHolder>(
                options
            ) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): TrendingDemonstrationViewHolder {
                val binding = TrendingDemonstrationListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return TrendingDemonstrationViewHolder(
                    binding, requireActivity().findNavController(
                        R.id.nav_host_container_main
                    )
                )
            }

            override fun onBindViewHolder(
                holder: TrendingDemonstrationViewHolder,
                position: Int,
                model: Demonstration
            ) {
                holder.bind(model)
            }

        }

        binding.rvTrending.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    private fun setupMostVotedListDemonstration() {
        val baseQuery = collectionRef.orderBy("upvote", Query.Direction.DESCENDING)
        val config = PagingConfig(10, 5, true)
        val options = FirestorePagingOptions.Builder<Demonstration>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(baseQuery, config) {
                val demonstration = it.toObject<Demonstration>()!!
                demonstration.id = it.id
                return@setQuery demonstration
            }
            .build()

        val adapter = object :
            FirestorePagingAdapter<Demonstration, MostUpvotedDemonstrationViewHolder>(
                options
            ) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): MostUpvotedDemonstrationViewHolder {
                val binding = MostUpvotedDemonstrationListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return MostUpvotedDemonstrationViewHolder(
                    binding, requireActivity().findNavController(
                        R.id.nav_host_container_main
                    )
                )
            }

            override fun onBindViewHolder(
                holder: MostUpvotedDemonstrationViewHolder,
                position: Int,
                model: Demonstration
            ) {
                holder.bind(model)
            }

        }

        binding.rvMostUpvoted.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    private fun setupMostRecentCreatedListDemonstration() {
        val baseQuery = collectionRef.orderBy("creationDate", Query.Direction.DESCENDING)
        val config = PagingConfig(10, 5, true)
        val options = FirestorePagingOptions.Builder<Demonstration>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(baseQuery, config) {
                val demonstration = it.toObject<Demonstration>()!!
                demonstration.id = it.id
                return@setQuery demonstration
            }
            .build()

        val adapter = object :
            FirestorePagingAdapter<Demonstration, MostRecentCreatedDemonstrationViewHolder>(
                options
            ) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): MostRecentCreatedDemonstrationViewHolder {
                val binding = MostRecentCreatedDemonstrationListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return MostRecentCreatedDemonstrationViewHolder(
                    binding, requireActivity().findNavController(
                        R.id.nav_host_container_main
                    )
                )
            }

            override fun onBindViewHolder(
                holder: MostRecentCreatedDemonstrationViewHolder,
                position: Int,
                model: Demonstration
            ) {
                holder.bind(model)
            }

        }

        binding.rvMostRecentCreated.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}