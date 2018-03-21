package com.robotmonsterlabs.ping.fragments;


import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.adaptors.AdaptorInvite;
import com.robotmonsterlabs.ping.adaptors.AdaptorPing;
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
public class FragmentInvitesList extends Fragment {

    View view;
    ListView invitesList;
    ArrayList<HashMap<String, String>> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view
        view = inflater.inflate(R.layout.fragment_invites_list, container, false);

        // Change the actionbar background color
        ActionBar actionbar = getActivity().getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getActivity().getResources().getColor(R.color.invites));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setTitle("Invites");

        // Get the views
        invitesList = (ListView) view.findViewById(R.id.invites_list);

        // Get all of the invites, needs proper ID
        new GetInvites().execute(getString(R.string.api_url)+"/invite/"+ ActivityDrawer.userId+"/getlist") ;

        // Return it
        return view;
    }

    private class GetInvites extends AsyncTask<String, Integer, String> {

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
                JSONArray jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new hashamp to be used for our adaptor
                    HashMap<String,String> jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("id", jsonObject.getString("id"));
                    jsonHashMap.put("title", jsonObject.getString("title"));
                    jsonHashMap.put("friend_username", jsonObject.getString("friend_username"));
                    jsonHashMap.put("friend_badge", jsonObject.getString("friend_badge"));

                    // Add the hashmap to the arraylist
                    data.add(jsonHashMap);

                }

                // Feed our adaptor our data now that we've created a nice ArrayList
                invitesList.setAdapter(new AdaptorInvite(getActivity(), data));


            } catch (Exception e) {
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

        }

    }

}
