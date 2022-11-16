package org.theromanticdev.unusualfitnessapp.presentation.view.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.SupportMapFragment
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.appComponent
import org.theromanticdev.unusualfitnessapp.databinding.FragmentWorkoutResultBinding
import org.theromanticdev.unusualfitnessapp.domain.models.WorkoutInfo
import org.theromanticdev.unusualfitnessapp.domain.usecases.SaveWorkoutInfoIntoRepositoryUseCase
import org.theromanticdev.unusualfitnessapp.domain.util.MapCalculator
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.WorkoutViewModel
import org.theromanticdev.unusualfitnessapp.util.*
import org.theromanticdev.unusualfitnessapp.util.singletones.GoogleMapDrawer
import org.theromanticdev.unusualfitnessapp.util.singletones.WorkoutInfoFormatter
import java.text.DateFormat
import javax.inject.Inject

class WorkoutResultFragment : Fragment(R.layout.fragment_workout_result) {

    @Inject
    lateinit var saveWorkoutInfoIntoRepositoryUseCase: SaveWorkoutInfoIntoRepositoryUseCase

    private var _binding: FragmentWorkoutResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var resultMapFragment: SupportMapFragment
    private val workoutViewModel: WorkoutViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().appComponent.inject(this)

    }

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
        with(workoutViewModel.currentWorkoutInfoHandler) {
            binding.resultDistance.resultValue = "${routeLengthMeters / 10 / 100.0}"
            binding.resultTime.resultValue = WorkoutInfoFormatter.formatDuration(timer.value!!)

            binding.workoutDate.text = WorkoutInfoFormatter.formatDate(startTime, DateFormat.SHORT)
            val formattedStartTime = WorkoutInfoFormatter.formatTime(startTime, DateFormat.SHORT)
            val formattedEndTime = WorkoutInfoFormatter.formatTime(endTime, DateFormat.SHORT)

            binding.workoutStartEndTime.text = "$formattedStartTime - $formattedEndTime"



            binding.saveButton.setOnClickListener {
                with(workoutViewModel.currentWorkoutInfoHandler) {

                    resultMapFragment.getMapAsync { map ->
                        map.setOnMapLoadedCallback {
                            map.snapshot { bitmap ->
                                saveWorkoutInfoIntoRepositoryUseCase.execute(
                                    WorkoutInfo(
                                        type = type,
                                        startTime = startTime,
                                        finishTime = endTime,
                                        distance = routeLengthMeters,
                                        route = DatabaseInfoConverter.routeListToString(route),
                                        duration = timer.value!!,
                                        snapshot = DatabaseInfoConverter.snapshotBitmapToByteArray(bitmap!!),
                                        centerPoint = DatabaseInfoConverter.centerLatLngToString(
                                            workoutViewModel.currentWorkoutInfoHandler.geometricCenterPoint
                                        ),
                                        zoom = MapCalculator.minZoomForSquareMap(
                                            metersHeight = workoutViewModel.currentWorkoutInfoHandler.routeHeightMeters,
                                            metersWidth = workoutViewModel.currentWorkoutInfoHandler.routeWidthMeters,
                                            sideInPixels = requireActivity().screenWidthInDP
                                        )
                                    )
                                )
                                findNavController().popBackStack()
                            }

                        }
                    }
                }
            }
        }
    }

    private fun showWorkoutRoute() {
        GoogleMapDrawer.drawPolyline(resultMapFragment, workoutViewModel.currentWorkoutInfoHandler.route)

        val minZoom = MapCalculator.minZoomForSquareMap(
            metersHeight = workoutViewModel.currentWorkoutInfoHandler.routeHeightMeters,
            metersWidth = workoutViewModel.currentWorkoutInfoHandler.routeWidthMeters,
            sideInPixels = requireActivity().screenWidthInDP
        )

        GoogleMapDrawer.moveCameraAndZoom(
            resultMapFragment, workoutViewModel.currentWorkoutInfoHandler.geometricCenterPoint, minZoom - 0.5f
        )
    }


}