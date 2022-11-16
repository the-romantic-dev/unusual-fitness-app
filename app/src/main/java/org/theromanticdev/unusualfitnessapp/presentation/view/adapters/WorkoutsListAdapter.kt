package org.theromanticdev.unusualfitnessapp.presentation.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.databinding.ItemWorkoutsListBinding
import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo
import org.theromanticdev.unusualfitnessapp.util.DatabaseInfoConverter
import org.theromanticdev.unusualfitnessapp.util.singletones.WorkoutInfoFormatter
import org.theromanticdev.unusualfitnessapp.util.workout.WorkoutTypes
import java.text.DateFormat

class WorkoutsListAdapter(
    private val workouts: Map<Int, WorkoutInfo>,
    private val onItemClickListener: (WorkoutInfo) -> Unit,
    private val onItemLongClickListener: (Int) -> Unit
) : RecyclerView.Adapter<WorkoutsListAdapter.ViewHolder>() {

    private val workoutsIds = workouts.keys.sorted()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemWorkoutsListBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_workouts_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = workouts[workoutsIds[position]]!!
        with(holder.binding) {
            workoutDate.text = WorkoutInfoFormatter.formatDate(info.startTime, DateFormat.MEDIUM)
            workoutDistance.text = WorkoutInfoFormatter.formatDistanceToKm(info.distance)
            workoutDuration.text = WorkoutInfoFormatter.formatDuration(info.duration)
            imageWorkoutType.setImageResource(WorkoutTypes.instance.types[info.type].imageResource)
            routeSnapshot.setImageBitmap(DatabaseInfoConverter.snapshotByteArrayToBitmap(info.snapshot))
        }

        holder.itemView.setOnClickListener { onItemClickListener(info) }
        holder.itemView.setOnLongClickListener { onItemLongClickListener(workoutsIds[position]); true }
    }

    override fun getItemCount() = workoutsIds.size
}