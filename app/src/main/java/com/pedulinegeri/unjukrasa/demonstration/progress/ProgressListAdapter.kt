package com.pedulinegeri.unjukrasa.demonstration.progress

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.ProgressListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationImageAdapter
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class ProgressListAdapter(
    private val navController: NavController,
    private val demonstrationId: String,
    private val uid: String,
    private val username: String
) :
    RecyclerView.Adapter<ProgressListAdapter.ViewHolder>() {

    private var progressList = arrayListOf<Progress>()

    inner class ViewHolder(private val binding: ProgressListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(progress: Progress) {
            binding.tvName.text = username
            binding.tvDate.text =
                SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(progress.creationDate)
            binding.tvTime.text =
                SimpleDateFormat("hh:mm aa", Locale.ENGLISH).format(progress.creationDate)

            val profileImageRef =
                Firebase.storage.reference.child("profile_picture/${uid}.png")

            profileImageRef.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(binding.ivPerson)
            }.addOnFailureListener {
                Picasso.get().load(R.drawable.profile_avatar_placeholder_large)
                    .into(binding.ivPerson)
            }

            binding.tvContent.text = Html.fromHtml(progress.description)

            val adapter = DemonstrationImageAdapter(navController)
            binding.vpImages.adapter = adapter

            if (progress.youtube_video.isNotBlank()) {
                adapter.addImageOrVideo(progress.youtube_video)
            }

            val imageRef =
                Firebase.storage.reference.child("progress_image/$demonstrationId/$absoluteAdapterPosition/$uid")

            imageRef.listAll().addOnSuccessListener {
                it.items.forEach {
                    it.downloadUrl.addOnSuccessListener { adapter.addImageOrVideo(it.toString()) }
                }

                if (progress.youtube_video.isBlank()) {
                    if (it.items.size == 0) {
                        binding.cvImages.visibility = View.GONE
                    } else if (it.items.size > 1) {
                        binding.intoTabLayout.visibility = View.VISIBLE
                    }
                }
            }

            TabLayoutMediator(binding.intoTabLayout, binding.vpImages) { _, _ -> }.attach()
            binding.vpImages.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if (adapter.itemCount > 1 || (adapter.itemCount > 0 && progress.youtube_video.isNotBlank())) {
                        if (position == 0 && progress.youtube_video.isNotBlank()) {
                            binding.intoTabLayout.visibility = View.GONE
                        } else {
                            binding.intoTabLayout.visibility = View.VISIBLE
                        }
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProgressListItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(progressList[position])
    }

    override fun getItemCount() = progressList.size

    fun initProgressList(progressList: ArrayList<Progress>) {
        this.progressList = progressList
        notifyDataSetChanged()
    }
}