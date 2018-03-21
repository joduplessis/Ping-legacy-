package com.robotmonsterlabs.ping.adaptors;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joduplessis on 15/05/14.
 */
public class AdaptorInvite extends BaseAdapter {

    Context context;
    ArrayList<HashMap<String,String>> data ;
    ImageView friendBadge ;
    TextView pingStatus ;
    TextView pingTitle ;
    ImageButton buttonAccept;
    ImageButton buttonReject ;

    public AdaptorInvite(Context context, ArrayList<HashMap<String,String>> data) {
        this.context = context ;
        this.data = data ;
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
            convertView = inflater.inflate(R.layout.adaptor_invite, null);

            // get our data, basic Hashmap stuff here
            HashMap<String,String> obj = data.get(position);

            // Set the id for use in the inner class
            final String innerClassId = obj.get("id").toString();

            // get the views
            friendBadge = (ImageView) convertView.findViewById(R.id.friend_badge) ;
            pingTitle = (TextView) convertView.findViewById(R.id.ping_title) ;
            pingStatus = (TextView) convertView.findViewById(R.id.ping_status) ;
            buttonAccept = (ImageButton) convertView.findViewById(R.id.button_accept) ;
            buttonReject = (ImageButton) convertView.findViewById(R.id.button_reject) ;

            // Actions for accepting
            buttonAccept.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AcceptInvites().execute(context.getResources().getString(R.string.api_url) + "/invite/"+innerClassId+"/accept/") ;
                    View v = (View) view.getParent();
                    TextView statusLine = (TextView) v.findViewById(R.id.ping_title);
                    ImageButton acceptButton = (ImageButton) v.findViewById(R.id.button_accept);
                    ImageButton rejectButton = (ImageButton) v.findViewById(R.id.button_reject);
                    statusLine.setText("Ping invite accepted.");
                    acceptButton.setVisibility(View.INVISIBLE);
                    rejectButton.setVisibility(View.INVISIBLE);
                }
            });

            // Actions for rejecting
            buttonReject.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new RejectInvites().execute(context.getResources().getString(R.string.api_url) + "/invite/"+innerClassId+"/reject/") ;
                    View v = (View) view.getParent();
                    TextView statusLine = (TextView) v.findViewById(R.id.ping_title);
                    ImageButton acceptButton = (ImageButton) v.findViewById(R.id.button_accept);
                    ImageButton rejectButton = (ImageButton) v.findViewById(R.id.button_reject);
                    statusLine.setText("Ping invite rejected.");
                    acceptButton.setVisibility(View.INVISIBLE);
                    rejectButton.setVisibility(View.INVISIBLE);
                }
            });

            // Set the values
            pingTitle.setText(obj.get("title"));
            pingStatus.setText("Added by "+obj.get("friend_username"));
            friendBadge.setImageResource(context.getResources().getIdentifier(
                    obj.get("friend_badge"),
                    "drawable",
                    context.getPackageName()));

        }

        return convertView;

    }

    private class RejectInvites extends AsyncTask<String, Integer, String> {

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
        }

    }

    private class AcceptInvites extends AsyncTask<String, Integer, String> {

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
        }

    }

}
