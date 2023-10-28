package com.example.cpen321mappost;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileManager {
    final static String TAG = "ProfileManager Activity";
    private final OkHttpClient httpClient = new OkHttpClient();

//    public void getUserData(String userId, final User.UserCallback callback) {
//        // Build the request URL. Modify this with your actual API URL.
//        String url = "http://4.204.251.146:8081/users/?userId=" + userId;
//
//        // Create a request object.
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        // Enqueue the request in the background.
//        httpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                // Handle the error. Be aware that this is called on a background thread.
//                Log.d(TAG, "Failed to request user data");
//                callback.onFailure(e);
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//
//                    Gson gson = new Gson();
//
//                    assert response.body() != null;
//                    User user = gson.fromJson(response.body().charStream(), User.class);
//
//                    callback.onSuccess(user);
//                } else {
//                    // Handle the error. Be aware that this is called on a background thread.
//                    callback.onFailure(new IOException("Unexpected code " + response));
//                }
//            }
//        });
//    }

    //-------------------------------------------- E N D --------------------------------------------//

    public void getUserData(User user, final User.UserCallback callback, final Activity activity) {
        String url = "http://4.204.251.146:8081/users/?userId=" + user.getUserId();

        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Failed to request user data");
                        callback.onFailure(e); // must be run on UI thread if updating UI components
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                assert response.body() != null;
                String responseData = response.body().string(); // your response data
                Gson gson = new Gson();
                User user = gson.fromJson(responseData, User.class);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(user); // must be run on UI thread if updating UI components
                    }
                });
            }
        });
    }

    public void putUserData(User user, final User.UserCallback callback, final Activity activity) {

        // chage this to PUT
        String url = "http://4.204.251.146:8081/users/update-profile";

        // Convert the User object to JSON format
        Gson gson = new Gson();
        String jsonUserData = gson.toJson(user); // 'user' is your User instance

        // Create a request body with the JSON representation of the user
        RequestBody body = RequestBody.create(jsonUserData, MediaType.parse("application/json; charset=utf-8"));

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .put(body) // Use PUT method
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Failed to update user data");
                        callback.onFailure(e); // must be run on UI thread if updating UI components
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Here, you can also parse the response and create a User object if your API returns the updated user as a response.
                // Otherwise, you might just want to confirm the success without parsing the response.
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // If you're just confirming the success, you can create a new 'success' method in your callback or pass a null user.
                        // Or, if you've parsed a response, pass the updated user here.
                        if (response.isSuccessful()) {
                            try {
                                // Here, we're not parsing a JSON response body, so we check the response directly.
                                if (response.code() == 200) {
                                    // The operation was successful. Notify the callback.
                                    callback.onSuccess(user);
                                } else {
                                    // Handle other response codes (like 4xx or 5xx errors)
                                    callback.onFailure(new IOException("Unexpected response when updating user: " + response));
                                }
                            } finally {
                                response.close(); // Important to avoid leaking resources
                            }
                        } else {
                            callback.onFailure(new IOException("Unexpected code " + response));
                        }
                    }
                });
            }
        });
    }

    public void postUserData(User user, final User.UserCallback callback, final Activity activity){

        // chage this to POST
        String url = "http://4.204.251.146:8081/users";

        // Convert the User object to JSON format
        Gson gson = new Gson();
        String jsonUserData = gson.toJson(user); // 'user' is your User instance

        // Create a request body with the JSON representation of the user
        RequestBody body = RequestBody.create(jsonUserData, MediaType.parse("application/json; charset=utf-8"));

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .post(body) // Use PUT method
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Failed to post user data");
                        callback.onFailure(e); // must be run on UI thread if updating UI components
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Here, you can also parse the response and create a User object if your API returns the updated user as a response.
                // Otherwise, you might just want to confirm the success without parsing the response.
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // If you're just confirming the success, you can create a new 'success' method in your callback or pass a null user.
                        // Or, if you've parsed a response, pass the updated user here.
                        if (response.isSuccessful()) {
                            try {
                                // Here, we're not parsing a JSON response body, so we check the response directly.
                                if (response.code() == 200) {
                                    // The operation was successful. Notify the callback.
                                    callback.onSuccess(user);
                                } else {
                                    // Handle other response codes (like 4xx or 5xx errors)
                                    callback.onFailure(new IOException("Unexpected response when updating user: " + response));
                                }
                            } finally {
                                response.close(); // Important to avoid leaking resources
                            }
                        } else {
                            callback.onFailure(new IOException("Unexpected code " + response));
                        }
                    }
                });
            }
        });

    }

}