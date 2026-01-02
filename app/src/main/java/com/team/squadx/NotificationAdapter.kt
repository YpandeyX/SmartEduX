package com.team.squadx

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.team.squadx.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val list: List<NotificationModel>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvTitle.text = item.title
        holder.binding.tvMessage.text = item.message
        holder.binding.tvTime.text = TimeUtils.getTimeAgo(item.timestamp)
    }


    override fun getItemCount(): Int = list.size
}
