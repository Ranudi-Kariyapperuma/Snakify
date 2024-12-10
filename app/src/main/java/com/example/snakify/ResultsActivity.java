package com.example.snakify;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResultsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_results);

        // Get the UI elements
        TextView resultTextView = findViewById(R.id.tvSnakeDetails);
        ImageView snakeImageView = findViewById(R.id.snakeImageView);

        // Get the data passed from the previous activity
        String response = getIntent().getStringExtra("response");
        String imageUriString = getIntent().getStringExtra("imageUri");

        if (response == null || response.isEmpty()) {
            response = "No data received. Please try again.";
            resultTextView.setText(response);
            return;
        }

        try {
            JSONObject responseObject = new JSONObject(response);
            StringBuilder formattedResults = new StringBuilder();

            if (responseObject.optString("status").equals("success")) {
                JSONArray predictions = responseObject.getJSONArray("predictions");

                for (int i = 0; i < predictions.length(); i++) {
                    JSONObject prediction = predictions.getJSONObject(i);
                    formattedResults.append("Snake Species: ").append(prediction.getString("species")).append("\n");
                    formattedResults.append("Family: ").append(prediction.getString("family")).append("\n");
                    formattedResults.append("Genus: ").append(prediction.getString("genus")).append("\n");
                    formattedResults.append("Poisonous: ").append(prediction.getBoolean("poisonous") ? "Yes" : "No").append("\n");
                    formattedResults.append("Primary Continent: ").append(prediction.getString("primary_continent")).append("\n");
                    formattedResults.append("Primary Country: ").append(prediction.getString("primary_country")).append("\n");
                    formattedResults.append("Confidence Level: ").append(String.format("%.2f%%", prediction.getDouble("confidence") * 100)).append("\n\n");
                }
            } else {
                formattedResults.append("Error: ").append(responseObject.optString("message", "Unknown error occurred."));
            }

            // Set the snake details in the TextView
            resultTextView.setText(formattedResults.toString());

            // Load and display the snake image using Glide
            if (imageUriString != null && !imageUriString.isEmpty()) {
                Uri imageUri = Uri.parse(imageUriString);
                Glide.with(this)
                        .load(imageUri)  // Load image from URI
                        .into(snakeImageView);  // Set the image into the ImageView
            } else {
                // If image URI is not available, set a default image
                snakeImageView.setImageResource(R.drawable.snakebite);  // Replace with your default image resource
            }

        } catch (Exception e) {
            resultTextView.setText("Error parsing data. Please try again.");
        }
    }
}
