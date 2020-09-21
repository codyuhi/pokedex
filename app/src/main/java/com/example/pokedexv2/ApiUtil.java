package com.example.pokedexv2;

// Import all the necessary libraries and modules for the app to work
import android.util.Log;

import com.example.pokedexv2.subclasses.Ability;
import com.example.pokedexv2.subclasses.Form;
import com.example.pokedexv2.subclasses.Game_Index;
import com.example.pokedexv2.subclasses.Held_Item;
import com.example.pokedexv2.subclasses.Item;
import com.example.pokedexv2.subclasses.Mmove;
import com.example.pokedexv2.subclasses.Move;
import com.example.pokedexv2.subclasses.Move_Learn_Method;
import com.example.pokedexv2.subclasses.Species;
import com.example.pokedexv2.subclasses.Sprites;
import com.example.pokedexv2.subclasses.Sstat;
import com.example.pokedexv2.subclasses.Stat;
import com.example.pokedexv2.subclasses.Ttype;
import com.example.pokedexv2.subclasses.Type;
import com.example.pokedexv2.subclasses.Version;
import com.example.pokedexv2.subclasses.Version_Detail;
import com.example.pokedexv2.subclasses.Version_Group;
import com.example.pokedexv2.subclasses.Version_Group_Detail;
import com.example.pokedexv2.subclasses.Vversion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiUtil{
    // Will only contain static measures and constants, so it's good to set the constructor:
    private ApiUtil(){}

// Define the default url for the API call
    public static final String BASE_API_URL =
            "https://pokeapi.co/api/v2/pokemon/";
// A function to build the url from the given string, title
    public static URL buildUrl(String title){
// setup the title here and define the full url
        String fullUrl = BASE_API_URL + title;

// Create a URL object
        URL url = null;

        try{
// If the url is valid, set the string url to be a URL object
            url = new URL(fullUrl);

        } catch (Exception e){
// else, print the exception
            e.printStackTrace();
        }
// Return the successfully crafted url
        return url;
    }

// A function to get the Json content from the API call
    public static String getJson(URL url) throws IOException{

// Establish a connection to the API
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

// try/catch to make sure the data is in valid format
        try{
// Create an input stream to take the connection input
            InputStream stream = connection.getInputStream();
// Create a scanner that will parse through the input stream
            Scanner scanner = new Scanner(stream);

// Check to see if the connection has data to process
            boolean hasData = scanner.hasNext();
            if(hasData){
// If the connection has data, return the data
                return scanner.next();
            } else {
// If the connection has no data, return null
                return null;
            }
        } catch (Exception e){
// If exception is found in the above code, log the error and return null
            Log.d("Error", e.toString());
            return null;
        }
        finally{
// End the connection
            connection.disconnect();
        }
    }

    // A function to get the pokemon from the json returned by the API
    public static Poke getPokesFromJson(String json){
// Declare constants for all the classes and subclasses that the JSON will provide info about in the API response
        final String ABILITIES = "abilities";
        final String ABILITY = "ability";
        final String NAME = "name";
        final String URL = "url";
        final String IS_HIDDEN = "is_hidden";
        final String SLOT = "slot";
        final String BASE_EXPERIENCE = "base_experience";
        final String FORMS = "forms";
        final String GAME_INDICES = "game_indices";
        final String GAME_INDEX = "game_index";
        final String VERSION = "version";
        final String HEIGHT = "height";
        final String HELD_ITEMS = "held_items";
        final String ITEM = "item";
        final String VERSION_DETAILS = "version_details";
        final String RARITY = "rarity";
        final String ID = "id";
        final String IS_DEFAULT = "is_default";
        final String LOCATION_AREA_ENCOUNTERS = "location_area_encounters";
        final String MOVES = "moves";
        final String MOVE = "move";
        final String VERSION_GROUP_DETAILS = "version_group_details";
        final String LEVEL_LEARNED_AT = "level_learned_at";
        final String MOVE_LEARN_METHOD = "move_learn_method";
        final String VERSION_GROUP = "version_group";
        final String ORDER = "order";
        final String SPECIES = "species";
        final String SPRITES = "sprites";
        final String BACK_DEFAULT = "back_default";
        final String BACK_FEMALE = "back_female";
        final String BACK_SHINY = "back_shiny";
        final String BACK_SHINY_FEMALE = "back_shiny_female";
        final String FRONT_DEFAULT = "front_default";
        final String FRONT_FEMALE = "front_female";
        final String FRONT_SHINY = "front_shiny";
        final String FRONT_SHINY_FEMALE = "front_shiny_female";
        final String STATS = "stats";
        final String BASE_STAT = "base_stat";
        final String EFFORT = "effort";
        final String STAT = "stat";
        final String TYPES = "types";
        final String TYPE = "type";
        final String WEIGHT = "weight";


// Create an array to hold the pokemon and initialize it to be null
        ArrayList<Poke> pokes = null;

        try{
// Convert the json string into a json object
            JSONObject jsonPokes = new JSONObject(json);

// Get JSON data for abilities array
            JSONArray arrayAbilities = jsonPokes.getJSONArray(ABILITIES);
// Find how many abilities the given pokemon has
            int numberOfAbilities = arrayAbilities.length();
// Create an array to hold all the abilities
            Ability[] abilities = new Ability[numberOfAbilities];
            for(int i = 0; i < numberOfAbilities; i++){
                JSONObject abilityJSON = arrayAbilities.getJSONObject(i);
                boolean abilityIsHiddenJSON = abilityJSON.getBoolean(IS_HIDDEN);
                int abilitySlotJSON = abilityJSON.getInt(SLOT);
                abilityJSON = abilityJSON.getJSONObject(ABILITY);
                Ability abilityInfoJSON = new Ability (
                        abilityJSON.getString(NAME),
                        abilityJSON.getString(URL),
                        abilityIsHiddenJSON,
                        abilitySlotJSON
                );
                abilities[i] = abilityInfoJSON;
            }

// Get integer for base_experience variable
            int base_experience = jsonPokes.getInt(BASE_EXPERIENCE);

// Get JSON data for forms array
            JSONArray arrayForms = jsonPokes.getJSONArray(FORMS);
            int numberOfForms = arrayForms.length();
            Form[] forms = new Form[numberOfForms];
            for(int i = 0; i < numberOfForms; i++){
                JSONObject formJSON = arrayForms.getJSONObject(i);
                Form formInfoJSON = new Form(
                        formJSON.getString(NAME),
                        formJSON.getString(URL)
                );
                forms[i] = formInfoJSON;
            }

// Get JSON data for game_indices
            JSONArray arrayGameIndices = jsonPokes.getJSONArray(GAME_INDICES);
            int numberOfGameIndices = arrayGameIndices.length();
            Game_Index[] game_indices = new Game_Index[numberOfGameIndices];
            for(int i = 0;i < numberOfGameIndices; i++){
                JSONObject gameIndexJSON = arrayGameIndices.getJSONObject(i);
                int gameIndexGameIndexJSON = gameIndexJSON.getInt(GAME_INDEX);
                JSONObject gameIndexVersionJSON = gameIndexJSON.getJSONObject(VERSION);
                Version gameIndexVersion = new Version(
                        gameIndexVersionJSON.getString(NAME),
                        gameIndexVersionJSON.getString(URL)
                );
                Game_Index game_index = new Game_Index(
                        gameIndexGameIndexJSON,
                        gameIndexVersion
                );
                game_indices[i] = game_index;

            }

// GET JSON data for height
            int height = jsonPokes.getInt(HEIGHT);

// Get JSON data for held_items
            JSONArray arrayHeldItems = jsonPokes.getJSONArray(HELD_ITEMS);
            int numberOfHeldItems = arrayHeldItems.length();
            Held_Item[] held_items = new Held_Item[numberOfHeldItems];
            for(int i = 0; i < numberOfHeldItems; i++){
                JSONObject heldItemJSON = arrayHeldItems.getJSONObject(i);
                JSONObject heldItemItemJSON = heldItemJSON.getJSONObject(ITEM);
                Item heldItemItem = new Item(
                        heldItemItemJSON.getString(NAME),
                        heldItemItemJSON.getString(URL)
                );
                JSONArray arrayHeldItemVersion = heldItemJSON.getJSONArray(VERSION_DETAILS);
                int numberOfHeldItemVersions = arrayHeldItemVersion.length();
                Version_Detail[] version_details = new Version_Detail[numberOfHeldItemVersions];
                for(int j = 0; j < numberOfHeldItemVersions; j++){
                    JSONObject heldItemVersionVersionDetails = arrayHeldItemVersion.getJSONObject(j);
                    int heldItemVersionRarity = heldItemVersionVersionDetails.getInt(RARITY);
                    JSONObject heldItemVersionVersionDetailsVversionJSON = heldItemVersionVersionDetails.getJSONObject(VERSION);
                    Vversion heldItemVersionVersionDetailsVversion = new Vversion(
                            heldItemVersionVersionDetailsVversionJSON.getString(NAME),
                            heldItemVersionVersionDetailsVversionJSON.getString(URL)
                    );
                    Version_Detail version_detail = new Version_Detail(
                            heldItemVersionRarity,
                            heldItemVersionVersionDetailsVversion
                    );
                    version_details[j] = version_detail;
                }
                Held_Item held_item = new Held_Item(
                        heldItemItem,
                        version_details
                );
                held_items[i] = held_item;
            }

// Get JSON data for id
            int id = jsonPokes.getInt(ID);

// Get JSON data for is_default
            boolean is_default = jsonPokes.getBoolean(IS_DEFAULT);

// Get JSON data for location_area_encounters
            String location_area_encounters = jsonPokes.getString(LOCATION_AREA_ENCOUNTERS);

// Get JSON data for moves
            JSONArray arrayMoves = jsonPokes.getJSONArray(MOVES);
            int numberOfMoves = arrayMoves.length();
            Move[] moves = new Move[numberOfMoves];
            for(int i = 0; i < numberOfMoves; i++){
                JSONObject moveJSON = arrayMoves.getJSONObject(i);
                JSONObject moveMmoveJSON = moveJSON.getJSONObject(MOVE);
                Mmove mmove = new Mmove(
                        moveMmoveJSON.getString(NAME),
                        moveMmoveJSON.getString(URL)
                );
                JSONArray movesVersionGroupDetails = moveJSON.getJSONArray(VERSION_GROUP_DETAILS);
                int numberOfMovesVersionGroupDetails = movesVersionGroupDetails.length();
                Version_Group_Detail[] version_group_details = new Version_Group_Detail[numberOfMovesVersionGroupDetails];
                for(int j = 0; j < numberOfMovesVersionGroupDetails; j++){
                    JSONObject versionGroupDetailsJSON = movesVersionGroupDetails.getJSONObject(j);
                    int level_learned_at = versionGroupDetailsJSON.getInt(LEVEL_LEARNED_AT);
                    JSONObject versionGroupDetailsMoveLearnMethod = versionGroupDetailsJSON.getJSONObject(MOVE_LEARN_METHOD);
                    Move_Learn_Method move_learn_method = new Move_Learn_Method(
                            versionGroupDetailsMoveLearnMethod.getString(NAME),
                            versionGroupDetailsMoveLearnMethod.getString(URL)
                    );
                    JSONObject versionGroupDetailsVersionGroup = versionGroupDetailsJSON.getJSONObject(VERSION_GROUP);
                    Version_Group version_group = new Version_Group(
                            versionGroupDetailsVersionGroup.getString(NAME),
                            versionGroupDetailsVersionGroup.getString(URL)
                    );
                    Version_Group_Detail version_group_detail = new Version_Group_Detail(
                            level_learned_at,
                            move_learn_method,
                            version_group
                    );
                    version_group_details[j] = version_group_detail;
                }
                Move move = new Move(
                        mmove,
                        version_group_details
                );
                moves[i] = move;
            }


// Get JSON data for name
            String name = jsonPokes.getString(NAME);

// Get JSON data for order
            int order = jsonPokes.getInt(ORDER);

// Get JSON data for species
            JSONObject specieJSON = jsonPokes.getJSONObject(SPECIES);
            Species species = new Species(
                    specieJSON.getString(NAME),
                    specieJSON.getString(URL)
            );

// Get JSON data for sprites
            JSONObject spriteJSON = jsonPokes.getJSONObject(SPRITES);
            Sprites sprites = new Sprites(
                    spriteJSON.getString(BACK_DEFAULT),
                    spriteJSON.getString(BACK_FEMALE),
                    spriteJSON.getString(BACK_SHINY),
                    spriteJSON.getString(BACK_SHINY_FEMALE),
                    spriteJSON.getString(FRONT_DEFAULT),
                    spriteJSON.getString(FRONT_FEMALE),
                    spriteJSON.getString(FRONT_SHINY),
                    spriteJSON.getString(FRONT_SHINY_FEMALE)
            );

// Get JSON data for stats
            JSONArray arrayStats = jsonPokes.getJSONArray(STATS);
            int numberOfStats = arrayStats.length();
            Stat[] stats = new Stat[numberOfStats];
            for(int i = 0; i < numberOfStats; i++){
                JSONObject statJSON = arrayStats.getJSONObject(i);
                int base_stat = statJSON.getInt(BASE_STAT);
                int effort = statJSON.getInt(EFFORT);
                JSONObject sstatJSON = statJSON.getJSONObject(STAT);
                Sstat sstat = new Sstat(
                        sstatJSON.getString(NAME),
                        sstatJSON.getString(URL)
                );
                Stat stat = new Stat(
                        base_stat,
                        effort,
                        sstat
                );
                stats[i] = stat;
            }

// Get JSON data for types array
            JSONArray arrayTypes = jsonPokes.getJSONArray(TYPES);
            int numberOfTypes = arrayTypes.length();
            Type[] types = new Type[numberOfTypes];
            for(int i = 0; i < numberOfTypes; i++){
                JSONObject typeJSON = arrayTypes.getJSONObject(i);
                int slot = typeJSON.getInt(SLOT);
                JSONObject ttypeJSON = typeJSON.getJSONObject(TYPE);
                Ttype ttype = new Ttype(
                        ttypeJSON.getString(NAME),
                        ttypeJSON.getString(URL)
                );
                Type type = new Type(
                        slot,
                        ttype
                );
                types[i] = type;
            }

// Get JSON data for weight
            int weight = jsonPokes.getInt(WEIGHT);

// Return the newly created pokemon
            Poke poke = new Poke(
                    abilities,
                    base_experience,
                    forms,
                    game_indices,
                    height,
                    held_items,
                    id,
                    is_default,
                    location_area_encounters,
                    moves,
                    name,
                    order,
                    species,
                    sprites,
                    stats,
                    types,
                    weight
            );

            return poke;

        } catch (Exception e){
// If an exception is thrown, print the exception
            e.printStackTrace();
        }

// After the function's done, return the ArrayList of pokemon
        Poke failPoke = new Poke(
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

// if the return statement wasn't triggered in the try/catch statement above, this is a failed json parse
        return failPoke;
    }

// a function for returning the fulllist of all pokemon from the pokeapi
    public static URL buildFullListUrl (){
// get all the pokemon species limiting to 1000 (Since there are currently only 807, this will grab all the pokemon)
        final String FULL_LIST_URL =
                "https://pokeapi.co/api/v2/pokemon-species?limit=1000";
        URL url = null;
        try{
// try setting the url to be the pokeapi url
            url = new URL(FULL_LIST_URL);
        } catch (Exception e){
// if creating the url failed, log the error
            Log.d("Error: ", e.getMessage());
        }
// return the url that was constructed from the pokeapi url (always going to be the same url string to query)
        return url;
    }

    public static String getFullListJson(URL url) throws IOException{
// initialize the httpurlconnection for the given url
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
// try creating a stream to grab the connection's response
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
// if the scanner finds data, grab the data
            boolean hasData = scanner.hasNext();
            if(hasData){
                return scanner.next();
            }else{
                return null;
            }
        } catch (Exception e){
// if there was not a proper stream to grab, log the error
            Log.d("Error: ", e.getMessage());
            return null;
        }
        finally{
// close the connection
            connection.disconnect();
        }
    }

// a function to get the jsonstring from the asana api
    public static String getAsanaJson(URL url) throws IOException{
// initialize basic authentication keys/tokens to put in the http request header
        String basicAuth = "Bearer ";
        basicAuth += "";
        // enter key here
// open the connection and set the Authorization header to be the basicAuth string that was defined above
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",basicAuth);

        try{
// try creating a stream to grab the response from the asana api
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();
// if the scanner finds data, grab the data
            if(hasData){
                return scanner.next();
            }else{
                return null;
            }
        } catch (Exception e){
// if the connection failed log the error
            Log.d("Error: ", e.getMessage());
            return null;
        }
        finally{
// close the connection
            connection.disconnect();
        }
    }
}
