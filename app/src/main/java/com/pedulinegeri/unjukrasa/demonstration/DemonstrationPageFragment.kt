package com.pedulinegeri.unjukrasa.demonstration

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.SystemClock
import android.text.Html
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
import java.text.SimpleDateFormat


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

    private lateinit var chipDefaultColor: ColorStateList

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

        chipDefaultColor = binding.chipUpvote.chipBackgroundColor!!

        toast = Toast.makeText(requireActivity().applicationContext, "", Toast.LENGTH_LONG)

        val db = Firebase.firestore
        val docRef = db.collection("demonstrations").document(args.id)
        docRef.get().addOnSuccessListener {
            demonstration = it?.toObject<Demonstration>()!!
            demonstration.id = args.id

            binding.tvTitle.text = demonstration.title
            binding.tvTo.text = "Unjuk rasa ini ditujukan kepada: ${demonstration.to}"

            setupFab()
            setupEditMode()
            authViewModel.isSignedIn.observe(viewLifecycleOwner, {
                if (it) {
                    updateUserData()
                } else {
                    binding.fabParticipate.setOnClickListener {
                        findNavController().navigate(
                            DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToNavigationLoginPage()
                        )
                    }
                    binding.fabUpvote.setOnClickListener {
                        findNavController().navigate(
                            DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToNavigationLoginPage()
                        )
                    }
                    binding.fabDownvote.setOnClickListener {
                        findNavController().navigate(
                            DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToNavigationLoginPage()
                        )
                    }
                }
            })
            setupImages()
            setupChips()
            setupPerson()
            binding.tvDescription.text = Html.fromHtml(demonstration.description)
            setupProgress()
        }.addOnFailureListener {
            toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
            toast.show()
        }

        setupToolbar()
    }

    private fun setupPerson() {
        personListAdapter = PersonListAdapter(findNavController())

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
            Firebase.storage.reference.child("demonstration_image/${demonstration.id}/${demonstration.initiatorUid}")

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
                R.id.actionEdit -> {
                    findNavController().navigate(
                        DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToEditDemonstrationPageFragment(
                            demonstration.id
                        )
                    )
                }
                R.id.actionCancelParticipate -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Batal Ikut")
                        .setMessage(
                            "Apakah kamu yakin ingin membatalkan partisipasi anda pada unjuk rasa ini?"
                        )
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            toast.setText("Anda telah berhasil membatalkan partisipasi anda pada unjuk rasa ini.")
                            toast.show()
                            binding.chipParticipant.text =
                                "${binding.chipParticipant.text.split(" ")[0].toLong() - 1} Ikut"
                            binding.chipParticipant.chipBackgroundColor = chipDefaultColor
                            binding.fabUpvote.show()
                            binding.fabDownvote.show()
                            if (demonstration.road_protests) binding.fabParticipate.show()

                            val data = hashMapOf(
                                "action" to "participation",
                                "demonstrationId" to demonstration.id
                            )

                            Firebase.functions("asia-southeast2")
                                .getHttpsCallable("cancelDemonstrationAction").call(data)
                                .addOnSuccessListener {
                                    if (!((it.data as HashMap<String, Any>)["success"] as Boolean)) {
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
                R.id.actionCancelUpvote -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Batal Mendukung")
                        .setMessage(
                            "Apakah kamu yakin ingin membatalkan dukungan anda pada unjuk rasa ini?"
                        )
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            toast.setText("Anda telah berhasil membatalkan dukungan anda pada unjuk rasa ini.")
                            toast.show()
                            binding.chipUpvote.text =
                                "${binding.chipUpvote.text.split(" ")[0].toLong() - 1} Dukung"
                            binding.chipUpvote.chipBackgroundColor = chipDefaultColor

                            val data = hashMapOf(
                                "action" to "upvote",
                                "demonstrationId" to demonstration.id
                            )

                            Firebase.functions("asia-southeast2")
                                .getHttpsCallable("cancelDemonstrationAction").call(data)
                                .addOnSuccessListener {
                                    if (!((it.data as HashMap<String, Any>)["success"] as Boolean)) {
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
                R.id.actionCancelDownvote -> {
                    toast.setText("Anda telah berhasil membatalkan penolakan anda pada unjuk rasa ini.")
                    toast.show()
                    binding.chipDownvote.text =
                        "${binding.chipDownvote.text.split(" ")[0].toLong() - 1} Menolak"
                    binding.chipDownvote.chipBackgroundColor = chipDefaultColor

                    val data = hashMapOf(
                        "action" to "downvote",
                        "demonstrationId" to demonstration.id
                    )

                    Firebase.functions("asia-southeast2")
                        .getHttpsCallable("cancelDemonstrationAction").call(data)
                        .addOnSuccessListener {
                            if (!((it.data as HashMap<String, Any>)["success"] as Boolean)) {
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
            binding.toolbar.menu.setGroupVisible(R.id.editMode, true)
            binding.cvAddProgress.visibility = View.VISIBLE
            binding.toolbar.menu.setGroupVisible(R.id.viewMode, false)
        } else {
            if (!hasAction) {
                binding.fabUpvote.hide()
                binding.fabDownvote.hide()
                binding.fabParticipate.hide()
            } else {
                binding.fabUpvote.show()
                binding.fabDownvote.show()
                if (demonstration.road_protests) binding.fabParticipate.show()
            }
        }
        binding.fabShare.show()
    }

    private fun updateUserData() {
        val db = Firebase.firestore
        val docRef = db.collection("users").document(authViewModel.uid)
        userSnapshotListener = docRef.addSnapshotListener { document, e ->
            val user = document?.toObject<User>()!!
            hasAction = !(demonstration.id in user.participation
                    || demonstration.id in user.upvote
                    || demonstration.id in user.downvote)

            binding.toolbar.menu.findItem(R.id.actionCancelParticipate).isVisible = false
            binding.toolbar.menu.findItem(R.id.actionCancelUpvote).isVisible = false
            binding.toolbar.menu.findItem(R.id.actionCancelDownvote).isVisible = false
            if (demonstration.id in user.participation) {
                binding.toolbar.menu.findItem(R.id.actionCancelParticipate).isVisible = true
                binding.chipParticipant.setChipBackgroundColorResource(R.color.green)
            }
            if (demonstration.id in user.upvote) {
                binding.toolbar.menu.findItem(R.id.actionCancelUpvote).isVisible = true
                binding.chipUpvote.setChipBackgroundColorResource(R.color.green)
            }
            if (demonstration.id in user.downvote) {
                binding.toolbar.menu.findItem(R.id.actionCancelDownvote).isVisible = true
                binding.chipDownvote.setChipBackgroundColorResource(R.color.red)
            }

            setupEditMode()
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

        if (this::userSnapshotListener.isInitialized) {
            userSnapshotListener.remove()
        }
    }

    private fun setupProgress() {
        progressInitialized = true

        progressListAdapter = ProgressListAdapter(
            findNavController(),
            demonstration.id,
            demonstration.persons[0].uid,
            demonstration.persons[0].name
        )

        binding.rvProgress.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            this.adapter = progressListAdapter
        }

        progressListAdapter.initProgressList(
            demonstration.progress
        )

        binding.cvAddProgress.setOnClickListener {
            if (demonstration.progress.size < 6) {
                findNavController().navigate(
                    DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToAddProgressPageFragment(
                        demonstration.id,
                        progressListAdapter.itemCount
                    )
                )
            } else {
                toast.setText("Perkembangan tidak bisa lebih dari 6 buah.")
                toast.show()
            }
        }

        if (!editMode && progressListAdapter.itemCount == 0) binding.tvProgress.visibility =
            View.GONE
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
                    if (demonstration.road_protests) binding.fabParticipate.show()
                }
                binding.fabShare.show()
            }
        })

        binding.fabParticipate.setOnClickListener {
            findNavController().navigate(
                DemonstrationPageFragmentDirections.actionDemonstrationPageFragmentToParticipateBottomSheetDialog(
                    SimpleDateFormat("dd MMMM yyyy").format(demonstration.datetime),
                    SimpleDateFormat("hh:mm aa").format(demonstration.datetime),
                    demonstration.location,
                    demonstration.id
                )
            )
            binding.chipParticipant.text =
                "${binding.chipParticipant.text.split(" ")[0].toLong() + 1} Ikut"
            binding.chipParticipant.setChipBackgroundColorResource(R.color.green)
            binding.fabUpvote.hide()
            binding.fabDownvote.hide()
            binding.fabParticipate.hide()

            val data = hashMapOf(
                "action" to "participation",
                "demonstrationId" to demonstration.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
                .addOnSuccessListener {
                    if (!((it.data as HashMap<String, Any>)["success"] as Boolean)) {
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
                putExtra(Intent.EXTRA_TEXT, "unjukrasa.com/demonstration/${demonstration.id}")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

            val data = hashMapOf(
                "action" to "share",
                "demonstrationId" to demonstration.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
        }

        binding.fabUpvote.setOnClickListener {
            toast.setText("Terima kasih telah mendukung!")
            toast.show()
            binding.chipUpvote.text =
                "${binding.chipUpvote.text.split(" ")[0].toLong() + 1} Dukung"
            binding.chipUpvote.setChipBackgroundColorResource(R.color.green)
            binding.fabUpvote.hide()
            binding.fabDownvote.hide()
            binding.fabParticipate.hide()

            val data = hashMapOf(
                "action" to "upvote",
                "demonstrationId" to demonstration.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
                .addOnSuccessListener {
                    if (!((it.data as HashMap<String, Any>)["success"] as Boolean)) {
                        toast.setText("Anda telah mengikuti/mendukung/menolak unjuk rasa ini sebelumnya.")
                        toast.show()
                    }
                }.addOnFailureListener {
                    toast.setText("Ada kesalahan, silahkan coba lagi. ($it)")
                    toast.show()
                }
        }

        binding.fabDownvote.setOnClickListener {
            toast.setText("Anda sudah menolak.")
            toast.show()
            binding.chipDownvote.text =
                "${binding.chipDownvote.text.split(" ")[0].toLong() + 1} Menolak"
            binding.chipDownvote.setChipBackgroundColorResource(R.color.red)
            binding.fabUpvote.hide()
            binding.fabDownvote.hide()
            binding.fabParticipate.hide()

            val data = hashMapOf(
                "action" to "downvote",
                "demonstrationId" to demonstration.id
            )

            Firebase.functions("asia-southeast2").getHttpsCallable("demonstrationAction").call(data)
                .addOnSuccessListener {
                    if (!((it.data as HashMap<String, Any>)["success"] as Boolean)) {
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
        if (!demonstration.road_protests) binding.chipParticipant.visibility = View.GONE

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
                    ParticipationListBottomSheetDialog.TypeList.PARTICIPANT,
                    demonstration.id
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
                    ParticipationListBottomSheetDialog.TypeList.UPVOTE,
                    demonstration.id
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
                    ParticipationListBottomSheetDialog.TypeList.DOWNVOTE,
                    demonstration.id
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
                    ParticipationListBottomSheetDialog.TypeList.SHARE,
                    demonstration.id
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}