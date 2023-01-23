package com.pedulinegeri.unjukrasa.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.pedulinegeri.unjukrasa.demonstration.Demonstration

object DiffCalback: DiffUtil.ItemCallback<Demonstration>() {
    override fun areItemsTheSame(
        oldItem: Demonstration,
        newItem: Demonstration
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Demonstration,
        newItem: Demonstration
    ): Boolean {
        return oldItem == newItem
    }
}