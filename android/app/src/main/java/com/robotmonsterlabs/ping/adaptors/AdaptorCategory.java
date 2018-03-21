package com.robotmonsterlabs.ping.adaptors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.fragments.FragmentPingsDetail;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AdaptorCategory extends BaseAdapter {

    Context context;
    ArrayList<HashMap<String,String>> data;
    CheckBox iconChecked ;
    TextView categoryLabel ;
    TextView categoryCount ;
    Integer id ;
    String title;
    String categoryId;
    ClosePopupFromCategoryPopup callback;

    private Activity parentActivity;

    public Integer pingId;

    public AdaptorCategory(Context context, ArrayList<HashMap<String, String>> data) {

        this.context = context;
        this.data = data;

        parentActivity = (ActivityDrawer) context;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception

        try {
            callback = (ClosePopupFromCategoryPopup) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ClosePopupFromSoundPopup");
        }

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
            convertView = inflater.inflate(R.layout.adaptor_category, null);

            // get our data, basic Hashmap stuff here
            HashMap<String,String> obj = data.get(position);

            // we set the id of this category
            title = obj.get("title");
            categoryId = obj.get("id");
            id = Integer.getInteger(categoryId);

            // get all of the views in our adaptor
            iconChecked = (CheckBox) convertView.findViewById(R.id.icon_checked) ;
            categoryLabel = (TextView) convertView.findViewById(R.id.category_label) ;
            categoryCount = (TextView) convertView.findViewById(R.id.category_count) ;

            //set the 2 field counts
            categoryLabel.setText(obj.get("title"));
            categoryCount.setText(obj.get("pings")+" Pings");

            // just for the sake of the app SO FAR, we hide this
            // show this for the meantime
            iconChecked.setVisibility(View.INVISIBLE);

            // set the fonts
            categoryLabel.setTypeface(ActivityDrawer.FONT_MEDIUM);
            categoryCount.setTypeface(ActivityDrawer.FONT_REGULAR);

            // If it's called from the the ping fragment
            if (obj.get("fromping").equals("yes")) {
                // If the ping called the adaptor, they would pass this variable
                convertView.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Call the callback
                        callback.closeAllPopups();
                        callback.updateCategoryValue(title, categoryId);
                        // Make the call to the API
                        // new UpdatePingCategory().execute(context.getString(R.string.api_url) + "/ping/" + pingId + "/update_category") ;
                    }
                });
            }

        }

        return convertView;

    }

    // Get the list of friend to invite to this Ping
    private class UpdatePingCategory extends AsyncTask<String, Integer, String> {

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

                // Success
                new ToastMessage().show(context, context.getString(R.string.toast_ping_category_updated));

            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(context, context.getString(R.string.toast_error));
            }
        }

    }

    // Callback interface
    public interface ClosePopupFromCategoryPopup {
        public void closeAllPopups();
        public void updateCategoryValue(String text, String id);
    }
}
