package com.robotmonsterlabs.ping.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robotmonsterlabs.ping.ActivityDrawer;
import com.robotmonsterlabs.ping.R;
import com.robotmonsterlabs.ping.adaptors.AdaptorCategory;

import com.robotmonsterlabs.ping.utility.GetDataFromUrl;
import com.robotmonsterlabs.ping.utility.PopupTooltip;
import com.robotmonsterlabs.ping.utility.ToastMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentCategoriesList extends Fragment {

    ListView categories ;
    View view ;
    ArrayList<HashMap<String,String>> data ;
    JSONArray jsonArray;
    RelativeLayout editPanel ;
    String currentCategoryTitleBeingEdited = null;
    String currentCategoryIdBeingEdited = null;
    Button editCurrentCategory ;
    Button deleteCurrentCategory ;
    Button addNewCategory ;
    ViewGroup frameLayout ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_categories_list, container, false);

        // Change the actionbar background color
        ActionBar actionbar = getActivity().getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getActivity().getResources().getColor(R.color.categories));
        actionbar.setBackgroundDrawable(colorDrawable);
        actionbar.setTitle("Categories");

        // Get all of the views on the page
        editPanel = (RelativeLayout) view.findViewById(R.id.edit_panel) ;
        frameLayout = (ViewGroup) view.findViewById(R.id.category_frame_layout) ;
        categories = (ListView) view.findViewById(R.id.categories_list) ;
        editCurrentCategory = (Button) view.findViewById(R.id.tool_edit);
        addNewCategory = (Button) view.findViewById(R.id.tool_add);
        deleteCurrentCategory = (Button) view.findViewById(R.id.tool_delete);

        // Set some fonts & some other properties of the views
        addNewCategory.setTypeface(ActivityDrawer.FONT_MEDIUM);
        categories.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Edit the current category
        editCurrentCategory.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the alertbox
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                // Store the ID and also the title of the category being edited
                final String updateCategoryId = currentCategoryIdBeingEdited;
                final String updateCategoryTitle = currentCategoryTitleBeingEdited;

                // Create the EditText field to use
                final EditText updateCategory = new EditText(getActivity());
                updateCategory.setText(updateCategoryTitle);

                // Set the parameters of the alert box
                alert.setTitle("Category");
                alert.setMessage("Update category title:");
                alert.setView(updateCategory);
                alert.setIcon(R.drawable.ui_icon_edit);

                // Set the "Okay" button & update
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new UpdateCategories().execute(getString(R.string.api_url) + "/category/" + updateCategoryId + "/update/?title=" + updateCategory.getText().toString());
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

        // Edit the current category
        deleteCurrentCategory.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the alertbox
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                // Store the category currently being edited
                final String updateCategoryId = currentCategoryIdBeingEdited;

                // Set the parameters of the alert box
                alert.setTitle("Delete");
                alert.setMessage("Are you sure you want to delete this category?");
                alert.setIcon(R.drawable.ui_icon_trash);

                // Set the "Okay" button & delete
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteCategories().execute(getString(R.string.api_url) + "/category/" + updateCategoryId + "/delete/");
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

        // Add new category
        addNewCategory.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the alertbox
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                // Create the EditText field to use
                final EditText updateCategory = new EditText(getActivity());

                // Set the parameters of the alert box
                alert.setTitle("Category");
                alert.setMessage("Category title:");
                alert.setView(updateCategory);
                alert.setIcon(R.drawable.ui_icon_edit);

                // Set the "Okay" button & update
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new CreateCategory().execute(getString(R.string.api_url) + "/category/" + ActivityDrawer.userId + "/create/?title=" + updateCategory.getText().toString());
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

        // Have the edit panel closed by default
        closeEditPanel();

        // Get all of the categories, needs proper ID
        new GetCategories().execute(getString(R.string.api_url) + "/category/" + ActivityDrawer.userId + "/getlist/") ;

        // return the view ;
        return view ;

    }

    private void openEditPanel() {
        editPanel.setVisibility(View.VISIBLE);
        currentCategoryIdBeingEdited = null;
        currentCategoryTitleBeingEdited = null;
        clearCategoryCheckboxes();
    }

    private void closeEditPanel() {
        editPanel.setVisibility(View.INVISIBLE);
        getActivity().invalidateOptionsMenu();
        currentCategoryIdBeingEdited = null;
        currentCategoryTitleBeingEdited = null;
        clearCategoryCheckboxes();
    }

    @Override
    public void onStart() {
        super.onStart();

        PopupTooltip tooltipPopup = new PopupTooltip(getActivity(), null) ;

        tooltipPopup.setText("Hi there!")
                .setHeight(50)
                .setWidth(120)
                .setY(250)
                .setX(50)
                .setHover(1000);

        //frameLayout.addView(tooltipPopup.getView(), tooltipPopup.getParams());

    }

    // here is our main async method for getting the api data
    // android.os.AsyncTask<Params, Progress, Result>
    // Params is the variable you feed to "execute" - URL below
    // The doInBackground method actually returns the result type to onPostExecute
    // Which actually receives it as a parameter
    // so execute feeds the first parameter and then it calls the post execute
    // with the return type
    // so there's like a hidden "governing method that actually does this
    // passes the variables around
    // onPostExecute doesn't take an array as the return type (obviously)

    private class GetCategories extends AsyncTask<String, Integer, String> {

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
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

            // Try for the JSON block
            try {

                // Initialize our data ArrayList
                data = new ArrayList<HashMap<String, String>>() ;

                // Get our data from the returned result in AsyncTask
                jsonArray = new JSONArray(result) ;

                // Iterate over the data from our json array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the object at the index
                    JSONObject jsonObject = jsonArray.getJSONObject(i) ;

                    // Create a new hashamp to be used for our adaptor
                    HashMap<String,String> jsonHashMap = new HashMap<String, String>();

                    // Add all the elements data
                    jsonHashMap.put("id", jsonObject.getString("id"));
                    jsonHashMap.put("title", jsonObject.getString("title"));
                    jsonHashMap.put("pings", jsonObject.getString("pings"));
                    jsonHashMap.put("fromping", "no");

                    // Add the hashmap to the arraylist
                    data.add(jsonHashMap);

                }

                // Feed our adaptor our data now that we've created a nice ArrayList
                categories.setAdapter(new AdaptorCategory(getActivity(), data));

                // When the user clicks on a list item
                categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View clickedView,
                                            int position, long id) {

                        // Only IF the edit panel is open, do we do stuff
                        if (editPanel.getVisibility()==View.VISIBLE) {

                            // Set up a hashmap for so we can get the arraylist object
                            HashMap<String,String> clickedOnObject = data.get(position) ;

                            // Clear all of the checkboxes first
                            clearCategoryCheckboxes();

                            // If there are no selections
                            if (currentCategoryIdBeingEdited==null) {
                                currentCategoryIdBeingEdited = clickedOnObject.get("id");
                                currentCategoryTitleBeingEdited = clickedOnObject.get("title");
                                CheckBox checkbox = (CheckBox) clickedView.findViewById(R.id.icon_checked);
                                checkbox.setChecked(true);
                            } else {
                                // If the user clicks on the same ListView item
                                if (currentCategoryIdBeingEdited.equals(clickedOnObject.get("id"))) {
                                    currentCategoryIdBeingEdited = null;
                                    currentCategoryTitleBeingEdited = null;
                                }
                                // If the user clicks on a different ListView item
                                if (!currentCategoryIdBeingEdited.equals(clickedOnObject.get("id"))) {
                                    currentCategoryIdBeingEdited = clickedOnObject.get("id");
                                    currentCategoryTitleBeingEdited = clickedOnObject.get("title");
                                    CheckBox checkbox = (CheckBox) clickedView.findViewById(R.id.icon_checked);
                                    checkbox.setChecked(true);
                                }
                            }
                        }
                    }
                });
            } catch (Exception e) {
                new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_error));
            }

        }

    }

    private class CreateCategory extends AsyncTask<String, Integer, String> {

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

            // Yes we should also catch an error here, but no
            new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_updated));

            // Get all of the categories, needs proper ID
            new GetCategories().execute(getString(R.string.api_url) + "/category/" + ActivityDrawer.userId + "/getlist/") ;

                    // Reset the UI
                    closeEditPanel();

        }

    }

    private class UpdateCategories extends AsyncTask<String, Integer, String> {

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

            // Yes we should also catch an error here, but no
            new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_updated));

            // Get all of the categories, needs proper ID
            new GetCategories().execute(getString(R.string.api_url) + "/category/" + ActivityDrawer.userId + "/getlist/") ;

            // Reset the UI
            closeEditPanel();

        }

    }

    private class DeleteCategories extends AsyncTask<String, Integer, String> {

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

            // Yes we should also catch an error here, but no
            new ToastMessage().show(getActivity().getApplicationContext(), getString(R.string.toast_updated));

            // Get all of the categories, needs proper ID
            new GetCategories().execute(getString(R.string.api_url) + "/category/" +
                    ActivityDrawer.userId+"/getlist/") ;

            // Reset the UI
                    closeEditPanel();

        }

    }

    public void clearCategoryCheckboxes() {

        // loop through the entire list of listview children
        for ( int x = 0 ; x < categories.getCount() ; x++ ) {
            // get the view
            View childView = categories.getChildAt(x);
            // get the checkbox
            CheckBox checkMarkIcon = (CheckBox) childView.findViewById(R.id.icon_checked);
            // make it visible
            checkMarkIcon.setChecked(false);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_categories_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // get the id of the menu item
        int id = item.getItemId();

        // if they press the edit button
        if (id == R.id.action_edit) {
            // If the edit panel is visible, we close it and set state to non-editable
            // Otherwise we open it and show the checkboxes
            if (editPanel.getVisibility()==View.VISIBLE) {
                // Close the panel
                closeEditPanel();
                // Put the actionbar button to the original (actually redundant here)
                // It gets reset in the closeEditPanel method
                item.setTitle(R.string.action_edit) ;
                // Remove the checkboxes next to the ListView items
                setCheckboxVisibility(View.INVISIBLE);
            } else {
                // Open the panel
                openEditPanel();
                // Set the new button text on the Actionbar
                item.setTitle(R.string.action_undo) ;
                // Show the checkboxes next to the ListView items
                setCheckboxVisibility(View.VISIBLE);

            }
        }

        // default return
        return super.onOptionsItemSelected(item);
    }

    public void setCheckboxVisibility(int visibility) {
        for ( int x = 0 ; x < categories.getCount() ; x++ ) {
            View childView = categories.getChildAt(x);
            CheckBox checkMarkIcon = (CheckBox) childView.findViewById(R.id.icon_checked);
            TextView categoryTitle = (TextView) childView.findViewById(R.id.category_label);
            checkMarkIcon.setVisibility(visibility);
            if (visibility==View.INVISIBLE)
                categoryTitle.setPadding(0,0,0,0) ;
            else
                categoryTitle.setPadding(150,0,0,0) ;
        }
    }

}
