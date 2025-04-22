package com.example.steptycoon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {
    private GoogleMap mMap;
    int stepCounter;
    long cps;
    long balance;
    TextView tvSteps,tvCps,tvBalance;
    SupportMapFragment mapFragment;
    private static final int REQUEST_LOCATION_PERMISSION = 111;
    FusedLocationProviderClient flpClient;
    Marker userMarker=null;
    SensorManager sensorManager;
    Sensor step_sensor;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //gets the persistent values from sharedPreferences and assigns them default values if they're not set up
        preferences= getSharedPreferences("com.step_tycoon",MODE_PRIVATE);//what's wrong here
        stepCounter=preferences.getInt("Steps",0);
        cps=preferences.getLong("CPS",1);
        balance=preferences.getLong("balance",0);
        //initialize the text views
        tvSteps=findViewById(R.id.tvSteps);
        tvCps=findViewById(R.id.tvCps);
        tvBalance=findViewById(R.id.tvBalance);
        tvSteps.setText("Steps: "+stepCounter);
        tvCps.setText("CPS: "+cps);
        tvBalance.setText("Balance: "+balance);
        //define the sensor manager and the step detector
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        step_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        //define the mapFragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //checks for the needed permissions, precise location and activity recognition
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        else{
            mapFragment.getMapAsync(this);
        }
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{android.Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }
    }
    //updates the balance and steps whenever there's a step detected
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepCounter++;
            balance+=cps;
            //updates the text views based on the new data
            tvSteps.setText("Steps: " + stepCounter);
            tvBalance.setText("Balance: "+balance);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    //Checks if the location permission was granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted, sync map fragment
                mapFragment.getMapAsync(this);
            } else {
                // Permission denied, close the app
                System.out.println("Precise location Permission declined");
                finish();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //when the map is ready, define the mMap and get locationUpdates
        mMap = googleMap;
        getLocationUpdates();

    }
    //main method of logic
    //gets location updates and changes the map position, rotation, and marker based on it
    @SuppressLint("MissingPermission")
    public void getLocationUpdates(){
        flpClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        LocationCallback cb = new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //if the locationResult is not empty, get the last location
                if (!locationResult.getLocations().isEmpty()) {
                    Location location = locationResult.getLastLocation();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .flat(true)
                            .anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_icon));
                    if(userMarker==null)
                        userMarker = mMap.addMarker(markerOptions);
                    else
                        userMarker.setPosition(latLng);
                    float speed = location.getSpeed();
                    float bearing = location.getBearing();
                    //only updates the bearing of the map if the speed is greater than 0.5 m/s for a more stable map
                    if (speed > 0.5 && bearing != 0.0f) {
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(latLng)
                                .zoom(18f)
                                .bearing(bearing)
                                .tilt(45f)
                                .build();
                        userMarker.setRotation(bearing);
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } else {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
                    }

                }
            }
        };

        flpClient.requestLocationUpdates(createLocationRequest(),cb, Looper.getMainLooper());
    }
    //onclick method for navigating between the two activities
    public void navigate(View view){
        Intent intent;
        // checks which view called it by the ID
        if(view.getId()==R.id.btnTycoon)
            intent=new Intent(MapActivity.this,TycoonActivity.class);
        else
            intent=new Intent(MapActivity.this,StatsActivity.class);
        //saves the persistent information before starting the new activity
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("Steps",stepCounter);
        editor.putLong("CPS",cps);
        editor.putLong("balance",balance);
        editor.apply();
        startActivity(intent);
    }
    // creates the locationRequest method
    private LocationRequest createLocationRequest(){
        LocationRequest request = LocationRequest.create();
        request.setWaitForAccurateLocation(true);
        request.setInterval(200);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return  request;
    }
    @Override
    protected void onStop() {
        super.onStop();
        //saves the persistent data before closing the app
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt("Steps",stepCounter);
        editor.putLong("CPS",cps);
        editor.putLong("balance",balance);
        editor.apply();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(this,step_sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

}