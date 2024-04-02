package com.digitalsln.stanserhorn.ui.trip_log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digitalsln.stanserhorn.data.locale.entries.TripLogEntry
import com.digitalsln.stanserhorn.databinding.ItemTripLogBinding
import com.digitalsln.stanserhorn.tools.DateUtils

class TripLogAdapter(private val onLongClickItem: (TripLogEntry) -> Unit = {}) : ListAdapter<TripLogEntry, TripLogAdapter.TripLogViewHolder>(DiffUtilItemCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripLogViewHolder {
        return TripLogViewHolder(
            ItemTripLogBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: TripLogViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
        holder.itemView.setOnLongClickListener { onLongClickItem.invoke(item); true }
    }

    class DiffUtilItemCallback : DiffUtil.ItemCallback<TripLogEntry>() {

        override fun areItemsTheSame(
            oldItem: TripLogEntry,
            newItem: TripLogEntry
        ): Boolean = oldItem.globeId == newItem.globeId

        override fun areContentsTheSame(
            oldItem: TripLogEntry,
            newItem: TripLogEntry
        ): Boolean = oldItem == newItem

    }

    class TripLogViewHolder(
        val binding: ItemTripLogBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: TripLogEntry) {
            binding.run {
                idValue.text = item.tripOfDay.toString()
                dateValue.text = DateUtils.shortenDate(item.date)
                timeValue.text = DateUtils.truncateSecondsFromTime(item.time)
                nameValue.text = if (item.ascent) "Bergfahrt" else "Talfahrt"
                peopleValue.text = item.numberPassengers.toString()
            }
        }
    }
}