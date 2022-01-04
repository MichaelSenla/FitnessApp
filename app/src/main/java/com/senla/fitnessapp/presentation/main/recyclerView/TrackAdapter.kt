package com.senla.fitnessapp.presentation.main.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senla.fitnessapp.databinding.TrackListItemBinding
import com.senla.fitnessapp.presentation.main.models.Track

class TrackAdapter : ListAdapter<Track, TrackAdapter.ItemHolder>(ItemComparator()) {

    class ItemHolder(private val binding: TrackListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            with(binding) {
                tvDistance.text = track.distance
                tvStartTime.text = track.startTime
                tvJoggingTime.text = track.joggingTime.toString()
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<Track>() {

        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {

        return ItemHolder(TrackListItemBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
    }
}