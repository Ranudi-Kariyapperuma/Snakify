package com.example.snakify;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        TextView resultTextView = findViewById(R.id.tvSnakeDetails);

        String response = getIntent().getStringExtra("response");
        if (response == null || response.isEmpty()) {
            response = "No data received. Please try again.";
        }

        resultTextView.setText(response);
    }

}