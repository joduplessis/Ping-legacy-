package com.robotmonsterlabs.ping.fragments;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentToken extends Fragment {

    public String token;
    View view;
    TextView heading, subheading, paragraph;

    public FragmentToken() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Save the view
        view = inflater.inflate(R.layout.fragment_token, container, false);

        // Change the actionbar background color
        ActionBar actionbar = getActivity().getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getActivity().getResources().getColor(R.color.token));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setTitle("Profile");

        heading = (TextView) view.findViewById(R.id.heading);
        subheading = (TextView) view.findViewById(R.id.subheading);
        paragraph = (TextView) view.findViewById(R.id.paragraph);

        // Assign the fonts for the TextViews
        heading.setTypeface(ActivityDrawer.FONT_MEDIUM);
        subheading.setTypeface(ActivityDrawer.FONT_REGULAR);
        paragraph.setTypeface(ActivityDrawer.FONT_REGULAR);

        paragraph.setText(token);

        // Return the view
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
