package com.example.pokedexv2;

// Import all the libraries and modules that the app needs to work
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DisplayResult extends AppCompatActivity {

// initialize a progress bar that will show while network calls are happening
    private ProgressBar mLoadingProgress;
// get the pokemon's id and set it to an impossible value to check for errors
    public int poke_id = new Integer(-1);
// initialize a new pokemon.  Since this whole page focuses on a single pokemon, this is publicly available
    public Poke poke = new Poke(
            null,
            0,
            null,
            null,
            0,
            null,
            -1,
            false,
            null,
            null,
            "undefined",
            0,
            null,
            null,
            null,
            null,
            0
    );
// x1 and x2 are used here to determine whether the user has swiped left or right
    public float x1, x2;
// the caught textview will show whether this pokemon has been marked as caught or not
    public TextView caught;
// the caughtBool will determine which messages are shown on the caughtButton and the caught textviews
    public boolean caughtBool;
// the asanaUrlString allows for asana api calls to be possible, and are publicly available
    public String asanaUrlString;
// Asana does all its identification of tasks based on GID, so having this variable ready allows for us to tell whether a pokemon is caught or not
    public String asanaGid;
// the asanauUrl is a url object version of the asana url string.
    URL asanaUrl;
// the constraint layout here will be used to change the background color based on the pokemon's type
    ConstraintLayout RL;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_result);
// get the intent information that was passed via putExtra.  This is usually the pokemon name/id
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
// initialize the progress bar and hide it until a network call is begun
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
        mLoadingProgress.setVisibility(View.INVISIBLE);
// intiialize the caught textview
        caught = findViewById(R.id.caught);
// define the constraint layout
        RL = (ConstraintLayout) findViewById(R.id.Layout);
// define the asanaUrlString to be the asana url which hold the information of whether a pokemon has been caught or not
        asanaUrlString = "https://app.asana.com/api/1.0/workspaces/1141491780889436/projects";
