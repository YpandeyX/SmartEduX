package com.team.squadx

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team.squadx.databinding.ItemFacultyBinding

class FacultyAdapter(
    private val list: List<Faculty>,
    private val onClick: (Faculty) -> Unit
) : RecyclerView.Adapter<FacultyAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFacultyBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFacultyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val f = list[position]

        holder.binding.tvName.text = f.name
        holder.binding.tvDept.text = "${f.department} | ${f.subjects.joinToString(", ")}"

        Glide.with(holder.itemView.context)
            .load(f.photoUrl)
            .placeholder(R.drawable.ic_profile)
            .into(holder.binding.imgPhoto)

        holder.itemView.setOnClickListener { onClick(f) }

//        holder.itemView.apply {
//            alpha = 0f
//            translationY = 50f
//
//            animate()
//                .alpha(1f)
//                .translationY(0f)
//                .setDuration(500)
//                .setStartDelay(position * 10L)
//                .start()
//        }

    }

    override fun getItemCount() = list.size
}
