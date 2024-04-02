package com.digitalsln.stanserhorn.ui.info_board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digitalsln.stanserhorn.data.locale.entries.InfoBoardEntry
import com.digitalsln.stanserhorn.databinding.ItemInfoBoardBinding

class InfoBoardAdapter : ListAdapter<InfoBoardEntry, InfoBoardAdapter.InfoBoardViewHolder>(InfoBoardDiffUtilItemCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InfoBoardViewHolder {
        return InfoBoardViewHolder(
            ItemInfoBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: InfoBoardViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    class InfoBoardDiffUtilItemCallback : DiffUtil.ItemCallback<InfoBoardEntry>() {

        override fun areItemsTheSame(
            oldItem: InfoBoardEntry,
            newItem: InfoBoardEntry
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: InfoBoardEntry,
            newItem: InfoBoardEntry
        ): Boolean = oldItem == newItem

    }

    class InfoBoardViewHolder(
        val binding: ItemInfoBoardBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: InfoBoardEntry) {
            binding.run {
                dateCreated.text = item.dateCreated
                dateFrom.text = item.from
                dateTo.text = item.until
                description.text = item.message
                authorName.text = item.creator
            }
        }
    }
}