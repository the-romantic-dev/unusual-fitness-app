package org.theromanticdev.unusualfitnessapp.presentation.view.fragments.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.theromanticdev.unusualfitnessapp.databinding.DialogWorkoutTypeSelectionBinding
import org.theromanticdev.unusualfitnessapp.presentation.view.adapters.WorkoutTypeSelectionListAdapter
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.WorkoutViewModel
import org.theromanticdev.unusualfitnessapp.util.workout.WorkoutTypes

class WorkoutTypeSelectionDialogFragment : DialogFragment() {

    private var _binding: DialogWorkoutTypeSelectionBinding? = null
    private val binding get() = _binding!!
    var itemClickListener: (Int) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWorkoutTypeSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.workoutTypeList.adapter =
            WorkoutTypeSelectionListAdapter(WorkoutTypes.instance.types, itemClickListener)
        binding.workoutTypeList.layoutManager = LinearLayoutManager(requireContext())
    }
}