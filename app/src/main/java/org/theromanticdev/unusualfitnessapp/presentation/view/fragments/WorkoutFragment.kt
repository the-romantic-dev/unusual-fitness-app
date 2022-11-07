package org.theromanticdev.unusualfitnessapp.presentation.view.fragments

import android.Manifest
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.appComponent
import org.theromanticdev.unusualfitnessapp.dagger.app.AppComponent
import org.theromanticdev.unusualfitnessapp.util.BroadcastReceiversCreator
import org.theromanticdev.unusualfitnessapp.databinding.FragmentWorkoutBinding
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.WorkoutViewModel
import org.theromanticdev.unusualfitnessapp.screenWidthInDP
import org.theromanticdev.unusualfitnessapp.services.WorkoutService
import org.theromanticdev.unusualfitnessapp.util.GoogleMapDrawer
import org.theromanticdev.unusualfitnessapp.util.IntentStrings
import org.theromanticdev.unusualfitnessapp.util.location.DeviceLocationManager
import org.theromanticdev.unusualfitnessapp.util.workout.WorkoutResult
import org.theromanticdev.unusualfitnessapp.util.workout.WorkoutStates
import javax.inject.Inject


class WorkoutFragment : Fragment(R.layout.fragment_workout) {

    @Inject
    lateinit var deviceLocationManager: DeviceLocationManager

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private val viewModel: WorkoutViewModel by activityViewModels()
    private lateinit var trainFragmentDaggerComponent: AppComponent

    private val locationSettingsRequest =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == -1) {
                lifecycleScope.launch {
                    if (!tryRequestLocationPermission()) showUserLocation()
                }
            }
        }

    private val locationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                lifecycleScope.launch {
                    if (!tryRequestLocationSettings()) showUserLocation()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        trainFragmentDaggerComponent = requireActivity().appComponent
        trainFragmentDaggerComponent.inject(viewModel)
        trainFragmentDaggerComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        if (viewModel.workoutResult.timer.value!! > 0) viewModel.workoutResult = WorkoutResult()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("the_romantic_dev", "Screen width is ${requireActivity().screenWidthInDP} dp")

        localBroadcastManager =
            LocalBroadcastManager.getInstance(requireActivity().applicationContext)

        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        binding.startWorkoutButton.setOnClickListener {
            lifecycleScope.launch {
                if (checkLocationConditions()) viewModel.workoutState.value = WorkoutStates.STARTED
            }
        }

        binding.pauseWorkoutButton.setOnClickListener {
            with (viewModel.workoutState) {
                if (value == WorkoutStates.STARTED ||
                    value == WorkoutStates.RESUMED
                ) {
                    value = WorkoutStates.PAUSED
                } else if (value == WorkoutStates.PAUSED) {
                    value = WorkoutStates.RESUMED
                }
            }
        }

        binding.stopWorkoutButton.setOnClickListener {
            viewModel.workoutState.value = WorkoutStates.STOPPED
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace<WorkoutResultFragment>(R.id.fragment_container_main)
                addToBackStack(null)
            }.commit()
        }
        viewModel.workoutState.observe(viewLifecycleOwner) { state ->
            val context = requireActivity()
            val intent = Intent(context, WorkoutService::class.java)
            when (state) {
                WorkoutStates.STARTED -> {
                    lifecycleScope.launch {
                        showUserLocation()
                    }
                    context.startService(intent)
                    viewModel.startTimer()
                    binding.startWorkoutButton.visibility = View.GONE
                    binding.stopAndPauseButtonsContainer.visibility = View.VISIBLE
                    viewModel.workoutResult.startTime = System.currentTimeMillis() / 1000
                }
                WorkoutStates.PAUSED -> {
                    viewModel.stopTimer()
                    binding.pauseWorkoutButton.setImageResource(R.drawable.workout_play_96)
                    localBroadcastManager.sendBroadcast(Intent(IntentStrings.WORKOUT_PAUSED_ACTION))
                    Log.e("the_romantic_dev", "Pause broadcast sent")
                }
                WorkoutStates.STOPPED -> {
                    context.stopService(intent)
                    viewModel.stopTimer()
                    viewModel.workoutResult.endTime = System.currentTimeMillis() / 1000
                }

                WorkoutStates.RESUMED -> {
                    binding.pauseWorkoutButton.setImageResource(R.drawable.workout_pause_96)
                    viewModel.startTimer()
                    localBroadcastManager.sendBroadcast(Intent(IntentStrings.WORKOUT_RESUMED_ACTION))
                    Log.e("the_romantic_dev", "ResumeFInt broadcast sent")
                }

                else -> {
                    throw NullPointerException("Workout state is null")
                }
            }
        }

        viewModel.workoutResult.timer.observe(viewLifecycleOwner) {
            binding.trainDuration.text = viewModel.workoutResult.formattedTime
        }

        registerServiceUpdatesReceiver()
    }


    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            if (checkLocationConditions()) showUserLocation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun showUserLocation() {
        val location = deviceLocationManager.getSingleLocation()
        if (location == null) toast("Can't get user location")
        else {
            with(GoogleMapDrawer) {
                drawMarker(
                    viewModel.userLocationBitmap,
                    mapFragment = mapFragment,
                    location = location
                )
                moveCameraAndZoom(mapFragment, location, 15f)
                //Binding may be null if coroutine is active while fragment view is destroyed.
                //It's happen when fragment replaced while geo is not continued, cause binding removed in onDestroyView
                if (_binding != null) {
                    binding.progressCircular.visibility = View.GONE
                    binding.mapFogging.visibility = View.GONE
                } else {
                    toast("Layout is not available")
                }

            }
        }
    }

    private fun tryRequestLocationPermission(): Boolean {
        val isGranted = deviceLocationManager.isPermissionGranted()
        if (!isGranted) {
            locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        return !isGranted
    }

    private suspend fun tryRequestLocationSettings(): Boolean {
        val settingsRequest = deviceLocationManager.getSettingsRequestOrNull()

        if (settingsRequest != null) {
            locationSettingsRequest.launch(settingsRequest)
        }
        return settingsRequest != null
    }

    private suspend fun checkLocationConditions(): Boolean {
        val isPermissionGranted = !tryRequestLocationPermission()
        val isSettingsEnabled = !tryRequestLocationSettings()
        return isSettingsEnabled && isPermissionGranted
    }

    private fun registerServiceUpdatesReceiver() {
        LocalBroadcastManager.getInstance(requireActivity().applicationContext).registerReceiver(
            BroadcastReceiversCreator.create { _, intent -> locationUpdatesCallback(intent)},
            IntentFilter(IntentStrings.USER_LOCATION_UPDATES_ACTION)
        )
    }

    private fun locationUpdatesCallback(intent: Intent) {
        val latitude = intent.extras!!.get(IntentStrings.LATITUDE_EXTRA) as Double
        val longitude = intent.extras!!.get(IntentStrings.LONGITUDE_EXTRA) as Double

        val lastLocation = LatLng(latitude, longitude)
        viewModel.workoutResult.addPointToRoute(lastLocation)
        with(GoogleMapDrawer) {
            drawPolyline(mapFragment, viewModel.workoutResult.route)
            drawMarker(viewModel.userLocationBitmap, mapFragment, lastLocation)
            moveCamera(mapFragment, lastLocation)
        }
    }

    private fun toast(text: String) {
        Toast.makeText(requireActivity(), text, Toast.LENGTH_LONG).show()
    }
}