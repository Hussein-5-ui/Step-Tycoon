package com.example.steptycoon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StatsActivity extends AppCompatActivity {

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
        TextView tvStepsStat = findViewById(R.id.tvStepsStat);
        TextView tvCPSStat = findViewById(R.id.tvCPSStat );
        TextView tvBalanceStat = findViewById(R.id.tvBalanceStat);

        //edit the textviews
        tvStepsStat.setText("Steps: " + steps);
        tvCPSStat.setText("CPS: " + cps);
        tvBalanceStat.setText("Balance: " + balance);

    }
}