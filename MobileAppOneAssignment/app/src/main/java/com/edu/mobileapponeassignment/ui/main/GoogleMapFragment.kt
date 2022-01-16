package com.edu.mobileapponeassignment.ui.main

import android.annotation.SuppressLint
import android.content.IntentSender
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.edu.mobileapponeassignment.databinding.FragmentGoogleMapBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class GoogleMapFragment: Fragment() {
    lateinit var mapView: MapView
    lateinit var googleMap: GoogleMap
    private lateinit var client: FusedLocationProviderClient
    private var _binding: FragmentGoogleMapBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoogleMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Always should get permission before calling this fragment
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val locationRequest = createLocationRequest()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val settingClient: SettingsClient = LocationServices.getSettingsClient(requireContext())
        val task: Task<LocationSettingsResponse> = settingClient.checkLocationSettings(builder.build())

        client = LocationServices.getFusedLocationProviderClient(requireActivity())
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        MapsInitializer.initialize(requireContext().applicationContext)
        mapView.getMapAsync {
            it.isMyLocationEnabled = true
            googleMap = it
        }

        task.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            getCurrentLocation()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(requireActivity(), 66)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val task = client.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                val latLng = LatLng(it.latitude, it.longitude)
                val options = MarkerOptions().position(latLng).title("You are here")
                // For zooming automatically to the location of the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                googleMap.addMarker(options)
            }
        }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}