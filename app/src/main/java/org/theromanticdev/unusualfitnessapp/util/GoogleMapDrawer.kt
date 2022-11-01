package org.theromanticdev.unusualfitnessapp.util

import android.graphics.Bitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

object GoogleMapDrawer {

    private var userMarker: Marker? = null

    private var polyline: Polyline? = null

    fun drawMarker(
        marker: Bitmap,
        mapFragment: SupportMapFragment,
        location: LatLng
    ){
        mapFragment.getMapAsync { googleMap ->
            val user = LatLng(location.latitude, location.longitude)

            if (userMarker != null) {
                userMarker!!.remove()
            }

            userMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(user)
                    .icon(BitmapDescriptorFactory.fromBitmap(marker))
                    .anchor(0.5f, 0.5f)
            )
        }
    }

    fun moveCamera(
        mapFragment: SupportMapFragment,
        location: LatLng
    ) {
        mapFragment.getMapAsync { map ->
            map.moveCamera(CameraUpdateFactory.newLatLng(location))
        }
    }

    fun moveCameraAndZoom(
        mapFragment: SupportMapFragment,
        location: LatLng,
        zoom: Float
    ) {
        mapFragment.getMapAsync { map ->
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
        }
    }

    fun drawPolyline(mapFragment: SupportMapFragment, points: List<LatLng>) {
        mapFragment.getMapAsync { map ->
            if (polyline != null) polyline!!.remove()
            polyline = map.addPolyline(
                PolylineOptions()
                    .addAll(points)
                    .color(0xffff0000.toInt())
                    .jointType(JointType.ROUND)
                    .width(25f)
            )
        }
    }


}