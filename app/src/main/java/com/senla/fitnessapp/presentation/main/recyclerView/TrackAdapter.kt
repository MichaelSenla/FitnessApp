package com.senla.fitnessapp.presentation.main.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senla.fitnessapp.databinding.TrackListItemBinding
import com.senla.fitnessapp.presentation.main.models.RecyclerViewTrack

class TrackAdapter : ListAdapter<RecyclerViewTrack, TrackAdapter.ItemHolder>(ItemComparator()) {

    class ItemHolder(private val binding: TrackListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recyclerViewTrack: RecyclerViewTrack) {
            with(binding) {
                tvDistance.text = recyclerViewTrack.distance
                tvStartTime.text = recyclerViewTrack.startTime
                tvJoggingTime.text = recyclerViewTrack.joggingTime.toString()
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<RecyclerViewTrack>() {

        override fun areItemsTheSame(
            oldItem: RecyclerViewTrack, newItem: RecyclerViewTrack): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RecyclerViewTrack, newItem: RecyclerViewTrack): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {

        return ItemHolder(
            TrackListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
    }
}