package org.theromanticdev.unusualfitnessapp.presentation.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.SupportMapFragment
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.appComponent
import org.theromanticdev.unusualfitnessapp.databinding.FragmentWorkoutResultBinding
import org.theromanticdev.unusualfitnessapp.domain.util.MapCalculator
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.WorkoutViewModel
import org.theromanticdev.unusualfitnessapp.screenWidthInDP
import org.theromanticdev.unusualfitnessapp.util.GoogleMapDrawer
import java.text.DateFormat
import java.util.*

class WorkoutResultFragment : Fragment(R.layout.fragment_workout_result) {

    private var _binding: FragmentWorkoutResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var resultMapFragment: SupportMapFragment
    private val viewModel: WorkoutViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().appComponent.injectIntoWorkoutResultFragment(this)

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
            Log.e("the_romantic_dev", buildString {
                append("kilometers = $routeLengthKilometers")
                appendLine()
                append("meters = $routeLengthMeters")
            })
            binding.resultDistance.resultValue = "$routeLengthKilometers"
            binding.resultTime.resultValue = formattedTime
            val startDate = Date((startTime ?: 0L) * 1000L)
            val endDate = Date((endTime ?: 0L) * 1000L)
            binding.workoutDate.text = DateFormat.getDateInstance(DateFormat.SHORT).format(startDate)
            val formattedStartTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(startDate)
            val formattedEndTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(endDate)

            binding.workoutStartEndTime.text = "$formattedStartTime - $formattedEndTime"
        }
    }

    private fun showWorkoutRoute() {
        GoogleMapDrawer.drawPolyline(resultMapFragment, viewModel.workoutResult.route)
        val mapHeight = binding.resultMapFragment.layoutParams.height / requireActivity().resources.displayMetrics.density
        val minZoom = MapCalculator.minZoomForHeightAndWidth(
            metersHeight = viewModel.workoutResult.routeHeightMeters,
            metersWidth = viewModel.workoutResult.routeWidthMeters,
            heightPixels = mapHeight.toInt(),
            widthPixels = requireActivity().screenWidthInDP
        )

        GoogleMapDrawer.moveCameraAndZoom(
            resultMapFragment, viewModel.workoutResult.geometricCenterPoint, minZoom - 0.5f
        )
    }
}