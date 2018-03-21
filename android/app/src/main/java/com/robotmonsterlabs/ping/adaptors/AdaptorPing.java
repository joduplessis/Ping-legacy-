package com.robotmonsterlabs.ping.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by joduplessis on 15/05/14.
 */
public class AdaptorPing extends BaseAdapter {

    Context context;
    ArrayList<HashMap<String,String>> data ;

    TextView pingTime ;
    TextView pingTitle ;
    TextView pingTimeremaining ;
    TextView pingDays ;
    ImageView pingDays_icon ;
    TextView pingTagged ;
    ImageView pingTagged_icon ;
    RelativeLayout pingFriendList ;

    public AdaptorPing(Context context, ArrayList<HashMap<String,String>> data) {
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
            convertView = inflater.inflate(R.layout.adaptor_ping, null);

            // get our data, basic Hashmap stuff here
            HashMap<String,String> obj = data.get(position);

            // get the views
            pingTime = (TextView) convertView.findViewById(R.id.ping_time);
            pingTitle = (TextView) convertView.findViewById(R.id.ping_title);
            pingTimeremaining = (TextView) convertView.findViewById(R.id.ping_timeremaining);
            pingDays = (TextView) convertView.findViewById(R.id.ping_days);
            pingDays_icon = (ImageView) convertView.findViewById(R.id.ping_days_icon);
            pingTagged_icon = (ImageView) convertView.findViewById(R.id.ping_tagged_icon);
            pingTagged = (TextView) convertView.findViewById(R.id.ping_tagged);

            // This is the container
            pingFriendList = (RelativeLayout) convertView.findViewById(R.id.ping_friend_list);
            createBadgeArrangement(obj.get("badges").split(","));

            // hide the repeat icon if it doesn't repeat
            if (obj.get("repeatsweekly").equals("no"))
                pingDays_icon.setVisibility(View.INVISIBLE);

            // Set the data for the ping
            pingTime.setText(obj.get("time"));

            // If the Ping is disabled
            if (obj.get("enabled")=="0") {
                pingTitle.setText(obj.get("title")+", Disabled");
            } else {
                pingTitle.setText(obj.get("title"));
            }
            pingTagged.setText(obj.get("createdby"));
            pingDays.setText(convertDaystoInitials(obj.get("repeatdays")));

            // Here we manage the time left
            if (isPingTriggeringToday(obj.get("repeatdays"))) {

                // Split the time from the API
                String[] timeParts = obj.get("time").split(":");

                // Default calendar
                Calendar now = Calendar.getInstance();

                // convert these to usable objects
                int timeHoursPing = Integer.parseInt(timeParts[0]);
                int timeMinutesPing = Integer.parseInt(timeParts[1]);
                int timeHoursNow = now.get(Calendar.HOUR_OF_DAY);
                int timeMinutesNow = now.get(Calendar.MINUTE);
                int timeLeftHours = timeHoursPing - timeHoursNow - 1;
                int timeLeftMinutes = 60 - (timeMinutesNow - timeMinutesPing);

                if (timeLeftHours==-1) {
                    timeLeftHours = 0;
                    timeLeftMinutes -= 60;
                }

                if (timeLeftHours<-1) {
                    timeLeftHours = 0;
                    timeLeftMinutes = 0;
                }

                if (timeLeftMinutes<0 && timeLeftHours==0) {
                    timeLeftMinutes = 0;
                }

                Log.d("PIING", "timeHoursPing "+timeHoursPing+"");
                Log.d("PIING", "timeMinutesPing "+timeMinutesPing+"");
                Log.d("PIING", "timeHoursNow "+timeHoursNow +"");
                Log.d("PIING", "timeMinutesNow "+timeMinutesNow +"");
                Log.d("PIING", "timeLeftHours "+timeLeftHours +"");
                Log.d("PIING", "timeLeftMinutes "+timeLeftMinutes +"");

                pingTimeremaining.setText(timeLeftHours+"h"+timeLeftMinutes+"m");

            }

            // Set the fonts
            pingTime.setTypeface(ActivityDrawer.FONT_TIME);
            pingTitle.setTypeface(ActivityDrawer.FONT_MEDIUM);
            pingTimeremaining.setTypeface(ActivityDrawer.FONT_MEDIUM);
            pingDays.setTypeface(ActivityDrawer.FONT_MEDIUM);
            pingTagged.setTypeface(ActivityDrawer.FONT_MEDIUM);

        }

        return convertView;

    }

    public String convertDaystoInitials(String days) {
        String dayString = "";
        if (days.length()>0) {
            String[] dayArray = days.split(",");
            for (int x = 0; x < dayArray.length; x++) {
                dayString += dayArray[x].substring(0, 1) + " ";
            }
        }
        return dayString;
    }

    public Boolean isPingTriggeringToday(String days) {
        Boolean isToday = false;
        if (days!="") {
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.UK);
            String asWeek = dateFormat.format(now);
            String[] dayArray = days.split(",");
            for (int x=0;x<dayArray.length;x++) {
                if (dayArray[x].equals(asWeek))
                    isToday = true;
            }
        }
        return isToday;
    }

    public void createBadgeArrangement(String[] badges) {

        ArrayList<Integer> usedX = new ArrayList<Integer>();
        ArrayList<Integer> usedY = new ArrayList<Integer>();

        int gridNumber = 5;

        // Get the container details
        RelativeLayout.LayoutParams imageProcessParams = (RelativeLayout.LayoutParams) pingFriendList.getLayoutParams();
        int containerWidth = imageProcessParams.width ;
        int containerHeight = imageProcessParams.height;

        for (int c=0; c<badges.length; c++) {

            Boolean foundImagePlacementSuccessfully = false;

            while (!foundImagePlacementSuccessfully) {

                // Get random grid co-ordinates
                int randomXForGrid = Math.round((float) Math.random()*gridNumber) - 1;
                int randomYForGrid = Math.round((float) Math.random()*gridNumber) - 1;

                if (!usedX.contains(randomXForGrid) && !usedY.contains(randomYForGrid)) {

                    // Get the co-ordinates
                    int randomXForMargin = randomXForGrid*(containerWidth/gridNumber);
                    int randomYForMargin = randomYForGrid*(containerHeight/gridNumber);

                    // Tell the loop to stop
                    foundImagePlacementSuccessfully = true;

                    // Add these to the used blocks
                    usedX.add(randomXForGrid);
                    usedY.add(randomYForGrid);

                    // Set the width & height of the image (random)
                    float smallest = 75f;
                    float imageRandom = (float) Math.random()*110;
                    if (imageRandom<smallest) imageRandom = smallest;
                    int imageSize = Math.round(imageRandom);

                    // create the new image
                    ImageView image = new ImageView(context);

                    // set the image background
                    image.setImageResource(context.getResources().getIdentifier(badges[c], "drawable", context.getPackageName()));;

                    // Set the random image dimensions & XY co-ordinates based on the grid
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageSize, imageSize);
                    params.leftMargin = randomXForMargin;
                    params.topMargin = randomYForMargin;

                    // Add the image
                    pingFriendList.addView(image, params);

                }

            }


        }
    }

}
