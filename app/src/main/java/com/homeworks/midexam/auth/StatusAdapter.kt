package com.homeworks.midexam.auth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homeworks.midexam.databinding.ItemStatusBinding
import com.homeworks.midexam.models.Status

class StatusAdapter(
    private val onEdit: (Status) -> Unit,
    private val onDelete: (Status) -> Unit
) : ListAdapter<Status, StatusAdapter.StatusViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Status>() {
        override fun areItemsTheSame(oldItem: Status, newItem: Status): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Status, newItem: Status): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val binding = ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StatusViewHolder(private val binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(status: Status) {
            binding.tvStatusText.text = status.statusText
            binding.tvStatusTime.text = formatTime(status.createdAt)

            binding.btnEdit.setOnClickListener { onEdit(status) }
            binding.btnDelete.setOnClickListener { onDelete(status) }
        }

        private fun formatTime(timestamp: Long): String {
            return "Just now"  // just for show
        }
    }
}