// build the asana url based on the asanaurl and the pokemon name that was passed with the intent
        URL messageUrl = ApiUtil.buildUrl(message);
        try{
// start a new AsyncTask for the customized url to get the pokemon info from the pokeapi
            new pokeQTask().execute(messageUrl);
            asanaUrl = new URL(asanaUrlString);
// start another new AsyncTask for the asanaUrl to see whether the pokemon is marked caught or not
            new asanaUtil().execute(asanaUrl);
        } catch (Exception e){
// if the above code fails, log the error and give user feedback
            Log.d("Error: ", e.getMessage());
            Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
// this function downloads the sprite and audio file
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
// define the imageview for the sprite
        ImageView bmImage;
// create an image task which will get the image
        public DownloadImageTask(ImageView bmImage) {
// while the image is being retrieved, keep the loading progress visible
            mLoadingProgress.setVisibility(View.VISIBLE);
            this.bmImage = bmImage;
        }
// background thread for network call
        protected Bitmap doInBackground(String... urls) {
// pokemon whose ids are over 803 don't have sprites or sounds in the database.
// If not for this if statement, going to those screens would make the app crash
            if(poke_id >= 803){
                return null;
            }
// get the url that was passed to the function
            String urldisplay = urls[0];
// initialize the image/bitmap
            Bitmap mIcon11 = null;
            try {
// create an input stream to grab the data for the sprite
                InputStream in = new java.net.URL(urldisplay).openStream();
// define the bitmap from the decoded stream
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
// if grabbing the image failed, log the error and give user feedback
                Log.e("Error", e.getMessage());
                Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            try {
// create a media player for the pokemon's cry
                MediaPlayer player = new MediaPlayer();
// set the audio stream
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
// pokemoncries.com the pokemons' cries systematically available by get request
                String cryUrl = "https://pokemoncries.com/";
                if(poke_id < 650){
// pokemon whose ids are less than 650 have their cries stored in a different directory than newer pokemon cries
                    cryUrl += "cries-old/" + poke_id + ".mp3";
                }else{
                    cryUrl += "cries/" + poke_id + ".mp3";
                }
// set the data source to be the cryurl that was constructed
                player.setDataSource(cryUrl);
                try{
// try preparing the player to be played
                    player.prepare();
                } catch (Exception e){
// if preparing the player gave an error, log it
                    Log.d("Error: ", e.getMessage());
                }
// start the pokemon's cry
                player.start();
            } catch (Exception e) {
                // TODO: handle exception
// if the above failed, log the error and give user feedback
                Log.d("Error: ", e.getMessage());
                Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
// return the sprite image
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
// after the network call is done, hide the progress bar
            mLoadingProgress.setVisibility(View.INVISIBLE);
// set the sprite to be displayed on the screen
            bmImage.setImageBitmap(result);
        }
    }

// the pokeQTask is the network call for the pokemon's info
    public class pokeQTask extends AsyncTask<URL, Void, String> {
        // This function runs the network call in the background
        @Override
        protected String doInBackground(URL... urls) {
            // Define the searchURL as the first url in the urls array
            URL searchURL = urls[0];
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

        // This function runs the network call when a POST is called
        @Override
        protected void onPostExecute(String result) {
            // Make the loading bar invisible after it's done loading
            mLoadingProgress.setVisibility(View.INVISIBLE);

            try{
                poke = ApiUtil.getPokesFromJson(result);
                updateFields();
            } catch (Exception e){
                Log.d("Error: ", e.getMessage());
                Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Override the onPreExecute() method
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Set the progress bar to be visible
            mLoadingProgress.setVisibility(View.VISIBLE);

        }
    }
// updateFields method updates the background based on type and updates the pokemon object's attributes
    protected void updateFields(){
// define the poke_id public variable
        poke_id = poke.id;
        try {
// this switch statement sets the background color based on the pokemon type
            switch (poke.types[poke.types.length - 1].ttype.name) {
                case "bug":
                    RL.setBackgroundColor(Color.parseColor("#A8B820"));
                    break;
                case "dark":
                    RL.setBackgroundColor(Color.parseColor("#705848"));
                    break;
                case "dragon":
                    RL.setBackgroundColor(Color.parseColor("#7038F8"));
                    break;
                case "electric":
                    RL.setBackgroundColor(Color.parseColor("#F8D030"));
                    break;
                case "fairy":
                    RL.setBackgroundColor(Color.parseColor("#EE99AC"));
                    break;
                case "fighting":
                    RL.setBackgroundColor(Color.parseColor("#C03028"));
                    break;
                case "fire":
                    RL.setBackgroundColor(Color.parseColor("#F08030"));
                    break;
                case "flying":
                    RL.setBackgroundColor(Color.parseColor("#A890F0"));
                    break;
                case "ghost":
                    RL.setBackgroundColor(Color.parseColor("#705898"));
                    break;
                case "grass":
                    RL.setBackgroundColor(Color.parseColor("#78C850"));
                    break;
                case "ground":
                    RL.setBackgroundColor(Color.parseColor("#E0C068"));
                    break;
                case "ice":
                    RL.setBackgroundColor(Color.parseColor("#98D8D8"));
                    break;
                case "normal":
                    RL.setBackgroundColor(Color.parseColor("#A8A878"));
                    break;
                case "poison":
                    RL.setBackgroundColor(Color.parseColor("#A040A0"));
                    break;
                case "psychic":
                    RL.setBackgroundColor(Color.parseColor("#F85888"));
                    break;
                case "rock":
                    RL.setBackgroundColor(Color.parseColor("#B8A038"));
                    break;
                case "steel":
                    RL.setBackgroundColor(Color.parseColor("#B8B8D0"));
                    break;
                case "water":
                    RL.setBackgroundColor(Color.parseColor("#6890F0"));
                    break;
                default:
                    // leave the background as the white color
            }
        } catch (Exception e){
// if setting the background didn't work, log the error and give user feedback
            Log.d("Error: ", e.getMessage());
            Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try{
// create a new downloadimagetask to get the pokemon's sprite
            new DownloadImageTask((ImageView) findViewById(R.id.imageView2)).execute(poke.sprites.front_default);
        } catch (Exception e){
// if creating the downloadimagetask failed, log the error and give user feedback
            Log.d("Error: ", e.getMessage());
            Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try{
// update the input field (this was used as a reference number previously
            TextView input = findViewById(R.id.input);
            input.setText(poke.id.toString());
// update the pokemon's name and capitalize the first character
            TextView name = findViewById(R.id.name);
            name.setText(Character.toUpperCase(poke.name.charAt(0)) + poke.name.substring(1));
            name.setVisibility(View.VISIBLE);
// update the pokemon's id
            TextView id = findViewById(R.id.poke_id);
            id.setText("Pokemon id: " + poke.id.toString());
            id.setVisibility(View.VISIBLE);
// update the pokemon's type
            TextView type = findViewById(R.id.type);
            String typeString = "";
// if there is more than one type for the pokemon, set the background color based on the second type,
            for(int i = 0; i < poke.types.length; i++){
                if(i > 0){
                    typeString += ", ";
                }
                typeString += Character.toUpperCase(poke.types[i].ttype.name.charAt(0)) + poke.types[i].ttype.name.substring(1);
            }
            if(poke.types.length > 1){
                type.setText("Types: " + typeString);
            }else{
                type.setText("Type: " + typeString);
            }
            type.setVisibility(View.VISIBLE);
// update the pokemon's height
            TextView height = findViewById(R.id.height);
            height.setText("Height: " + poke.height.toString());
            height.setVisibility(View.VISIBLE);
// update the pokemon's weight
            TextView weight = findViewById(R.id.weight);
            weight.setText("Weight: " + poke.weight.toString());
            weight.setVisibility(View.VISIBLE);
// update the pokemon's hp
            TextView hp = findViewById(R.id.hp);
            hp.setText("HP: " + poke.stats[5].base_stat.toString());
            hp.setVisibility(View.VISIBLE);
// update the pokemon's base attack
            TextView attack = findViewById(R.id.attack);
            attack.setText("Attack: " + poke.stats[4].base_stat.toString());
            attack.setVisibility(View.VISIBLE);
// update the pokemon's base defense
            TextView defense = findViewById(R.id.defense);
            defense.setText("Defense: " + poke.stats[3].base_stat.toString());
            defense.setVisibility(View.VISIBLE);
// update the pokemon's base speed
            TextView speed = findViewById(R.id.speed);
            speed.setText("Speed: " + poke.stats[0].base_stat.toString());
            speed.setVisibility(View.VISIBLE);
// update the pokemon's base spc attack
            TextView spc_attack = findViewById(R.id.spc_attack);
            spc_attack.setText("Spc. Attack: " + poke.stats[2].base_stat.toString());
            spc_attack.setVisibility(View.VISIBLE);
// update the pokemon's base spc defense
            TextView spc_defense = findViewById(R.id.spc_defense);
            spc_defense.setText("Spc. Defense: " + poke.stats[1].base_stat.toString());
            spc_defense.setVisibility(View.VISIBLE);
// update the caughtbutton visibility
            Button caughtButton = findViewById(R.id.caughtButton);
            caughtButton.setVisibility(View.VISIBLE);
// when the caught button is pressed, update the caught value
            caughtButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View arg0){
                        try{
                            new updateCaught().execute(asanaUrlString, "name=" + poke.name);
                        }catch(Exception e){
// if updating the caught value fails, log the error
                            Log.d("Error: ", e.getMessage());
                        }
                    }
            });

        } catch (Exception e){
// if updating the fields didn't work, log the error and give user feedback
            Log.d("Error: ",e.getMessage());
            Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


// this function allows for the page to be swiped left or right to change the pokemon
    public boolean onTouchEvent(MotionEvent touchEvent){
// when the screen is touched,
        switch(touchEvent.getAction()){
// get the initial x position of the user's touch when the finger first touches the screen
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
// get the final x position of the user's touch when the user's finger leaves the screen
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
// if the initial x value of the touch is less than the final value, they have swiped left
                if(x1 < x2){
                    if(poke.id == 1){
// catch it before they swipe left on bulbasaur, who is the edge case and should not be able to swipe left
                        Toast.makeText(getApplicationContext(),"Cannot swipe left.  Bulbasaur is the first Pokemon.", Toast.LENGTH_LONG).show();
                        break;
                    }
// create a new intent and pass the previous pokemon's id to the new intent
                    Intent intent = new Intent(DisplayResult.this, DisplayResult.class);
                    intent.putExtra(MainActivity.EXTRA_MESSAGE, Integer.toString(poke.id - 1));
                    startActivity(intent);
                }else if(x1 > x2){
// if the beginning x value is more than the final x value, they swiped right
                    if(poke.id == 807){
// compensate for edge case for Zeraora
                        Toast.makeText(getApplicationContext(),"Cannot swipe right.  Zeraora is the last Pokemon.", Toast.LENGTH_LONG).show();
                        break;
                    }
// create new intent and pass the poke-id for the next pokemon in the list
                    Intent intent = new Intent(DisplayResult.this, DisplayResult.class);
                    intent.putExtra(MainActivity.EXTRA_MESSAGE, Integer.toString(poke.id + 1));
                    startActivity(intent);
                }
                break;
        }
        return false;
    }
// an AsyncTask for working with the Asana API for the caught values
    private class asanaUtil extends AsyncTask<URL,Void,String>{

        @Override
        protected String doInBackground(URL... urls) {
// network call. Grab the passed url
            URL searchURL = urls[0];
            try {
// get the string version of the json response from the asana api
                String test = ApiUtil.getAsanaJson(searchURL);
// cast the string to a JSONObject
                JSONObject testObject = new JSONObject(test);
// grab the json array which contains all the pokemon info
                JSONArray testArray = testObject.getJSONArray("data");
// for every object in the json array, check to see if the current pokemon's name is in there
                for(int i = 0; i < testArray.length(); i++){
                    JSONObject finalObject = testArray.getJSONObject(i);
                    String sss = finalObject.getString("name");
                    Boolean isCaught = sss.equals(poke.name);
                    if(isCaught){
// get the asanaGID for the current pokemon that has already been marked as caught
                        asanaGid = finalObject.getString("gid");
                        caughtBool = true;
                        break;
                    }else{
// if the pokemon is not caught, show the caughtbool to be false
                        caughtBool = false;
                    }
                }
            } catch (Exception e){
// log the error and give user feedback if the network call fails
                Log.d("Error: ",e.getMessage());
                Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
// after the network call is over, update the caughtbool and textviews/buttons
            if(caughtBool){
// set the text to be caught if the caughtbool is true
                caught.setText("CAUGHT");
// set the text color to be green
                caught.setTextColor(Color.parseColor("#00ff00"));
// set the caught text to be visible
                caught.setVisibility(View.VISIBLE);
// show the caughtbutton's text to be mark uncaught to prompt the user to do the right thing
                Button caughtButton = findViewById(R.id.caughtButton);
                caughtButton.setText("Mark Uncaught");
            }else{
// dynamic prompt based on whether the pokemon has been caught or not
                caught.setText("NOT CAUGHT");
                caught.setVisibility(View.VISIBLE);
            }

        }
    }

// the updateCaught method connects with the Asana API and is able to CREATE or DELETE the pokemon entry
    private class updateCaught extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... inputs) {
// create user authentication for the Asana API
            String basicAuth = "Bearer ";
            basicAuth += "0/858ac4da00dcb3891449ca41645ee76b";
// if the pokemon hasn't been caught yet, we want to create a new asana entry for the pokemon so it can be marked caught
            if(!caughtBool){
                try{
// get the url that was passed in for the asana API
                    URL url = new URL(inputs[0]);
// initialize the http connection
                    HttpURLConnection connection = (HttpsURLConnection) url.openConnection();
// set the method to be a POST for the asana API
                    connection.setRequestMethod("POST");
// set the request content type
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
// add the authentication info to the http header for the Asana API
                    connection.setRequestProperty("Authorization",basicAuth);
                    connection.setDoOutput(true);
                    DataOutputStream write = new DataOutputStream(connection.getOutputStream());
// add the pokemon's name to be written to the RESTful API
                    write.writeBytes(inputs[1]);
                    write.flush();
                    write.close();
// log the http request's response
                    Log.e("Response: ", connection.getResponseMessage() + "");
// get the asana json so the gid can be used
                    String test = ApiUtil.getAsanaJson(asanaUrl);
// parse the string first into a json object and then a json array
                    JSONObject testObject = new JSONObject(test);
                    JSONArray testArray = testObject.getJSONArray("data");
// iterate through and find the pokemon name for the current pokemon
                    for(int i = 0; i < testArray.length(); i++){
                        JSONObject finalObject = testArray.getJSONObject(i);
                        String sss = finalObject.getString("name");
                        Boolean isCaught = sss.equals(poke.name);
                        if(isCaught){
// update the asanaGid
                            asanaGid = finalObject.getString("gid");
                            caughtBool = true;
                            break;
                        }else{
                            caughtBool = false;
                        }
                    }
// set the caught bool to be true now that the entry has been updated to include the current pokemon
                    caughtBool = true;
                } catch (Exception e){
// if this asana api call failed, log the error and give user feedback
                    Log.d("Error: ", e.getMessage());
                    Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }else{
                try{
// define the asana url to be based on the asanaGid for the current pokemon
                    URL url = new URL("https://app.asana.com/api/1.0/projects/" + asanaGid);
// open the http connection
                    HttpURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setInstanceFollowRedirects(false);
// set the method to delete to delete something from the Asana RESTful API
                    connection.setRequestMethod("DELETE");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("charset", "utf-8");
                    connection.setUseCaches (false);
// authenticate the user
                    connection.setRequestProperty("Authorization",basicAuth);

                    int responseCode = connection.getResponseCode();
                    connection.disconnect();
                    caughtBool = false;
                } catch (Exception e){
                    Log.d("Error: ", e.getMessage());
                    Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
// set the progress bar to be visible while the network call is happening
            mLoadingProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result){
// after the network call is over, hide the progress bar
            mLoadingProgress.setVisibility(View.INVISIBLE);
            if(caughtBool){
// after the network call is over, if the pokemon was marked caught, update the text view and caught button appropriately
                Button caughtButton = findViewById(R.id.caughtButton);
                caughtButton.setText("Mark Uncaught");
                caught.setText("CAUGHT");
                caught.setTextColor(Color.parseColor("#00ff00"));
                caught.setVisibility(View.VISIBLE);
            }else{
// if the pokemon was marked uncaught, update the text view and caught button appropriately
                Button caughtButton = findViewById(R.id.caughtButton);
                caughtButton.setText("Mark Caught");
                caught.setTextColor(Color.parseColor("#8b0000"));
                caught.setText("NOT CAUGHT");
                caught.setVisibility(View.VISIBLE);
            }
        }
    }
}
