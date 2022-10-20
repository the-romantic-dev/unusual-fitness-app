package org.theromanticdev.unusualfitnessapp.util

import android.graphics.Bitmap
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import javax.inject.Inject

class GoogleMapDrawer @Inject constructor(private val userMarkerBitmap: Bitmap) {

    private var userMarker: Marker? = null

    fun showUserLocationOnMap(
        mapFragment: SupportMapFragment, location: LatLng) {
        mapFragment.getMapAsync { googleMap ->
            val user = LatLng(location.latitude, location.longitude)

            if (userMarker != null) {
                userMarker!!.remove()
            }

            userMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(user)
                    .icon(BitmapDescriptorFactory.fromBitmap(userMarkerBitmap))
            )
            googleMap.setMinZoomPreference(15f)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(user))
        }
    }

    fun drawPolyline(mapFragment: SupportMapFragment, pos1: LatLng, pos2: LatLng) {
        mapFragment.getMapAsync { googleMap ->
            googleMap.addPolyline(
                PolylineOptions().add(
                    pos1,
                    pos2
                )
            )
        }
    }


}