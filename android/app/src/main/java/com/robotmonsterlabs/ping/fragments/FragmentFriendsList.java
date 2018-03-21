package com.robotmonsterlabs.ping.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.ActivityLogin;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.adaptors.AdaptorCategory;
import com.robotmonsterlabs.ping.adaptors.AdaptorFriend;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentFriendsList extends Fragment {

    View view ;
    ListView friendsList ;
    ArrayList<HashMap<String,String>> friendsListData ;
    Context context;
    ArrayList<HashMap<String, String>> data;

    public FragmentFriendsList() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the view
        view = inflater.inflate(R.layout.fragment_friends_list, container, false);

        // Change the actionbar background color
        ActionBar actionbar = getActivity().getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getActivity().getResources().getColor(R.color.friends));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setTitle("Friends");

        // Get the friends ListView
        friendsList = (ListView) view.findViewById(R.id.friends_listview) ;

        // Retrieve the user Facebook ID from the shared preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
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
                                new GetFriendData().execute(getString(R.string.api_url)+"/friend/"+ ActivityDrawer.userId +"/getlist?data="+concatFriendIdList) ;

                            } catch (Exception e) {
                                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
                                Log.e("PIING", "JSON Exception fetching friends list from facebook: "+e.toString());
                            }
                        }
                    }
                }
        ).executeAsync();

        return view;

    }

    @Override
    public void onStart () {
        super.onStart() ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class GetFriendData extends AsyncTask<String, Integer, String> {

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
                    jsonHashMap.put("name", jsonObject.getString("name"));
                    jsonHashMap.put("badge", jsonObject.getString("badge"));
                    jsonHashMap.put("status", jsonObject.getString("status"));
                    jsonHashMap.put("showinvitebutton", "no");

                    // Add the hashmap to the arraylist
                    data.add(jsonHashMap);
                }

                // feed our adaptor our data
                friendsList.setAdapter(new AdaptorFriend(getActivity(), data));

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }
        }

    }

}
