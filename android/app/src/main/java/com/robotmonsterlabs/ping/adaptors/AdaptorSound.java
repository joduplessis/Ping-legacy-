package com.robotmonsterlabs.ping.adaptors;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joduplessis on 2015/07/25.
 */
public class AdaptorSound extends BaseAdapter {

    Context context;
    String[] data ;
    Integer id = 0;
    ClosePopupFromSoundPopup callback;

    private Activity parentActivity;

    public AdaptorSound(Context context, String[] data) {

        this.context = context ;
        this.data = data ;

        parentActivity = (ActivityDrawer) context;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception

        try {
            callback = (ClosePopupFromSoundPopup) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ClosePopupFromSoundPopup");
        }
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // the basic system service for inflating the views
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // we check if the view is null, unless scrolling
        if (convertView == null) {

            // assign the XML view to this BaseAdaptor's convertView
            convertView = inflater.inflate(R.layout.adaptor_sound, null);

            // get our data, basic Hashmap stuff here
            final String soundTitle = data[position];

            // get the views
            Button soundButtonLabel = (Button) convertView.findViewById(R.id.button_label);
            ImageButton soundButtonPlay = (ImageButton) convertView.findViewById(R.id.button_play);

            // set the view
            soundButtonLabel.setText(soundTitle);

            // Select sound
            soundButtonLabel.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Make the call to the API
                    // new UpdatePingSound().execute(context.getString(R.string.api_url) + "/ping/" + id + "/update_sound") ;
                    // Call the callback
                    callback.closeAllPopups();
                    callback.updateSoundValue(soundTitle);
                }
            });

            // Play & pause sound event
            soundButtonPlay.setOnClickListener(new Button.OnClickListener() {
                MediaPlayer mediaPlayer = MediaPlayer.create(context, context.getResources().getIdentifier(soundTitle, "raw", context.getPackageName()));
                Boolean isPlaying = false;
                @Override
                public void onClick(View view) {
                    if (isPlaying) {
                        mediaPlayer.pause();
                        isPlaying = false;
                        view.setBackground(context.getResources().getDrawable(R.drawable.play));
                    } else {
                        mediaPlayer.start();
                        isPlaying = true;
                        view.setBackground(context.getResources().getDrawable(R.drawable.pause));
                    }
                }
            });
        }

        return convertView;

    }

    // Get the list of friend to invite to this Ping
    private class UpdatePingSound extends AsyncTask<String, Integer, String> {

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

                }

                // Success
                new ToastMessage().show(context, context.getString(R.string.toast_ping_sound_updated));

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(context, context.getString(R.string.toast_error));
            }
        }

    }

    // Callback interface
    public interface ClosePopupFromSoundPopup {
        public void closeAllPopups();
        public void updateSoundValue(String text);
    }

}
