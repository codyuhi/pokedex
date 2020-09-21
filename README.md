# Pokedex2
 
Project #1 – Mobile Operating Systems
Description
I created an Android application which allows Pokemon players to access base stat
information and track catching activities for all Pokemon. This application accesses two
RESTful APIs: Poke API (found at https://pokeapi.co/) and Asana API (found at
https://asana.com/guide/help/api/api). Poke API allows the application to access Pokemon
information in JSON format, while Asana API allows the application to keep track of whether a
Pokemon has been caught or not (data stored by Asana in JSON format as well). The application
also uses Google’s Cloud Vision API for its Optical Character Recognition (OCR) capabilities.
The application has four screens:
• A main screen, which the user can choose their method of querying a Pokemon. Users
can search a Pokemon by its name/id, scan a Pokémon’s name, or select the desired
Pokemon from a list of all Pokemon
• A display results screen, which displays the queried Pokémon’s information and allows
the user to mark it as caught or not caught
• A full list screen, which displays a list of all Pokemon
• A scan name screen, which allows the user to point their phone’s camera at any
Pokémon’s name and automatically pull up that Pokémon’s display screen
The application will be a powerful tool in keep track of which Pokemon a user has already
caught so he/she can achieve the goal of being a true Pokemon master.
Objectives
Create a mobile application which implements the following:
1. Multi-threaded (MT) programming. The application will use a background thread for all
network activities.
2. Inter-Process Communication (IPC) and Remote Procedure Calls (RPC). The application
will make RESTful API calls to a remote service.
3. Marshalling and Serialization. The application will convert between JSON/XML to
native data structures and custom classes.
4. The RESTful Create, Read, Update, and Delete (CRUD) paradigm. Not only will the
application be able to GET data, but also be able to create and update/delete data from the
RESTful API.
Task
1. Pick an embedded or mobile Operating System (OS) and become familiar with its
development platform.
2. Install the SDK for the development platform.
3. Develop a simple application of your choice. This project must be approved by the
instructor.
a. The app must call an external API service and exchange data.
b. The app must use at least two different APIs. Note that getting a list and
retrieving an element can be considered two different APIs.
c. Create at least two pages for the app, with at least 2 UI components per page. At
least one UI component must show parsed information obtained from the web
services.
d. Ensure that the web service APIs allow for sufficient access for the mobile
application to accomplish its use-case.
Results
1. Google’s Android OS was chosen as the target OS for this project. Therefore, the
development environment that was used for the project is Android Studio.
2. Installing and using the developer’s kit for this OS and production environment were
made possible by using resources that are listed at the end of this report.
3. The Android App is fully functional on the target device of a Samsung Galaxy S10+
running on Android v9. Implementation requirements are described below:
a. Because the application accesses two separate RESTful APIs, calling the APIs
over the network will be described below in two sections:
i. Poke API
The application calls the Poke API to obtain information for two reasons. Its primary
purpose is to obtain the JSON object data for a single Pokemon. Its secondary purpose is to 
obtain a JSON array containing the names of all Pokemon. Connecting with the Poke API is
done as an asynchronous task, and not run on the application’s main thread. This is because
network tasks often take longer to complete than tasks performed locally, and making the UI
unavailable while waiting for a network task to complete could cause serious usability issues. As
a rule, the UI should not be restricted from continuing to work as it is waiting for a network call
to complete, and Android has a hard restriction that keeps developers from running network
threads on the same thread as UI components.
Below is a code snippet which is used to perform an asynchronous network call (see
comments for a line-by-line explanation as to what it is doing).
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
 Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),
Toast.LENGTH_LONG).show();
 }
// Return the completed result (or null for failed url)
 return result;
 }
