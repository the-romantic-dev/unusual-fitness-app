package org.theromanticdev.unusualfitnessapp.presentation.view.fragments

import android.Manifest
import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.appComponent
import org.theromanticdev.unusualfitnessapp.dagger.app.AppComponent
import org.theromanticdev.unusualfitnessapp.databinding.FragmentWorkoutBinding
import org.theromanticdev.unusualfitnessapp.domain.util.MapCalculator
import org.theromanticdev.unusualfitnessapp.presentation.view.fragments.dialog.WorkoutTypeSelectionDialogFragment
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.WorkoutViewModel
import org.theromanticdev.unusualfitnessapp.services.WorkoutService
import org.theromanticdev.unusualfitnessapp.util.*
import org.theromanticdev.unusualfitnessapp.util.location.DeviceLocationManager
import org.theromanticdev.unusualfitnessapp.util.singletones.*
import org.theromanticdev.unusualfitnessapp.util.workout.CurrentWorkoutInfoHandler
import org.theromanticdev.unusualfitnessapp.util.workout.WorkoutTypes
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class WorkoutFragment : Fragment() {

    private val workoutViewModel: WorkoutViewModel by activityViewModels()
    /*private val sharedWorkoutInfoViewModel: SharedWorkoutInfoViewModel by activityViewModels()*/
    lateinit var daggerComponent: AppComponent


    @Inject
    lateinit var deviceLocationManager: DeviceLocationManager

    private val userMarkerBitmap: Lazy<Bitmap> = lazy {
        requireActivity().getVectorResourceAsScaledBitmap(
            R.drawable.map_user_location,
            96, 96
        )
    }

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapFragment: SupportMapFragment


    private lateinit var localBroadcastManager: LocalBroadcastManager

    private val serviceIntent by lazy { Intent(requireActivity(), WorkoutService::class.java) }

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
        daggerInject()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        if (workoutViewModel.currentWorkoutInfoHandler.timer.value!! > 0) workoutViewModel.currentWorkoutInfoHandler =
            CurrentWorkoutInfoHandler()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localBroadcastManager =
            LocalBroadcastManager.getInstance(requireActivity().applicationContext)

        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        binding.startWorkoutButton.setOnClickListener {
            lifecycleScope.launch {
                if (checkLocationConditions()) workoutViewModel.workoutState.value = WorkoutStates.STARTED
            }
        }

        binding.pauseWorkoutButton.setOnClickListener {
            with(workoutViewModel.workoutState) {
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
            workoutViewModel.workoutState.value = WorkoutStates.STOPPED
            /*updateWorkoutInfoForResultFragment()*/
            val action = WorkoutFragmentDirections.actionWorkoutFragmentToWorkoutResultFragment()
            findNavController().navigate(action)
        }

        binding.selectWorkoutTypeFab.setOnClickListener {
            val dialog = WorkoutTypeSelectionDialogFragment()
            dialog.itemClickListener = { pos ->
                binding.selectWorkoutTypeFab.setImageResource(WorkoutTypes.instance.types[pos].imageResource)
                workoutViewModel.currentWorkoutInfoHandler.type = pos
                dialog.dismiss()
            }
            dialog.show(childFragmentManager, "Type selection dialog")
        }

        workoutViewModel.workoutState.observe(viewLifecycleOwner) { state ->
            when (state) {
                WorkoutStates.STARTED -> startWorkout()
                WorkoutStates.PAUSED -> pauseWorkout()
                WorkoutStates.STOPPED -> stopWorkout()
                WorkoutStates.RESUMED -> resumeWorkout()
                else -> requireActivity().toast("WORKOUT STATE ERROR")
            }
        }

        workoutViewModel.currentWorkoutInfoHandler.timer.observe(viewLifecycleOwner) {
            val formattedTime =
                WorkoutInfoFormatter.formatDuration(workoutViewModel.currentWorkoutInfoHandler.timer.value!!)
            binding.trainDuration.text = formattedTime
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


    private fun daggerInject() {
        daggerComponent = requireActivity().appComponent
        daggerComponent.inject(workoutViewModel)
        daggerComponent.inject(this)
    }


    private suspend fun showUserLocation() {
        val location = deviceLocationManager.getSingleLocation()
        if (location == null) requireContext().toast("Can't get user location")
        else {

            with(GoogleMapDrawer) {
                drawMarker(
                    userMarkerBitmap.value,
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
                    requireContext().toast("Layout is not available")
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
            BroadcastReceiversCreator.create { _, intent -> locationUpdatesCallback(intent) },
            IntentFilter(IntentStrings.USER_LOCATION_UPDATES_ACTION)
        )
    }

    private fun locationUpdatesCallback(intent: Intent) {
        val latitude = intent.extras!!.get(IntentStrings.LATITUDE_EXTRA) as Double
        val longitude = intent.extras!!.get(IntentStrings.LONGITUDE_EXTRA) as Double

        val lastLocation = LatLng(latitude, longitude)
        workoutViewModel.currentWorkoutInfoHandler.addPointToRoute(lastLocation)
        with(GoogleMapDrawer) {
            drawPolyline(mapFragment, workoutViewModel.currentWorkoutInfoHandler.route)
            drawMarker(userMarkerBitmap.value, mapFragment, lastLocation)
            moveCamera(mapFragment, lastLocation)
        }
    }

    suspend fun makeSnapshot(mapFragment: SupportMapFragment): Bitmap {
        val zoom = calculateZoom()
        val location = workoutViewModel.currentWorkoutInfoHandler.geometricCenterPoint

        return suspendCoroutine { continuation ->
            mapFragment.getMapAsync { map ->
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
                map.setOnMapLoadedCallback {
                    map.snapshot { bitmap -> continuation.resume(bitmap!!) }
                }
            }
        }
    }

    private fun calculateZoom(): Float {
        with (workoutViewModel.currentWorkoutInfoHandler) {
            return MapCalculator.minZoomForSquareMap(
                routeHeightMeters,
                routeWidthMeters,
                requireActivity().screenWidthInDP
            )
        }
    }

    private fun startWorkout() {
        lifecycleScope.launch {
            showUserLocation()
        }
        requireActivity().startService(serviceIntent)
        workoutViewModel.startTimer()
        binding.startWorkoutButton.visibility = View.GONE
        binding.stopAndPauseButtonsContainer.visibility = View.VISIBLE
        workoutViewModel.currentWorkoutInfoHandler.startTime =
            System.currentTimeMillis() / 1000
    }

    private fun pauseWorkout() {
        workoutViewModel.stopTimer()
        binding.pauseWorkoutButton.setImageResource(R.drawable.workout_play)
        localBroadcastManager.sendBroadcast(Intent(IntentStrings.WORKOUT_PAUSED_ACTION))
    }

    private fun stopWorkout() {
        requireActivity().stopService(serviceIntent)
        workoutViewModel.stopTimer()
        workoutViewModel.currentWorkoutInfoHandler.endTime = System.currentTimeMillis() / 1000
    }

    private fun resumeWorkout() {
        binding.pauseWorkoutButton.setImageResource(R.drawable.workout_pause)
        workoutViewModel.startTimer()
        localBroadcastManager.sendBroadcast(Intent(IntentStrings.WORKOUT_RESUMED_ACTION))
    }


}