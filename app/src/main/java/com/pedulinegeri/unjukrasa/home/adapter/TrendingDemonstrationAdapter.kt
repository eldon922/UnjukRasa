package com.pedulinegeri.unjukrasa.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.databinding.TrendingDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.Demonstration
import com.pedulinegeri.unjukrasa.home.DemonstrationOnClickListener
import com.squareup.picasso.Picasso

class TrendingDemonstrationAdapter(
    private val demonstrationOnClickListener: DemonstrationOnClickListener
) :
    PagingDataAdapter<Demonstration, TrendingDemonstrationAdapter.TrendingDemonstrationViewHolder>(
        DiffCalback
    ) {

    inner class TrendingDemonstrationViewHolder(
        val binding: TrendingDemonstrationListItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: TrendingDemonstrationViewHolder, position: Int) {
        getItem(position)?.let { demonstration ->
            val folderRef =
                Firebase.storage.reference.child("demonstration_image/${demonstration.id}")

            folderRef.listAll().addOnSuccessListener { folderResult ->
                if (folderResult.prefixes.size > 0) {
                    folderResult.prefixes[0].listAll().addOnSuccessListener { listResult ->
                        val imageRef =
                            Firebase.storage.reference.child("demonstration_image/${demonstration.id}/${folderResult.prefixes[0].name}/${listResult.items[0].name}")

                        imageRef.downloadUrl.addOnSuccessListener {
                            Picasso.get().load(it)
                                .into(
                                    holder.binding.ivThumbnail
                                )
                        }
                    }
                } else {
                    Picasso.get()
                        .load("http://img.youtube.com/vi/${demonstration.youtubeVideo}/0.jpg")
                        .into(
                            holder.binding.ivThumbnail
                        )
                }
            }


            holder.binding.root.setOnClickListener {
                demonstrationOnClickListener.onClick(demonstration.id)
            }


            holder.binding.tvTitle.text = demonstration.title
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrendingDemonstrationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TrendingDemonstrationListItemBinding.inflate(layoutInflater, parent, false)
        return TrendingDemonstrationViewHolder(binding)
    }
}