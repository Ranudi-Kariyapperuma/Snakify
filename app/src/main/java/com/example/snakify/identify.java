package com.example.snakify;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private static final int PERMISSION_REQUEST_CODE = 100;
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
                if (checkPermission()) {
                    openImagePicker(); // Open image picker if permission is granted
                } else {
                    requestPermission(); // Request permission
                }
            }
        });
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // For Android 6.0 to Android 12
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                    PERMISSION_REQUEST_CODE);
        } else {
            // For Android 6.0 to Android 12
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker(); // Open image picker after permission is granted
            } else {
                Toast.makeText(this, "Permission denied. Please allow access to select an image.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
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
                .baseUrl("http://103.253.146.6:8000/")
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
                        intent.putExtra("imageUri", imageUri.toString());
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
