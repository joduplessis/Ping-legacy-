package com.robotmonsterlabs.ping;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.adaptors.AdaptorSnooze;
import com.robotmonsterlabs.ping.utility.Alarm;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

public class ActivityTrigger extends Activity {

    View view;
    Button buttonSnooze;
    TextView textTitle;
    ListView friendsSnooze;
    MediaPlayer mediaPlayer;
    ImageButton soundOnOff;
    RelativeLayout mainLayout;
    ImageView dragTarget;

    Typeface FONT_REGULAR ;
    Typeface FONT_MEDIUM ;
    Typeface FONT_TIME ;

    Boolean repeats;

    public String pingId = "2";

    // For node
    String createdByName = "";
    String createdByBadge = "";
    String userCount = "";

    String facebookId = "";
    String facebookName = "";
    String userId = "";

    float leftVol = 0f;
    float rightVol = 0f;
    float fadeinDuration ;
    int fadeinDelay = 10;
    final Handler h = new Handler();
    float containerHeight;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Boilerplate init sequence for the fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger);

        Intent intent = getIntent();
        pingId = intent.getStringExtra("id");

        // Screen height
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        containerHeight = size.y;

        // Get the shared preference user id
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        facebookId = sharedPref.getString("facebookid", "0");
        facebookName = sharedPref.getString("facebookname", "0");
        userId = sharedPref.getString("userid", "0");

        // set up the fonts
        FONT_REGULAR = Typeface.createFromAsset(this.getAssets(), "museo_regular.otf");
        FONT_MEDIUM = Typeface.createFromAsset(this.getAssets(), "museo_medium.otf");
        FONT_TIME = Typeface.createFromAsset(this.getAssets(), "HelveticaNeueUltraLight.otf");

        // Find the views
        buttonSnooze = (Button) findViewById(R.id.button_snooze);
        textTitle = (TextView) findViewById(R.id.text_title);
        friendsSnooze = (ListView) findViewById(R.id.friends_snooze);
        soundOnOff = (ImageButton) findViewById(R.id.sound_onoff);
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        dragTarget = (ImageView) findViewById(R.id.drag_target);

        ObjectAnimator.ofFloat(dragTarget, "y", dragTarget.getY(), containerHeight).setDuration(1000).start();

        // Set the resource for the button
        soundOnOff.setBackgroundResource(R.drawable.sound_on);

        // Set the click event
        soundOnOff.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    soundOnOff.setBackgroundResource(R.drawable.sound_off);
                } else {
                    mediaPlayer.start();
                    soundOnOff.setBackgroundResource(R.drawable.sound_on);
                }
            }
        });


        // Gesture down
        mainLayout.setOnTouchListener(new View.OnTouchListener() {

            float difference;
            float original;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction()== MotionEvent.ACTION_DOWN) {
                    original = textTitle.getY();
                    difference = event.getY() - original;
                    ObjectAnimator.ofFloat(dragTarget, "y", dragTarget.getY(), containerHeight-250f).setDuration(1000).start();
                }

                if (event.getAction()== MotionEvent.ACTION_MOVE) {
                    float newY = event.getY() - difference;
                    textTitle.setY(newY);
                    Log.d("PIING", repeats+"");
                }

                // Disable after swipe if it doesn't repeat
                if (event.getAction()== MotionEvent.ACTION_UP) {
                    if (textTitle.getY() > 1750f) {
                        // Kill the sound
                        mediaPlayer.stop();
                        // Hide the text
                        textTitle.setVisibility(View.INVISIBLE);
                        // If it doesn't repeat, then disable it
                        if (!repeats) {
                            new DisablePing().execute(getString(R.string.api_url) + "/ping/" + pingId + "/disable/");
                        }
                        // Go back to the other activity
                         onBackPressed();
                        // finish();
                    } else {
                        ObjectAnimator.ofFloat(textTitle, "y", textTitle.getY(), original).setDuration(1000).start();
                        ObjectAnimator.ofFloat(dragTarget, "y", dragTarget.getY(), containerHeight).setDuration(1000).start();
                    }

                }

                return true;
            }

        });

        // Fonts
        buttonSnooze.setTypeface(FONT_REGULAR);
        textTitle.setTypeface(FONT_TIME);

        // Now call our ASyncMethods
        new GetPingDetail().execute(getString(R.string.api_url) + "/ping/" + pingId + "/get/") ;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class DisablePing extends AsyncTask<String, Integer, String> {

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
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
            }

            // Try for the JSON block
            try {

                // Get our data from the returned result in AsyncTask
                JSONArray jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Add all the elements data
                    // jsonObject.getString("success");

                }

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
            }

        }

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
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
            }

            // Try for the JSON block
            try {

                // Get our data from the returned result in AsyncTask
                JSONArray jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Add all the elements data
                    /*
                    jsonObject.getString("createdby");
                    jsonObject.getString("time");
                    jsonObject.getString("repeatsweekly");
                    jsonObject.getString("repeatdays");
                    jsonObject.getString("category");
                    jsonObject.getString("fadein");
                    jsonObject.getString("enabled");
                    jsonObject.getString("badges");
                    */

                    if (jsonObject.getString("repeatsweekly").equals("true"))
                        repeats = true;
                    else
                        repeats = false;

                    createdByName = jsonObject.getString("createdby");
                    createdByBadge = jsonObject.getString("createdbybadge");
                    userCount = jsonObject.getString("usercount");

                    // Now do the node connections
                    textTitle.setText(jsonObject.getString("title"));

                    // Setup the node connection
                    createNodeJsConnection(jsonObject.getString("id"));

                    // Play the music track
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), getResources().getIdentifier(jsonObject.getString("sound"), "raw", getPackageName()));
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(0f, 0f);
                    mediaPlayer.start();

                    // fadein
                    fadeinDuration = Float.parseFloat(jsonObject.getString("fadein"));
                    h.postDelayed(increaseVol, fadeinDelay);

                }

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
            }

        }

    }

    final Runnable increaseVol = new Runnable(){
        public void run() {
            float incrementAmount = 1f/(fadeinDuration*1000/fadeinDelay);
            mediaPlayer.setVolume(leftVol, rightVol);
            if(leftVol < 1.0f){
                leftVol += incrementAmount;
                rightVol += incrementAmount;
                // Rerun our thread
                h.postDelayed(increaseVol, fadeinDelay);
            }
        }
    };

    public void createNodeJsConnection(String pingId) {

        try {

            // Log.d("PIING", "Sending to Node");

            JSONObject dataForNode = new JSONObject();

            dataForNode.put("userid", userId);
            dataForNode.put("username", createdByName);
            dataForNode.put("userbadge", createdByBadge);
            dataForNode.put("pingid", pingId);
            dataForNode.put("usercount", userCount);
            dataForNode.put("time", System.currentTimeMillis() / 1000L+"");

            mSocket.connect();
            mSocket.emit("updateUsers", dataForNode);

        } catch(JSONException e) {
            Log.e("PIING", "ERROR "+e.toString());
        }

        mSocket.on("updateUserList", updateUserList);

    }

    // Node socket
    // The URL is hard set because STRING get intialized after we need them
    // So the IP is just hard set here - it's the only place, everywhere else
    // The STRINGS file is intialized and used
    {
        try {
            mSocket = IO.socket("http://46.101.59.60:3000");
        } catch (URISyntaxException e) {
            Log.e("PIING", e.toString());
        }
    }

    // Emitter
    private Emitter.Listener updateUserList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        // Get our data from the returned result in AsyncTask
                        JSONArray jsonArray = (JSONArray) args[0];

                        // Log
                        // Log.d("PIING", "... Returned data -> " + jsonArray);

                        // Initialize our data array
                        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

                        // Iterate over the data from our json array
                        for (int i = 0; i < jsonArray.length(); i++) {

                            // Get the object at the index
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            // Create a new hashamp to be used for our adaptor
                            HashMap<String, String> jsonHashMap = new HashMap<String, String>();

                            // Add all the elements data
                            jsonHashMap.put("userid", jsonObject.getString("userid"));
                            jsonHashMap.put("username", jsonObject.getString("username"));
                            jsonHashMap.put("userbadge", jsonObject.getString("userbadge"));
                            jsonHashMap.put("pingid", jsonObject.getString("pingid"));
                            jsonHashMap.put("time", jsonObject.getString("time"));

                            data.add(jsonHashMap);

                        }

                        friendsSnooze.setAdapter(new AdaptorSnooze(getApplicationContext(), data));

                    } catch (JSONException e) {
                        Log.e("PIING", e.toString());
                    }
                }
            });
        }
    };


}
