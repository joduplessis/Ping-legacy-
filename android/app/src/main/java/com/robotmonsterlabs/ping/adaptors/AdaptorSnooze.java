package com.robotmonsterlabs.ping.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joduplessis on 15/05/14.
 */
public class AdaptorSnooze extends BaseAdapter {

    Context context;
    ArrayList<HashMap<String,String>> data ;

    ImageView friendBadge ;
    TextView friendTime ;
    TextView friendUsername ;
    TextView friendStatus ;

    public AdaptorSnooze(Context context, ArrayList<HashMap<String,String>> data) {
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
            convertView = inflater.inflate(R.layout.adaptor_snooze, null);

            // get our data, basic Hashmap stuff here
            HashMap<String,String> obj = data.get(position);

            // get the views
            friendBadge = (ImageView) convertView.findViewById(R.id.friend_badge) ;
            friendTime = (TextView) convertView.findViewById(R.id.friend_time) ;
            friendUsername = (TextView) convertView.findViewById(R.id.friend_username) ;
            friendStatus = (TextView) convertView.findViewById(R.id.friend_status) ;

            // Put the Data
            friendBadge.setImageResource(context.getResources().getIdentifier(obj.get("userbadge"), "drawable", context.getPackageName()));
            friendTime.setText(obj.get("time"));
            friendUsername.setText(obj.get("username"));

            // Set the typeface
            friendTime.setTypeface(ActivityDrawer.FONT_MEDIUM);
            friendUsername.setTypeface(ActivityDrawer.FONT_MEDIUM);
            friendStatus.setTypeface(ActivityDrawer.FONT_MEDIUM);

        }

        return convertView;

    }

}
