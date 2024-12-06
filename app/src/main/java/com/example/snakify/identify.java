package com.example.snakify;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class identify extends AppCompatActivity {
    private static final int IMAGE_PICK_REQUEST = 1;
    private Uri imageUri;
    private Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_identify);

        uploadButton = findViewById(R.id.btnUploadImage);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData(); // Assign to imageUri
            String imagePath = getRealPathFromURI(imageUri);

            if (imagePath != null) {
                File imageFile = new File(imagePath);
                uploadImage(imageFile);
            } else {
                showError("Invalid image selected. Please try again.");
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            Log.e("Image Path", "Cursor is null. Path: " + uri.getPath());
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String filePath = cursor.getString(idx);
            cursor.close();
            Log.e("Image Path", "File Path: " + filePath);
            return filePath;
        }
    }

    private void uploadImage(File imageFile) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://103.253.146.6:8000/predict")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        Call<ResponseBody> call = apiService.uploadImage(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        Log.d("API_SUCCESS", "Response: " + responseBody);

                        Intent intent = new Intent(identify.this, ResultsActivity.class);
                        intent.putExtra("response", responseBody);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("API_ERROR", "Error parsing response", e);
                        showError("Failed to parse server response.");
                    }
                } else {
                    Log.e("API_ERROR", "Server returned error: " + response.code());
                    showError("Failed to retrieve data. Server error.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Network failure: " + t.getMessage(), t);
                showError("Failed to connect to the server. Please try again.");
            }
        });
    }

    private void showError(String message) {
        Intent intent = new Intent(identify.this, ResultsActivity.class);
        intent.putExtra("response", message);
        startActivity(intent);
    }
}
