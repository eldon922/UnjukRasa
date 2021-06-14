package com.pedulinegeri.unjukrasa.demonstration.person

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedulinegeri.unjukrasa.databinding.AddPersonListItemBinding
import com.pedulinegeri.unjukrasa.databinding.PersonListItemBinding


class AddPersonListAdapter(private val dataSet: ArrayList<String>) :
    RecyclerView.Adapter<AddPersonListAdapter.ViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(private val binding: AddPersonListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(dataSet[adapterPosition])
            }
        }

        fun bind(text: String) {
            binding.tvName.text = text
        }
    }

    fun addPerson(person: String) {
        dataSet.add(person)
        notifyItemInserted(itemCount-1)
    }

    fun clear(){
        dataSet.clear()
        notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val binding = AddPersonListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(dataSet[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}