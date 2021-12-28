package com.olamachia.maptrackerweekeighttask

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var map: GoogleMap
    private var marker1 : Marker? = null
    private var marker2 : Marker? = null
    lateinit var databaseRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        databaseRef = FirebaseDatabase.getInstance().getReference("Users")
        databaseRef.addValueEventListener(logListener)

         //OnClickListener to the floating action button to open the next activity
//        val floatingActionButton = findViewById<FloatingActionButton>(R.id.load_second_implementation)
//        floatingActionButton.setOnClickListener { loadSecondImplementation() }

    }

    //gets location access and displays the google map
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getLocationAccess()
        getLocationUpdates()
        startLocationUpdates()

    }


    //an object of the ValueEventListener class to listen for changes at a location
    private val logListener = object : ValueEventListener {

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(applicationContext, "Could not read from database", Toast.LENGTH_LONG).show()
        }

        //listen for data changes in the database and update the marker
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            //if the snapshot exists in the database, get it and store it in a variable
            val locationLogging = dataSnapshot.child("stanley").getValue(LocationModel::class.java)

            if (dataSnapshot.exists()) {

                val partnerLat = locationLogging!!.latitude
                val partnerLong = locationLogging.longitude

                if ((partnerLat != null )  && (partnerLong != null)) {
                    val partnerLoc = LatLng(partnerLat, partnerLong)
                    Toast.makeText(this@MapsActivity, "${partnerLoc.latitude}", Toast.LENGTH_LONG).show()

                   //create a marker at the read location and display it on the map
                    map.clear()
                    map.addMarker(
                        MarkerOptions().position(partnerLoc)
                            .title("Stanley")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.stanley))
                    )

                   // assign the partners location to a marker
                    map.addMarker(MarkerOptions().position(partnerLoc).title("Rotimi").icon(BitmapDescriptorFactory.fromResource(R.drawable.akeem_rotimi)))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(partnerLoc, 16.0f))


                    Toast.makeText(applicationContext, "Locations accessed from the database", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MapsActivity, "Not found", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //loads the location on the map if permission has ben granted
    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            map.isMyLocationEnabled = true

        } else
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
    }

    //set the interval for receiving location updates and connect to a database
    private fun getLocationUpdates() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 20000
        locationRequest.fastestInterval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation

                    //get a reference to the database

                    val locationLogging = LocationModel(location.latitude, location.longitude)

                    //store location on firebase using the path RotimiLocation
                    databaseRef.child("RotimiLocation").setValue(locationLogging).addOnSuccessListener {
                        Toast.makeText(applicationContext, "Locations written into the database", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "Error occurred while writing the locations", Toast.LENGTH_LONG).show()
                    }



                    //get the user location, add a marker then zoom the map to building level
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.clear()
                    map.addMarker(MarkerOptions().position(latLng).title("Rotimi")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.akeem_rotimi)))

                    marker2?.position = latLng

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
                }
            }
        }
    }

    //checks for permission and register the location call back with the location client
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }else{Toast.makeText(this,"permission Granted",Toast.LENGTH_LONG).show()}

    }

    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {

            } else {
                Toast.makeText(
                    this,
                    "User has not granted location access permission",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
    // function to open the secondImplementationActivity
//    private fun loadSecondImplementation() {
//        val intent = Intent(applicationContext, PokemonList::class.java)
//        startActivity(intent)
//    }
}