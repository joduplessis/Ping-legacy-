package com.robotmonsterlabs.ping.fragments;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.adaptors.AdaptorCategory;
import com.robotmonsterlabs.ping.adaptors.AdaptorFriend;
import com.robotmonsterlabs.ping.adaptors.AdaptorPing;
import com.robotmonsterlabs.ping.adaptors.AdaptorSound;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPingsDetail extends Fragment {

    View view;
    String[] soundClipArray;
    HashMap<String,String> jsonHashMap;
    ArrayList<HashMap<String, String>> data;
    JSONArray jsonArray;

    public String pingId = "0";
    public String categoryId = "0";
    public String pingType = "update";

    RelativeLayout daysSelectorContainer;
    LinearLayout daysSelectorList;
    CheckBox daySunday;
    CheckBox dayMonday;
    CheckBox dayTuesday;
    CheckBox dayWednesday;
    CheckBox dayThursday;
    CheckBox dayFriday;
    CheckBox daySaturday;
    Button dayUpdate;
    RelativeLayout timeSelectorContainer;
    LinearLayout timeSelectorList;
    TimePicker timePicker;
    Button timeUpdate;
    RelativeLayout settingsListContainer;
    LinearLayout settingsList;
    RelativeLayout pingFriends;
    Button button_addFriends;
    RelativeLayout settingTime;
    TextView settingTimeLabel;
    TextView settingTimeValue;
    RelativeLayout settingDays;
    TextView settingDaysLabel;
    TextView settingDaysValue;
    RelativeLayout settingTitle;
    TextView settingTitleLabel;
    TextView settingTitleValue;
    RelativeLayout settingCategory;
    TextView settingCategoryLabel;
    public TextView settingCategoryValue; // Accessed from the Activity for the interface callback
    RelativeLayout settingSound;
    TextView settingSoundLabel;
    public TextView settingSoundValue; // Accessed from the Activity for the interface callback
    RelativeLayout settingFadein;
    TextView settingFadeinLabel;
    TextView settingFadeinValue;
    SeekBar settingFadeinValueSeekbar;
    RelativeLayout settingRepeats;
    TextView settingRepeatsLabel;
    Switch settingRepeatsValue;
    RelativeLayout settingEnabled;
    TextView settingEnabledLabel;
    Switch settingEnabledValue;
    Button pingUpdate;
    RelativeLayout soundSelectorContainer;
    LinearLayout soundSelectorList;
    ListView soundSelectorListview;
    Button soundUpdate;
    RelativeLayout categorySelectorContainer;
    LinearLayout categorySelectorList;
    ListView categorySelectorListview;
    Button categoryUpdate;
    RelativeLayout friendsSelectorContainer;
    LinearLayout friendsSelectorList;
    ListView friendsSelectorListview;
    Button friendsUpdate;

    ArrayList<String> dayArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pings_detail, container, false);


        // Friends selection
        friendsSelectorContainer = (RelativeLayout) view.findViewById(R.id.friends_selector_container);
        friendsSelectorList = (LinearLayout) view.findViewById(R.id.friends_selector_list);
        friendsSelectorListview = (ListView) view.findViewById(R.id.friends_selector_listview);
        friendsSelectorContainer.setVisibility(View.INVISIBLE); // Hide it
        friendsUpdate = (Button) view.findViewById(R.id.friends_update);
        friendsUpdate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendsSelectorContainer.setVisibility(View.INVISIBLE);
            }
        });

        // Friends bar at the top
        pingFriends = (RelativeLayout) view.findViewById(R.id.ping_friends);
        button_addFriends = (Button) view.findViewById(R.id.button_add_friends);
        button_addFriends.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendsSelectorContainer.setVisibility(View.VISIBLE);
            }
        });

        // -----------------------------------------------------------------------------------------

        // Time settings
        settingTime = (RelativeLayout) view.findViewById(R.id.setting_time);
        settingTimeLabel = (TextView) view.findViewById(R.id.setting_time_label);
        settingTimeValue = (TextView) view.findViewById(R.id.setting_time_value);
        settingTime.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSelectorContainer.setVisibility(View.VISIBLE);
            }
        });

        // Time selection container
        timeSelectorContainer = (RelativeLayout) view.findViewById(R.id.time_selector_container);
        timeSelectorList = (LinearLayout) view.findViewById(R.id.time_selector_list);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timeSelectorContainer.setVisibility(View.INVISIBLE); // Hide it
        timeUpdate = (Button) view.findViewById(R.id.time_update);
        timeUpdate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Store our variables from the TimePicker
                String minuteValue = timePicker.getCurrentMinute().toString();
                String hourValue = timePicker.getCurrentHour().toString();
                // Update the UI
                settingTimeValue.setText(hourValue+":"+minuteValue);
                // Hide the popup
                timeSelectorContainer.setVisibility(View.INVISIBLE);
            }
        });

        // -----------------------------------------------------------------------------------------

        // Days settings
        settingDays = (RelativeLayout) view.findViewById(R.id.setting_days);
        settingDaysLabel = (TextView) view.findViewById(R.id.setting_days_label);
        settingDaysValue = (TextView) view.findViewById(R.id.setting_days_value);
        settingDays.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                daysSelectorContainer.setVisibility(View.VISIBLE);
            }
        });

        // Day selection container
        daysSelectorContainer = (RelativeLayout) view.findViewById(R.id.days_selector_container);
        daysSelectorList = (LinearLayout) view.findViewById(R.id.days_selector_list);
        daySunday = (CheckBox) view.findViewById(R.id.day_sunday);
        dayMonday = (CheckBox) view.findViewById(R.id.day_monday);
        dayTuesday = (CheckBox) view.findViewById(R.id.day_tuesday);
        dayWednesday = (CheckBox) view.findViewById(R.id.day_wednesday);
        dayThursday = (CheckBox) view.findViewById(R.id.day_thursday);
        dayFriday = (CheckBox) view.findViewById(R.id.day_friday);
        daySaturday = (CheckBox) view.findViewById(R.id.day_saturday);
        daysSelectorContainer.setVisibility(View.INVISIBLE); // Hide it
        dayUpdate = (Button) view.findViewById(R.id.day_update);
        dayUpdate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the array
                dayArray = new ArrayList<String>();
                // Get all the checked boxes
                if (daySunday.isChecked()) dayArray.add("Sunday");
                if (dayMonday.isChecked()) dayArray.add("Monday");
                if (dayTuesday.isChecked()) dayArray.add("Tuesday");
                if (dayWednesday.isChecked()) dayArray.add("Wednesday");
                if (dayThursday.isChecked()) dayArray.add("Thursday");
                if (dayFriday.isChecked()) dayArray.add("Friday");
                if (daySaturday.isChecked()) dayArray.add("Saturday");
                // Update the UI
                settingDaysValue.setText(convertDaystoInitials(TextUtils.join(",", dayArray)));
                // Hide the popup
                daysSelectorContainer.setVisibility(View.INVISIBLE);
            }
        });

        // -----------------------------------------------------------------------------------------

        // Title settings
        settingTitle = (RelativeLayout) view.findViewById(R.id.setting_title);
        settingTitleLabel = (TextView) view.findViewById(R.id.setting_title_label);
        settingTitleValue = (TextView) view.findViewById(R.id.setting_title_value);
        settingTitle.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the alertbox
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                // New edit text view
                final EditText updateStatus = new EditText(getActivity());
                updateStatus.setText(settingTitleValue.getText().toString());
                // set the parameters
                alert.setTitle("Title");
                alert.setMessage("Update the Ping title");
                alert.setView(updateStatus);
                alert.setIcon(R.drawable.ui_icon_edit);
                // Set the "Okay" button
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update the user status
                        settingTitleValue.setText(updateStatus.getText().toString());
                    }
                });
                // Set the negative button
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                // Show our alert box
                alert.show();
            }
        });

        // -----------------------------------------------------------------------------------------

        // Sound settings
        settingSound = (RelativeLayout) view.findViewById(R.id.setting_sound);
        settingSoundLabel = (TextView) view.findViewById(R.id.setting_sound_label);
        settingSoundValue = (TextView) view.findViewById(R.id.setting_sound_value);
        settingSound.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundSelectorContainer.setVisibility(View.VISIBLE);
            }
        });

        // Sound selection
        soundSelectorContainer = (RelativeLayout) view.findViewById(R.id.sound_selector_container);
        soundSelectorList = (LinearLayout) view.findViewById(R.id.sound_selector_list);
        soundSelectorListview = (ListView) view.findViewById(R.id.sound_selector_listview);
        soundUpdate = (Button) view.findViewById(R.id.sound_update);
        soundSelectorContainer.setVisibility(View.INVISIBLE); // Hide it
        soundUpdate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundSelectorContainer.setVisibility(View.INVISIBLE);
            }
        });

        // -----------------------------------------------------------------------------------------

        // Category settings
        settingCategory = (RelativeLayout) view.findViewById(R.id.setting_category);
        settingCategoryLabel = (TextView) view.findViewById(R.id.setting_category_label);
        settingCategoryValue = (TextView) view.findViewById(R.id.setting_category_value);
        settingCategory.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                categorySelectorContainer.setVisibility(View.VISIBLE);
            }
        });

        // Category selection
        categorySelectorContainer = (RelativeLayout) view.findViewById(R.id.category_selector_container);
        categorySelectorList = (LinearLayout) view.findViewById(R.id.category_selector_list);
        categorySelectorListview = (ListView) view.findViewById(R.id.category_selector_listview);
        categoryUpdate = (Button) view.findViewById(R.id.category_update);
        categorySelectorContainer.setVisibility(View.INVISIBLE); // Hide it
        categoryUpdate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                categorySelectorContainer.setVisibility(View.INVISIBLE);
            }
        });

        // -----------------------------------------------------------------------------------------

        // Main settings
        settingsListContainer = (RelativeLayout) view.findViewById(R.id.settings_list_container);
        settingsList = (LinearLayout) view.findViewById(R.id.settings_list);

        // The other ones
        settingFadein = (RelativeLayout) view.findViewById(R.id.setting_fadein);
        settingFadeinLabel = (TextView) view.findViewById(R.id.setting_fadein_label);
        settingFadeinValue = (TextView) view.findViewById(R.id.setting_fadein_value);
        settingFadeinValueSeekbar = (SeekBar) view.findViewById(R.id.setting_fadein_value_seekbar);
        settingRepeats = (RelativeLayout) view.findViewById(R.id.setting_repeats);
        settingRepeatsLabel = (TextView) view.findViewById(R.id.setting_repeats_label);
        settingRepeatsValue = (Switch) view.findViewById(R.id.setting_repeats_value);
        settingEnabled = (RelativeLayout) view.findViewById(R.id.setting_enabled);
        settingEnabledLabel = (TextView) view.findViewById(R.id.setting_enabled_label);
        settingEnabledValue = (Switch) view.findViewById(R.id.setting_enabled_value);

        // Make the SeekBar active and set the value
        settingFadeinValueSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar view,int pos, boolean bool) {
                settingFadeinValue.setText(String.valueOf(Math.round(pos/20)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar view) { }
            @Override
            public void onStopTrackingTouch(SeekBar view) { }
        });

        // Update button and call to save the Ping
        pingUpdate = (Button) view.findViewById(R.id.ping_update);
        if (pingType.equals("new")) pingUpdate.setText("Create");

        pingUpdate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Store all our variables to pass the API
                String id = pingId;
                String title = settingTitleValue.getText().toString();
                String time = "2015-01-01 " + settingTimeValue.getText().toString();
                String days = convertInitialsToDays(settingDaysValue.getText().toString());
                String category = categoryId;
                String sound = settingSoundValue.getText().toString();
                String fadein = settingFadeinValue.getText().toString();
                String repeat = settingRepeatsValue.isChecked() ? "1" : "0";
                String enabled = settingEnabledValue.isChecked() ? "1" : "0";

                // Create & sanitize the string
                String queryString = "userid="+ActivityDrawer.userId+"&id="+id+"&title="+title+"&time="+time+"&days="+days+"&category="+category+"&sound="+sound+"&fadein="+fadein+"&repeat="+repeat+"&enabled="+enabled;
                String sanitizedString = ActivityDrawer.sanitizeStringForUrl(queryString);

                // Call theAPI
                if (pingType.equals("new")) {
                    new CreatePingDetail().execute(getString(R.string.api_url) + "/ping/" + ActivityDrawer.userId + "/create/?" + sanitizedString);
                }

                if (pingType.equals("update")) {
                    new UpdatePingDetail().execute(getString(R.string.api_url) + "/ping/" + pingId + "/update/?" + sanitizedString);
                }

            }
        });

        // Get all of the categories, friends & ping detail
        new GetCategoryDataForPing().execute(getString(R.string.api_url) + "/category/"+ ActivityDrawer.userId+"/getlist") ;
        if (pingType.equals("update")) {
            new GetPingDetail().execute(getString(R.string.api_url) + "/ping/" + pingId + "/get/");
        } else {

        }

        // Get the sound clip array
        soundClipArray = getActivity().getResources().getStringArray(R.array.sound_array);

        // Now assign our sound clip array to the adaptor
        soundSelectorListview.setAdapter(new AdaptorSound(getActivity(), soundClipArray));

        // Get the friends of the user
        // Retrieve the user Facebook ID from the shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String sharedPreferencesFacebookId = sharedPref.getString("facebookid", "0");
        // We make a graph request with saved access token from the login in the previous activity
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+sharedPreferencesFacebookId+"/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        // Initialize the variables for storing the IDs
                        // The friendList is the default array we get from Facebook
                        // We itirate over that are store it in a ArrayList
                        // The concatFriendIdList holds a comma seperated list of all the friend FID
                        // Same as the PHP Explode functionality
                        JSONArray friendList;
                        ArrayList<String> friendIdList = new ArrayList<String>();
                        String concatFriendIdList = "";

                        // If there is an error with the Facebook API token
                        if (response.getError() != null) {
                            // Show the user the error
                            // This will probably happen if the user is not logged in
                            new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
                        } else {
                            try {
                                // Get the "data" response object (JSON)
                                friendList = response.getJSONObject().getJSONArray("data");

                                // Iterate through the JSONArray to get friend IDs
                                // Add these to the ArrayList
                                for (int i = 0; i < friendList.length(); i++) {
                                    String userId = response
                                            .getJSONObject()
                                            .getJSONArray("data")
                                            .getJSONObject(i)
                                            .getString("id")
                                            .toString();
                                    friendIdList.add(userId);
                                }

                                // Concatenate the list into a string
                                concatFriendIdList = TextUtils.join(",", friendIdList);

                                // Start the Async process & retrieve the friend list
                                new GetFriendDataForPing().execute(getString(R.string.api_url) + "/ping/"+pingId+"/friends?data="+concatFriendIdList) ;

                            } catch (Exception e) {
                                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
                                Log.e("PIING", "JSON Exception fetching friends list from facebook: "+e.toString());
                            }
                        }
                    }
                }
        ).executeAsync();

        // Return the default view
        return view;
    }

    private class GetPingDetail extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            // do some setup
        }

        protected String doInBackground(String... params) {
            try {
                // setup our okhttp client
                GetDataFromUrl okHttpWrapper = new GetDataFromUrl();
                // return the string from the url
                return okHttpWrapper.getData(params[0]) ;
            } catch (IOException e) {
                // if something breaks, we return an error instead of the json
                Log.e("PIING", e.toString());
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
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

            // Try for the JSON block
            try {

                // Get our data from the returned result in AsyncTask
                JSONArray jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new HashMap to be used for our adaptor
                    jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("id", jsonObject.getString("id"));
                    jsonHashMap.put("title", jsonObject.getString("title"));
                    jsonHashMap.put("createdby", jsonObject.getString("createdby"));
                    jsonHashMap.put("time", jsonObject.getString("time"));
                    jsonHashMap.put("repeatsweekly", jsonObject.getString("repeatsweekly"));
                    jsonHashMap.put("repeatdays", jsonObject.getString("repeatdays"));
                    jsonHashMap.put("category", jsonObject.getString("category"));
                    jsonHashMap.put("categoryid", jsonObject.getString("categoryid"));
                    jsonHashMap.put("sound", jsonObject.getString("sound"));
                    jsonHashMap.put("fadein", jsonObject.getString("fadein"));
                    jsonHashMap.put("enabled", jsonObject.getString("enabled"));
                    jsonHashMap.put("badges", jsonObject.getString("badges"));

                    // set this category id
                    categoryId = jsonObject.getString("categoryid");

                    // Populate the list
                    populateSettingsList();

                }

            } catch (Exception e) {
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
                Log.e("PIING", e.toString());
            }

        }

    }

    private class CreatePingDetail extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            // do some setup
        }

        protected String doInBackground(String... params) {
            try {
                // setup our okhttp client
                GetDataFromUrl okHttpWrapper = new GetDataFromUrl();
                // return the string from the url
                return okHttpWrapper.getData(params[0]) ;
            } catch (IOException e) {
                // if something breaks, we return an error instead of the json
                Log.e("PIING", e.toString());
                // return blank
                return "" ;
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d("PIING", "Progress update: " + progress[0]) ;
        }

        protected void onPostExecute(String result) {

            // First, if the returned JSON is blank
            if (result.equals("")) {
                Log.e("PIING", "API has returned empty");
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

            try {
                // Get our json from our API
                JSONArray jsonArray = new JSONArray(result) ;

                // Deleted?
                Boolean hasBeenCreated = false;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the user object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new hashamp to be used for our adaptor
                    HashMap<String,String> jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("success", jsonObject.getString("success"));

                    // Get teh feedback
                    if (jsonObject.getString("success").equals("yes"))
                        hasBeenCreated = true;

                }

                // If it has been successfully deleted
                if (hasBeenCreated) {
                    new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_created));
                    // Get our fragment manager
                    // Move to our list fragment
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentPingsList fragment = new FragmentPingsList();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else {
                    new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
                }

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }
        }

    }

    private class UpdatePingDetail extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            // do some setup
        }

        protected String doInBackground(String... params) {
            try {
                // setup our okhttp client
                GetDataFromUrl okHttpWrapper = new GetDataFromUrl();
                // return the string from the url
                return okHttpWrapper.getData(params[0]) ;
            } catch (IOException e) {
                // if something breaks, we return an error instead of the json
                Log.e("PIING", e.toString());
                // return blank
                return "" ;
            }
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d("PIING", "Progress update: " + progress[0]) ;
        }

        protected void onPostExecute(String result) {

            // First, if the returned JSON is blank
            if (result.equals("")) {
                Log.e("PIING", "API has returned empty");
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

            try {
                // Get our json from our API
                JSONArray jsonArray = new JSONArray(result) ;

                // Deleted?
                Boolean hasBeenCreated = false;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the user object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new hashamp to be used for our adaptor
                    HashMap<String,String> jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("success", jsonObject.getString("success"));

                    // Get teh feedback
                    if (jsonObject.getString("success").equals("yes"))
                        hasBeenCreated = true;

                }

                // If it has been successfully deleted
                if (hasBeenCreated) {
                    new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_created));
                    // Get our fragment manager
                    // Move to our list fragment
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentPingsList fragment = new FragmentPingsList();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else {
                    new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
                }

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }
        }

    }

    private class GetFriendDataForPing extends AsyncTask<String, Integer, String> {

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

            // Initialize our ArrayList
            data = new ArrayList<HashMap<String, String>>() ;

            // First, if the returned JSON is blank
            if (result.equals("")) {
                Log.e("PIING", "API has returned empty");
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

            try {
                // Get our json from our API
                JSONArray jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the user object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new hashamp to be used for our adaptor
                    HashMap<String,String> jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("id", jsonObject.getString("id"));
                    jsonHashMap.put("facebookid", jsonObject.getString("facebookid"));
                    jsonHashMap.put("name", jsonObject.getString("name"));
                    jsonHashMap.put("badge", jsonObject.getString("badge"));
                    jsonHashMap.put("status", jsonObject.getString("status"));
                    jsonHashMap.put("approved", jsonObject.getString("approved"));
                    jsonHashMap.put("pingid", pingId+"");
                    jsonHashMap.put("showinvitebutton", "yes"); // ping ID

                    // Add the hashmap to the arraylist
                    data.add(jsonHashMap);
                }

                // feed our adaptor our data
                friendsSelectorListview.setAdapter(new AdaptorFriend(getActivity(), data));

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }
        }

    }

    private class GetCategoryDataForPing extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            // do some setup
        }

        protected String doInBackground(String... params) {
            try {
                // setup our okhttp client
                GetDataFromUrl okHttpWrapper = new GetDataFromUrl();
                // return the string from the url
                return okHttpWrapper.getData(params[0]) ;
            } catch (IOException e) {
                // if something breaks, we return an error instead of the json
                Log.e("PIING", e.toString());
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
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

            // Try for the JSON block
            try {

                // Initialize our data ArrayList
                data = new ArrayList<HashMap<String, String>>() ;

                // Get our data from the returned result in AsyncTask
                jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new hashamp to be used for our adaptor
                    HashMap<String,String> jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("id", jsonObject.getString("id"));
                    jsonHashMap.put("pingid", pingId);
                    jsonHashMap.put("title", jsonObject.getString("title"));
                    jsonHashMap.put("pings", jsonObject.getString("pings"));
                    jsonHashMap.put("fromping", "yes");

                    // Add the hashmap to the arraylist
                    data.add(jsonHashMap);

                }

                // Feed our adaptor our data now that we've created a nice ArrayList
                categorySelectorListview.setAdapter(new AdaptorCategory(getActivity(), data));

                // When the user clicks on a list item
                categorySelectorListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View clickedView,
                                            int position, long id) {

                    }
                });
            } catch (Exception e) {
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

        }

    }

    private class DeletePing extends AsyncTask<String, Integer, String> {

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

            // First, if the returned JSON is blank
            if (result.equals("")) {
                Log.e("PIING", "API has returned empty");
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

            try {
                // Get our json from our API
                JSONArray jsonArray = new JSONArray(result) ;

                // Deleted?
                Boolean hasBeenDeleted = false;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the user object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new hashamp to be used for our adaptor
                    HashMap<String,String> jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("success", jsonObject.getString("success"));

                    // Get teh feedback
                    if (jsonObject.getString("success").equals("yes"))
                        hasBeenDeleted = true;

                }

                // If it has been successfully deleted
                if (hasBeenDeleted) {
                    // Get our fragment manager
                    // Move to our list fragment
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentPingsList fragment = new FragmentPingsList();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else {
                    new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
                }

            } catch (Exception e) {
                Log.e("PIING", e.toString());
            }
        }

    }

    public void populateSettingsList() {

        // Get the intials of the weekdays
        settingDaysValue.setText(convertDaystoInitials(jsonHashMap.get("repeatdays")));

        // Set the rest of the plain string variables
        settingTimeValue.setText(jsonHashMap.get("time").split(" ")[1]);
        settingTitleValue.setText(jsonHashMap.get("title"));
        settingCategoryValue.setText(jsonHashMap.get("category"));
        settingSoundValue.setText(jsonHashMap.get("sound"));
        settingFadeinValue.setText(jsonHashMap.get("fadein"));

        // Create the mosaic for the user badges
        createBadgeArrangement(jsonHashMap.get("badges").split(","));



        // If the Ping fades in
        if (jsonHashMap.get("fadein").equals("yes"))
            settingRepeatsValue.setChecked(true);

        // If the Ping is enabled
        if (jsonHashMap.get("enabled").equals("yes"))
            settingEnabledValue.setChecked(true);

    }

    // Convert the days to 2 intials
    public String convertDaystoInitials(String days) {
        String[] dayArray = days.split(",");
        String dayString = "";
        for (int x=0;x<dayArray.length;x++) {
            dayString += dayArray[x].substring(0, 2)+" ";
        }
        return dayString;
    }

    // Convert the day intials back to string
    public String convertInitialsToDays(String days) {
        String[] dayArray = days.split(" ");
        ArrayList<String> weekdayArray = new ArrayList<String>();
        for (int x=0;x<dayArray.length;x++) {
            if (dayArray[x].equals("Mo")) weekdayArray.add("Monday");
            if (dayArray[x].equals("Tu")) weekdayArray.add("Tuesday");
            if (dayArray[x].equals("We")) weekdayArray.add("Wednesday");
            if (dayArray[x].equals("Th")) weekdayArray.add("Thursday");
            if (dayArray[x].equals("Fr")) weekdayArray.add("Friday");
            if (dayArray[x].equals("Sa")) weekdayArray.add("Saturday");
            if (dayArray[x].equals("Su")) weekdayArray.add("Sunday");
        }
        return TextUtils.join(",",weekdayArray);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_ping, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (pingType.equals("new")) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {
            new DeletePing().execute(getString(R.string.api_url) + "/ping/"+pingId+"/delete/") ;
            FragmentManager fragmentManager = getFragmentManager();
            FragmentPingsList fragment = new FragmentPingsList();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        if (id == R.id.action_cancel) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentPingsList fragment = new FragmentPingsList();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        return super.onOptionsItemSelected(item);
    }

    public void closePopupWindows() {
        soundSelectorContainer.setVisibility(View.INVISIBLE);
        categorySelectorContainer.setVisibility(View.INVISIBLE);
        timeSelectorContainer.setVisibility(View.INVISIBLE);
    }

    public void createBadgeArrangement(String[] badges) {

        ArrayList<Integer> usedX = new ArrayList<Integer>();
        ArrayList<Integer> usedY = new ArrayList<Integer>();

        int gridNumber = 5;

        // Get the container details
        LinearLayout.LayoutParams imageProcessParams = (LinearLayout.LayoutParams) pingFriends.getLayoutParams();
        int containerHeight = imageProcessParams.height;
        int containerWidth = imageProcessParams.width;

        for (int c=0; c<badges.length; c++) {

            Boolean foundImagePlacementSuccessfully = false;

            while (!foundImagePlacementSuccessfully) {

                // Get random grid co-ordinates
                int randomXForGrid = Math.round((float) Math.random()*gridNumber) - 1;
                int randomYForGrid = Math.round((float) Math.random()*gridNumber) - 1;
                int randomXForMargin = randomXForGrid*(containerWidth/gridNumber);
                int randomYForMargin = randomYForGrid*(containerHeight/gridNumber);

                if (!usedX.contains(randomXForGrid) && !usedY.contains(randomYForGrid)) {

                    // Tell the loop to stop
                    foundImagePlacementSuccessfully = true;

                    // Add these to the used blocks
                    usedX.add(randomXForGrid);
                    usedY.add(randomYForGrid);

                    // Set the width & height of the image (random)
                    float smallest = 75f;
                    float imageRandom = (float) Math.random()*110;
                    if (imageRandom<smallest) imageRandom = smallest;
                    int imageSize = Math.round(imageRandom);

                    // create the new image
                    ImageView image = new ImageView(getActivity());

                    // set the image background
                    image.setImageResource(getActivity().getResources().getIdentifier(badges[c], "drawable", getActivity().getPackageName()));;

                    // Set the random image dimensions & XY co-ordinates based on the grid
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageSize, imageSize);
                    params.leftMargin = randomXForMargin;
                    params.topMargin = randomYForMargin;

                    // Add the image
                    pingFriends.addView(image, params);

                }

            }


        }
    }

}
