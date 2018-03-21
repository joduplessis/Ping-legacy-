package com.robotmonsterlabs.ping.fragments;


import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.ActivityTrigger;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.adaptors.AdaptorCategory;
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
public class FragmentPingsList extends Fragment {

    View view;
    ListView pingsList;
    ArrayList<HashMap<String, String>> data;
    LinearLayout pingsCategories;
    RelativeLayout pingsListLayout;
    JSONArray jsonArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get the view
        view = inflater.inflate(R.layout.fragment_pings_list, container, false);

        // Change the actionbar background color
        ActionBar actionbar = getActivity().getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getActivity().getResources().getColor(R.color.pings));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setTitle("Pings");

        // Get all the views
        pingsCategories = (LinearLayout) view.findViewById(R.id.pings_categories);
        pingsListLayout = (RelativeLayout) view.findViewById(R.id.pings_list_layout);
        pingsList = (ListView) view.findViewById(R.id.pings_list);

        // Hide the category bar at the bottom for now
        pingsCategories.setVisibility(View.INVISIBLE);

        // Get all of the categories, needs proper ID
        new GetPings().execute(getString(R.string.api_url) + "/ping/" + ActivityDrawer.userId+ "/getlist") ;

        // Return the default view
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        // if they press the delete button
        if (id == R.id.action_add) {
            // Get our fragment manager
            FragmentManager fragmentManager = getFragmentManager();
            FragmentPingsDetail fragment = new FragmentPingsDetail();
            fragment.pingId = "0";
            fragment.pingType = "new";
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_ping_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class GetPings extends AsyncTask<String, Integer, String> {

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

                // Display the count of the array and display the whale pic if it's 0
                // if (jsonArray.length()==0) {
                if (jsonArray.length()==0) {
                    ImageView whaleImage = new ImageView(getActivity());
                    whaleImage.setImageResource(R.drawable.whale);
                    whaleImage.setScaleX(0.5f);
                    whaleImage.setScaleY(0.5f);
                    pingsListLayout.addView(whaleImage);
                }

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // Create a new hashamp to be used for our adaptor
                    HashMap<String, String> jsonHashMap = new HashMap<String, String>();

                    // Split the string we got from API
                    String[] dateArray = jsonObject.getString("time").split(" ");
                    String[] timeArray = dateArray[1].split(":");
                    String timeString = timeArray[0]+":"+timeArray[1];

                    // Add all the elements data
                    jsonHashMap.put("id", jsonObject.getString("id"));
                    jsonHashMap.put("title", jsonObject.getString("title"));
                    jsonHashMap.put("createdby", jsonObject.getString("createdby"));
                    jsonHashMap.put("time", timeString);
                    jsonHashMap.put("repeatsweekly", jsonObject.getString("repeatsweekly"));
                    jsonHashMap.put("repeatdays", jsonObject.getString("repeatdays"));
                    jsonHashMap.put("badges", jsonObject.getString("badges"));
                    jsonHashMap.put("owner", jsonObject.getString("owner"));

                    // Add the hashmap to the arraylist
                    data.add(jsonHashMap);
                }

                // Feed our adaptor our data now that we've created a nice ArrayList
                pingsList.setAdapter(new AdaptorPing(getActivity(), data));

                // When the user clicks on a list item
                pingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View clickedView,
                                            int position, long id) {
                        // Get our fragment manager
                        if (data.get(position).get("owner").equals("true")) {
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentPingsDetail fragment = new FragmentPingsDetail();
                            fragment.pingId = data.get(position).get("id");
                            fragment.pingType = "update";
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        } else {
                            new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_ping_nonowner));
                        }
                    }
                });
            } catch (Exception e) {
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

            // Test trigger our ping, and give it the id
            /*
            Intent triggerIntent = new Intent(getActivity().getApplicationContext(), ActivityTrigger.class);
            triggerIntent.putExtra("id","1");
            triggerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().getApplication().startActivity(triggerIntent);
            */

        }

    }
}
