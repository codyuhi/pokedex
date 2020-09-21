package com.example.pokedexv2;

// Import all the necessary libraries and modules for the app to work
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexv2.subclasses.FullListAdapter;
import com.example.pokedexv2.subclasses.FullListPoke;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class FullList extends AppCompatActivity {
// initialize the progress bar to give feedback while network calls are happening
    private ProgressBar mLoadingProgress;
// initialize the recyclerview which will hold all the pokemon and the capability to select a pokemon
    private RecyclerView rvFullList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
// this activity uses the full_list xml file for its UI
        setContentView(R.layout.full_list);
        try{
// set the background for the screen to be the fullist image in the drawable folder
            ConstraintLayout ll = (ConstraintLayout) findViewById(R.id.Layout);
            ll.setBackgroundResource(R.drawable.fullist);
        } catch(Exception e){
// if setting the background didn't work, log the error
            Log.d("Error: ", e.getMessage());
        }
// define the progress bar for feedback when network calls are ongoing
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
// define the recycler view which will hold all the pokemon names and ids
        rvFullList = (RecyclerView) findViewById(R.id.rv_fullList);
// give the recyclerview some style by giving a divider between pokemon
        rvFullList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));;
        LinearLayoutManager fullListLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvFullList.setLayoutManager(fullListLayoutManager);

        try{
// build the full list url request to the pokeapi
            URL fullListUrl = ApiUtil.buildFullListUrl();
            new fullListQueryTask().execute(fullListUrl);
        } catch (Exception e){
// if the full list url request fails, log the error
            Log.d("Error: ", e.getMessage());
        }
    }

// create an asynctask to get the full list of pokemon from the pokeapi
    public class fullListQueryTask extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... urls){
// define the search url to access the pokeapi
            URL searchURL = urls[0];
// initialize the result string outside of the try catch block so it can be returned
            String result = null;
            try{
// get the fulllist's json from the given search URL (pokeapi)
                result = ApiUtil.getFullListJson(searchURL);
            } catch (Exception e){
// if unable to get the fulllist's json, log the error
                Log.d("Error: ", e.getMessage());
            }
// return the resulting jsonstring so it can be used
            return result;
        }

        @Override
        protected void onPostExecute(String result){
// after the network call is finished, update the textview for the error and make the progressbar invisible
            TextView tvError = (TextView) findViewById(R.id.tvError);
            mLoadingProgress.setVisibility(View.INVISIBLE);
            if(result == null){
// if no result was given from the network call, show the error on the screen
                rvFullList.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);
            }else{
// if the network call successfully returned a jsonstring, define and display the recyclerview
// build an array list of pokemon from the returned jsonstring by parsing the string for json data
                ArrayList<FullListPoke> fullListPokes = getFullListFromJson(result);
                try{
// set the adapter to be the newly created arrayList
                    FullListAdapter adapter = new FullListAdapter(fullListPokes);
                    rvFullList.setAdapter(adapter);
                } catch (Exception e){
// if the above code fails, log the error
                    Log.d("Error:", e.getMessage());
                }
            }
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
// show the progress bar while the network call is ongoing
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }

    protected static ArrayList<FullListPoke> getFullListFromJson(String json){
// initialize and define some variables that will be used to get json data
        final String NAME = "name";
        final String URL = "url";
        final String RESULTS = "results";
// initialize a new arraylist which will hold all the pokemon after this is over
        ArrayList<FullListPoke> fullListPokes = new ArrayList<FullListPoke>();

        try{
// create a json object from the string that was returned from the pokeapi
            JSONObject fullListJsonObject = new JSONObject(json);
// the json object starts with an array of values, so get that array so we can work with individual objects
            JSONArray fullListJsonArray = fullListJsonObject.getJSONArray(RESULTS);
// define the number of pokemon based on the size of the array
            int numberOfPokes = fullListJsonArray.length();
// iterate through every pokemon in the list of jsonobjects and create a new FullListPoke object for every pokemon
            for(int i = 0; i < numberOfPokes; i++){
// get the json object
                JSONObject fullListPoke = fullListJsonArray.getJSONObject(i);
// parse through the object and define the FullListPoke's name, url, and pokemon id
// the name and url were returned from the pokeapi, but the id can be inferred based on the iterator
                FullListPoke fulllistpoke = new FullListPoke(
                        fullListPoke.getString(NAME),
                        fullListPoke.getString(URL),
                        i + 1
                );
// add the newly created FullListPoke object to the jsonarray
                fullListPokes.add(fulllistpoke);
            }
        } catch (Exception e){
// if creating the FullListPoke objects failed, log the error
            Log.d("Error: ",e.getMessage());
        }
// return the array list of pokemon
        return fullListPokes;
    }
}
