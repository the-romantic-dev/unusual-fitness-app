package org.theromanticdev.unusualfitnessapp.presentation.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.appComponent
import org.theromanticdev.unusualfitnessapp.dagger.trainFragment.TrainFragmentComponent
import org.theromanticdev.unusualfitnessapp.databinding.FragmentTrainBinding
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.TrainViewModel
import org.theromanticdev.unusualfitnessapp.services.TrainingService
import org.theromanticdev.unusualfitnessapp.util.GoogleMapDrawer
import org.theromanticdev.unusualfitnessapp.util.LocationSettingsChecker
import javax.inject.Inject


class TrainFragment : Fragment(R.layout.fragment_train) {

    @Inject
    lateinit var locationSettingsChecker: LocationSettingsChecker

    @Inject
    lateinit var googleMapDrawer: GoogleMapDrawer

    private var _binding: FragmentTrainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapFragment: SupportMapFragment

    private val viewModel: TrainViewModel by viewModels()
    private lateinit var trainFragmentDaggerComponent: TrainFragmentComponent

    private val locationUpdatesReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            Log.e("the_romantic_dev", "Get broadcast from TrainService")
            val latitude = intent.extras?.get("latitude") as Double?
                ?: throw NullPointerException("Can't get latitude from TrainService")
            val longitude = intent.extras?.get("longitude") as Double?
                ?: throw NullPointerException("Can't get longitude from TrainService")

            if (viewModel.lastLocation != null) {
                googleMapDrawer.drawPolyline(
                    mapFragment,
                    LatLng(viewModel.lastLocation!!.latitude, viewModel.lastLocation!!.longitude),
                    LatLng(latitude, longitude)
                )
                Log.e("the_romantic_dev", "Drew user route")
                //processUserGeo()
            }
            viewModel.lastLocation = LatLng(latitude, longitude)
            googleMapDrawer.showUserLocationOnMap(mapFragment, viewModel.lastLocation!!)
        }
    }

    private val locationSettingsRequest =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            Log.e("the_romantic_dev", "${it.resultCode}")
            if (it.resultCode == -1) {
                checkConditionsAndShowUserLocation()
            }

        }

    private val locationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                checkConditionsAndShowUserLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        trainFragmentDaggerComponent =
            this.requireActivity().appComponent.trainComponent()
                .trainFragment(this)
                .build()

        trainFragmentDaggerComponent.injectIntoViewModel(viewModel)
        trainFragmentDaggerComponent.injectIntoFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        binding.startTrainingButton.setOnClickListener {
            startTrainingButtonOnClick(this.requireActivity())
        }
        viewModel.trainButtonState.observe(viewLifecycleOwner) { isStart ->
            if (isStart) {
                binding.startTrainingButton.setImageResource(R.drawable.ic_play_96)
            } else {
                binding.startTrainingButton.setImageResource(R.drawable.ic_pause_96)
            }
        }

        viewModel.isLocationProvided.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressCircular.visibility = View.GONE
                binding.mapFogging.visibility = View.GONE
            } else {
                binding.progressCircular.visibility = View.VISIBLE
                binding.mapFogging.visibility = View.VISIBLE
            }
        }

        viewModel.timer.observe(viewLifecycleOwner) { time ->
            val hours = if (time / 3600 > 9) time / 3600 else "0${time / 3600}"
            val minutes = if (time / 60 % 60 > 9) time / 60 % 60 else "0${time / 60 % 60}"
            val seconds = if (time % 60 > 9) time % 60 else "0${time % 60}"
            binding.trainDuration.text = "$hours:$minutes:$seconds"
        }

        LocalBroadcastManager.getInstance(this.requireActivity()).registerReceiver(
            locationUpdatesReceiver,
            IntentFilter("UserLocationUpdates")
        )
    }

    override fun onStart() {
        super.onStart()
        Log.e("the_romantic_dev", "resm")
        checkConditionsAndShowUserLocation()
    }

    override fun onResume() {
        super.onResume()
        Log.e("the_romantic_dev", "resm")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkConditionsAndShowUserLocation() {
        locationSettingsChecker.check()
            .addOnSuccessListener {
                if (isLocationPermissionGranted()) {
                    lifecycleScope.launch {
                        val location = viewModel.getInitialLocation()
                        googleMapDrawer.showUserLocationOnMap(
                            mapFragment = mapFragment,
                            location = LatLng(location!!.latitude, location.longitude)
                        )
                    }
                } else requestLocationPermission()
            }.addOnFailureListener { exception ->
                if (exception is ApiException) {
                    when (exception.statusCode) {
                        CommonStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                val intentSenderRequest =
                                    IntentSenderRequest.Builder(exception.status.resolution!!)
                                        .build()
                                locationSettingsRequest.launch(intentSenderRequest)
                            } catch (sendEx: IntentSender.SendIntentException) {

                            }
                        }
                    }
                }
            }
    }

    private fun startTrainingButtonOnClick(context: Context) {
        val intent = Intent(context, TrainingService::class.java)
        val isButtonStart = viewModel.trainButtonState.value!!
        if (isButtonStart) {
            checkConditionsAndShowUserLocation()
            viewModel.trainButtonState.value = false
            context.startService(intent)
            viewModel.startTimer()
        } else {
            context.stopService(intent)
            viewModel.trainButtonState.value = true
            viewModel.stopTimer()
        }
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}