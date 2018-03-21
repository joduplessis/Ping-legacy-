package com.robotmonsterlabs.ping.adaptors;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AdaptorFriend extends BaseAdapter {

    Context context;
    ArrayList<HashMap<String,String>> data;
    HashMap<String,String> jsonHashMap;

    Button inviteButton ;
    TextView friendStatus ;
    TextView friendName ;
    ImageView friendThumbnail ;

    String pingId ;
    String userId ;

    public AdaptorFriend(Context context, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // the basic system service for inflating the views
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // we check if the view is null, unless scrolling, etc. http://stackoverflow.com/questions/14745780/understanding-convertview-parameter-of-getview-method
        if (convertView == null) {

            // assign the XML view to this BaseAdaptor's convertView
            convertView = inflater.inflate(R.layout.adaptor_friend, null);

            // get our data, basic Hashmap stuff here
            HashMap<String,String> obj = data.get(position);

            // get the views on the adaptor
            inviteButton = (Button) convertView.findViewById(R.id.invite_button);
            friendStatus = (TextView) convertView.findViewById(R.id.friend_status);
            friendName = (TextView) convertView.findViewById(R.id.friend_name);
            friendThumbnail = (ImageView) convertView.findViewById(R.id.friend_thumbnail);

            // fill them with data
            int newImage = context.getResources().getIdentifier(obj.get("badge"), "drawable", context.getApplicationContext().getPackageName());
            friendName.setText(obj.get("name"));
            friendStatus.setText(obj.get("status"));
            friendThumbnail.setImageResource(newImage);

            // Set the fonts
            friendName.setTypeface(ActivityDrawer.FONT_REGULAR);
            friendStatus.setTypeface(ActivityDrawer.FONT_MEDIUM);

            // If it's just a standard friend list, don't show the button
            // Otherwise get the pinginvite
            if (obj.get("showinvitebutton").equals("no")) {
                inviteButton.setVisibility(View.INVISIBLE);
            } else {
                pingId = obj.get("pingid");
                userId = obj.get("id");
                new GetInviteForPing().execute(context.getString(R.string.api_url) + "/invite/" + pingId + "/get?friend=" + userId) ;
            }

        }

        // Default return
        return convertView;

    }

    // Get the list of friend to invite to this Ping
    private class CreateFriendInviteForPing extends AsyncTask<String, Integer, String> {

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
                new ToastMessage().show(context, context.getString(R.string.toast_error));
            }

            try {
                // Get our json from our API
                JSONArray jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the user object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new hashamp
                    HashMap<String,String> jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("success", jsonObject.getString("success"));

                    // Add the hashmap to the arraylist
                    data.add(jsonHashMap);
                }

                // Update button
                inviteButton.setText(context.getString(R.string.button_friend_uninvite));

                // Success
                new ToastMessage().show(context, context.getString(R.string.toast_ping_invite_created));

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(context, context.getString(R.string.toast_error));
            }
        }

    }

    // Get the list of friend to invite to this Ping
    private class DeleteFriendInviteForPing extends AsyncTask<String, Integer, String> {

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
                new ToastMessage().show(context, context.getString(R.string.toast_error));
            }

            try {
                // Get our json from our API
                JSONArray jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the user object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new hashamp
                    HashMap<String,String> jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("success", jsonObject.getString("success"));

                    // Add the hashmap to the arraylist
                    data.add(jsonHashMap);
                }

                // Update button
                inviteButton.setText(context.getString(R.string.button_friend_invite));

                // Success
                new ToastMessage().show(context, context.getString(R.string.toast_ping_invite_deleted));

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(context, context.getString(R.string.toast_error));
            }
        }

    }

    // Get the list of friend to invite to this Ping
    private class GetInviteForPing extends AsyncTask<String, Integer, String> {

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
                new ToastMessage().show(context, context.getString(R.string.toast_error));
            }

            try {
                // Get our json from our API
                JSONArray jsonArray = new JSONArray(result) ;

                // Create a new hashamp
                jsonHashMap = new HashMap<String, String>();

                // Iterate over the data from our json array - only be one
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the user object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Add all the elements data
                    jsonHashMap.put("id", jsonObject.getString("id"));
                    jsonHashMap.put("ping_id", jsonObject.getString("ping_id"));
                    jsonHashMap.put("user_id", jsonObject.getString("user_id"));
                    jsonHashMap.put("approved", jsonObject.getString("approved"));

                }

                final int jsonArrayLenth = jsonArray.length();
                final String inviteId = jsonHashMap.get("id");

                // Then we create it, otherwise we delete it
                if (jsonArrayLenth==0) {
                    inviteButton.setText(context.getString(R.string.button_friend_invite));
                } else {
                    inviteButton.setText(context.getString(R.string.button_friend_uninvite));
                }

                // Click event for the button
                inviteButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get the button instance
                        Button pressedButton = (Button) view.findViewById(R.id.invite_button);
                        // If this ping doesn't exist
                        if (jsonArrayLenth==0) {
                            pressedButton.setText(context.getString(R.string.button_friend_invited));
                            // Send request to API
                            new CreateFriendInviteForPing().execute(context.getString(R.string.api_url) + "/invite/" + pingId + "/create?friend=" + userId) ;
                            // Get the info again
                            new GetInviteForPing().execute(context.getString(R.string.api_url) + "/invite/" + pingId + "/get?friend=" + userId) ;
                        } else {
                            pressedButton.setText(context.getString(R.string.button_friend_uninvited));
                            // Send request to API
                            new DeleteFriendInviteForPing().execute(context.getString(R.string.api_url) + "/invite/" + inviteId + "/delete") ;
                            // Get the info again
                            new GetInviteForPing().execute(context.getString(R.string.api_url) + "/invite/" + pingId + "/get?friend=" + userId) ;
                        }
                    }
                });

                // User feedback
                new ToastMessage().show(context, context.getString(R.string.toast_updated));

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(context, context.getString(R.string.toast_error));
            }
        }

    }
}
