package com.robotmonsterlabs.ping.fragments;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.ActivityLogin;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.adaptors.AdaptorFriend;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfile extends Fragment {

    ImageView userBadge;
    TextView userName;
    TextView userStatus;
    TextView userTip;
    View view;
    HashMap<String,String> jsonHashMap;
    RelativeLayout userLayout;
    String[] badges;

    public FragmentProfile() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Save the view
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Change the actionbar background color
        ActionBar actionbar = getActivity().getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getActivity().getResources().getColor(R.color.profile));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setTitle("Profile");

        // Get the array of badges
        badges = getResources().getStringArray(R.array.badges_array);

        // Get the elements on screen
        userBadge = (ImageView) view.findViewById(R.id.user_badge);
        userName = (TextView) view.findViewById(R.id.user_name);
        userStatus = (TextView) view.findViewById(R.id.user_status);
        userTip = (TextView) view.findViewById(R.id.user_tip);
        userLayout = (RelativeLayout) view.findViewById(R.id.user_layout);

        // Assign the fonts for the TextViews
        userName.setTypeface(ActivityDrawer.FONT_MEDIUM);
        userStatus.setTypeface(ActivityDrawer.FONT_REGULAR);
        userTip.setTypeface(ActivityDrawer.FONT_REGULAR);

        // Start the Async process & retrieve the friend list
        new GetProfileData().execute(getString(R.string.api_url) + "/profile/" + ActivityDrawer.facebookId + "/get") ;

        // Getting the touch gesture
        userBadge.setOnTouchListener(new View.OnTouchListener() {

            // We set the variable so that it doesn't continually drag
            // We only want to move the image when this is false (stationary)
            Boolean duplicatedImageViewIsBeingMoved = false;

            // Set the current badge position
            int currentBadgePosition = 0;

            public boolean onTouch(View v, MotionEvent event) {
                // So here: if it's  NOT being moved (is stationary) the proceed
                if (!duplicatedImageViewIsBeingMoved) {

                    // Increment the image counter
                    currentBadgePosition++;

                    // If the array is out of bounds
                    if (currentBadgePosition >= badges.length)
                        currentBadgePosition = 0;

                    // Get image resource
                    int newImage = getResources().getIdentifier(badges[currentBadgePosition], "drawable", getActivity().getPackageName());

                    // Now we set the variable to moving
                    // This is to ensure it doesn't perpetually loop
                    duplicatedImageViewIsBeingMoved = true;

                    // We duplicate our user badge & set it's layout params
                    ImageView duplicatedUserBadge = new ImageView(getActivity());
                    duplicatedUserBadge.setImageResource(newImage);
                    duplicatedUserBadge.setLeft(userBadge.getLeft());
                    duplicatedUserBadge.setLayoutParams(userBadge.getLayoutParams());

                    // Now add it to the layout
                    userLayout.addView(duplicatedUserBadge);

                    // Update he source badge
                    userBadge.setImageResource(newImage);
                    userBadge.setTag(badges[currentBadgePosition]);

                    // Animate the duplicated user badge
                    ObjectAnimator hoverMovement = ObjectAnimator.ofFloat(
                            duplicatedUserBadge,
                            "x",
                            userBadge.getLeft(),
                            userBadge.getWidth() * -1
                    );

                    // Now we set some boilerplate animation code
                    hoverMovement.setDuration(250);
                    hoverMovement.start();

                    // Only when the animation is finished, do we reset teh variable
                    // So that the user can swipe again
                    hoverMovement.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            duplicatedImageViewIsBeingMoved = false;
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                }

                // Return true as default
                return true;
            }
        });

        // Now we enable a click on the status so that the user can update their status
        userStatus.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick (View view) {
                // Create the alertbox
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                // New edit text view
                final EditText updateStatus = new EditText(getActivity());
                updateStatus.setText(userStatus.getText().toString());

                // set the parameters
                alert.setTitle("Status");
                alert.setMessage("Update your status:");
                alert.setView(updateStatus);
                alert.setIcon(R.drawable.ui_icon_edit);

                // Set the "Okay" button
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update the user status
                        userStatus.setText(updateStatus.getText().toString());
                    }
                });

                // Set the negative button
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                // Show our alert box
                alert.show();
            }
        });

        // Return the view
        return view;
    }

    private class GetProfileData extends AsyncTask<String, Integer, String> {

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

            // Initialize our Hashmap
            jsonHashMap = new HashMap<String, String>();

            // First, if the returned JSON is blank
            if (result.equals("")) {
                Log.e("PIING", "API has returned empty");
                new ToastMessage().show(getActivity().getApplicationContext(),
                        getString(R.string.toast_error));
            }

            try {
                // Get our json from our API
                JSONArray jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the user object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Add all the elements data
                    jsonHashMap.put("id", jsonObject.getString("id"));
                    jsonHashMap.put("facebookid", jsonObject.getString("facebookid"));
                    jsonHashMap.put("name", jsonObject.getString("name"));
                    jsonHashMap.put("status", jsonObject.getString("status"));
                    jsonHashMap.put("badge", jsonObject.getString("badge"));

                    // Now update our view of the user profile
                    userName.setText(jsonObject.getString("name"));
                    userStatus.setText(jsonObject.getString("status"));
                    userBadge.setImageResource(getResources().getIdentifier(
                            jsonObject.getString("badge"),
                            "drawable",
                            getActivity().getPackageName()));
                }
            } catch (Exception e) {
                Log.e("PIING", e.toString());
                new ToastMessage().show(getActivity().getApplicationContext(),
                        getString(R.string.toast_error));
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get the id of the menu item
        int id = item.getItemId();
        // If they press save at the top
        if (id == R.id.action_save) {
            // Get the new values
            String uName = ActivityDrawer.sanitizeStringForUrl(userName.getText().toString());
            String uStatus = ActivityDrawer.sanitizeStringForUrl(userStatus.getText().toString());
            String uBadge = userBadge.getTag().toString();
            // Update the shared preference FACEBOOKNAME
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("facebookname", uName);
            editor.apply();
            // Update the drawer details
            int newImage = getResources().getIdentifier(uBadge, "drawable", getActivity().getPackageName());
            ActivityDrawer currentActivity = (ActivityDrawer) getActivity();
            currentActivity.drawerBadge.setImageResource(newImage);
            currentActivity.drawerStatus.setText(ActivityDrawer.unsanitizeStringForUrl(uStatus));
            // Call the API
            new SetProfileData().execute(getString(R.string.api_url) + "/profile/" + ActivityDrawer.facebookId + "/update?badge="+uBadge+"&name=" + uName + "&status=" + uStatus);
        }
        return super.onOptionsItemSelected(item);
    }

    private class SetProfileData extends AsyncTask<String, Integer, String> {

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
            // Again we should catch an error here, but I can do that later
            new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_updated));
        }

    }

}
