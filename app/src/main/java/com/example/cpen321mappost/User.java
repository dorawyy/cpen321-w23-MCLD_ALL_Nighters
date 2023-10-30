package com.example.cpen321mappost;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

public class User {
    private static User instance = null;
    private static ProfileManager profileManager = null;
    private static final String TAG = "User";

    // User attributes
    private String userId;
    private String userName;
    private String userEmail;
    private String userGender;
    private String userBirthdate;
    private String token;

    // Private constructor so no other class can instantiate
    private User() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        this.userId = firebaseUser.getUid();
        this.userName = firebaseUser.getDisplayName();
        this.userEmail = firebaseUser.getEmail();
        this.userGender = "none";
        this.userBirthdate = "none";
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    token = task.getResult();

                    String msg = "FCM Token: " + token;
                    Log.d(TAG, msg);
                });

    }

    public User(String userId){

        this.userId = userId;
        this.userName = "none";
        this.userEmail = "none";
        this.userGender = "none";
        this.userBirthdate = "none";
        this.token = null;

    }

    // Method to get the single instance of user
    public static synchronized User getInstance() {

        if (instance == null) {

            instance = new User();
            profileManager = new ProfileManager();

            profileManager.getUserData(instance, new Activity(), new UserCallback() {
                @Override
                public String onSuccess(User user) {

                    Log.d(TAG, "IN the user class, succeed in getting user data");

                    Gson gson = new Gson();
                    String jsonUserData = gson.toJson(user);

                    Log.d(TAG, jsonUserData);

                    instance = gson.fromJson(jsonUserData, User.class);

                    return jsonUserData;
                }

                @Override
                public void onFailure(Exception e) {

                    profileManager.postUserData(instance, new Activity(), new User.UserCallback() {
                        @Override
                        public String onSuccess(User user) {

                            return null;
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }

                    });

                }
            });

        }

        return instance;

    }

    public interface UserCallback {
        String onSuccess(User user);
        void onFailure(Exception e);
    }

    // Getters and possibly setters if you need to change user's attributes after instantiation

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserBirthdate() {
        return userBirthdate;
    }

    public void setUserBirthdate(String userBirthdate) {
        this.userBirthdate = userBirthdate;
    }

    // Add other methods here...
}
