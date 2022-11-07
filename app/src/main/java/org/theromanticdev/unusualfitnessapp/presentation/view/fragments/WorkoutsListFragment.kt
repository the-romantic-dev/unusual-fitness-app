package org.theromanticdev.unusualfitnessapp.presentation.view.fragments

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.databinding.FragmentWorkoutsListBinding
import org.theromanticdev.unusualfitnessapp.domain.models.Point
import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo
import org.theromanticdev.unusualfitnessapp.presentation.view.adapters.WorkoutsListAdapter
import org.theromanticdev.unusualfitnessapp.util.WorkoutInfoFormatter


class WorkoutsListFragment : Fragment(R.layout.fragment_workouts_list) {

    private var _binding: FragmentWorkoutsListBinding? = null
    private val binding get() = _binding!!
    private val testList = lazy {
        listOf(
            WorkoutInfo(
                1, 13000, 14000, 1000, listOf(
                    Point(59.948514, 30.377725),
                    Point(59.948675, 30.369550),
                    Point(59.947010, 30.368241),
                    Point(59.944366, 30.368434),
                    Point(59.943990, 30.377596)
                ),900, WorkoutInfoFormatter.bitmapToByteArray(
                    BitmapFactory.decodeResource(requireActivity().resources, R.drawable.route))
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.workoutsList.adapter = WorkoutsListAdapter(testList.value)
        binding.workoutsList.layoutManager = LinearLayoutManager(requireActivity())

    }
}