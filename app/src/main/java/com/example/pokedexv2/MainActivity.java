package com.example.pokedexv2;

// Import all the necessary libraries/modules for the app to work
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

// caughtCount represents the number of pokemon that have been marked as caught
    public int caughtCount;
// define the extra message that will be passed between intents
    public static final String EXTRA_MESSAGE = "com.example.pokedexv2.MESSAGE";
// initialize the progress bar
    private ProgressBar mLoadingProgress;
// Initialize a string that contains the empty string for the default name
// The default name will ultimately be passed in the intent to the DisplayResult class
    public String DEFAULT_NAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// set the content view for this class to be activity_main
        setContentView(R.layout.activity_main);
// Access the constraint layout to change the main screen's background
        ConstraintLayout RL = (ConstraintLayout) findViewById(R.id.am);
// set the background image to the mysterydungeon image in the drawable directory
        RL.setBackgroundResource(R.drawable.mysterydungeon);
// create button variable to access the search button
        Button search = (Button) findViewById(R.id.search);
// create button variable to access the ScanPokeName activity
        Button scanPokeName = (Button) findViewById(R.id.scanPokeName);
// create button variable to access the fullList activity
        Button fullList = (Button) findViewById(R.id.fullList);
// get the user input
        final EditText userInput = (EditText) findViewById(R.id.editText);
// create the progress bar and set its visibility to invisible (until network calls happen)
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
        mLoadingProgress.setVisibility(View.INVISIBLE);

// the asanaUrl accesses the asana RESTful API.  With this API, we create a new project for every caught pokemon
// this API is used here to find how many pokemon have been marked caught by the user.
        URL asanaUrl;
        String asanaUrlString = "https://app.asana.com/api/1.0/workspaces/1141491780889436/projects";
        try{
// convert the string URL to be a URL object
            asanaUrl = new URL(asanaUrlString);
// perform network call requesting the number of caught pokemon
            new asanaUtil().execute(asanaUrl);
        }catch(Exception e){
// if something goes wrong with the asana network call, log the error
            Log.d("Error: ", e.getMessage());
        }

// function to take the user to the ScanPokeName activity
// the ScanPokeName activity allows the user to show their camera to a pokemon name and pull its info up from there
        scanPokeName.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                try {
// start the next activity with an intent
                    Intent scanIntent = new Intent(MainActivity.this, ScanPokeName.class);
                    startActivity(scanIntent);
                } catch (Exception e){
// if the above code fails, log the error
                    Log.d("Error: ",e.getMessage());
                }
            }
        });

// function to take the user to the FullList activity
// the FullList activity shows the user all the pokemon in the pokeapi RESTful API's database
        fullList.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                try {
// start the next activity with an intent
                    Intent fullListIntent = new Intent(MainActivity.this, FullList.class);
                    startActivity(fullListIntent);
                } catch (Exception e){
// if the above code fails, log the error
                    Log.d("Error: ",e.getMessage());
                }
            }
        });

