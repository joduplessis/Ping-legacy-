package com.robotmonsterlabs.ping.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joduplessis on 15/05/14.
 */
public class AdaptorDrawer extends BaseAdapter {

    Context context;
    String[] data ;

    public AdaptorDrawer(Context context, String[] data) {
        this.context = context ;
        this.data = data ;
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

        // we check if the view is null, unless scrolling, etc. http://stackoverflow.com/questions/14745780/understanding-convertview-parameter-of-getview-method
        if (convertView == null) {

            // assign the XML view to this BaseAdaptor's convertView
            convertView = inflater.inflate(R.layout.adaptor_drawer, null);

            // get our data, basic Hashmap stuff here
            String menuTitle = data[position];

            // get the view
            TextView drawerMenuText = (TextView) convertView.findViewById(R.id.settings_label);
            TextView drawerMenuValue = (TextView) convertView.findViewById(R.id.settings_value);

            // set the view
            drawerMenuText.setText(menuTitle);

            // Set the color
            if (menuTitle.equals("Pings")) {
                drawerMenuText.setTextColor(context.getResources().getColor(R.color.pings));
                drawerMenuValue.setBackgroundResource(context.getResources().getIdentifier("drawer_count_background_pings", "drawable", context.getPackageName()));
            }
            if (menuTitle.equals("Categories")) {
                drawerMenuText.setTextColor(context.getResources().getColor(R.color.categories));
                drawerMenuValue.setBackgroundResource(context.getResources().getIdentifier("drawer_count_background_categories", "drawable", context.getPackageName()));
            }
            if (menuTitle.equals("Friends")) {
                drawerMenuText.setTextColor(context.getResources().getColor(R.color.friends));
                drawerMenuValue.setBackgroundResource(context.getResources().getIdentifier("drawer_count_background_friends", "drawable", context.getPackageName()));
            }
            if (menuTitle.equals("Invites")) {
                drawerMenuText.setTextColor(context.getResources().getColor(R.color.invites));
                drawerMenuValue.setBackgroundResource(context.getResources().getIdentifier("drawer_count_background_invites", "drawable", context.getPackageName()));
            }
            if (menuTitle.equals("Profile")) {
                drawerMenuText.setTextColor(context.getResources().getColor(R.color.profile));
                drawerMenuValue.setVisibility(View.INVISIBLE);
            }

        }

        return convertView;

    }

}
