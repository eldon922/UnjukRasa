package com.pedulinegeri.unjukrasa.demonstration

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.auth.AuthViewModel
import com.pedulinegeri.unjukrasa.databinding.FragmentDemonstrationPageBinding
import com.pedulinegeri.unjukrasa.demonstration.participation.ParticipationListBottomSheetDialog
import com.pedulinegeri.unjukrasa.demonstration.person.PersonListAdapter
import com.pedulinegeri.unjukrasa.demonstration.progress.ProgressListAdapter
import com.pedulinegeri.unjukrasa.profile.User


class DemonstrationPageFragment : Fragment() {

    private var _binding: FragmentDemonstrationPageBinding? = null
    private val binding get() = _binding!!

    private val args: DemonstrationPageFragmentArgs by navArgs()

    private lateinit var toast: Toast

    private val authViewModel: AuthViewModel by activityViewModels()

    private var editMode = false
    private var hasAction = true

    private lateinit var personListAdapter: PersonListAdapter
    private lateinit var demonstrationImageAdapter: DemonstrationImageAdapter
    private lateinit var progressListAdapter: ProgressListAdapter

    private var progressInitialized = false

    private var lastClickTime = 0L

    private lateinit var demonstration: Demonstration

    private lateinit var userSnapshotListener: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDemonstrationPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

        val db = Firebase.firestore
        val docRef = db.collection("demonstrations").document(args.id)
        docRef.get().addOnSuccessListener {
            demonstration = it?.toObject<Demonstration>()!!

            binding.tvTitle.text = demonstration.title

            setupFab()
            setupEditMode()
            updateUserData()
            setupImages()
            setupChips()
            setupPerson()
            setupDescription()
            setupProgress()
        }.addOnFailureListener {
            toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
            toast.show()
        }

