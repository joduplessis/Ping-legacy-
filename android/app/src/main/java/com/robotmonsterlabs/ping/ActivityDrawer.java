package com.robotmonsterlabs.ping;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.robotmonsterlabs.ping.adaptors.*;
import com.robotmonsterlabs.ping.fragments.*;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.OpenWebAddress;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ActivityDrawer extends Activity implements ListView.OnItemClickListener,
                                                        AdaptorSound.ClosePopupFromSoundPopup,
                                                        AdaptorCategory.ClosePopupFromCategoryPopup {

    private String[] drawerMenuItems;
    private DrawerLayout  drawerLayout;
    private ListView drawerList;
    private CharSequence drawerTitle ;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout drawerLinearLayout ;

    public ImageView drawerBadge;
    public TextView drawerUsername;
    public TextView drawerStatus;

    private String userToken = "";

    public HashMap<String,String> jsonHashMap;

    public static Typeface FONT_REGULAR ;
    public static Typeface FONT_MEDIUM ;
    public static Typeface FONT_TIME ;

    public static String userId = "13";
    public static String facebookId;
    public static String facebookName;

    Button drawerHelp;
    Button drawerLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Mandatory super call
        super.onCreate(savedInstanceState);

        // set the default content view
        setContentView(R.layout.activity_drawer);

        // Get the shared preference user id
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        facebookId = sharedPref.getString("facebookid", "0");
        facebookName = sharedPref.getString("facebookname", "0");
        userId = sharedPref.getString("userid", "0");

        // For the console, so we know what's happening
        Log.d("PIING", facebookName+" logged in with ID: "+userId+" & Facebook ID: "+facebookId);

        // if the fragment container exists, we set hte default fragment
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) { return; }
            FragmentPingsList firstFragment = new FragmentPingsList() ;
            getFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment, "PROFILE").commit();
        }

        // set up the fonts
        FONT_REGULAR = Typeface.createFromAsset(this.getAssets(), "museo_regular.otf");
        FONT_MEDIUM = Typeface.createFromAsset(this.getAssets(), "museo_medium.otf");
        FONT_TIME = Typeface.createFromAsset(this.getAssets(), "HelveticaNeueUltraLight.otf");

        //set the action bar title
        setTitle("Ping");

        /*

        // Get our buttons
        drawerHelp = (Button) findViewById(R.id.drawer_help);
        drawerLogout = (Button) findViewById(R.id.drawer_logout);
        drawerHelp.setTypeface(FONT_REGULAR);
        drawerLogout.setTypeface(FONT_REGULAR);

        // Clicks
        drawerHelp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                new OpenWebAddress().open(getApplicationContext(), "http://www.robotmonsterlabs.com/ping");
            }
        });
        drawerLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("userid", "");
                editor.putString("facebookid", "");
                editor.putString("facebookname", "");
                editor.apply();
                userId = "";
                facebookId = "";
                facebookName = "";
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_logout));
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
            }
        });

        // get the linear layout view that the menu resides in
        drawerLinearLayout = (LinearLayout) findViewById(R.id.drawer_linear_layout);

        // get the drawer menu items from our string resource file
        drawerMenuItems = getResources().getStringArray(R.array.drawer_array);

        // get the actual draw list view, inside the drawerLayout
        drawerList = (ListView) findViewById(R.id.drawer_list);

        // Set the adapter for the list view from our adaptor
        drawerList.setAdapter(new AdaptorDrawer(this, drawerMenuItems));

        // Set the list's click listener, we're extending implementing it in our class
        drawerList.setOnItemClickListener(this);

        // Get the user drawer elements
        drawerBadge = (ImageView) findViewById(R.id.drawer_badge);
        drawerUsername = (TextView) findViewById(R.id.drawer_username);
        drawerStatus = (TextView) findViewById(R.id.drawer_userstatus);
        drawerUsername.setTypeface(FONT_REGULAR);
        drawerStatus.setTypeface(FONT_MEDIUM);

        // set the home button
        getActionBar().setIcon(R.drawable.ui_icon_menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // get the drawer layout view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // drawer toggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ui_icon_menu,
                R.string.drawer_open,
                R.string.drawer_close) {

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };

        // Activate it
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Get all of the categories, needs proper ID
        new GetDrawerData().execute(getString(R.string.api_url) + "/drawer/"+userId+"/get") ;

        */

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        // Mandatory call
        super.onPostCreate(savedInstanceState);

        // Get our buttons
        drawerHelp = (Button) findViewById(R.id.drawer_help);
        drawerLogout = (Button) findViewById(R.id.drawer_logout);
        drawerHelp.setTypeface(FONT_REGULAR);
        drawerLogout.setTypeface(FONT_REGULAR);

        // Clicks
        drawerHelp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                new OpenWebAddress().open(getApplicationContext(), "http://www.robotmonsterlabs.com/ping");
            }
        });
        drawerLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("userid", "");
                editor.putString("facebookid", "");
                editor.putString("facebookname", "");
                editor.apply();
                userId = "";
                facebookId = "";
                facebookName = "";
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_logout));
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
            }
        });

        // get the linear layout view that the menu resides in
        drawerLinearLayout = (LinearLayout) findViewById(R.id.drawer_linear_layout);

        // get the drawer menu items from our string resource file
        drawerMenuItems = getResources().getStringArray(R.array.drawer_array);

        // get the actual draw list view, inside the drawerLayout
        drawerList = (ListView) findViewById(R.id.drawer_list);

        // Set the adapter for the list view from our adaptor
        drawerList.setAdapter(new AdaptorDrawer(this, drawerMenuItems));

        // Set the list's click listener, we're extending implementing it in our class
        drawerList.setOnItemClickListener(this);

        // Get the user drawer elements
        drawerBadge = (ImageView) findViewById(R.id.drawer_badge);
        drawerUsername = (TextView) findViewById(R.id.drawer_username);
        drawerStatus = (TextView) findViewById(R.id.drawer_userstatus);
        drawerUsername.setTypeface(FONT_REGULAR);
        drawerStatus.setTypeface(FONT_MEDIUM);

        // set the home button
        getActionBar().setIcon(R.drawable.ui_icon_menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // get the drawer layout view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // drawer toggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ui_icon_menu,
                R.string.drawer_open,
                R.string.drawer_close) {

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };

        // Activate it
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Get all of the categories, needs proper ID
        new GetDrawerData().execute(getString(R.string.api_url) + "/drawer/"+userId+"/get") ;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        // We do this in each fragment
        // getMenuInflater().inflate(R.menu.menu_activity_drawer, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // placeholder for loading an activity
        // Intent myIntent = new Intent(ActivityApp.this, ActivityLogin.class);
        // myIntent.putExtra("key", "value");
        // ActivityApp.this.startActivity(myIntent);

        // set up teh fragment transactions
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // set up custom animations
        transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out);

        // get the id of the menu item
        int id = item.getItemId();

        // when they press back
        if (id == R.id.action_back)
            transaction.replace(R.id.fragment_container, new FragmentFriendsList());

        // commit the animations
        transaction.commit();

        // have no idea abour this line here VVV - why?
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {

        // get the label of the menu item, based on the array
        String menuLabelClicked = drawerMenuItems[position] ;

        // Get our fragment manager
        FragmentManager fragmentManager = getFragmentManager();

        // if the user presses Pings
        if (menuLabelClicked.equals("Pings")) {
            FragmentPingsList fragment = new FragmentPingsList();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        // if the user presses Categories
        if (menuLabelClicked.equals("Categories")) {
            FragmentCategoriesList fragment = new FragmentCategoriesList();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        // if the user presses Friends
        if (menuLabelClicked.equals("Friends")) {
            FragmentFriendsList fragment = new FragmentFriendsList();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        // if the user presses Invites
        if (menuLabelClicked.equals("Invites")) {
            FragmentInvitesList fragment = new FragmentInvitesList();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        // if the user presses Profile
        if (menuLabelClicked.equals("Profile")) {
            FragmentProfile fragment = new FragmentProfile();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        // if the user presses Token
        if (menuLabelClicked.equals("Token")) {
            FragmentToken fragment = new FragmentToken();
            fragment.token = userToken;
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }

        // Highlight the selected item, update the title, and close the drawer
        drawerList.setItemChecked(position, true);

        // set the title from the array position for the one that is clicked
        setTitle(menuLabelClicked);

        // close the drawer again, used to be the listview, now we close the enclosing layout
        // drawerLayout.closeDrawer(drawerList); <-
        drawerLayout.closeDrawer(drawerLinearLayout);

    }

    @Override
    public void setTitle(CharSequence title) {

        drawerTitle = title;
        getActionBar().setTitle(drawerTitle);

        // Setting the typeface of the action gives us an error so far
        // We need to fix that

        // Spannable text = new SpannableString(getActionBar().getTitle());
        // text.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        // text.setTypeface(FONT_MEDIUM);
        // getActionBar().setTitle(text);

        //int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        // int titleId = R.id.action_bar_title;
        // TextView yourTextView = (TextView) findViewById(titleId);
        // yourTextView.setTypeface(FONT_MEDIUM);

        // Get the ANDROID actionbar title
        //int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        //TextView titleView = (TextView) findViewById(titleId);
        //TextView titleView = (TextView) findViewById(R.id.text_title);
        //titleView.setTypeface(FONT_MEDIUM);
        //titleView.setVisibility(View.INVISIBLE);
        //Log.d("PIING", titleId+"");


        //int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        //int titleId = R.id.action_bar_title;
        //TextView yourTextView = (TextView) findViewById(titleId);


        // Get the ANDROID actionbar title
        //int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        //TextView titleView = (TextView) findViewById(titleId);
        //TextView titleView = (TextView) findViewById(R.id.text_title);
        //titleView.setTypeface(FONT_MEDIUM);
        //titleView.setVisibility(View.INVISIBLE);
        //Log.d("PIING", titleId+"");



    }

    private class GetDrawerData extends AsyncTask<String, Integer, String> {

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
                // There will only ever be one
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the object at the index
                    // Only one is returned here
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new hashamp to be used for our returned data
                    jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("pings", jsonObject.getString("pings"));
                    jsonHashMap.put("categories", jsonObject.getString("categories"));
                    jsonHashMap.put("friends", jsonObject.getString("friends"));
                    jsonHashMap.put("invites", jsonObject.getString("invites"));
                    jsonHashMap.put("name", jsonObject.getString("name"));
                    jsonHashMap.put("status", jsonObject.getString("status"));
                    jsonHashMap.put("badge", jsonObject.getString("badge"));
                    jsonHashMap.put("token", jsonObject.getString("token"));

                    // Set all of the UI detail
                    setDrawerUserData();

                }

            } catch (Exception e) {
                new ToastMessage().show(getApplicationContext(), getString(R.string.toast_error));
                Log.e("PIING", e.toString());
            }
        }
    }

    public void setDrawerUserData() {
        userToken = jsonHashMap.get("token");
        // Set the drawer elements
        drawerStatus.setText(jsonHashMap.get("status"));
        drawerUsername.setText(jsonHashMap.get("name"));
        drawerBadge.setImageResource(getResources().getIdentifier(jsonHashMap.get("badge"), "drawable", getPackageName()));
        //Itirate through the list of menu items
        for (int drawerMenuCount=0; drawerMenuCount<drawerList.getChildCount(); drawerMenuCount++) {
            // Get the iew in the ListView
            View v = drawerList.getChildAt(drawerMenuCount);
            // Find the fields we want to edit
            TextView drawerListViewCount = (TextView) v.findViewById(R.id.settings_value);
            TextView drawerListViewTitle =  (TextView) v.findViewById(R.id.settings_label);
            // Iterate through all of the possible values
            if (drawerListViewTitle.getText().equals("Pings")) {
                drawerListViewCount.setText(jsonHashMap.get("pings"));
            }
            if (drawerListViewTitle.getText().equals("Categories")) {
                drawerListViewCount.setText(jsonHashMap.get("categories"));
            }
            if (drawerListViewTitle.getText().equals("Friends")) {
                drawerListViewCount.setText(jsonHashMap.get("friends"));
            }
            if (drawerListViewTitle.getText().equals("Invites")) {
                drawerListViewCount.setText(jsonHashMap.get("invites"));
            }
            if (drawerListViewTitle.getText().equals("Profile")) {
                drawerListViewCount.setVisibility(View.INVISIBLE);
            }
            if (drawerListViewTitle.getText().equals("Token")) {
                drawerListViewCount.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void closeAllPopups() {
        FragmentPingsDetail fragment = (FragmentPingsDetail) getFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.closePopupWindows();
    }

    @Override
    public void updateSoundValue(String text) {
        FragmentPingsDetail fragment = (FragmentPingsDetail) getFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.settingSoundValue.setText(text);
    }

    @Override
    public void updateCategoryValue(String text, String id) {
        FragmentPingsDetail fragment = (FragmentPingsDetail) getFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.settingCategoryValue.setText(text);
        fragment.categoryId = id;
    }

    public static String sanitizeStringForUrl(String str) {
        String sanitizedname = str.replaceAll(" ", "%20");
        return sanitizedname;
    }

    public static String unsanitizeStringForUrl(String str) {
        String sanitizedname = str.replaceAll("%20", " ");
        return sanitizedname;
    }


}
