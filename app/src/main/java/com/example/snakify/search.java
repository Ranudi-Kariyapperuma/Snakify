package com.example.snakify;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class search extends AppCompatActivity {

    private static final String BASE_URL = "https://www.googleapis.com/";
    private static final String API_KEY = "AIzaSyApAUJI3E-6fVMuejPCXBVypBMqNWD4n6k";
    private static final String CX = "91812f850381844cc";

    private EditText etSearch;
    private Button btnSearch;
    private TextView tvResults;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("searchActivity", "onCreate called");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        tvResults = findViewById(R.id.tvResults);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etSearch.getText().toString();
                if (!query.isEmpty()) {
                    searchSnake(query);
                } else {
                    tvResults.setText("Please enter a snake name.");
                }
            }
        });
    }

    private void searchSnake(String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleSearchAPI api = retrofit.create(GoogleSearchAPI.class);

        Call<SearchResponse> call = api.searchSnakes(API_KEY, CX, query);
        call.enqueue(new Callback<SearchResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder results = new StringBuilder();
                    for (SearchResponse.SearchItem item : response.body().items) {
                        results.append("Title: ").append(item.title).append("\n")
                                .append("Link: ").append(item.link).append("\n")
                                .append("Snippet: ").append(item.snippet).append("\n\n");
                    }
                    tvResults.setText(results.toString());
                } else {
                    tvResults.setText("No results found.");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                tvResults.setText("Error: " + t.getMessage());
            }
        });
    }
}