// This function executes after the network call is complete
 @Override
 protected void onPostExecute(String result) {
// Make the loading bar invisible after it's done loading
 mLoadingProgress.setVisibility(View.INVISIBLE);
// if the pokeapi is not able to return
// pokemon json for the given pokemon name/id
 if(result == null){
// tell the user that the given pokemon name does not exist
 Toast.makeText(getApplicationContext(),"Unable to find pokemon named \"" +
DEFAULT_NAME + "\"", Toast.LENGTH_LONG).show();
 return;
 }
 try{
// create intent and pass the pokemon name to the DisplayResult activity
 Intent displayResultIntent = new Intent(MainActivity.this,
DisplayResult.class);
 displayResultIntent.putExtra(EXTRA_MESSAGE, DEFAULT_NAME);
 startActivity(displayResultIntent);
 } catch (Exception e){
// if the intent does not work properly, log the error and give user feedback
 Log.d("Error: ", e.getMessage());
 Toast.makeText(getApplicationContext(),"Pokemon named \"" + DEFAULT_NAME + "\"
has too much data to pass across intents", Toast.LENGTH_LONG).show();
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
The java class in the snippet above is an extension of the AsyncTask class and overrides
certain methods to meet this project’s specific requirements. The doInBackground method is
overridden so that a string version of the JSON object that is stored with Poke API is accessible.
It takes a group of URLs and returns a string which will ultimately hold a string version of the 
JSON object that is stored by Poke API for the selected Pokemon. However, the actual
accessing the GET response from Poke API is handled by another function in the ApiUtil class.
This is the code for that (see comments for a line-by-line explanation of what the code is doing):
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
Basically, the ApiUtil.getJson() method opens a connection to the given URL and uses a
scanner to grab the data that is returned by Poke API. The data is returned as a string. Once the 
data is available as a string and the network call is complete, the onPostExecute() method creates
a new intent and passes the Pokémon’s name or id to the DisplayResult class in order for the
Pokemon’s information to be displayed there.
The network call to access the full list of all pokemon names is very similar to the
network call for a single pokemon’s information. A method called fullListQueryTask is created
as an extension of AsyncTask and it grabs a string JSON response from Poke API using a
method that is similar to the method that is shown above. This is the code for the
fullListQueryTask (see comments for a line-by-line analysis):
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
// after the network call is finished, update the textview for the error
// and make the progressbar invisible
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
The JSON array that is obtained from this network call is then able to be used to populate
the full list view of all Pokemon. The actual JSON is grabbed from the HttpUrlConnection with
a method identical to the method shown for the single Pokemon JSON object.
ii. Asana API
Network calls for Asana API are performed much like how Poke API calls are done.
However, the Asana API requires authentication in order to provide any functionality. This
makes things more complicated, as the mobile application had to pass authentication information
as part of the HTTP header to the API. Furthermore, the application used different HTTP
methods to implement CRUD functionality with Asana API. Evaluated below are the 
implementations of GET (for reading data), POST (for creating/updating data), and DELETE
(for deleting data) with Asana API.
GETting data is simple and has already been accomplished with Poke API. The added
complication of required authentication complicates things a little, but not much. Below is the
code used to create a class called updateCaught that extends the AsyncTask class, makes a
network call to Asana API, and has the option to create/delete entries on Asana API (see
comments for line-by-line analysis of the code):
// the updateCaught method connects with the Asana API and is able to CREATE or DELETE the pokemon entry
 private class updateCaught extends AsyncTask<String,Void,String>{
 @Override
 protected String doInBackground(String... inputs) {
// create user authentication for the Asana API
 String basicAuth = "Bearer ";
 basicAuth += "0/858ac4da00dcb3891449ca41645ee76b";
// if the pokemon hasn't been caught yet, we want to
// create a new asana entry for the pokemon so it can be marked caught
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
 Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),
Toast.LENGTH_LONG).show();
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
 Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),
Toast.LENGTH_LONG).show();
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
// after the network call is over, if the pokemon was marked caught,
// update the text view and caught button appropriately
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
Based on whether the pokemon was caught or not, pressing a button will initiate a call to Asana
API to either create or delete a new entry for the pokemon which marks it as caught or not
caught. HTTP headers, attributes, and methods are added to the connection before the 
connection is executed, which defines what kind of HTTP request is being performed and what
data will be included in the API request.
For all of the network calls to all APIs and every instance of passed data, the AsyncTask
extension has the option to perform operations both right before the asynchronous network call is
begun in a different thread and right after the network call is finished. These happen in the
onPreExecute and onPostExecute methods of the AsyncTask class extension. In most of the
onPreExecute methods, the application makes the progress bar visible to show the user that they
should wait. On the onPostExecute methods, the application hides the progress bar to indicate
that the network call is finished. In the onPostExecute methods, the application begins
processing and parsing the data that was retrieved from the APIs on the network calls. The
ScanPokeName class also uses a method very similar to the fullListQueryTask to get the full list
of all Pokemon since it needs to have a list of all valid Pokemon names to compare the OCR
output with.
b. Because the application accesses two separate RESTful APIs, using the returned
data is described below in two sections:
i. Poke API
Working with the JSON strings returned by Poke API meant that several classes and
subclasses had to be created to properly parse the API responses. Every Pokemon JSON object
entry has several attributes, many of which are either subclasses with further attributes or arrays
of more attributes/subclasses. Below is the Pokemon class that I defined with all the
attributes/subclasses/arrays that Poke API had information for:
package com.example.pokedexv2;
// Import the necessary classes to get the app to work
import com.example.pokedexv2.subclasses.Ability;
import com.example.pokedexv2.subclasses.Form;
import com.example.pokedexv2.subclasses.Game_Index;
import com.example.pokedexv2.subclasses.Held_Item;
import com.example.pokedexv2.subclasses.Move;
import com.example.pokedexv2.subclasses.Species;
import com.example.pokedexv2.subclasses.Sprites;
import com.example.pokedexv2.subclasses.Stat;
import com.example.pokedexv2.subclasses.Type;
import java.io.Serializable;
public class Poke implements Serializable {
// all of these attributes are available in the json provided from the pokeapi
 public com.example.pokedexv2.subclasses.Ability[] abilities;
 public Integer base_experience;
 public com.example.pokedexv2.subclasses.Form[] forms;
 public com.example.pokedexv2.subclasses.Game_Index[] game_indices;
 public Integer height;
 public com.example.pokedexv2.subclasses.Held_Item[] held_items;
 public Integer id;
 public boolean is_default;
 public String location_area_encounters;
 public com.example.pokedexv2.subclasses.Move[] moves;
 public String name;
 public Integer order;
 public com.example.pokedexv2.subclasses.Species species;
 public com.example.pokedexv2.subclasses.Sprites sprites;
 public com.example.pokedexv2.subclasses.Stat[] stats;
 public com.example.pokedexv2.subclasses.Type[] types;
 public Integer weight;
 public Poke(
 Ability[] abilities,
 Integer base_experience,
 Form[] forms,
 Game_Index[] game_indices,
 Integer height,
 Held_Item[] held_items,
 Integer id,
 boolean is_default,
 String location_area_encounters,
 Move[] moves,
 String name,
 Integer order,
 Species species,
 Sprites sprites,
 Stat[] stats,
 Type[] types,
 Integer weight
 ){
 this.abilities = abilities;
 this.base_experience = base_experience;
 this.forms = forms;
 this.game_indices = game_indices;
 this.height = height;
 this.held_items = held_items;
 this.id = id;
 this.is_default = is_default;
 this.location_area_encounters = location_area_encounters;
 this.moves = moves;
 this.name = name;
 this.order = order;
 this.species = species;
 this.stats = stats;
 this.sprites = sprites;
 this.types = types;
 this.weight = weight;
 }
}
Each attribute that is available in the Poke class is accessible in the Poke API response.
Below is a snippet of how my code parsed through and created a Poke object using the string
JSON response that was returned by Poke API (see comments):
// A function to get the pokemon from the json returned by the API
 public static Poke getPokesFromJson(String json){
// Declare constants for all the classes and subclasses that
// the JSON will provide info about in the API response
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
 JSONObject heldItemVersionVersionDetailsVversionJSON =
heldItemVersionVersionDetails.getJSONObject(VERSION);
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
 Version_Group_Detail[] version_group_details = new
Version_Group_Detail[numberOfMovesVersionGroupDetails];
 for(int j = 0; j < numberOfMovesVersionGroupDetails; j++){
 JSONObject versionGroupDetailsJSON = movesVersionGroupDetails.getJSONObject(j);
 int level_learned_at = versionGroupDetailsJSON.getInt(LEVEL_LEARNED_AT);
 JSONObject versionGroupDetailsMoveLearnMethod =
versionGroupDetailsJSON.getJSONObject(MOVE_LEARN_METHOD);
 Move_Learn_Method move_learn_method = new Move_Learn_Method(
 versionGroupDetailsMoveLearnMethod.getString(NAME),
 versionGroupDetailsMoveLearnMethod.getString(URL)
 );
 JSONObject versionGroupDetailsVersionGroup =
versionGroupDetailsJSON.getJSONObject(VERSION_GROUP);
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
First, the application had to cast the string JSON response into a JSON object. After the
data was in a JSON object, the application was able to specify which parts of the data should be
assigned to certain attributes of the Poke class object. When the Poke attribute was not a
standard data structure, further processing had to be done. This means that for arrays of custom
subclasses, the application had to iterate through the JSON data and create custom class objects
little by little and then add them to an array which was then assigned to the Poke class attribute.
After all the parsing of information from the original JSON object was over, the custom
subclasses and values were assigned to a new Poke class object and returned to be used by the
DisplayResult class. This has to be done for every Pokemon that is shown on the DisplayResult
page. In the code snippet above, the subclasses’ individual attributes can be seen as they are
defined while parsing through the original JSON data.
Another custom class that was created for the application was the FullListPoke class.
This class has fewer necessary attributes on it, since the full list API query only returns JSON
data that contains a Pokemon’s name and url. The FullListPoke class also has an integer id
attribute for the Pokemon ID. This is the definition of the FullListPoke class:
package com.example.pokedexv2.subclasses;
public class FullListPoke {
// the fulllistpoke class is defined by the data given by the pokeapi
 public String name;
 public String url;
 public int id;
 public FullListPoke(
 String name,
 String url,
 int id
 ){
 this.name = name;
 this.url = url;
 this.id = id;
 }
}
The FullListPoke class has its attributes filled in a method in the FullList class. This is
how the FullListPoke class object has its attributes defined in the FullList class:
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
// the json object starts with an array of values,
// so get that array so we can work with individual objects
 JSONArray fullListJsonArray = fullListJsonObject.getJSONArray(RESULTS);
// define the number of pokemon based on the size of the array
 int numberOfPokes = fullListJsonArray.length();
// iterate through every pokemon in the list of
// jsonobjects and create a new FullListPoke object for every pokemon
 for(int i = 0; i < numberOfPokes; i++){
// get the json object
 JSONObject fullListPoke = fullListJsonArray.getJSONObject(i);
// parse through the object and define the FullListPoke's name, url, and pokemon id
// the name and url were returned from the pokeapi,
// but the id can be inferred based on the iterator
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
Much like the definition of attributes for the Poke object, the FullListPoke object is
defined by converting the string JSON data into a JSON object, then accessing the JSON array
which hold all the instances of a Pokemon, then creating a new FullListPoke class instance for
every object in the JSON array and defining the FullListPoke’s attributes according to the data in
the JSON object. Furthermore each FullListPoke is assigned an integer id by the value of the
iterator i since Poke API does not give that information from the API request that gives all
Pokemon information.
Parsing through the JSON data that is returned by the API call is valuable because it
allows for custom classes to be defined based on data that is maintained by a third party and is
easily manipulated according to the application’s needs. In this project, parsing the data made it
possible to easily pass the Pokemon’s information across different UI components and improved
the readability of the code because attribute access is done by the class name <DOT>
attribute/function name instead of working with magical numbers and arrays.
ii. Asana API
Working with the data returned by Asana API is a little different than working with the
data returned from Poke API. Because Asana is only holding an array of Pokemon names for all
Pokemon that are marked as caught, the only data that needs to be worked with by the
application is whether the name of a specific Pokemon exists in the array and how many
Pokemon are currently there in the array. This means that a custom data structure does not have
to be defined in order to have functionality with Asana. Nevertheless, the data must be worked
with and so code snippets are given for how the application uses the data received from network
calls to Asana API.
On the MainActivity page, a TextView shows the count of how many Pokemon have
been caught. This data is pulled from Asana API and that looks like this:
// try getting the json string from the asana network call's response
 String test = ApiUtil.getAsanaJson(searchURL);
// cast the string to be a json object
 JSONObject testObject = new JSONObject(test);
// use the given json, which contains a json array called "data"
 JSONArray testArray = testObject.getJSONArray("data");
// the number of caught pokemon will be the length of the array,
// so access that variable and make it publicly available
 caughtCount = testArray.length();
// after the network call is done, update the caughtCount textview to show the
number of pokemon caught
 TextView caughtCountTv = (TextView) findViewById(R.id.caughtCount);
 caughtCountTv.setText(Integer.toString(caughtCount) + "/807 Pokemon
Caught");
This operation is visibly simpler than creating a whole custom class, but its simplicity
still fulfills its purpose. Asana data is also worked with on the DisplayResult class entirely
within the AsyncTask extensions that are expressed in code snippets for section A of this task.
As can be seen there, the Asana API data is cast into an array. This array is iterated through and
compared with the current Pokemon’s name to see if the current pokemon exists in the array of
already caught pokemon. If the pokemon has not been marked as caught, all UI components
having to do with catching the pokemon are updated accordingly.
For all other examples of code expressing the application’s functionality, please see the
code included in the zip file that was submitted.
c. All visible pages and UI components are evaluated in the sections below:
i. UI components that were customized for the entire application:
Picture of the Pokedex Custom App Icon
I created a custom app icon using GIMP. I was able to add this custom app icon to the
project by copying my created appicon.ico file into the drawable folder for the android project,
creating a new image asset, setting the icon type to be my own image, then clicking finish. The
steps for implementing my own app icon were taken from https://www.stechies.com/set-changeicon-android-app/.
Picture of the Changed App Bar Color
I changed the color for the app bar because I thought the default green color did not
match the color scheme for a Pokedex as recognized from Pokedexes in the Pokemon series. To
change the app bar color to be red instead of green, I changed the color for the colorPrimary in
the styles.xml file and the colors.xml file. The result looks like this:
styles.xml
<resources>
 <!-- Base application theme. -->
 <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
 <!-- Customize your theme here. -->
 <item name="colorPrimary">#ff0000</item>
 <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
 <item name="colorAccent">@color/colorAccent</item>
 </style>
 <style name="ProgressBarStyle">
 <item name="colorAccent">#ff0000</item>
 </style>
</resources>
colors.xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
 <color name="colorPrimary">#5c5c5c5c</color>
 <color name="colorPrimaryDark">#1f1f1f</color>
 <color name="colorAccent">#FF0000</color>
</resources>
With the above code snippets, I set the default colors for the whole application’s style to
be a dark blackish color and a Pokemon-style red. This includes for all progress bars. By
default, all progress bars have a magenta color. By changing the colorAccent for progress bars, I
made the progress bars appear red. I followed tutorials from
https://stackoverflow.com/questions/26496411/android-material-status-bar-color-wont-change
and https://www.zidsworld.com/android-development/how-to-set-progressbar-colorprogrammatically-in-android-java/ to accomplish this color change for the whole app.
I added a back button for all activities that were not the main activity to make it easier to
navigate between activities. To do this, I added properties to the child activities in the manifest
file. I designated that the main activity is the parent activity for all other activities, which
allowed the application to automatically apply the back button option at the top bar on the screen
for every child activity. This made my manifest file look like this for the child activities:
<activity android:name=".MainActivity">
 <intent-filter>
 <action android:name="android.intent.action.MAIN" />
 <category android:name="android.intent.category.LAUNCHER" />
 </intent-filter>
 </activity>
<!-- Include/use the DisplayResult class to show results-->
 <activity android:name=".DisplayResult"
 android:parentActivityName=".MainActivity">
 <meta-data
 android:name="android.support.PARENT_ACTIVITY"
 android:value=".MainActivity"/>
 </activity>
<!-- include the fulllist class and make it so it can easily return to the main
page-->
 <activity android:name=".FullList"
 android:parentActivityName=".MainActivity">
 <meta-data
 android:name="android.support.PARENT_ACTIVITY"
 android:value=".MainActivity"/>
 </activity>
<!-- include the scanpokename class and make it so it can easily return to the
main page-->
 <activity android:name=".ScanPokeName"
 android:parentActivityName=".MainActivity">
 <meta-data
 android:name="android.support.PARENT_ACTIVITY"
 android:value=".MainActivity"/>
 </activity>
AndroidManifest code snippet for all activities
ii. MainActivity.java
MainActivity Screen
The MainActivity screen has a lot of UI components to it. In summary, the screen has a
background image, several TextViews, and EditTextView, and several buttons.
• Background Image
In order to set the background image, I downloaded an image and placed it in the drawable
folder. Then on the MainActivity class, I created a ConstraintLayout object and used the
setBackgroundResource method to define the background image file as the desired background
image. To access the ConstraintLayout for the full screen, I had to also give the
ConstraintLayout an id in the activity_main.xml file. In the MainActivity.java file, setting the
background image looked like this:
// Access the constraint layout to change the main screen's background
 ConstraintLayout RL = (ConstraintLayout) findViewById(R.id.am);
// set the background image to the mysterydungeon image in the drawable directory
 RL.setBackgroundResource(R.drawable.mysterydungeon);
// create button variable to access the search button
Code snippet from MainActivity.java
In the activity_main.xml file, setting the id for the ConstraintLayout looked like this:
<androidx.constraintlayout.widget.ConstraintLayout
xmlns:android="http://schemas.android.com/apk/res/android"
 xmlns:app="http://schemas.android.com/apk/res-auto"
 xmlns:tools="http://schemas.android.com/tools"
 android:layout_width="match_parent"
 android:layout_height="match_parent"
 tools:context=".MainActivity"
 android:id="@+id/am">
. . .
</android.constraintlayout.widget.ConstraintLayout>
Code snippet from activity_main.xml
Implementing this code gives the result of the background image being set as seen in the
screenshot above.
• Text Views
MainActivity screen (TextViews emphasized)
There are several TextViews defined and displayed on the MainActivity screen. The most
prominent TextView is the one displaying the title. To define the text for that TextView and
display it on the activity, changes must be made in the activity_main.xml file. In
activity_main.xml, the TextView must be placed on the screen with constraints defined in the
code snippet below:
<TextView
 android:id="@+id/pokedex_title"
 android:layout_width="wrap_content"
 android:layout_height="wrap_content"
 android:text="Pokedex"
 android:textStyle="bold"
 android:textSize="75dp"
 app:layout_constraintBottom_toTopOf="@+id/editText"
 app:layout_constraintEnd_toEndOf="parent"
 app:layout_constraintStart_toStartOf="parent"
 app:layout_constraintTop_toTopOf="parent" />
Code snippet from activity_main.xml
Here, the text for the TextView is defined by android:text=”Pokedex”. The TextView is
further stylized by setting its style to bold and changing the size to be more prominent than other
elements on the page. The app:layout_constraintxxx properties constrain the TextView to be
displayed at a certain position on the screen. Instantiating the TextView in MainActivity.java is
unnecessary because its text is static and does not change. This same setup applies to the
copyright TextView which has a static text message. However when displaying the number of
Pokemon caught, the text value must be dynamically set in MainActivity.java. Setting the text
for that TextView looks like this:
// after the network call is done, update the caughtCount textview to show the number
of pokemon caught
 TextView caughtCountTv = (TextView) findViewById(R.id.caughtCount);
 caughtCountTv.setText(Integer.toString(caughtCount) + "/807 Pokemon
Caught");
Code snippet from MainActivity.java
In this code snippet, the TextView’s text value is dynamically set by the setText method.
In this case, the caughtCount variable is an integer which is defined during the network call to
the Asana API.
• Edit Text View
MainActivity screen (EditText view emphasized)
Much like setting the TextView UI components, the EditText box is defined in the
activity_main.xml file. This looks like:
<EditText
 android:id="@+id/editText"
 android:layout_width="wrap_content"
 android:layout_height="wrap_content"
 android:ems="10"
 android:hint="Enter Pokemon Name/ID"
 android:inputType="textPersonName"
 app:layout_constraintBottom_toBottomOf="parent"
 app:layout_constraintEnd_toEndOf="parent"
 app:layout_constraintStart_toStartOf="parent"
 app:layout_constraintTop_toTopOf="parent"
 app:layout_constraintVertical_bias="0.3" />
Code snippet from activity_main.xml
One of the unique attributes of the EditText view is that a hint can be defined for the text
box. This hint is used to prompt the user concerning what they should input into the text box. 
This EditText view was instantiated in the MainActivity.java file to be able to not only be
submitted by clicking the labeled button, but to submit by pressing the enter key on the virtual
keyboard. This was accomplished by the code snippet below (see comments):
// Enable the EditText to be able to start a new intent
// if the user presses the enter key instead of the designated search button
 userInput.setOnKeyListener(new View.OnKeyListener() {
 @Override
 public boolean onKey(View v, int keyCode, KeyEvent event) {
 try {
// If the event is a key-down event on the "enter" button
 if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode ==
KeyEvent.KEYCODE_ENTER)) {
// Perform action on key press
 String givenText =
userInput.getText().toString().toLowerCase();
// check if the given string is an empty string
 if(givenText.length() == 0){
// if it's an empty string, don't do anything and tell the user to give a real pokemon
name
 Toast.makeText(getApplicationContext(),"Please enter a
Pokemon name to search", Toast.LENGTH_LONG).show();
 return false;
 }
// set the DEFAULT_NAME variable to be the user's input
 DEFAULT_NAME = givenText;
// Create a pokeUrl with the given string for pokemon to be searched
 URL pokeUrl = ApiUtil.buildUrl(givenText);
// create a new pokeQueryTask and execute the AsyncTask passing the built URL for the
pokeapi
 new pokeQueryTask().execute(pokeUrl);
 return true;
 }
 } catch (Exception e){
// if any of the above fails, log the error
// and give user feedback to give a proper pokemon name
 Log.d("Error: ",e.getMessage());
 Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),
Toast.LENGTH_LONG).show();
 }
 return false;
 }
 });
 }
Code snippet from MainActivity.java
The first if statement from the code snippet above is the one that checks to see if the enter
button is pressed. If so, process the text as if the submit Button was pressed.
• Buttons
MainActivity screen (emphasis added for buttons)
All three of the buttons here do basically the same thing: allow the user to navigate to a
different screen. The search button is used in conjunction with the EditText view to search
for a certain Pokemon based on the given name or Pokemon ID. This button also checks to
make sure that the Pokemon name given in the EditText view is a valid Pokemon name
before attempting to navigate to another screen and work with the Poke API’s JSON data. It
accomplishes this by doing the following code when pressed (see comments):
// function to take the user directly to the DisplayResult activity,
// passing the given pokemon name/id
// this function takes the user input in the EditText view and
// passes it in the intent for the DisplayResult class to use
 search.setOnClickListener(new View.OnClickListener(){
 public void onClick(View arg0){
 try {
// take the user's input for the pokemon name/id.
// Cast the information to string and set all chars to lower case
 String givenText = userInput.getText().toString().toLowerCase();
// define the default name to be the given text
 DEFAULT_NAME = givenText;
// Create a pokeUrl with the given string for pokemon to be searched
 URL pokeUrl = ApiUtil.buildUrl(givenText);
// if the user submitted an empty string, tell them they need to give a pokemon name
// this eliminates unnecessary network calls
 if(givenText.length() == 0){
// Toast the prompt for pokemon name/id if nothing was given
 Toast.makeText(getApplicationContext(),"Please enter a Pokemon
name to search", Toast.LENGTH_LONG).show();
 }else {
// if there was user input given, create a new pokeQueryTask
// and execute the AsyncTask passing the built URL for the pokeapi
 new pokeQueryTask().execute(pokeUrl);
 }
// if any of the above failed, log the error and give user feedback
// for what went wrong
 } catch (Exception e){
 Log.d("Error: ",e.getMessage());
 Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),
Toast.LENGTH_LONG).show();
 }
 }
 });
MainActivity code snippet
As seen above, the onClick event automatically rejects an empty string. It also checks to
make sure that the given pokemon name or id returns a valid response from Poke API. If the
response returned is invalid, the user will not be taken to another screen. If it is a valid Pokemon
name, the user will be taken to the DisplayResult activity through an intent and pass the
Pokemon name or ID in the intent. Buttons are defined in the activity_main.xml file like this:
<Button
 android:id="@+id/search"
 android:layout_width="wrap_content"
 android:layout_height="wrap_content"
 android:text="Search"
 app:layout_constraintBottom_toBottomOf="parent"
 app:layout_constraintEnd_toEndOf="parent"
 app:layout_constraintHorizontal_bias="0.498"
 app:layout_constraintStart_toStartOf="parent"
 app:layout_constraintTop_toBottomOf="@+id/editText"
 app:layout_constraintVertical_bias="0.064" />
activity_main.xml code snippet
Creating a Button is like creating a TextView. The text can be defined in the xml file,
and its position on the screen will be designated by constraints placed on the Button tag.
The second button allows the user to be taken to a different screen which allows them to
use the phone’s camera to pan over a Pokemon name and automatically pull up the Pokedex
entry for that Pokemon. Because this even does not need to pass any information or check to see
if a given input is valid, it is more straightforward.
// function to take the user to the ScanPokeName activity
// the ScanPokeName activity allows the user to show their camera to a
// pokemon name and pull its info up from there
 scanPokeName.setOnClickListener(new View.OnClickListener(){
 public void onClick(View arg0){
 try {
// start the next activity with an intent
 Intent scanIntent = new Intent(MainActivity.this,
ScanPokeName.class);
 startActivity(scanIntent);
 } catch (Exception e){
// if the above code fails, log the error
 Log.d("Error: ",e.getMessage());
 }
 }
 });
MainActivity code snippet
As seen above, the code for this is much simpler because there is no input validation. A
new intent is simply defined and started. The full list button is very similar to this button, except
that the intent takes the user to the FullList screen instead of the ScanPokeName screen.
iii. DisplayResult.java
DisplayResult Screen
The DisplayResult screen is used to show basic stats for the selected pokemon, the
Pokemon’s sprite, and whether the pokemon has been caught by the user. This is also the screen
from which the user can update whether the pokemon has been caught. This screen, as a result
has several UI components. The screen has an ImageView, several dynamically updated 
TextViews, a Button, plays the pokemon’s cry (audio), a dynamically changed background color
based on the Pokemon’s type, and can be swiped left or right to navigate between Pokemon.
• Image View
The Pokemon’s sprite is found at a url that is returned by Poke API as an attribute of the
Poke class object. In order to dedicate a portion of the screen to displaying the ImageView, this
code was placed in the display_result.xml file:
<ImageView
 android:id="@+id/imageView2"
 android:layout_width="328dp"
 android:layout_height="352dp"
 app:layout_constraintBottom_toBottomOf="parent"
 app:layout_constraintEnd_toEndOf="parent"
 app:layout_constraintHorizontal_bias="0.493"
 app:layout_constraintStart_toStartOf="parent"
 app:layout_constraintTop_toTopOf="parent"
 app:layout_constraintVertical_bias="0.002" />
Code snippet from display_result.xml file
This is very similar to the way that other xml elements are declared. The ImageView is
updated with a sprite that is obtained by a network call to the URL that is given by Poke API.
Since it is obtained in a network call, an AsyncTask is spun up and grabs the resource stored at
the URL for the sprite (see comments).
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
 Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),
