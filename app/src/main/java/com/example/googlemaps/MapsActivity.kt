package com.example.googlemaps

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import android.location.Location
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.googlemaps.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.IOException
import java.sql.Types.NULL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //private lateinit var mMap: GoogleMap
    private var flag : Boolean = false
    private lateinit var binding: ActivityMapsBinding
    private lateinit var new_place : LatLng
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permCode = 101


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getCurrentLocationUser()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun getCurrentLocationUser() {
        if(ActivityCompat.checkSelfPermission(
            this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permCode)
            return
        }
            val getLocation = fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location ->

                if(location != null){
                    currentLocation=location

                    Toast.makeText(applicationContext, currentLocation.latitude.toString() + "" + currentLocation.longitude.toString(), Toast.LENGTH_LONG).show()

                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }

    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            permCode -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                getCurrentLocationUser()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onMapReady(googleMap: GoogleMap) {

        if (::currentLocation.isInitialized) {
            val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            val markerOptions = MarkerOptions().position(latLng).title("Your current location")

            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7f))
            googleMap.addMarker(markerOptions)
        }


                fun closeKeyboard(view: View) {
                    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }

        fun openKeyboard(view: View) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, 0)
        }

                val inputLong : TextInputEditText = findViewById(R.id.inputLeft)
                val inputWidth : TextInputEditText = findViewById(R.id.inputRight)
                val layoutLong : TextInputLayout = findViewById(R.id.layoutLeft)
                val layoutWidth : TextInputLayout = findViewById(R.id.layoutRight)
                val buttonS : Button = findViewById(R.id.button_search)


                val input_anim: Animation = AnimationUtils.loadAnimation(
                    applicationContext,
                    R.anim.slide_down_and_fade_in
                )

                val input_anim_out: Animation = AnimationUtils.loadAnimation(
                    applicationContext,
                    R.anim.slide_up_and_fade_out
                )

                buttonS.setOnClickListener{
                    layoutLong.visibility = View.VISIBLE
                    layoutWidth.visibility = View.VISIBLE
                    layoutLong.startAnimation(input_anim)
                    layoutWidth.startAnimation(input_anim)
                    inputLong.requestFocus()
                    openKeyboard(inputLong)
                    flag = true
                }

                inputLong.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        if (inputLong.text.toString().isEmpty()) {
                            inputLong.setText("0")
                        }
                        try {
                            if (inputLong.text.toString().toDouble() > 90) {
                                inputLong.setText("90")
                            }
                            if (inputLong.text.toString().toDouble() < -90) {
                                inputLong.setText("-90")
                            }
                        } catch (e: IOException) {
                            inputLong.setText("0")
                            e.printStackTrace()
                            Log.e("IO", "IOException: " + e.message)
                            Toast.makeText(
                                applicationContext,
                                "IOException: " + e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: NumberFormatException) {
                            inputLong.setText("0")
                            e.printStackTrace()
                            Log.e("NumberFormat", "NumberFormatException: " + e.message)
                            Toast.makeText(
                                applicationContext,
                                "NumberFormatException: " + e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        closeKeyboard(inputLong)
                    }
                }

        inputWidth.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                closeKeyboard(inputWidth)
                if(inputWidth.text.toString().isEmpty()){
                    inputWidth.setText("0")
                }
                try{
                    if(inputWidth.text.toString().toDouble() > 180){
                        inputWidth.setText("180")
                    }
                    if(inputWidth.text.toString().toDouble() < -180){
                        inputWidth.setText("-180")
                    }
                } catch (e: IOException) {
                    inputWidth.setText("0")
                    e.printStackTrace()
                    Log.e("IO", "IOException: " + e.message)
                    Toast.makeText(applicationContext, "IOException: " + e.message, Toast.LENGTH_SHORT).show()
                } catch (e: NumberFormatException) {
                    inputWidth.setText("0")
                    e.printStackTrace()
                    Log.e("NumberFormat", "NumberFormatException: " + e.message)
                    Toast.makeText(applicationContext, "NumberFormatException: " + e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


       googleMap.setOnMapClickListener {
               if(flag != false){
                   inputLong.clearFocus()
                   inputWidth.clearFocus()
                   layoutLong.startAnimation(input_anim_out)
                   layoutWidth.startAnimation(input_anim_out)
                   layoutLong.visibility = View.INVISIBLE
                   layoutWidth.visibility = View.INVISIBLE
                   flag = false
               }
       }


        inputLong.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (inputLong.text.toString().isEmpty()) {
                    inputLong.setText("0")
                }
                try {
                    if (inputLong.text.toString().toDouble() > 90) {
                        inputLong.setText("90")
                    }
                    if (inputLong.text.toString().toDouble() < -90) {
                        inputLong.setText("-90")
                    }
                } catch (e: IOException) {
                    inputLong.setText("0")
                    e.printStackTrace()
                    Log.e("IO", "IOException: " + e.message)
                    Toast.makeText(
                        applicationContext,
                        "IOException: " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: NumberFormatException) {
                    inputLong.setText("0")
                    e.printStackTrace()
                    Log.e("NumberFormat", "NumberFormatException: " + e.message)
                    Toast.makeText(
                        applicationContext,
                        "NumberFormatException: " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                closeKeyboard(inputLong)
                if (inputWidth.text.toString() != "") {
                    googleMap.clear()
                    new_place = LatLng(
                        inputLong.text.toString().toDouble(),
                        inputWidth.text.toString().toDouble()
                    )
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(new_place))
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new_place, 15f))
                    var newMarker =
                        MarkerOptions().position(new_place).title("Your selected location")
                    googleMap.addMarker(newMarker)
                    inputLong.setText(null)
                    inputWidth.setText(null)
                    layoutLong.startAnimation(input_anim_out)
                    layoutWidth.startAnimation(input_anim_out)
                    layoutLong.visibility = View.INVISIBLE
                    layoutWidth.visibility = View.INVISIBLE
                    flag = false
                } else {
                    inputWidth.requestFocus()
                    openKeyboard(inputWidth)
                }
                true
            } else {
                false
            }
        }


        inputWidth.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                if(inputWidth.text.toString().isEmpty()){
                    inputWidth.setText("0")
                }
                try{
                if(inputWidth.text.toString().toDouble() > 180){
                    inputWidth.setText("180")
                }
                if(inputWidth.text.toString().toDouble() < -180){
                    inputWidth.setText("-180")
                }
            } catch (e: IOException) {
                    inputWidth.setText("0")
            e.printStackTrace()
            Log.e("IO", "IOException: " + e.message)
            Toast.makeText(applicationContext, "IOException: " + e.message, Toast.LENGTH_SHORT).show()
        } catch (e: NumberFormatException) {
                    inputWidth.setText("0")
            e.printStackTrace()
            Log.e("NumberFormat", "NumberFormatException: " + e.message)
            Toast.makeText(applicationContext, "NumberFormatException: " + e.message, Toast.LENGTH_SHORT).show()
        }
                closeKeyboard(inputWidth)
                inputWidth.clearFocus()
                if(inputLong.text.toString() == "") {
                    inputLong.requestFocus()
                    openKeyboard(inputLong)
                }else{
                googleMap.clear()
                new_place = LatLng(
                    inputLong.text.toString().toDouble(),
                    inputWidth.text.toString().toDouble()
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(new_place))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new_place, 15f))
                var newMarker =
                    MarkerOptions().position(new_place).title("Your selected location")
                googleMap.addMarker(newMarker)
                inputLong.setText(null)
                inputWidth.setText(null)
                layoutLong.startAnimation(input_anim_out)
                layoutWidth.startAnimation(input_anim_out)
                layoutLong.visibility = View.INVISIBLE
                layoutWidth.visibility = View.INVISIBLE
                    flag = false
                }
                true
            } else {
                false
            }
        }


                }
}