package com.education4all

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.education4all.TourAdapter.TourViewHolder
import com.education4all.mathCoachAlg.tours.Tour

class TourAdapter(private val tours: ArrayList<Tour>, var context: Context) :
    RecyclerView.Adapter<TourViewHolder>() {
    private var listener: OnUserClickListener? = null

    interface OnUserClickListener {
        fun onUserClick(position: Int)
    }

    fun setOnUserClickListener(listener: OnUserClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TourViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.tourblock, viewGroup, false)
        return TourViewHolder(view, listener)
    }

    override fun onBindViewHolder(tourViewHolder: TourViewHolder, tourNumber: Int) {
        val tour = tours[tourNumber]
        var info = tour.info()
        val datetime = tour.dateTime()
        if (tour.rightTasks == tour.totalTasks) info = context.getString(R.string.star) + " " + info
        val tourdatetime = tourViewHolder.tourdatetime
        tourdatetime.id = tourNumber + 1
        tourdatetime.text = datetime
        tourdatetime.tag = tourNumber
        val tourinfo = tourViewHolder.tourinfo
        tourinfo.id = tourNumber
        tourinfo.text = info
        tourinfo.tag = tourNumber
        val arrow = tourViewHolder.arrow
        arrow.tag = tourNumber
    }

    override fun getItemCount(): Int {
        return tours.size
    }

    class TourViewHolder(itemView: View, listener: OnUserClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var tourdatetime: TextView
        var tourinfo: TextView
        var arrow: Button

        init {
            tourdatetime = itemView.findViewById(R.id.tourdatetime)
            tourinfo = itemView.findViewById(R.id.tourinfo)
            arrow = itemView.findViewById(R.id.arrow)
            itemView.setOnClickListener { v: View? ->
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onUserClick(position)
                    }
                }
            }
        }
    }
}