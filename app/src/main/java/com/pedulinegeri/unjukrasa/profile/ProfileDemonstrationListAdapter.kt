package com.pedulinegeri.unjukrasa.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pedulinegeri.unjukrasa.MainFragmentDirections
import com.pedulinegeri.unjukrasa.databinding.MostActiveTodayDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.ProfileDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.RecommendedDemonstrationListItemBinding
import com.pedulinegeri.unjukrasa.databinding.TrendingDemonstrationListItemBinding
import com.squareup.picasso.Picasso

class ProfileDemonstrationListAdapter(
    private val dataSet: ArrayList<DemonstrationTitle>,
    private val viewType: ViewType,
    private val mainNavController: NavController
) :
    RecyclerView.Adapter<ProfileDemonstrationListAdapter.ViewHolder>() {

    enum class ViewType {
        TRENDING, MOST_ACTIVE, RECOMMENDED, PROFILE
    }

    inner class ViewHolder(private val binding: ViewBinding) :
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
                                .into((binding as ProfileDemonstrationListItemBinding).ivThumbnail)
                        }
                    }
                } else {
                    Picasso.get().load(demonstrationTitle.youtubeThumbnailUrl).into((binding as ProfileDemonstrationListItemBinding).ivThumbnail)
                }
            }

            binding.root.setOnClickListener {
                mainNavController.navigate(MainFragmentDirections.actionGlobalDemonstrationPageFragment(demonstrationTitle.id))
            }

            (binding as ProfileDemonstrationListItemBinding).tvTitle.text = demonstrationTitle.title
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = when (this.viewType) {
            ViewType.TRENDING -> TrendingDemonstrationListItemBinding.inflate(
                LayoutInflater.from(
                    viewGroup.context
                ), viewGroup, false
            )
            ViewType.MOST_ACTIVE -> MostActiveTodayDemonstrationListItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
            ViewType.RECOMMENDED -> RecommendedDemonstrationListItemBinding.inflate(
                LayoutInflater.from(
                    viewGroup.context
                ), viewGroup, false
            )
            ViewType.PROFILE -> ProfileDemonstrationListItemBinding.inflate(
                LayoutInflater.from(
                    viewGroup.context
                ), viewGroup, false
            )
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}