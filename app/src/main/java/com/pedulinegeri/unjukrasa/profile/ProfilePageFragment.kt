package com.pedulinegeri.unjukrasa.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.GlideApp
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.FragmentProfilePageBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationListAdapter


class ProfilePageFragment : Fragment() {

    private var _binding: FragmentProfilePageBinding? = null
    private val binding get() = _binding!!

    private val args: ProfilePageFragmentArgs by navArgs()

    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        setupTabLayout()

        binding.rvDemonstration.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = DemonstrationListAdapter(
                arrayListOf("1111", "2222", "2222", "2222", "2222"),
                DemonstrationListAdapter.ViewType.PROFILE,
                requireActivity().findNavController(R.id.nav_host_container_main)
            )
        }

        binding.fabAdd.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_container_main)
                .navigate(R.id.action_main_screen_to_navigation_new_demonstration_page)
        }

        binding.rvDemonstration.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) binding.fabAdd.hide() else if (dy < 0) binding.fabAdd.show()
            }
        })

        if (args.userUID != "") {
            binding.appbar.visibility = View.VISIBLE
            binding.fabAdd.hide()
            uid = args.userUID
        } else {
            uid = Firebase.auth.currentUser!!.uid
        }

        val imageRef =
            Firebase.storage.reference.child("profile_picture/${uid}.jpg")

        GlideApp.with(requireContext())
            .load(imageRef)
            .into(binding.ivPerson)

        val db = Firebase.firestore
        val docRef = db.collection("users").document(Firebase.auth.currentUser!!.uid)
        docRef.addSnapshotListener { snapshot, e ->
            binding.tvName.text = snapshot?.data?.get("name").toString()
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.fabAdd.show()
                when (tab.text) {
                    resources.getString(R.string.mendukung) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(
                            arrayListOf("1111", "2222", "2222", "2222", "2222"),
                            DemonstrationListAdapter.ViewType.PROFILE,
                            requireActivity().findNavController(
                                R.id.nav_host_container_main
                            )
                        )
                    resources.getString(R.string.membuat) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(
                            arrayListOf("1111"),
                            DemonstrationListAdapter.ViewType.PROFILE,
                            requireActivity().findNavController(
                                R.id.nav_host_container_main
                            )
                        )
                    resources.getString(R.string.ditandai) -> binding.rvDemonstration.adapter =
                        DemonstrationListAdapter(
                            arrayListOf(),
                            DemonstrationListAdapter.ViewType.PROFILE,
                            requireActivity().findNavController(
                                R.id.nav_host_container_main
                            )
                        )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}