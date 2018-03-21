package com.robotmonsterlabs.ping;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.robotmonsterlabs.ping.adaptors.AdaptorFriend;
import com.robotmonsterlabs.ping.utility.Alarm;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.OpenWebAddress;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ActivityLogin extends Activity {

    Button privacyPolicy;
    LoginButton loginButton;
    CallbackManager callbackManager;

    /**
     * The process for the onCreate method follows:
     * GraphAPI checks (with the saved token) if the user ID saved in shared preferences loads
     * If it doesn't, the user needs to log in - else it loads the Drawer Activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Facebook init, we initialize this BEFORE the FB call
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Start service
        Intent intent = new Intent(this, ServiceTrigger.class);
        this.startService(intent);

        // For now
        // LoginManager.getInstance().logOut();


        // Boilerplate init sequence for the fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get the buttons
        privacyPolicy = (Button) findViewById(R.id.button_privacypolicy);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        // Facebook permissions
        loginButton.setReadPermissions("user_friends");

        // Callback manager for Facebook login
        callbackManager = CallbackManager.Factory.create();

        // When the user clicks on the Facebook login button
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Set the current access token for the request
                        // We do this so that we can use it everwhere else
                        AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
                        // here is the actual ME graph request to get the FB ID
                        // The only thing we save to shared preferences is the Facebook ID
                        // The access token we can get with AccessToken.getCurrentAccessToken()
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // If the API returns an error
                                            new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
                                        } else {
                                            // If it's successful and we get the ID & save the ID to the sharedPreferences
                                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("facebookid", object.optString("id"));
                                            editor.putString("facebookname", object.optString("name"));
                                            editor.apply();
                                            // Check if the user is registered
                                            new GetUserAccount().execute(getString(R.string.api_url) + "/profile/" + object.optString("id") + "/get") ;
                                        }
                                    }
                                }).executeAsync();
                    }

                    @Override
                    public void onCancel() {}

                    @Override
                    public void onError(FacebookException exception) {
                        new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
                    }
                });


        // Get the Facebook ID to check with the Graph API
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sharedPreferencesFacebookId = sharedPref.getString("facebookid", "0");

        // We make a graph request with saved access token from shared preferences
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "/"+sharedPreferencesFacebookId, null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() != null)
                            new ToastMessage().show(getApplicationContext(), getString(R.string.toast_please_login));
                        else
                            loadDrawerActivity(); // we're assuming they've got an account & because LOGIN -> GET -> CREATE
                    }
                }
        ).executeAsync();

        // Set the font for the privacy policy button
        privacyPolicy.setTypeface(Typeface.createFromAsset(this.getAssets(), "museo_regular.otf"));

        // When the user clicks on the privacy policy button
        // We open the web address using our utility class
        privacyPolicy.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                new OpenWebAddress().open(getApplicationContext(), getString(R.string.website_url));
            }
        });

    }

    public void loadDrawerActivity() {
        new ToastMessage().show(getApplicationContext(), getString(R.string.toast_login));
        Intent intent = new Intent(getApplicationContext(), ActivityDrawer.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class GetUserAccount extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {}

        protected String doInBackground(String... params) {
            try {
                // setup our okhttp client
                GetDataFromUrl okHttpWrapper = new GetDataFromUrl();
                // return the string from the url
                return okHttpWrapper.getData(params[0]) ;
            } catch (IOException e) {
                // if something breaks, we return an error instead of the json
                Log.e("PIING JSON", e.toString());
                // return blank
                return "" ;
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d("PIING", "Progress update: " + progress[0]) ;
        }

        protected void onPostExecute(String result) {

            // Get the Facebook ID to check with the Graph API
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String sharedPreferencesFacebookId = sharedPref.getString("facebookid", "0");
            String sharedPreferencesFacebookName = sharedPref.getString("facebookname", "0");

            // If the string appears empty
            if (result.equals("")) {
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
            }

            // Try for the JSON block
            try {

                // Get our data from the returned result in AsyncTask
                JSONArray jsonArray = new JSONArray(result) ;

                // If there is nothing returned (user doesn't exist)
                if (jsonArray.length()==0) {

                    // Create them
                    String sanitizedname = sharedPreferencesFacebookName.replaceAll(" ", "%20");
                    new CreateNewUserAccount().execute(getString(R.string.api_url) + "/profile/" + sharedPreferencesFacebookId + "/create?name="+sanitizedname) ;

                } else {

                    // Iterate over the data from our json array
                    for (int i = 0; i < jsonArray.length(); i++) {

                        // Get the object at the index - only one is returned here
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // Store the variable
                        String returnedUserId = jsonObject.getString("id");

                        // Set the user ID in shared preferences
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("userid", returnedUserId);
                        editor.commit();

                        // Go to our main activity
                        loadDrawerActivity();

                    }
                }

            } catch (Exception e) {
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
                Log.e("PIING", e.toString());
            }

        }

    }

    private class CreateNewUserAccount extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {}

        protected String doInBackground(String... params) {
            try {
                // setup our okhttp client
                GetDataFromUrl okHttpWrapper = new GetDataFromUrl();
                // return the string from the url
                return okHttpWrapper.getData(params[0]) ;
            } catch (IOException e) {
                // if something breaks, we return an error instead of the json
                Log.e("PIING JSON", e.toString());
                // return blank
                return "" ;
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d("PIING", "Progress update: " + progress[0]) ;
        }

        protected void onPostExecute(String result) {

            // If the string appears empty
            if (result.equals("")) {
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
            }

            // Try for the JSON block
            try {

                // Get our data from the returned result in AsyncTask
                JSONArray jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                // There will only ever be one
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the object at the index - only one is returned here
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Store the variable
                    String returnedUserId = jsonObject.getString("id");

                    // Set the user ID in shared preferences
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userid", returnedUserId);
                    editor.commit();

                    // Go to our main activity
                    loadDrawerActivity();

                }

            } catch (Exception e) {
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
                Log.e("PIING", e.toString());
            }

        }

    }


}
