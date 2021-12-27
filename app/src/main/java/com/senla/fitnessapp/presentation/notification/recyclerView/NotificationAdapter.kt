package com.senla.fitnessapp.presentation.notification.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senla.fitnessapp.databinding.NotificationListItemBinding
import com.senla.fitnessapp.presentation.notification.models.Notification

class NotificationAdapter: ListAdapter<Notification,
        NotificationAdapter.ItemHolder>(ItemComparator()) {

    class ItemHolder(private val binding: NotificationListItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) = with(binding) {
                tvNotificationTitle.text = notification.title
                tvNotificationDateAndTime.text = notification.time
        }

        companion object {

            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(NotificationListItemBinding.inflate(LayoutInflater
                    .from(parent.context), parent, false))
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
    }
}