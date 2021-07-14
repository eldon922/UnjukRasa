package com.pedulinegeri.unjukrasa.demonstration.person

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.databinding.AddPersonListItemBinding


class AddPersonListAdapter(private val dataSet: ArrayList<String>) :
    RecyclerView.Adapter<AddPersonListAdapter.ViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    inner class ViewHolder(private val binding: AddPersonListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(dataSet[absoluteAdapterPosition])
            }
        }

        fun bind(text: String) {
            binding.tvName.text = text
        }
    }

    fun addPerson(person: String) {
        dataSet.add(person)
        notifyItemInserted(itemCount - 1)
    }

    fun clear() {
        dataSet.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = AddPersonListItemBinding.inflate(
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