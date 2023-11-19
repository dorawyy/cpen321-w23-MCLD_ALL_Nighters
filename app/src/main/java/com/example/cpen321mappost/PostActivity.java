package com.example.cpen321mappost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;


import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {
    private ImageView imgPreview;
    private EditText titleEditText;
    private EditText mainTextEditText;
    final static String TAG = "PostActivity";

    //ChatGPT usage: Yes
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.containsValue(false)) {
                    Toast.makeText(this, "Permission denied to read storage", Toast.LENGTH_SHORT).show();
                }
            });

    //ChatGPT usage: Yes

    private final ActivityResultLauncher<Intent> getImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    imgPreview.setImageURI(imageUri);
                }
            });

    //ChatGPT usage: Yes
    public interface JsonPostCallback {
        void onSuccess(JSONObject postedData);
        void onFailure(Exception e);
    }

    //ChatGPT usage: Partial
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imgPreview = findViewById(R.id.imgPreview);
        titleEditText = findViewById(R.id.titleEditText);
        mainTextEditText = findViewById(R.id.mainTextEditText);
        Button uploadImageButton = findViewById(R.id.uploadImageButton);
        Button saveButton = findViewById(R.id.saveButton);
        Intent receivedIntent = getIntent();

        double latitude = Double.parseDouble(receivedIntent.getStringExtra("latitude"));
        double longitude = Double.parseDouble(receivedIntent.getStringExtra("longitude"));


        // Check permissions at runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
        }

        //Upload the image

        uploadImageButton.setOnClickListener(v -> {
            Intent pickImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getImage.launch(pickImage);
        });
        //Save the content of post

        saveButton.setOnClickListener(v -> {

            try {
                // Construct the JSON object
                JSONObject postData = new JSONObject();

                postData.put("userId", User.getInstance().getUserId());
                postData.put("time", getCurrentDateUsingCalendar());

                JSONObject coordinate = new JSONObject();
                coordinate.put("latitude", latitude);
                coordinate.put("longitude", longitude);
                postData.put("coordinate", coordinate);

                JSONObject content = new JSONObject();
                content.put("title", titleEditText.getText().toString());
                content.put("body", mainTextEditText.getText().toString());
                postData.put("content", content);

                postJsonData(postData, PostActivity.this, new JsonPostCallback() {
                    @Override
                    public void onSuccess(JSONObject postedData) {

                        Intent intent=new Intent(PostActivity.this, MapsActivity.class);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(PostActivity.this, "Failed to post!", Toast.LENGTH_SHORT).show();

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    //ChatGPT usage: Partial
    public void postJsonData(JSONObject postData, final Activity activity, final JsonPostCallback callback){

        String url = "http://4.204.251.146:8081/posts";
        OkHttpClient httpClient = HttpClient.getInstance();

        String jsonStrData = postData.toString();
        Log.d(TAG, "Posting Data: " + jsonStrData);

        RequestBody body = RequestBody.create(jsonStrData, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(() -> {

                    Log.e(TAG, "FAILURE POST POSTS: " + e);

                    callback.onFailure(e);

                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful()) {

                    activity.runOnUiThread(() -> {

                        Log.d(TAG, "Unexpected server response, the code is: " + response.code());
                        callback.onFailure(new IOException("Unexpected response " + response));

                    });

                } else {

                    Log.d(TAG, "USER POST POSTS SUCCEED");

                    activity.runOnUiThread(() -> callback.onSuccess(postData));

                }
            }
        });
    }

    //ChatGPT usage: Partial
    public String getCurrentDateUsingCalendar () {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are indexed from 0
        int year = calendar.get(Calendar.YEAR);

        return day + "-" + month + "-" + year;

    }
}