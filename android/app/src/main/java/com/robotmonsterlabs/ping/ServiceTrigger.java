package com.robotmonsterlabs.ping;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import android.app.AlarmManager;

import com.robotmonsterlabs.ping.utility.Alarm;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class ServiceTrigger extends IntentService {

    Handler triggerNextPingHandler = new Handler();
    Handler getNextPingHandler = new Handler();

    int triggerInterval = 2000; // 2 seconds
    int getInterval = 1000*75; // 1 minute 15 seconds, to make sure it's not in the same minute

    HashMap<String,String> data;

    int hour ;
    int minute ;
    int weekday ;

    public ServiceTrigger() {
        super("ServiceTrigger");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Check for ping updates every hour
        getNextPingHandler.postDelayed(GetPingRunnable, getInterval);
        triggerNextPingHandler.postDelayed(TriggerPingRunnable, triggerInterval);

        // We do this once to kick off the service
        getNextPing();

        // Handle intents
        if (intent != null) {
            final String action = intent.getAction();
        }
    }

    @Override
     public int onStartCommand(Intent intent, int flags, int startId) {

        // Tell the user it's started
        Toast.makeText(this, "Starting up Ping service", Toast.LENGTH_SHORT).show();

        // Default return
        return super.onStartCommand(intent,flags,startId);
    }

    final Runnable GetPingRunnable = new Runnable(){
        public void run(){
            // Get today's day
            getNextPing();
            getNextPingHandler.postDelayed(GetPingRunnable, getInterval);
        }
    };

    final Runnable TriggerPingRunnable = new Runnable(){
        public void run(){
            // Get today's day
            triggerNextPing();
            triggerNextPingHandler.postDelayed(TriggerPingRunnable, triggerInterval);
        }
    };

    public void getNextPing() {

        // Get today's details
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        weekday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekday==0) weekday = 7; // API starts on 'monday' not 'sunday'

        // Get the shared preference user id
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        new GetNewPing().execute(getString(R.string.api_url) +
                "/ping/" +
                sharedPref.getString("userid", "0") +
                "/get_next_ping/?day=" + weekday + "&hour=" + hour + "&minute=" + minute) ;

        /*
        Log.d("PIING", "Adding dummy data");
        data = new HashMap<String,String>();
        data.put("id","1");
        data.put("time","2015-01-01 10:43:00");
        data.put("title","A ping!");
        data.put("sound", "alarm_one");
        data.put("fadein","5");
        */

    }

    public void triggerNextPing() {

        // If our ping object isn't null
        if (data!=null) {

            Log.d("PIING", "Ping trigger - data is not null, ping scheduled");

            // Update the daily details to compare pings
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            weekday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (weekday==0) weekday = 7; // API start on 'monday' not 'sunday'

            // Get the stored ping details
            String pingTime = data.get("time").split(" ")[1];
            String pingHour = pingTime.split(":")[0];
            String pingMinute = pingTime.split(":")[1];

            /*
            Log.d("PIING", "pingTime >"+pingTime);
            Log.d("PIING", "Current hour >"+hour);
            Log.d("PIING", "Current minute >"+minute);
            */

            // If the time is now, then trigger it
            if (pingHour.equals(hour+"")&&pingMinute.equals(minute+"")) {

                // Trigger our ping, and give it the id
                Intent triggerIntent = new Intent(getApplicationContext(), ActivityTrigger.class);
                triggerIntent.putExtra("id",data.get("id"));
                triggerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(triggerIntent);

                // This ensures the ping isn't trigger repeatedly
                data = null;

                // Call this (outside of the handler) to get the next one right away
                getNextPing();

            }

        } else {

            Log.d("PIING", "Ping trigger - data is null, NO ping scheduled");

        }

    }

    private class GetNewPing extends AsyncTask<String, Integer, String> {

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

                // If there is nothing returned (user doesn't exist)
                if (jsonArray.length()!=0) {

                    // Iterate over the data from our json array
                    for (int i = 0; i < jsonArray.length(); i++) {

                        // Get the object at the index - only one is returned here
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // Store the variable
                        data = new HashMap<String,String>();
                        data.put("id",jsonObject.getString("id"));
                        data.put("time",jsonObject.getString("time"));
                        data.put("title",jsonObject.getString("title"));
                        data.put("sound", jsonObject.getString("sound"));
                        data.put("fadein",jsonObject.getString("fadein"));

                        Log.d("PIING", jsonObject.getString("title"));

                        // Alarm alarm = new Alarm();
                        // alarm.cancelAlarm(getApplicationContext());
                        // alarm.setAlarm(getApplicationContext(), data);

                    }
                }

            } catch (Exception e) {
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
                Log.e("PIING", e.toString());
            }

        }

    }


}
