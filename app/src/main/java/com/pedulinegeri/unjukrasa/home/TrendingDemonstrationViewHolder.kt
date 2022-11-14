package com.pedulinegeri.unjukrasa.home

import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.MainFragmentDirections
import com.pedulinegeri.unjukrasa.databinding.TrendingDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.Demonstration
import com.squareup.picasso.Picasso


class TrendingDemonstrationViewHolder(
    private val binding: TrendingDemonstrationListItemBinding,
    private val mainNavController: NavController
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(demonstration: Demonstration) {
        val folderRef =
            Firebase.storage.reference.child("demonstration_image/${demonstration.id}")

        folderRef.listAll().addOnSuccessListener { folderResult ->
            if (folderResult.prefixes.size > 0) {
                folderResult.prefixes[0].listAll().addOnSuccessListener {
                    val imageRef =
                        Firebase.storage.reference.child("demonstration_image/${demonstration.id}/${folderResult.prefixes[0].name}/${it.items[0].name}")

                    imageRef.downloadUrl.addOnSuccessListener {
                        Picasso.get().load(it)
                            .into(binding.ivThumbnail)
                    }
                }
            } else {
                Picasso.get()
                    .load("http://img.youtube.com/vi/${demonstration.youtubeVideo}/0.jpg")
                    .into(binding.ivThumbnail)
            }
        }

        binding.root.setOnClickListener {
            mainNavController.navigate(
                MainFragmentDirections.actionGlobalDemonstrationPageFragment(
                    demonstration.id
                )
            )
        }

        binding.tvTitle.text = demonstration.title
    }
}