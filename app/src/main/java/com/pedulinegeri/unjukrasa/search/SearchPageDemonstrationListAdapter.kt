package com.pedulinegeri.unjukrasa.search

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.MainFragmentDirections
import com.pedulinegeri.unjukrasa.databinding.MostRecentCreatedDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.profile.DemonstrationTitle
import com.squareup.picasso.Picasso

class SearchPageDemonstrationListAdapter(
    private val dataSet: List<DemonstrationTitle>,
    private val mainNavController: NavController
) :
    RecyclerView.Adapter<SearchPageDemonstrationListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MostRecentCreatedDemonstrationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(demonstrationTitle: DemonstrationTitle) {
            val folderRef =
                Firebase.storage.reference.child("demonstration_image/${demonstrationTitle.id}")

            folderRef.listAll().addOnSuccessListener { folderResult ->
                if (folderResult.prefixes.size > 0) {
                    folderResult.prefixes[0].listAll().addOnSuccessListener {
                        val imageRef =
                            Firebase.storage.reference.child("demonstration_image/${demonstrationTitle.id}/${folderResult.prefixes[0].name}/${it.items[0].name}")

                        imageRef.downloadUrl.addOnSuccessListener {
                            Picasso.get().load(it)
                                .into(binding.ivThumbnail)
                        }
                    }
                } else {
                    Picasso.get()
                        .load(demonstrationTitle.youtubeThumbnailUrl)
                        .into(binding.ivThumbnail)
                }
            }

            binding.root.setOnClickListener {
                mainNavController.navigate(
                    MainFragmentDirections.actionGlobalDemonstrationPageFragment(
                        demonstrationTitle.id
                    )
                )
            }

            binding.tvTitle.text = demonstrationTitle.title
            binding.tvDescription.text = Html.fromHtml(demonstrationTitle.description)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = MostRecentCreatedDemonstrationListItemBinding.inflate(
            LayoutInflater.from(
                viewGroup.context
            ), viewGroup, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}