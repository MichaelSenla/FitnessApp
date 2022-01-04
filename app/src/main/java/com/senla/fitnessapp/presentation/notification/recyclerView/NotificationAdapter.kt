package com.senla.fitnessapp.presentation.notification.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.senla.fitnessapp.data.database.models.Notification
import com.senla.fitnessapp.databinding.NotificationListItemBinding

class NotificationAdapter :
    ListAdapter<Notification, NotificationAdapter.ItemHolder>(ItemComparator()) {

    var listener: OnNotificationAdapterItemClickListener? = null

    class ItemHolder(private val binding: NotificationListItemBinding,
                     private val listener: OnNotificationAdapterItemClickListener?)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) = with(binding) {
            tvNotificationTitle.text = notification.title
            tvNotificationDateAndTime.text = notification.time
        }

        fun setListeners(notification: Notification) {
            with(binding) {
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener?.changeItem(adapterPosition, notification.id)
                    }
                }
                ivDeleteIcon.setOnClickListener {
                    listener?.deleteItem(notification) //getItem(adapterPosition)
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
                parent, false), listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
        holder.setListeners(getItem(position))
    }

    override fun submitList(list: MutableList<Notification>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    interface OnNotificationAdapterItemClickListener {
        fun deleteItem(notification: Notification)
        fun changeItem(position: Int, id: Int)
    }
}