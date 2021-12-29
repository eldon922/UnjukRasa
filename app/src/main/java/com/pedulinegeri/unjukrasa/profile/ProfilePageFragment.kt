package com.pedulinegeri.unjukrasa.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentProfilePageBinding
import com.squareup.picasso.Picasso


class ProfilePageFragment : Fragment() {

    private var _binding: FragmentProfilePageBinding? = null
    private val binding get() = _binding!!

    private val args: ProfilePageFragmentArgs by navArgs()

    private val authViewModel: AuthViewModel by activityViewModels()

    private lateinit var uid: String

    private lateinit var userSnapshotListener: ListenerRegistration

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

        binding.fabAdd.setOnClickListener {
            requireActivity().findNavController(R.id.navHostContainerMain)
                .navigate(R.id.actionMainScreenToNavigationNewDemonstrationPage)
        }

        binding.rvDemonstration.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) binding.fabAdd.hide() else if (dy < 0) binding.fabAdd.show()
            }
        })

        if (args.userUID.isNotBlank()) {
            binding.appbar.visibility = View.VISIBLE
            binding.fabAdd.hide()
            uid = args.userUID
        } else {
            uid = authViewModel.uid
        }

        val imageRef =
            Firebase.storage.reference.child("profile_picture/$uid.png")

        imageRef.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(binding.ivPerson)
        }.addOnFailureListener {
            Picasso.get().load(R.drawable.profile_avatar_placeholder_large).into(binding.ivPerson)
        }
    }

    override fun onResume() {
        super.onResume()

        val db = Firebase.firestore
        val docRef = db.collection("users").document(uid)
        userSnapshotListener = docRef.addSnapshotListener { snapshot, e ->
            val user = snapshot?.toObject<User>()!!
            binding.tvName.text = user.name
            authViewModel.saveName(user.name)

            binding.tabDemonstration.getTabAt(0)!!.text = "Membuat (${user.demonstrations.size})"
            binding.rvDemonstration.apply {
                this.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                this.adapter = ProfileDemonstrationListAdapter(
                    user.demonstrations,
                    requireActivity().findNavController(R.id.navHostContainerMain)
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()

        userSnapshotListener.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}