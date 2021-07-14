package com.pedulinegeri.unjukrasa.demonstration.person

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.databinding.RemovePersonListItemBinding


class RemovePersonListAdapter(
    private val dataSet: ArrayList<String>
) :
    RecyclerView.Adapter<RemovePersonListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: RemovePersonListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                binding.checkBox.toggle()
            }
        }

        fun bind(text: String) {
            if (text.isNotEmpty()) {
                binding.chipRole.text = text
            } else {
                binding.chipRole.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RemovePersonListItemBinding.inflate(
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