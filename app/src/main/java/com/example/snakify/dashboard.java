package com.example.snakify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        View identifyLayout = findViewById(R.id.identifyLayout);


        identifyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(dashboard.this, identify.class);
                startActivity(intent);
            }
        });

        View firstaidLayout = findViewById(R.id.firstaidLayout);


         firstaidLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(dashboard.this, firstaid.class);
                startActivity(intent);
            }
        });

        View venomusLayout = findViewById(R.id.venomusLayout);


        venomusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(dashboard.this, venomus.class);
                startActivity(intent);
            }
        });


        View nonvenomusLayout = findViewById(R.id.nonvenomusLayout);


        nonvenomusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(dashboard.this, nonvenomus.class);
                startActivity(intent);
            }
        });


        View accountLayout = findViewById(R.id.accountLayout);


        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(dashboard.this, account.class);
                startActivity(intent);
            }
        });


    }
}