        setupToolbar()
    }

    private fun setupPerson() {
        personListAdapter = PersonListAdapter(findNavController(), authViewModel.uid)

        binding.rvPerson.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = personListAdapter
        }

        personListAdapter.initPersonList(
            demonstration.persons
        )
    }

    private fun setupImages() {
        demonstrationImageAdapter = DemonstrationImageAdapter(findNavController())
        binding.vpImages.adapter = demonstrationImageAdapter

        TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ -> }.attach()
        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {
                    binding.intoTabLayout.visibility = View.GONE
                } else {
                    binding.intoTabLayout.visibility = View.VISIBLE
                }
            }
        })

        if (demonstration.youtube_video.isNotBlank()) {
            demonstrationImageAdapter.addImageOrVideo(demonstration.youtube_video)
        }

        val imageRef =
            Firebase.storage.reference.child("demonstration_image/${args.id}/${demonstration.initiatorUid}")

        imageRef.listAll().addOnSuccessListener {
            it.items.forEach {
                it.downloadUrl.addOnSuccessListener { demonstrationImageAdapter.addImageOrVideo(it.toString()) }
            }
        }.addOnFailureListener {
            toast.setText("Gagal memuat gambar. ($it)")
            toast.show()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.toolbar.setOnMenuItemClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnMenuItemClickListener false
            }
            lastClickTime = SystemClock.elapsedRealtime()

            when (it.itemId) {
                R.id.action_edit -> {
                    findNavController().navigate(
                        DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToNewDemonstrationPageFragment(
                            true
                        )
                    )
                }
                R.id.action_cancel_participate -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Batal Ikut")
                        .setMessage(
                            "Apakah kamu yakin ingin membatalkan partisipasi anda pada unjuk rasa ini?"
                        )
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            val data = hashMapOf(
                                "action" to "participation",
                                "demonstrationId" to args.id
                            )

                            Firebase.functions("asia-southeast2")
                                .getHttpsCallable("cancelDemonstrationAction").call(data)
                                .addOnSuccessListener {
                                    if ((it.data as HashMap<String, Any>)["success"] as Boolean) {
                                        toast.setText("Anda telah berhasil membatalkan partisipasi anda pada unjuk rasa ini.")
                                        toast.show()
                                        binding.chipParticipant.text =
                                            "${binding.chipParticipant.text.split(" ")[0].toLong() - 1} Dukung"
                                    } else {
                                        toast.setText("Anda belum mengikuti/mendukung/menolak unjuk rasa ini sebelumnya.")
                                        toast.show()
                                    }
                                }.addOnFailureListener {
                                    toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                                    toast.show()
                                }
                        }
                        .setNegativeButton(android.R.string.cancel, null).show()
                }
                R.id.action_cancel_upvote -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Batal Mendukung")
                        .setMessage(
                            "Apakah kamu yakin ingin membatalkan dukungan anda pada unjuk rasa ini?"
                        )
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            val data = hashMapOf(
                                "action" to "upvote",
                                "demonstrationId" to args.id
                            )

                            Firebase.functions("asia-southeast2")
                                .getHttpsCallable("cancelDemonstrationAction").call(data)
                                .addOnSuccessListener {
                                    if ((it.data as HashMap<String, Any>)["success"] as Boolean) {
                                        toast.setText("Anda telah berhasil membatalkan dukungan anda pada unjuk rasa ini.")
                                        toast.show()
                                        binding.chipUpvote.text =
                                            "${binding.chipUpvote.text.split(" ")[0].toLong() - 1} Dukung"
                                    } else {
                                        toast.setText("Anda belum mengikuti/mendukung/menolak unjuk rasa ini sebelumnya.")
                                        toast.show()
                                    }
                                }.addOnFailureListener {
                                    toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                                    toast.show()
                                }
                        }
                        .setNegativeButton(android.R.string.cancel, null).show()
                }
                R.id.action_cancel_downvote -> {
                    val data = hashMapOf(
                        "action" to "downvote",
                        "demonstrationId" to args.id
                    )

                    Firebase.functions("asia-southeast2")
                        .getHttpsCallable("cancelDemonstrationAction").call(data)
                        .addOnSuccessListener {
                            if ((it.data as HashMap<String, Any>)["success"] as Boolean) {
                                toast.setText("Anda telah berhasil membatalkan penolakan anda pada unjuk rasa ini.")
                                toast.show()
                                binding.chipDownvote.text =
                                    "${binding.chipDownvote.text.split(" ")[0].toLong() - 1} Dukung"
                            } else {
                                toast.setText("Anda belum mengikuti/mendukung/menolak unjuk rasa ini sebelumnya.")
                                toast.show()
                            }
                        }.addOnFailureListener {
                            toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                            toast.show()
                        }
                }
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun setupEditMode() {
        editMode = demonstration.initiatorUid == authViewModel.uid

        if (editMode) {
            binding.fabUpvote.hide()
            binding.fabDownvote.hide()
            binding.fabParticipate.hide()
            binding.toolbar.menu.setGroupVisible(R.id.edit_mode, true)
        } else {
            binding.cvAddProgress.visibility = View.GONE
            if (!hasAction) {
                binding.fabUpvote.hide()
                binding.fabDownvote.hide()
                binding.fabParticipate.hide()
            } else {
                binding.fabUpvote.show()
                binding.fabDownvote.show()
                binding.fabParticipate.show()
            }
        }
        binding.fabShare.show()
    }

    private fun updateUserData() {
        val db = Firebase.firestore
        val docRef = db.collection("users").document(authViewModel.uid)
        userSnapshotListener = docRef.addSnapshotListener { snapshot, e ->
            val user = snapshot?.toObject<User>()!!
            hasAction = !(args.id in user.participation
                    || args.id in user.upvote
                    || args.id in user.downvote)
            setupEditMode()

            binding.toolbar.menu.findItem(R.id.action_cancel_participate).isVisible = false
            binding.toolbar.menu.findItem(R.id.action_cancel_upvote).isVisible = false
            binding.toolbar.menu.findItem(R.id.action_cancel_downvote).isVisible = false
            when (args.id) {
                in user.participation -> {
                    binding.toolbar.menu.findItem(R.id.action_cancel_participate).isVisible = true
                }
                in user.upvote -> {
                    binding.toolbar.menu.findItem(R.id.action_cancel_upvote).isVisible = true
                }
                in user.downvote -> {
                    binding.toolbar.menu.findItem(R.id.action_cancel_downvote).isVisible = true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (this::userSnapshotListener.isInitialized) {
            updateUserData()
        }
    }

    override fun onPause() {
        super.onPause()

        userSnapshotListener.remove()
    }

    private fun setupProgress() {
        progressInitialized = true

        progressListAdapter = ProgressListAdapter(findNavController(), args.id, authViewModel.uid, authViewModel.name)

        binding.rvProgress.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = progressListAdapter
        }

        progressListAdapter.initProgressList(
            demonstration.progress
        )

        binding.cvAddProgress.setOnClickListener {
            findNavController().navigate(DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToAddProgressPageFragment(args.id, progressListAdapter.itemCount))
        }
    }

    private fun setupFab() {
        binding.nsv.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.fabUpvote.hide()
                binding.fabDownvote.hide()
                binding.fabShare.hide()
                binding.fabParticipate.hide()
            } else if (scrollY < oldScrollY || scrollY <= 0) {
                if (!editMode && hasAction) {
                    binding.fabUpvote.show()
                    binding.fabDownvote.show()
                    binding.fabParticipate.show()
                }
                binding.fabShare.show()
            }
        })

        binding.fabParticipate.setOnClickListener {
            val data = hashMapOf(
                "action" to "participation",
                "demonstrationId" to args.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
                .addOnSuccessListener {
                    if ((it.data as HashMap<String, Any>)["success"] as Boolean) {
                        findNavController().navigate(R.id.action_demonstrationPageFragment_to_participateBottomSheetDialog)
                        binding.chipParticipant.text =
                            "${binding.chipParticipant.text.split(" ")[0].toLong() + 1} Ikut"
                    } else {
                        toast.setText("Anda telah mengikuti/mendukung/menolak unjuk rasa ini sebelumnya.")
                        toast.show()
                    }
                }.addOnFailureListener {
                    toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                    toast.show()
                }
        }

        binding.fabShare.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

            val data = hashMapOf(
                "action" to "share",
                "demonstrationId" to args.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
        }

        binding.fabUpvote.setOnClickListener {
            val data = hashMapOf(
                "action" to "upvote",
                "demonstrationId" to args.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
                .addOnSuccessListener {
                    if ((it.data as HashMap<String, Any>)["success"] as Boolean) {
                        toast.setText("Terima kasih telah mendukung!")
                        toast.show()
                        binding.chipUpvote.text =
                            "${binding.chipUpvote.text.split(" ")[0].toLong() + 1} Dukung"
                    } else {
                        toast.setText("Anda telah mengikuti/mendukung/menolak unjuk rasa ini sebelumnya.")
                        toast.show()
                    }
                }.addOnFailureListener {
                    toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                    toast.show()
                }
        }

        binding.fabDownvote.setOnClickListener {
            val data = hashMapOf(
                "action" to "downvote",
                "demonstrationId" to args.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
                .addOnSuccessListener {
                    if ((it.data as HashMap<String, Any>)["success"] as Boolean) {
                        toast.setText("Anda sudah menolak.")
                        toast.show()
                        binding.chipDownvote.text =
                            "${binding.chipDownvote.text.split(" ")[0].toLong() + 1} Menolak"
                    } else {
                        toast.setText("Anda telah mengikuti/mendukung/menolak unjuk rasa ini sebelumnya.")
                        toast.show()
                    }
                }.addOnFailureListener {
                    toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                    toast.show()
                }
        }
    }

    private fun setupChips() {
        binding.chipParticipant.text = "${demonstration.participation} Ikut"
        binding.chipUpvote.text = "${demonstration.upvote} Dukung"
        binding.chipDownvote.text = "${demonstration.downvote} Menolak"
        binding.chipShare.text = "${demonstration.share} Membagikan"

        binding.chipParticipant.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.PARTICIPANT
                )
            )
        }

        binding.chipUpvote.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.UPVOTE
                )
            )
        }

        binding.chipDownvote.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.DOWNVOTE
                )
            )
        }

        binding.chipShare.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                return@setOnClickListener
            }
            lastClickTime = SystemClock.elapsedRealtime()

            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipationListBottomSheetDialog(
                    ParticipationListBottomSheetDialog.TypeList.SHARE
                )
            )
        }
    }

    private fun setupDescription() {
        binding.reDescription.setEditorFontSize(16)
        binding.reDescription.setEditorFontColor(binding.tvTitle.currentTextColor)
        binding.reDescription.setEditorBackgroundColor(binding.root.solidColor)
        binding.reDescription.setInputEnabled(false)
        binding.reDescription.html = demonstration.description
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}