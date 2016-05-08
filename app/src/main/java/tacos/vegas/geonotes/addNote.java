package tacos.vegas.geonotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class addNote extends AppCompatActivity {
    String lat;
    String lng;
    String TAG = "addNote";
    //Shared Preferences Name
    public static final String PREFS_NAME = "geonotesPrefs";

    // Will show the string "data" that holds the results
    TextView results;
    // URL of object to be parsed

    //String JsonURL = "https://raw.githubusercontent.com/ianbar20/JSON-Volley-Tutorial/master/Example-JSON-Files/Example-Object.JSON";
    // This string will hold the results
    //String data = "";
    // Defining the Volley request queue that handles the URL request concurrently
    RequestQueue requestQueue;


    //This is the button handler for submit note
    public void submitNote(View view) {

        TextView userTextView = (TextView) findViewById(R.id.user);
        String user = userTextView.getText().toString();

//        TextView typeTextView = (TextView) findViewById(R.id.type);
//        String type = typeTextView.getText().toString();
        String type = "text";

        final TextView noteTextView = (TextView) findViewById(R.id.note);

        String note = noteTextView.getText().toString();
        try {
            note = URLEncoder.encode(note, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "couldn't encode 'note' for the query");
        }

//        String JsonURL = "http://192.168.1.10/geonotes/api/add.php?user="+user+"&lat=54&lng=-115&type=text&data=androidteest";

        //String JsonURL = "http://192.168.1.10/geonotes/api/add.php?user="+user+"&lat="+lat+"&lng="+lng+"&type="+type+"&data="+note;

        String apiURL = getResources().getString(R.string.apiURL);
        String JsonURL = apiURL + "add.php?user=" + user + "&lat=" + lat + "&lng=" + lng + "&type=" + type + "&data=" + note;

        // Creating the JsonObjectRequest class called obreq, passing required parameters:
        //GET is used to fetch data from the server, JsonURL is the URL to be fetched from.
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, JsonURL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            // Adds the data string to the TextView "results"
                            results.setText(response.getString("message"));

                            if (response.getInt("status") == 200) {
                                noteTextView.setText("");
                                noteTextView.setInputType(0);

                                //Send a Successful Toast
                                //Toast.makeText(context, response.getString("message"), duration).show();



                                Context context = getApplicationContext();
                                CharSequence text = "Your note was added successfully!";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                //return to main activity
                                finish();
                            }else{
                                Context context = getApplicationContext();
                                CharSequence text = "There was an issue adding your note";
                                int duration = Toast.LENGTH_LONG;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }

                        }
                        // Try and catch are included to handle any errors due to JSON
                        catch (JSONException e) {
                            // If an error occurs, this prints the error to the log
                            e.printStackTrace();
                        }
                    }
                },
                // The final parameter overrides the method onErrorResponse() and passes VolleyError
                //as a parameter
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreq);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Intent intent = getIntent();
        lat = intent.getStringExtra("lat"); //if it's a string you stored.
        lng = intent.getStringExtra("lng");

        TextView debugTextView = (TextView) findViewById(R.id.debug);
        debugTextView.setText(lat + lng);


        //Gets username from SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", "geouser");

        Log.v("Addnote", "try to get username in prefs: " + username);

        TextView myUsername = (TextView) findViewById(R.id.user);
        myUsername.setText(username);


        // Creates the Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Casts results into the TextView found within the main layout XML with id jsonData
        results = (TextView) findViewById(R.id.jsonData);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //save username to SharedPreferences
        EditText username = (EditText) findViewById(R.id.user);
        Log.v("Addnote", "saving username to prefs" + username.getText().toString());

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username.getText().toString());
        //commit changes
        editor.commit();

    }

}
