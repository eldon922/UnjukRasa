package com.pedulinegeri.unjukrasa.demonstration.progress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.ProgressListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationImageAdapter
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat


class ProgressListAdapter(private val navController: NavController, private val demonstrationId: String, private val uid: String, private val username: String) :
    RecyclerView.Adapter<ProgressListAdapter.ViewHolder>() {

    private var progressList = arrayListOf<Progress>()

    inner class ViewHolder(private val binding: ProgressListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(progress: Progress) {
            binding.tvName.text = username
            binding.textView7.text = uid
            binding.tvDate.text = SimpleDateFormat("dd MMM yyyy").format(progress.creationDate)
            binding.tvTime.text = SimpleDateFormat("hh:mm aa").format(progress.creationDate)

            val profileImageRef =
                Firebase.storage.reference.child("profile_picture/${uid}.png")

            profileImageRef.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(binding.ivPerson)
            }.addOnFailureListener {
                Picasso.get().load(R.drawable.no_img).into(binding.ivPerson)
            }

            binding.reContent.setEditorFontSize(16)
            binding.reContent.setEditorFontColor(binding.textView7.currentTextColor)
            binding.reContent.setEditorBackgroundColor(binding.root.solidColor)
            binding.reContent.setInputEnabled(false)
            binding.reContent.html = progress.description

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

                if (it.items.size == 0 && progress.youtube_video.isBlank()) {
                    binding.cvImages.visibility = View.GONE
                }
            }
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