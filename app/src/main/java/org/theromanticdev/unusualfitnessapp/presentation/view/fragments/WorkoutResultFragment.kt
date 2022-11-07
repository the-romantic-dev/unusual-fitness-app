package org.theromanticdev.unusualfitnessapp.presentation.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.SupportMapFragment
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.appComponent
import org.theromanticdev.unusualfitnessapp.databinding.FragmentWorkoutResultBinding
import org.theromanticdev.unusualfitnessapp.domain.util.MapCalculator
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.WorkoutViewModel
import org.theromanticdev.unusualfitnessapp.screenWidthInDP
import org.theromanticdev.unusualfitnessapp.util.GoogleMapDrawer
import org.theromanticdev.unusualfitnessapp.util.WorkoutInfoFormatter
import java.text.DateFormat
import java.util.*
import kotlin.math.round

class WorkoutResultFragment : Fragment(R.layout.fragment_workout_result) {

    private var _binding: FragmentWorkoutResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var resultMapFragment: SupportMapFragment
    private lateinit var navController: NavController
    private val viewModel: WorkoutViewModel by activityViewModels()

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


    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultMapFragment = childFragmentManager.findFragmentById(R.id.result_map_fragment) as SupportMapFragment
        showWorkoutRoute()
        with (viewModel.workoutResult) {
            binding.resultDistance.resultValue = "${round(routeLengthMeters / 100.0) / 10.0}"
            binding.resultTime.resultValue = formattedTime

            binding.workoutDate.text = WorkoutInfoFormatter.formatDate(startTime ?: 0L, DateFormat.SHORT)
            val formattedStartTime = WorkoutInfoFormatter.formatTime(startTime ?: 0L, DateFormat.SHORT)
            val formattedEndTime = WorkoutInfoFormatter.formatTime(endTime ?: 0L, DateFormat.SHORT)

            binding.workoutStartEndTime.text = "$formattedStartTime - $formattedEndTime"
            navController = findNavController()

            /*resultMapFragment.getMapAsync { map ->
                map.setOnMapLoadedCallback {
                    map.snapshot { bitmap ->
                        binding.rararara.setImageBitmap(bitmap!!)
                    }
                }
            }*/
        }
    }

    private fun showWorkoutRoute() {
        GoogleMapDrawer.drawPolyline(resultMapFragment, viewModel.workoutResult.route)
        val mapHeight = binding.resultMapFragment.layoutParams.height / requireActivity().resources.displayMetrics.density
        val minZoom = MapCalculator.minZoomForHeightAndWidth(
            metersHeight = viewModel.workoutResult.routeHeightMeters,
            metersWidth = viewModel.workoutResult.routeWidthMeters,
            pixelsHeight = mapHeight.toInt(),
            pixelsWidth = requireActivity().screenWidthInDP
        )

        GoogleMapDrawer.moveCameraAndZoom(
            resultMapFragment, viewModel.workoutResult.geometricCenterPoint, minZoom - 0.5f
        )
    }
}