Toast.LENGTH_LONG).show();
 }
. . .
// return the sprite image
 return mIcon11;
 }
Code snippet from DisplayResult.java
This returned sprite is then set as the image displayed in the ImageView that is defined in
the xml file.
• Text Views
The TextViews are defined dynamically here in the same way that TextViews are defined
and displayed on the MainActivity screen. Information is taken from the Poke object that was
defined from the Poke API call, but essentially setting the UI is the same.
• Button
This button performs either a POST or DELETE request to the Asana API based on whether
an entry for the current Pokemon exists in Asana. For the UI, the Button prompt is also updated
based on whether the Pokemon has been caught. Defining and implementing the button is very
similar to how buttons are done for the MainActivity screen.
• Audio
Playing the Pokemon’s cry is done by accessing an audio file that is stored on
pokemoncries.com. Because this is another network call, this is done at the same time as 
accessing the Pokemon’s sprite. This code takes place directly after the ImageView code in
DisplayResult.java (see comments):
try {
// create a media player for the pokemon's cry
 MediaPlayer player = new MediaPlayer();
// set the audio stream
 player.setAudioStreamType(AudioManager.STREAM_MUSIC);
// pokemoncries.com the pokemons' cries systematically available by get request
 String cryUrl = "https://pokemoncries.com/";
 if(poke_id < 650){
// pokemon whose ids are less than 650 have their cries stored in a different
directory than newer pokemon cries
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
 Toast.makeText(getApplicationContext(),"Error: " + e.getMessage(),
Toast.LENGTH_LONG).show();
 }
Code snippet from DisplayResult.java
• Dynamically Changed Background Color
The background color is dynamically changed based on the Pokemon’s type. This is done
after the Poke API network call is finished and the Poke class object’s type attribute has been
defined. To change the background color, this switch statement is implemented:
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
Code snippet from DisplayResult.java
The RL object is a ConstraintLayout. This method of changing the background is very
similar to the method of changing the background to be an image from the drawable folder. The
important part about changing the color in this case is how the color is dynamically changed to
match the current Pokemon’s type.
• Change Pokemon by swiping left or right
To allow for faster changing between Pokemon, the application implements the functionality
of creating a new intent and starting a new screen if it detects that the user has swiped left or
right on the DisplayResult screen. The code for this is found below (see comments):
// x1 and x2 are used here to determine whether the user has swiped left or right
 public float x1, x2;
. . .
// this function allows for the page to be swiped left or right to change the pokemon
 public boolean onTouchEvent(MotionEvent touchEvent){
// when the screen is touched,
 switch(touchEvent.getAction()){
// get the initial x position of the user's touch when the
// finger first touches the screen
 case MotionEvent.ACTION_DOWN:
 x1 = touchEvent.getX();
 break;
// get the final x position of the user's touch when the
// user's finger leaves the screen
 case MotionEvent.ACTION_UP:
 x2 = touchEvent.getX();
// if the initial x value of the touch is less than the
// final value, they have swiped left
 if(x1 < x2){
 if(poke.id == 1){
// catch it before they swipe left on bulbasaur,
// who is the edge case and should not be able to swipe left
 Toast.makeText(getApplicationContext(),"Cannot swipe left.
Bulbasaur is the first Pokemon.", Toast.LENGTH_LONG).show();
 break;
 }
// create a new intent and pass the previous pokemon's id to the new intent
 Intent intent = new Intent(DisplayResult.this,
DisplayResult.class);
 intent.putExtra(MainActivity.EXTRA_MESSAGE,
Integer.toString(poke.id - 1));
 startActivity(intent);
 }else if(x1 > x2){
// if the beginning x value is more than the final x value, they swiped right
 if(poke.id == 807){
// compensate for edge case for Zeraora
 Toast.makeText(getApplicationContext(),"Cannot swipe right.
Zeraora is the last Pokemon.", Toast.LENGTH_LONG).show();
 break;
 }
// create new intent and pass the poke-id for the next pokemon in the list
 Intent intent = new Intent(DisplayResult.this,
DisplayResult.class);
 intent.putExtra(MainActivity.EXTRA_MESSAGE,
Integer.toString(poke.id + 1));
 startActivity(intent);
 }
 break;
 }
 return false;
 }
Code snippet from DisplayResult.java
iv. FullList.java
FullList Screen
The FullList screen may be one of the more complicated displays used in this application. It
implements a background image and a RecyclerView that holds the names and IDs of every
Pokemon. Background Image
The background image in the FullList screen is set in the same way that the background
image is set in the MainActivity screen. The only difference is the file that is chosen as the
background image.
• Recycler View
This view is simply defined in full_list.xml with:
• <androidx.recyclerview.widget.RecyclerView
 android:id="@+id/rv_fullList"
 android:layout_width="match_parent"
 android:layout_height="match_parent">
Code snippet from full_list.xml
However, since the RecyclerView itself is not the focus of stylization, but every item in the
RecyclerView is – the RecyclerView has several item properties shown in a different xml file
that is specifically for defining the style of individual items:
• <?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
 android:orientation="horizontal" android:layout_width="match_parent"
 android:layout_height="wrap_content"
 android:weightSum="100">
 <ImageView
 android:layout_width="0dp"
 android:layout_weight="15"
 android:layout_height="wrap_content"
 android:src="@drawable/full_list_open"
 android:padding="8dp"/>
 <LinearLayout
 android:layout_width="0dp"
 android:layout_weight="85"
 android:layout_height="wrap_content"
 android:orientation="vertical">
 <TextView
 android:layout_width="match_parent"
 android:layout_height="wrap_content"
 android:id="@+id/fullListName"
 android:textColor="@color/colorPrimaryDark"
 android:textSize="@dimen/name_size"
 android:paddingTop="8dp"
 android:paddingLeft="5dp"
 android:text="@string/pokemon_name"/>
 <TextView
 android:layout_width="match_parent"
 android:layout_height="wrap_content"
 android:id="@+id/fullListId"
 android:textColor="@color/colorAccent"
 android:textSize="@dimen/id_size"
 android:paddingBottom="8dp"
 android:paddingLeft="10dp"
 android:text="@string/pokemon_id_number"/>
 </LinearLayout>
</LinearLayout>
Code snippet from full_list_item.xml
This positions the text on each item in the RecyclerView as well as access the sprite and sets
the color for text. To instantiate and define all items in the RecyclerView, this code is used in an
adapter class file (see comments):
• package com.example.pokedexv2.subclasses;
// Import the necessary libraries/modules for the app to work
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pokedexv2.DisplayResult;
import com.example.pokedexv2.MainActivity;
import com.example.pokedexv2.R;
import java.util.ArrayList;
public class FullListAdapter extends
RecyclerView.Adapter<FullListAdapter.FullListViewHolder>{
// create a arraylist for all the fulllist pokes
 ArrayList<FullListPoke> flpokes;
 public FullListAdapter(ArrayList<FullListPoke> flpokes){
 this.flpokes = flpokes;
 }
 @Override
 public FullListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int
viewType) {
// initialize the context
 Context context = parent.getContext();
// create the itemview
 View itemView = LayoutInflater.from(context)
 .inflate(R.layout.full_list_item, parent, false);
 return new FullListViewHolder(itemView);
 }
 @Override
 public void onBindViewHolder(@NonNull FullListViewHolder holder, int
position) {
// bind the viewholder for the fulllistpoke
 FullListPoke fullListPoke = flpokes.get(position);
 holder.bind(fullListPoke);
 }
 @Override
 public int getItemCount() {
 return flpokes.size();
 }
 public class FullListViewHolder extends RecyclerView.ViewHolder implements
View.OnClickListener{
 TextView fullListName;
 TextView fullListId;
 public FullListViewHolder(View itemView) {
 super(itemView);
// the fulllist name and id are found and set for the item
 fullListName = (TextView) itemView.findViewById(R.id.fullListName);
 fullListId = (TextView) itemView.findViewById(R.id.fullListId);
 itemView.setOnClickListener(this);
 }
 public void bind (FullListPoke fullListPoke){
 try{

fullListName.setText(Character.toUpperCase(fullListPoke.name.charAt(0))
 + fullListPoke.name.substring(1));
 fullListId.setText("Pokemon id: " +
Integer.toString(fullListPoke.id));
 } catch(Exception e){
 Log.d("Error: ", e.getMessage());
 }
 }
 @Override
 public void onClick(View view) {
// when clicked, start a new intent for the pokemon name who occupied the cell
that was clicked
 int position = getAdapterPosition();
 FullListPoke flpoke = flpokes.get(position);
 Intent intent = new Intent(view.getContext(), DisplayResult.class);
 intent.putExtra(MainActivity.EXTRA_MESSAGE, flpoke.name);
 view.getContext().startActivity(intent);
 }
 }
}
Code snippet from FullListAdapter.java
Once each individual item is defined and the code is setup to handle the json data
properly, the RecyclerView is instantiated on the screen like any other view.
• public class FullListViewHolder extends RecyclerView.ViewHolder implements
View.OnClickListener{
 TextView fullListName;
 TextView fullListId;
 public FullListViewHolder(View itemView) {
 super(itemView);
// the fulllist name and id are found and set for the item
 fullListName = (TextView) itemView.findViewById(R.id.fullListName);
 fullListId = (TextView) itemView.findViewById(R.id.fullListId);
 itemView.setOnClickListener(this);
 }
 public void bind (FullListPoke fullListPoke){
 try{

fullListName.setText(Character.toUpperCase(fullListPoke.name.charAt(0))
 + fullListPoke.name.substring(1));
 fullListId.setText("Pokemon id: " +
Integer.toString(fullListPoke.id));
 } catch(Exception e){
 Log.d("Error: ", e.getMessage());
 }
 }
Code snippet from FullList.java
v. ScanPokeName.java
ScanPokeName Screen
The ScanPokeName screen is simple to implement but as impressive visuals thanks to the
Google Cloud Vision API. This OCR capability is used in only two UI components for this
screen. The first component is accessing the phone’s CameraView as a SurfaceView and
displaying the detected text on screen through a TextView.
• CameraView
The CameraView is begun on the screen by ensuring that first permissions are enabled for
camera usage by the application and then opening the camera and displaying it on the screen.
The xml file for this capability is relatively simple and can be seen here:
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
 android:layout_width="match_parent"
 android:layout_height="match_parent"
 xmlns:tools="http://schemas.android.com/tools"
 tools:context="com.example.pokedexv2.ScanPokeName">
 <SurfaceView
 android:id="@+id/surface_view"
 android:layout_width="match_parent"
 android:layout_height="match_parent"/>
 <TextView
 android:id="@+id/text_view"
 android:text="No Text Detected"
 android:layout_alignParentBottom="true"
 android:textColor="@android:color/white"
 android:textSize="20sp"
 android:layout_width="match_parent"
 android:layout_height="match_parent"/>
 <ProgressBar
 android:id="@+id/pb_loading"
 style="?android:attr/progressBarStyle"
 android:layout_width="wrap_content"
 android:layout_height="wrap_content"
 android:layout_alignParentStart="true"
 android:layout_alignParentLeft="true"
 android:layout_alignParentTop="true"
 android:layout_alignParentEnd="true"
 android:layout_alignParentRight="true"
 android:layout_alignParentBottom="true"
 android:layout_marginStart="176dp"
 android:layout_marginLeft="176dp"
 android:layout_marginTop="341dp"
 android:layout_marginEnd="187dp"
 android:layout_marginRight="187dp"
 android:layout_marginBottom="342dp"
 android:visibility="invisible"/>
</RelativeLayout>
Code snippet from scan_poke_name.xml
Implementing this is so simple, in fact, that the biggest part of the scan_poke_name.xml
file was the progress bar which is implemented on all other screens as well. Instantiating the
camera view looks like this (see comments):
// initialize a surfaceview which will hold the camera view
 SurfaceView cameraView;
// create a text view which will show what text the reader sees on the screen
 TextView textView;
// initialize a camera source which will access the phone's camera
 CameraSource cameraSource;
// set a request for the camera permission id
 final int RequestCameraPermissionID = 1001;
// initialize an empty string array that will hold all the pokemons' names after the
network call to the pokeapi
 String[] all_pokemon;
// initialize a progressbar which will be visible when network calls are happening
 private ProgressBar mLoadingProgress;
// initialize a boolean which will stop the camera from processing extra pokemon
strings after a valid pokemon name is found
 boolean found;
 @Override
 public void onRequestPermissionsResult(int requestCode, @NonNull String[]
permissions, @NonNull int[] grantResults){
// switch statement to start the camera if the proper permissions have been given
 switch(requestCode){
// check the camera permission,
 case RequestCameraPermissionID: {
// if permission has been granted
 if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
// check the manifest to see if the app properly needs the camera
// if the permissions have not been enabled, request the camera permission

if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) !=
PackageManager.PERMISSION_GRANTED){
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
// create an asynctask to perform a network call to the pokeapi and get all valid
pokemon names
 new scanPokeQueryTask().execute(scanNameUrl);
 } catch (Exception e){
// if the url build or the asynctask above failed, log the error
 Log.d("Error: ", e.getMessage());
 }
// textrecognizer is from the Google Cloud Vision API. This recognizes text that the
camera sees
// create a new text recognizer to find text on the camera source
 TextRecognizer textRecognizer = new
TextRecognizer.Builder(getApplicationContext()).build();
 if(!textRecognizer.isOperational()){
// if the text recognizer is unable to start, log that its dependencies are not yet
enabled
 Log.w("ScanPokeName","Detector dependencies are not yet available");
 }else{
// define the camera source to be built on the text recognizer defined above. Build it
// the camera source will be the back camera, have 1280 by 1024, refresh at 2 fps, and
will be able to autofocus the camera
 cameraSource = new
CameraSource.Builder(getApplicationContext(),textRecognizer)
 .setFacing(CameraSource.CAMERA_FACING_BACK)
.setRequestedPreviewSize(1280,1024)
 .setRequestedFps(2.0f)
.setAutoFocusEnabled(true)
 .build();
// use the getHolder method of the camera view with a callback. This will check
permissions again
 cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
 @Override
 public void surfaceCreated(SurfaceHolder holder) {
 try{
// basically if the permissions aren't right, set them right
 if(ActivityCompat.checkSelfPermission(getApplicationContext(),
Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
 ActivityCompat.requestPermissions(ScanPokeName.this,
 new String[]{Manifest.permission.CAMERA},
 RequestCameraPermissionID);
 return;
 }
// start the cameraSource
 cameraSource.start(cameraView.getHolder());
 }catch (IOException e){
// print the error if the camera source fails to start or permissions have issues
being set
 e.printStackTrace();
 }
 }
 @Override
 public void surfaceChanged(SurfaceHolder holder, int format, int
width, int height) {
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
 public void receiveDetections(Detector.Detections<TextBlock>
detections) {
// create an array which holds the detected items
 final SparseArray<TextBlock> items =
detections.getDetectedItems();
// if something was detected, do the below stuff
 if(items.size() != 0){
// create a new runnable which when it runs will do stuff with the text
 textView.post(new Runnable() {
 @Override
public void run() {
// create a new stringbuilder to handle the detected text
 StringBuilder stringBuilder = new StringBuilder();
// for every item detected, we will append it to the stringbuilder and check to see if
it's a pokemon
 for(int i = 0; i < items.size(); ++i){
// get the item from the items array
 TextBlock item = items.valueAt(i);
// append the new item onto the stringbuilder
 stringBuilder.append(item.getValue());
try{
// if the network call to get all the pokemon names hasn't finished yet, don't check
to see if the string is a pokemon name yet
 if(all_pokemon != null){
 if(!found &&
Arrays.asList(all_pokemon).contains(stringBuilder.toString().toLowerCase())){
// if the network call has finished, check to see if the all_pokemon array contains
the string found by the camera (cast to lowercase)
// if the string is a pokemon's name, start a new intent and pass the pokemon name to
the DisplayResult activity
 Intent intent = new
Intent(ScanPokeName.this, DisplayResult.class);

intent.putExtra(MainActivity.EXTRA_MESSAGE, stringBuilder.toString().toLowerCase());
 startActivity(intent);
// once the pokemon has been found, we don't want to scan for anymore pokemon
 ScanPokeName.this.finish();
// mark the found variable to be true so no more intents will be created despite more
valid pokemon names being found
// (or duplicating an intent creation for the same pokemon name while the camera is
still enabled and the intent is processing)
 found = true;
return;
 }
 }
 } catch (Exception e){
// if there's an error working with the detected text, log it
 Log.d("Error: ", e.getMessage());
 }
// append a newline to the detected text's stringbuilder to show it better on the
cameraview screen
 stringBuilder.append("\n");
 }
// set the text on the textview to be the string builder so you know what's going on
as the camera scans for text
 textView.setText(stringBuilder.toString());
 }
 });
 }
 }
 });
 }
 }
Code snippet from ScanPokeName.java
• Text View
The TextView is shown in the code above to be defined while the scanner views every
frame of camera input. It updates for every frame, or two times per second. The purpose of
allowing the detected text to be displayed on the screen is to provide the user with feedback
as to what is being detected by the phone’s camera so the user can adjust the phone’s
placement to properly scan the text.
In order for the Google Cloud Vision API to be used, it must be included in the gradle
and the manifest. Camera permissions must also be enabled. These look like the code
below:
implementation 'com.google.android.gms:play-services-vision:19.0.0'
Code snippet from gradle file
<!-- Google Cloud Vision API -->
 <meta-data android:name="com.google.android.gms.vision.DEPENDENCIES"
 android:value="ocr"/>
 </application>
Code snippet from manifest
Problem Solving
While creating this app, I encountered many instances of the app crashing. Because there
were more resources to monitor than previous projects that I have worked on, I became well
acquainted with Android Studio’s debugger. By systematically placing try/catch blocks and
evaluating error logs, I was able to bypass errors to get the application to work. There were three
problems that kept popping up as problems and proved to be the biggest bugs that needed to be
addressed in the application.
• App crashing when audio resource does not play properly.
o On the DisplayResult screen, if the Pokemon cry does not play properly the app
will crash unless addressed. The interesting thing that this error taught me is that
a try/catch block will only catch a single exception. I nested much of the audio
player in a try/catch block to prevent anything from happening. But certain
pokemon do not properly play the audio (like Chikorita and Cyndaquil, for some
reason). When the audio does not play properly, one try/catch block is not
enough. I had to nest more than one try/catch block to compensate for this error
or else the app would crash. Here is the code showing what I did (see the
try/catch block right to handle player.prepare and comments):
• try {
// create a media player for the pokemon's cry
 MediaPlayer player = new MediaPlayer();
// set the audio stream
 player.setAudioStreamType(AudioManager.STREAM_MUSIC);
// pokemoncries.com the pokemons' cries systematically available by get request
 String cryUrl = "https://pokemoncries.com/";
 if(poke_id < 650){
// pokemon whose ids are less than 650 have their cries stored in a different
directory than newer pokemon cries
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
 Toast.makeText(getApplicationContext(),"Error: " +
e.getMessage(), Toast.LENGTH_LONG).show();
 }
Code snippet from DisplayResult.java
• String too long to be passed with intents
o Initially, I passed the JSON string response from the MainActivity to the
DisplayResult screen. This was to avoid unnecessary switching between screens
and network calls by taking user input, performing a network call and gathering
the GET response, and passing that to be parsed on the other activity. However, I
learned through a lot of debugging and googling that there is a limit to the size of
data that can be passed between intents in the form of a string. Because of this
limit, I adjusted the code to perform a network call to the Poke API to verify that
the user’s input is a valid Pokemon name and if it was valid, pass the name to the
DisplayResult activity where the network call would happen again but this time
the JSON data would be retrieved and parsed to be used on the DisplayScreen.
After testing, I found that this did not affect performance too much, so I kept it
that way.
• Google Cloud Vision API queueing up strings to be processed and starting multiple
intents
o The Cloud Vision API performed an OCR scan for text 2 times for every second.
Sometimes, there is a lot of text on the screen and all of that text needs to be
processed by the mobile device asynchronously between the background thread
(validating the text strings with the array of all pokemon names) and the
foreground thread (the camera OCR taking in new frames of text data).
Therefore, the application would buffer between the time a valid pokemon name
was given to the camera and the time that the camera source was destroyed, and
the new intent began. What happened was that before the new intent could
properly be initialized and the previous activity killed, several instances of the
pokemon name would be detected as valid text and a new intent would be started
for every time it found the pokemon name between the time the first Pokemon
name was detected and the camera source was killed. This was bad because it
meant a lot of unnecessary network calls and the app was playing the pokemon’s
cry several times over and over (which is bad user experience). To solve this, I
created a boolean variable that would be efficiently set on the first instance that a
valid pokemon name was detected. Then for each check for a valid pokemon
name the application would first check whether a pokemon name has already been
found before beginning to iterate through the array of valid pokemon names.
Because the Boolean restricted from unnecessary array checks to be run, this
made it so that only one intent was created and no other intents were created and
freezing up the application.
• try{
// if the network call to get all the pokemon names hasn't finished yet, don't
check to see if the string is a pokemon name yet
 if(all_pokemon != null){
 if(!found && 
Arrays.asList(all_pokemon).contains(stringBuilder.toString().toLowerCase())){
// if the network call has finished, check to see if the all_pokemon array
contains the string found by the camera (cast to lowercase)
// if the string is a pokemon's name, start a new intent and pass the pokemon
name to the DisplayResult activity
 Intent intent = new
Intent(ScanPokeName.this, DisplayResult.class);

intent.putExtra(MainActivity.EXTRA_MESSAGE,
stringBuilder.toString().toLowerCase());
 startActivity(intent);
// once the pokemon has been found, we don't want to scan for anymore pokemon
 ScanPokeName.this.finish();
// mark the found variable to be true so no more intents will be created
despite more valid pokemon names being found
// (or duplicating an intent creation for the same pokemon name while the
camera is still enabled and the intent is processing)
 found = true;
return;
 }
 }
 } catch (Exception e){
// if there's an error working with the detected text, log it
 Log.d("Error: ", e.getMessage());
 }
Conclusion
This project was one of the most challenging and educational projects that I have worked
on. It was challenging not only because I have not worked with Java or Android before, but
because there are so many versions of Android with so many different dependencies and
deprecations that finding good resources online that are consistent to each other and compatible
with all other implemented techniques was difficult. This is one of the main reasons why I chose
to develop the application in Java: because I was not finding resources to program for this
functionality as easily in Kotlin. I initially began the project in Kotlin, but all of the most helpful
tutorials and walkthroughs had syntax explicitly defined using Java.
I learned a lot about debugging. I was able to find errors in the code due to strategic
try/catch blocks and knowing the errors made things easier to debug. I became familiar with 
Android studio and the file structure of Android applications using Java. I learned how to
implement both design and functionality for an Android application and that is very empowering.
This project accomplishes all requirements as listed above in the objectives section. The
code implements MT programming in the form of several network calls to many APIs and online
resources. IPC and RPC RESTful API calls were implemented through the network calls to the
Poke and Asana RESTful APIs. The application parses JSON strings returned from the API calls
into JSON objects, then accesses those objects to define attributes of several custom classes.
Data is primarily worked with on the DisplayResult activity by accessing the attributes of a
custom class. Lastly, the application applies the RESTful CRUD paradigm by performing API
calls that read, create, and delete data from the Poke and Asana RESTful APIs.
This project helped me to gain experience and confidence in my own lifelong learning. I
hope to apply the things that I learned in this project to make Android applications for myself as
side projects and harness the power and portability of mobile operating systems.
Cody Uhi
10/22/2019
Resources
DISCLAIMER: Although I wrote the program in Java, I began the project in Kotlin and a lot of
the concepts were cemented in my mind through tutorials that were given for Kotlin. Therefore,
I include those resources here to give an adequate reference for everything that went into this
project.
• https://www.nplix.com/basic-kotlin-android/
• https://www.techotopia.com/index.php/An_Overview_of_Android_Intents_in_Kotlin
• https://www.quora.com/Im-using-ImageView-in-Android-Studio-but-Android-Studiodoesnt-show-my-image-in-the-preview-When-I-install-the-application-in-my-Androidphone-the-image-will-be-shown-in-the-activity-Why
• https://stackoverflow.com/questions/6759036/how-to-send-view-to-back-how-to-controlthe-z-order-programmatically
• https://stackoverflow.com/questions/45108239/how-to-create-a-button-in-kotlin-thatopens-a-new-activity-android-studio
• https://stackoverflow.com/questions/5670754/android-how-to-stop-animation-betweenactivity-changes
• https://stackoverflow.com/questions/6972295/switching-activities-without-animation
• https://stackoverflow.com/questions/45108239/how-to-create-a-button-in-kotlin-thatopens-a-new-activity-android-studio
• https://developer.android.com/training/transitions/start-activity
• https://stackoverflow.com/questions/46177133/http-request-in-kotlin
• https://androidclarified.com/android-volley-example/
• https://stackoverflow.com/questions/45219379/how-to-make-an-api-request-in-kotlin
• https://www.youtube.com/watch?v=XU-rlStKBAY&list=PLlxmoA0rQLzEmWs4T99j2w6VnaQVGEtR&index=7
• https://www.youtube.com/watch?v=YpcTVXQ007s
• https://www.youtube.com/watch?v=GQxiESqZpgo&list=PLlxmoA0rQLzEmWs4T99j2w6VnaQVGEtR&index=8
• https://www.youtube.com/watch?v=iby1dW_Ze0U&list=PLlxmoA0rQLzEmWs4T99j2w6VnaQVGEtR&index=14
• https://www.youtube.com/watch?v=xJkawEe0g1g&list=PLlxmoA0rQLzEmWs4T99j2w6VnaQVGEtR&index=17
• https://www.youtube.com/watch?v=sw_FFEoJ6f4&list=PLlxmoA0rQLzEmWs4T99j2w6VnaQVGEtR&index=26
• https://app.pluralsight.com/course-player?clipId=517c02b6-2a96-4053-ae89-
848ebf698f3d
• https://javapapers.com/android/get-user-input-in-android/
• https://www.javatpoint.com/android-toast-example
• https://stackoverflow.com/questions/16461882/how-to-pass-an-object-of-a-class-usingan-intent
• https://stackoverflow.com/questions/11464890/first-char-to-upper-case
• https://stackoverflow.com/questions/432037/how-do-i-center-text-horizontally-andvertically-in-a-textview
• https://alvinalexander.com/blog/post/java/java-faq-create-array-int-example-syntax
• https://stackoverflow.com/questions/5776851/load-image-from-url
• https://stackoverflow.com/questions/26496411/android-material-status-bar-color-wontchange
• https://www.zidsworld.com/android-development/how-to-set-progressbar-colorprogrammatically-in-android-java/
• https://www.android-examples.com/set-layout-background-color-programmaticallyandroid/
• https://bulbapedia.bulbagarden.net/wiki/Template:Bug_color
• https://pokemoncries.com/
• https://stackoverflow.com/questions/5976805/android-linearlayout-gradient-background
• https://stackoverflow.com/questions/25803443/how-to-read-big-json-conten-from-url
• https://stackoverflow.com/questions/37900719/convert-the-string-into-stringbuilder-injava
• https://www.colorhexa.com/000000
• http://materialdesignicons.com/
• https://stackoverflow.com/questions/51075150/how-to-set-border-for-androidrecyclerview-grid-layout
• https://stackoverflow.com/questions/51075150/how-to-set-border-for-androidrecyclerview-grid-layout
• https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-betweenitems-in-recyclerview
• https://stackoverflow.com/questions/13390864/setting-background-image-in-java
• https://www.youtube.com/watch?v=kKqZoS4THnY
• https://www.stechies.com/set-change-icon-android-app/
• https://stackoverflow.com/questions/46328854/post-request-with-java-asynctask
• https://jsoup.org/download
• https://jsoup.org/download
• https://www.youtube.com/watch?v=31lJ7QvEyIQ
• https://www.youtube.com/watch?v=xoTKpstv9f0
• https://stackoverflow.com/questions/1128723/how-do-i-determine-whether-an-arraycontains-a-particular-value-in-java
• https://www.youtube.com/watch?v=xoTKpstv9f0
