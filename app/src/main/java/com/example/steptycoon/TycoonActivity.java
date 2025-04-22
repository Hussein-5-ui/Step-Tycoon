package com.example.steptycoon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class TycoonActivity extends AppCompatActivity implements SensorEventListener{
    TextView titleLabel,cpsLabel,coinLabel;
    Integer stepCount=0;//Only for stats page
    ArrayList<TycoonObject> tycoons;
    TycoonAdapter adapter;
    ListView lstTycoon;
    SensorManager sensorManager;
    Sensor acc_sensor;
    Button mapButton,statsButton;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tycoon);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{android.Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }
        //ListView
        lstTycoon=findViewById(R.id.tycoonList);
        tycoons=new ArrayList<TycoonObject>();
        tycoons.add(new TycoonObject("Auto Walkers",10,100,true));
        tycoons.add(new TycoonObject("Running",100,150));
        tycoons.add(new TycoonObject("Stairs",200,450));
        tycoons.add(new TycoonObject("Jumping Jacks",500,1000));
        tycoons.add(new TycoonObject("Extreme Sports",10000,10000));
        tycoons.add(new TycoonObject("Flight",100000,100000));
        //Saved Instance for Tycoons
        if(savedInstanceState!=null) {
            for (int i = 0; i < tycoons.size(); i++) {
                tycoons.get(i).savedInstanceConvert(savedInstanceState.getInt("tyc#" + i, 0));
            }
            stepCount = savedInstanceState.getInt("stepCount", 0);//Or whatever the before stepcount was
            adapter = new TycoonAdapter(getApplicationContext(), tycoons);
            adapter.totalMoney = savedInstanceState.getInt("TotalMoney", 0);
        }
        else{
            adapter=new TycoonAdapter(getApplicationContext(),tycoons);
        }
        lstTycoon.setAdapter(adapter);
        //Sensors
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acc_sensor=sensorManager.getDefaultSensor((Sensor.TYPE_STEP_DETECTOR));
        //Get Labels
        titleLabel=findViewById(R.id.gymTitle);
        titleLabel.setText("Gym");//Change to whoever's log in it is
        cpsLabel=findViewById(R.id.absltCPSLabel);
        coinLabel=findViewById(R.id.totalCoinLabel);

        mapButton=findViewById(R.id.mapActivity);
        mapButton.setText("Back To Map");
        mapButton.setOnClickListener(v->{
            Intent intent=new Intent(getApplicationContext(),MapActivity.class);
            startActivity(intent);
        });
        statsButton=findViewById(R.id.statsActivity);
        statsButton.setText("Stats");
        statsButton.setOnClickListener(v->{
            Intent intent=new Intent(getApplicationContext(), StatsActivity.class);
            startActivity(intent);
        });


    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_STEP_DETECTOR) {
            cpsLabel.setText("Coins Per Step: "+adapter.updateTotalCps());
            coinLabel.setText("Total Coins: "+adapter.totalMoney);
            stepCount++;
        };

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager.registerListener(this,acc_sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("totalMoney",adapter.totalMoney);
        for(int i=0;i<adapter.tycoons.size();i++){
            outState.putInt("tyc#"+i,adapter.tycoons.get(i).count);
        }
        outState.putInt("stepCount",stepCount);

        //Shared Pref
        SharedPreferences sharedPref;
        sharedPref = getSharedPreferences("com.step_tycoon", Context.
                MODE_PRIVATE);
        sensorManager.unregisterListener(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Steps",stepCount);
        editor.putLong("CPS",adapter.getTotalCps());
        editor.putLong("balance",adapter.totalMoney);
        editor.apply();
    }


}