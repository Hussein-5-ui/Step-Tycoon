package com.example.steptycoon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatsActivity extends AppCompatActivity {

    TextView tvStepsStat;
    TextView tvCPSStat;
    TextView tvBalanceStat;
    Button btnShareStats;
    Button btnGoToTycoon;
    Button btnMapReturn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get the shared preferences
        SharedPreferences preferences = getSharedPreferences("com.step_tycoon", MODE_PRIVATE);

        //save them as variables
        int steps = preferences.getInt("Steps", 0);
        long cps = preferences.getLong("CPS", 0);
        long balance = preferences.getLong("balance", 0);

        //find the textviews
        tvStepsStat = findViewById(R.id.tvStepsStat);
        tvCPSStat = findViewById(R.id.tvCPSStat);
        tvBalanceStat = findViewById(R.id.tvBalanceStat);
        btnShareStats = findViewById(R.id.btnShareStats);
        btnGoToTycoon = findViewById(R.id.btnGoToTycoon);
        btnMapReturn= findViewById(R.id.btnMapReturn);

        //edit the textviews
        tvStepsStat.setText("Steps: " + steps);
        tvCPSStat.setText("CPS: " + cps);
        tvBalanceStat.setText("Balance: " + balance);

        btnShareStats.setOnClickListener(view -> {
           //share as plaintext
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "My step Tycoon stats: Steps: " + steps + ", CPS: "+cps+", Balance: "+balance);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });

        btnMapReturn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            //intent.putExtra("username",etData.getText().toString());
            startActivity(intent);
        });
        btnGoToTycoon.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), TycoonActivity.class);
            //intent.putExtra("username",etData.getText().toString());
            startActivity(intent);
        });
    }
}
