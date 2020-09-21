package com.example.pokedexv2;

// Import all the necessary libraries/modules for the app to work
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class ScanPokeName extends AppCompatActivity {

// initialize a surfaceview which will hold the camera view
    SurfaceView cameraView;
// create a text view which will show what text the reader sees on the screen
    TextView textView;
// initialize a camera source which will access the phone's camera
    CameraSource cameraSource;
// set a request for the camera permission id
    final int RequestCameraPermissionID = 1001;
// initialize an empty string array that will hold all the pokemons' names after the network call to the pokeapi
    String[] all_pokemon;
// initialize a progressbar which will be visible when network calls are happening
    private ProgressBar mLoadingProgress;
// initialize a boolean which will stop the camera from processing extra pokemon strings after a valid pokemon name is found
    boolean found;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
// switch statement to start the camera if the proper permissions have been given
        switch(requestCode){
// check the camera permission,
            case RequestCameraPermissionID: {
// if permission has been granted
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
// check the manifest to see if the app properly needs the camera
// if the permissions have not been enabled, request the camera permission
                    if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(ScanPokeName.this,
                                new String[]{Manifest.permission.CAMERA},
                                RequestCameraPermissionID);
                        return;
                    }
                    try{
// if the camera permissions have been enabled, start the camera source
                        cameraSource.start(cameraView.getHolder());
                    }catch(IOException e){
// print any errors that occur while checking for permissions/starting camera
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
// the xml file that defines the UI for this activity is the scan_poke_name file
        setContentView(R.layout.scan_poke_name);

// define the camera view from the xml file
        cameraView = (SurfaceView) findViewById(R.id.surface_view);
// define the textview which will show all the detected text on the screen
        textView = (TextView) findViewById(R.id.text_view);
// define the progress bar
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
// define found to be false since a pokemon name has not been found by the camera yet
        found = false;

        try{
// build a url to get the full list of pokemon
            URL scanNameUrl = ApiUtil.buildFullListUrl();
// create an asynctask to perform a network call to the pokeapi and get all valid pokemon names
            new scanPokeQueryTask().execute(scanNameUrl);
        } catch (Exception e){
// if the url build or the asynctask above failed, log the error
            Log.d("Error: ", e.getMessage());
        }

// textrecognizer is from the Google Cloud Vision API.  This recognizes text that the camera sees
// create a new text recognizer to find text on the camera source
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()){
// if the text recognizer is unable to start, log that its dependencies are not yet enabled
            Log.w("ScanPokeName","Detector dependencies are not yet available");
        }else{
// define the camera source to be built on the text recognizer defined above. Build it
// the camera source will be the back camera, have 1280 by 1024, refresh at 2 fps, and will be able to autofocus the camera
            cameraSource = new CameraSource.Builder(getApplicationContext(),textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280,1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
// use the getHolder method of the camera view with a callback.  This will check permissions again
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try{
// basically if the permissions aren't right, set them right
                        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                            ActivityCompat.requestPermissions(ScanPokeName.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
// start the cameraSource
                        cameraSource.start(cameraView.getHolder());
                    }catch (IOException e){
// print the error if the camera source fails to start or permissions have issues being set
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
// This is empty but needs to be there for the callback function
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
// if the surface is destroyed, stop the camera
                    cameraSource.stop();
                }
            });

// set the processor for the text recognizer
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }
// receive the text detections so we can do stuff with detected text
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
// create an array which holds the detected items
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
// if something was detected, do the below stuff
                    if(items.size() != 0){
// create a new runnable which when it runs will do stuff with the text
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
// create a new stringbuilder to handle the detected text
                                StringBuilder stringBuilder = new StringBuilder();
// for every item detected, we will append it to the stringbuilder and check to see if it's a pokemon
                                for(int i = 0; i < items.size(); ++i){
// get the item from the items array
                                    TextBlock item = items.valueAt(i);
// append the new item onto the stringbuilder
                                    stringBuilder.append(item.getValue());
                                    try{
// if the network call to get all the pokemon names hasn't finished yet, don't check to see if the string is a pokemon name yet
                                        if(all_pokemon != null){
                                            if(!found && Arrays.asList(all_pokemon).contains(stringBuilder.toString().toLowerCase())){
// if the network call has finished, check to see if the all_pokemon array contains the string found by the camera (cast to lowercase)
// if the string is a pokemon's name, start a new intent and pass the pokemon name to the DisplayResult activity
                                                Intent intent = new Intent(ScanPokeName.this, DisplayResult.class);
                                                intent.putExtra(MainActivity.EXTRA_MESSAGE, stringBuilder.toString().toLowerCase());
                                                startActivity(intent);
// once the pokemon has been found, we don't want to scan for anymore pokemon
                                                ScanPokeName.this.finish();
// mark the found variable to be true so no more intents will be created despite more valid pokemon names being found
// (or duplicating an intent creation for the same pokemon name while the camera is still enabled and the intent is processing)
                                                found = true;
                                                return;
                                            }
                                        }
                                    } catch (Exception e){
// if there's an error working with the detected text, log it
                                        Log.d("Error: ", e.getMessage());
                                    }
// append a newline to the detected text's stringbuilder to show it better on the cameraview screen
                                    stringBuilder.append("\n");
                                }
// set the text on the textview to be the string builder so you know what's going on as the camera scans for text
                                textView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

// this function will grab all the pokemon names from the pokeapi so they can be compared with the detected text
    public class scanPokeQueryTask extends AsyncTask<URL,Void, String>{
        @Override
        protected String doInBackground(URL... urls){
// get the url that was passed by ApiUtil's fullList builder
            URL searchURL = urls[0];
// initialize a string which will hold the results of the network call
            String result = null;
            try{
// get the string results of the network call
                result = ApiUtil.getFullListJson(searchURL);
            }catch (Exception e){
// if the network call fails, log the error
                Log.d("Error: ", e.getMessage());
            }
// return the string json response to the pokeapi GET request
            return result;
        }

        @Override
        protected void onPostExecute(String result){
// after the network call is finished, hide the progress bar
            mLoadingProgress.setVisibility(View.INVISIBLE);
// cast the string results to be a json object
            getFullListFromJson(result);
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
// make the progress bar visible while a network call is ongoing
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }

    protected void getFullListFromJson(String json){
// setup some finalized variables which will be used to access the json attributes
        final String NAME = "name";
        final String URL = "url";
        final String RESULTS = "results";

        try{
// cast the string json variable to be a json object
            JSONObject fullListJsonObject = new JSONObject(json);
// the json object has an array that holds everything, so get that array
            JSONArray fullListJsonArray = fullListJsonObject.getJSONArray(RESULTS);
// the number of pokemon will be the size of this new array, so define that
            int numberOfPokes = fullListJsonArray.length();
// initialize the all_pokemon array to be a string the size of the fullListJsonArray
            all_pokemon = new String[numberOfPokes];
// for every pokemon, add their string name to the all_pokemon array
            for(int i = 0; i < numberOfPokes; i++){
                JSONObject fullListPoke = fullListJsonArray.getJSONObject(i);
                all_pokemon[i] = fullListPoke.getString(NAME);
            }
        } catch (Exception e){
// if something fails, log the error
            Log.d("Error: ",e.getMessage());
        }
    }
}
