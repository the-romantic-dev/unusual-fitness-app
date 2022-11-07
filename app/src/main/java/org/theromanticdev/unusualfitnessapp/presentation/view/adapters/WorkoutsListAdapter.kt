package org.theromanticdev.unusualfitnessapp.presentation.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.databinding.ViewWorkoutsListItemBinding
import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo
import org.theromanticdev.unusualfitnessapp.util.WorkoutInfoFormatter
import java.text.DateFormat

class WorkoutsListAdapter(private val workouts: List<WorkoutInfo>): RecyclerView.Adapter<WorkoutsListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding = ViewWorkoutsListItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_workouts_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with (holder.binding) {
            val info = workouts[position]
            workoutDate.text = WorkoutInfoFormatter.formatDate(info.startTime, DateFormat.MEDIUM)
            workoutDistance.text = WorkoutInfoFormatter.formatDistanceToKm(info.distance)
            workoutDuration.text = WorkoutInfoFormatter.formatDuration(info.duration)
            imageWorkoutType.setImageResource(WorkoutInfoFormatter.getResourceByWorkoutType(info.type))
            routeSnapshot.setImageBitmap(WorkoutInfoFormatter.byteArrayToBitmap(info.snapshot))
        }
    }

    override fun getItemCount() = workouts.size
}