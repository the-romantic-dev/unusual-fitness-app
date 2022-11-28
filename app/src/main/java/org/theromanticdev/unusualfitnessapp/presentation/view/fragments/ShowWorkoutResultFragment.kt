package org.theromanticdev.unusualfitnessapp.presentation.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.SupportMapFragment
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.databinding.FragmentWorkoutResultBinding
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.ShowResultViewModel
import org.theromanticdev.unusualfitnessapp.util.DatabaseInfoConverter
import org.theromanticdev.unusualfitnessapp.util.singletones.GoogleMapDrawer
import org.theromanticdev.unusualfitnessapp.util.singletones.WorkoutInfoFormatter
import java.text.DateFormat

class ShowWorkoutResultFragment : Fragment(R.layout.fragment_workout_result) {

    private var _binding: FragmentWorkoutResultBinding? = null
    private val binding get() = _binding!!

    private val showResultViewModel: ShowResultViewModel by activityViewModels()

    private lateinit var resultMapFragment: SupportMapFragment


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultMapFragment =
            childFragmentManager.findFragmentById(R.id.result_map_fragment) as SupportMapFragment

        showWorkoutRoute()

        binding.resultDistance.resultValue =
            "${showResultViewModel.workoutInfo!!.distance / 10 / 100.0}"

        binding.resultTime.resultValue =
            WorkoutInfoFormatter.formatDuration(showResultViewModel.workoutInfo!!.duration)

        binding.workoutDate.text =
            WorkoutInfoFormatter.formatDate(
                showResultViewModel.workoutInfo!!.startTime,
                DateFormat.SHORT
            )

        val formattedStartTime = WorkoutInfoFormatter.formatTime(
            showResultViewModel.workoutInfo!!.startTime,
            DateFormat.SHORT
        )
        val formattedEndTime = WorkoutInfoFormatter.formatTime(
            showResultViewModel.workoutInfo!!.finishTime,
            DateFormat.SHORT
        )

        binding.workoutStartEndTime.text = "$formattedStartTime - $formattedEndTime"

        binding.buttonContainer.visibility = View.GONE

    }

    private fun showWorkoutRoute() {
        GoogleMapDrawer.drawPolyline(
            resultMapFragment,
            DatabaseInfoConverter.routeStringToLatLngList(showResultViewModel.workoutInfo!!.route)
        )

        GoogleMapDrawer.moveCameraAndZoom(
            resultMapFragment,
            DatabaseInfoConverter.centerStringToLatLng(showResultViewModel.workoutInfo!!.centerPoint),
            showResultViewModel.workoutInfo!!.zoom - 0.5f
        )
    }



}