package com.digitalsln.stanserhorn.ui.reservation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.digitalsln.stanserhorn.R
import com.digitalsln.stanserhorn.data.locale.entries.ReservationEntry
import com.digitalsln.stanserhorn.databinding.ItemReservationBinding
import com.digitalsln.stanserhorn.tools.DateUtils
import com.digitalsln.stanserhorn.tools.Logger

class ReservationAdapter : ListAdapter<ReservationEntry, ReservationAdapter.ReservationViewHolder>(DiffUtilItemCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReservationViewHolder {
        return ReservationViewHolder(
            ItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    class DiffUtilItemCallback : DiffUtil.ItemCallback<ReservationEntry>() {

        override fun areItemsTheSame(
            oldItem: ReservationEntry,
            newItem: ReservationEntry
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ReservationEntry,
            newItem: ReservationEntry
        ): Boolean = oldItem == newItem

    }

    class ReservationViewHolder(
        val binding: ItemReservationBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: ReservationEntry) {
            binding.run {
                val colorDrawable = getDrawableForTicketColorString(item.ticketColor)
                if (colorDrawable > -1) {
                    Glide.with(root.context)
                        .load(colorDrawable)
                        .circleCrop()
                        .into(marker)
                }

                val ascent = if ((item.id % 2).toInt() == 0) true else false
                destinationImg.setImageResource(if (ascent) R.drawable.ic_ascent_arrow else R.drawable.ic_descent_arrow)

                time.text = DateUtils.truncateSecondsFromTime(item.timeAscent)
                peopleCount.text = (item.numberAdults + item.numberKids + item.numberBabies + item.numberDisabled).toString()

                var descriptionStr: String = item.occasion
                if (descriptionStr == "") {
                    descriptionStr = item.agency
                } else if (item.agency != "") {
                    descriptionStr += ", ${item.agency}"
                }
                var nameString: String = item.guideFirstName
                if (nameString == "") {
                    nameString = item.guideLastName
                } else if (item.guideLastName != "") {
                    nameString += " ${item.guideLastName}"
                }
                if (descriptionStr == "") {
                    descriptionStr = nameString
                } else if (nameString != "") {
                    descriptionStr += " - $nameString"
                }
                description.text = descriptionStr
            }
        }

        private fun getDrawableForTicketColorString(ticketColorString: String): Int {
            return when (ticketColorString) {
                "keine" -> -1
                "blau" -> R.drawable.ticket_blau
                "blau-weiss" -> R.drawable.ticket_blauweiss
                "braun" -> R.drawable.ticket_braun
                "gelb" -> R.drawable.ticket_gelb
                "lila" -> R.drawable.ticket_lila
                "orange" -> R.drawable.ticket_orange
                "weiss" -> R.drawable.ticket_weiss
                else -> {
                    Logger.e("In ReservationAdapter: Cannot find ticket color drawable for ticket color string '$ticketColorString'.")
                    -1
                }
            }
        }
    }
}