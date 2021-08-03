package com.pedulinegeri.unjukrasa.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.NotificationListItemBinding
import com.pedulinegeri.unjukrasa.demonstration.DemonstrationImageAdapter


class NotificationListAdapter(private val dataSet: List<String>, private val fragmentManager: FragmentManager, private val navController: NavController) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: NotificationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            val adapter = DemonstrationImageAdapter(fragmentManager, navController)
            binding.vpImages.adapter = adapter
            adapter.initDemonstrationImageList(
                arrayListOf(
                    "https://www.youtube.com/watch?v=G7H9uo3j5FQ".takeLast(11),
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/320px-Image_created_with_a_mobile_phone.png",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/320px-Image_created_with_a_mobile_phone.png",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/320px-Image_created_with_a_mobile_phone.png",
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/320px-Image_created_with_a_mobile_phone.png"
                )
            )
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotificationListItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size

}