package com.senla.fitnessapp.presentation.notification.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senla.fitnessapp.data.database.models.Notification
import com.senla.fitnessapp.databinding.NotificationListItemBinding

class NotificationAdapter(val listener: OnNotificationAdapterItemClickListener) :
    ListAdapter<Notification, NotificationAdapter.ItemHolder>(ItemComparator()) {

    inner class ItemHolder(private val binding: NotificationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) = with(binding) {
            tvNotificationTitle.text = notification.title
            tvNotificationDateAndTime.text = notification.time
        }

        init {
            with(binding) {
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.changeItem(adapterPosition, getItem(adapterPosition).id)
                    }
                }
                ivDeleteIcon.setOnClickListener {
                    Log.e("ADAPTER", adapterPosition.toString())
                    Log.e("LISTSIZE", itemCount.toString())
                    listener.deleteItem(getItem(adapterPosition))
                }
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

        return ItemHolder(
            NotificationListItemBinding.inflate(LayoutInflater.from(parent.context),
                parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<Notification>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    interface OnNotificationAdapterItemClickListener {
        fun deleteItem(notification: Notification)
        fun changeItem(position: Int, id: Int)
    }
}