// function to take the user directly to the DisplayResult activity, passing the given pokemon name/id
// this function takes the user input in the EditText view and passes it in the intent for the DisplayResult class to use
        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                try {
// take the user's input for the pokemon name/id. Cast the information to string and set all chars to lower case
                    String givenText = userInput.getText().toString().toLowerCase();
// define the default name to be the given text
                    DEFAULT_NAME = givenText;
// Create a pokeUrl with the given string for pokemon to be searched
                    URL pokeUrl = ApiUtil.buildUrl(givenText);
// if the user submitted an empty string, tell them they need to give a pokemon name
// this eliminates unnecessary network calls
                    if(givenText.length() == 0){
// Toast the prompt for pokemon name/id if nothing was given
                        Toast.makeText(getApplicationContext(),"Please enter a Pokemon name to search", Toast.LENGTH_LONG).show();
                    }else {
// if there was user input given, create a new pokeQueryTask and execute the AsyncTask passing the built URL for the pokeapi
                        new pokeQueryTask().execute(pokeUrl);
                    }
// if any of the above failed, log the error and give user feedback for what went wrong
                } catch (Exception e){
                    Log.d("Error: ",e.getMessage());
                    Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

// Enable the EditText to be able to start a new intent if the user presses the enter key instead of the designated search button
        userInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                try {
// If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
// Perform action on key press
                        String givenText = userInput.getText().toString().toLowerCase();
// check if the given string is an empty string
                        if(givenText.length() == 0){
// if it's an empty string, don't do anything and tell the user to give a real pokemon name
                            Toast.makeText(getApplicationContext(),"Please enter a Pokemon name to search", Toast.LENGTH_LONG).show();
                            return false;
                        }
// set the DEFAULT_NAME variable to be the user's input
                        DEFAULT_NAME = givenText;
// Create a pokeUrl with the given string for pokemon to be searched
                        URL pokeUrl = ApiUtil.buildUrl(givenText);
// create a new pokeQueryTask and execute the AsyncTask passing the built URL for the pokeapi
                        new pokeQueryTask().execute(pokeUrl);
                        return true;
                    }
                } catch (Exception e){
// if any of the above fails, log the error and give user feedback to give a proper pokemon name
                    Log.d("Error: ",e.getMessage());
                    Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }

    public URL sURL;
    // This is used to run the network thread in the background
    public class pokeQueryTask extends AsyncTask<URL, Void, String> {
        // This function runs the network call in the background
        @Override
        protected String doInBackground(URL... urls) {
            // Define the searchURL as the first url in the urls array
            URL searchURL = urls[0];
            sURL = searchURL;
            // Initialize the result to be null
            String result = null;
            // Try/catch to make sure the given url is correct and the json response is
            // valid
            try {
                // The result is found
                result = ApiUtil.getJson(searchURL);
            } catch (Exception e) {
                // If the result is not found, log the error message
                Log.e("Error: ", e.getMessage());
                Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            // Return the completed result (or null for failed url)
            return result;
        }

        // This function executes after the network call is complete
        @Override
        protected void onPostExecute(String result) {
            // Make the loading bar invisible after it's done loading
            mLoadingProgress.setVisibility(View.INVISIBLE);
// if the pokeapi is not able to return pokemon json for the given pokemon name/id
            if(result == null){
// tell the user that the given pokemon name does not exist
                Toast.makeText(getApplicationContext(),"Unable to find pokemon named \"" + DEFAULT_NAME + "\"", Toast.LENGTH_LONG).show();
                return;
            }
            try{
// create intent and pass the pokemon name to the DisplayResult activity
                Intent displayResultIntent = new Intent(MainActivity.this, DisplayResult.class);
                displayResultIntent.putExtra(EXTRA_MESSAGE, DEFAULT_NAME);
                startActivity(displayResultIntent);
            } catch (Exception e){
// if the intent does not work properly, log the error and give user feedback
                Log.d("Error: ", e.getMessage());
                Toast.makeText(getApplicationContext(),"Pokemon named \"" + DEFAULT_NAME + "\" has too much data to pass across intents", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Override the onPreExecute() method
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Set the progress bar to be visible when the network call is in progress
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }

// this is another asynctask to handle the asana network call
    private class asanaUtil extends AsyncTask<URL,Void,String> {

        @Override
        protected String doInBackground(URL... urls) {
// initialize the searchURL to be the passed asanaURl
            URL searchURL = urls[0];

            try {
// try getting the json string from the asana network call's response
                String test = ApiUtil.getAsanaJson(searchURL);
// cast the string to be a json object
                JSONObject testObject = new JSONObject(test);
// use the given json, which contains a json array called "data"
                JSONArray testArray = testObject.getJSONArray("data");
// the number of caught pokemon will be the length of the array, so access that variable and make it publicly available
                caughtCount = testArray.length();
            } catch (Exception e) {
// if any of the above code fails, log the error and give user feedback
                Log.d("Error: ", e.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
// when all the above is complete, end the background thread
            return null;
        }

        @Override
        protected void onPostExecute(String result){
// after the network call is done, update the caughtCount textview to show the number of pokemon caught
            TextView caughtCountTv = (TextView) findViewById(R.id.caughtCount);
            caughtCountTv.setText(Integer.toString(caughtCount) + "/807 Pokemon Caught");
        }
    }
}

