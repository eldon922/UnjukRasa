package com.pedulinegeri.unjukrasa.home.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.databinding.MostUpvotedDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.Demonstration
import com.pedulinegeri.unjukrasa.home.DemonstrationOnClickListener
import com.squareup.picasso.Picasso

class MostUpvotedDemonstrationAdapter(
    private val demonstrationOnClickListener: DemonstrationOnClickListener
) :
    PagingDataAdapter<Demonstration, MostUpvotedDemonstrationAdapter.MostUpvotedDemonstrationViewHolder>(
        DiffCalback
    ) {

    inner class MostUpvotedDemonstrationViewHolder(
        val binding: MostUpvotedDemonstrationListItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: MostUpvotedDemonstrationViewHolder, position: Int) {
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
            holder.binding.tvDescription.text = Html.fromHtml(demonstration.description)
            holder.binding.tvNumber.text = (position + 1).toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MostUpvotedDemonstrationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MostUpvotedDemonstrationListItemBinding.inflate(layoutInflater, parent, false)
        return MostUpvotedDemonstrationViewHolder(binding)
    }
}