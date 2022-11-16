package org.theromanticdev.unusualfitnessapp.presentation.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.databinding.ItemWorkoutTypesSelectionListBinding
import org.theromanticdev.unusualfitnessapp.util.workout.WorkoutTypes

class WorkoutTypeSelectionListAdapter(
    private val types: List<WorkoutTypes.Type>,
    private val itemClickListener: (Int) -> Unit
) :
    RecyclerView.Adapter<WorkoutTypeSelectionListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemWorkoutTypesSelectionListBinding.bind(itemView)

        fun setListener(position: Int) {
            itemView.setOnClickListener {itemClickListener(position)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_types_selection_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            typeIcon.setImageResource(types[position].imageResource)
            typeName.text = types[position].name
        }
        holder.setListener(position)
    }

    override fun getItemCount() = types.size
}