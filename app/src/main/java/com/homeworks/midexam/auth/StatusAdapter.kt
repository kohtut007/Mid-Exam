package com.homeworks.midexam.auth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homeworks.midexam.R
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_status, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStatusText: TextView = itemView.findViewById(R.id.tvStatusText)
        private val tvStatusTime: TextView = itemView.findViewById(R.id.tvStatusTime)
        private val btnEdit: TextView = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: TextView = itemView.findViewById(R.id.btnDelete)

        fun bind(status: Status) {
            tvStatusText.text = status.statusText
            tvStatusTime.text = formatTime(status.createdAt)

            btnEdit.setOnClickListener { onEdit(status) }
            btnDelete.setOnClickListener { onDelete(status) }
        }

        private fun formatTime(timestamp: String): String {
            return "Just now"
        }
    }
}


