package com.digitalsln.stanserhorn.ui.daily_menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digitalsln.stanserhorn.data.locale.entries.DailyMenuEntry
import com.digitalsln.stanserhorn.data.locale.entries.InfoBoardEntry
import com.digitalsln.stanserhorn.databinding.ItemDailyMenuBinding
import com.digitalsln.stanserhorn.databinding.ItemInfoBoardBinding

class DailyMenuAdapter: ListAdapter<DailyMenuEntry, DailyMenuAdapter.DailyMenuViewHolder>(DailyMenuDiffUtilItemCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyMenuViewHolder {
        return DailyMenuViewHolder(
            ItemDailyMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: DailyMenuViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    class DailyMenuDiffUtilItemCallback : DiffUtil.ItemCallback<DailyMenuEntry>() {

        override fun areItemsTheSame(
            oldItem: DailyMenuEntry,
            newItem: DailyMenuEntry
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: DailyMenuEntry,
            newItem: DailyMenuEntry
        ): Boolean = oldItem == newItem

    }

    class DailyMenuViewHolder(
        val binding: ItemDailyMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: DailyMenuEntry) {
            binding.run {
                title.text = item.title
                description.text = item.text
            }
        }
    }
}