package tacos.vegas.geonotes;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //Maps and Geolocation Stuff
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lng;
    Location mLastLocation;
    TextView txtOutputLat, txtOutputLng;

    //Permissions
    int PERMISSION_ACCESS_FINE_LOCATION;

    //View Notes stuff
    ArrayList<DataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;

    //Debug Things
    String TAG = "GeoNotes MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtOutputLat = (TextView) findViewById(R.id.textView);
        txtOutputLng = (TextView) findViewById(R.id.textView2);

        buildGoogleApiClient();

        // Creates the Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        //Begin View Notes Stuff
        listView = (ListView) findViewById(R.id.list);
        dataModels = new ArrayList<>();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    //    Button for adding a new note
    public void addNote(View view) {
        Intent myIntent = new Intent(this, addNote.class);
        myIntent.putExtra("lat", lat); //Optional parameters
        myIntent.putExtra("lng", lng); //Optional parameters
        this.startActivity(myIntent);
    }

    //    Button for adding a new note
    public void viewNotes(View view) {

        View drawer = findViewById(R.id.drawer);

        if (drawer.getVisibility() == View.GONE) {
            drawer.setVisibility(View.VISIBLE);
        } else {
            drawer.setVisibility(View.GONE);
        }

//
//        Intent myIntent = new Intent(this, viewNotes.class);
//        myIntent.putExtra("lat", lat); //Optional parameters
//        myIntent.putExtra("lng", lng); //Optional parameters
//        this.startActivity(myIntent);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);


        } else {
            //I have permission
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(600000); // Update location every minute

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                lat = String.valueOf(mLastLocation.getLatitude());
                lng = String.valueOf(mLastLocation.getLongitude());
            }
            updateUI();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lng = String.valueOf(location.getLongitude());
        updateUI();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();

    }


    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    RequestQueue requestQueue;


    //Get Nearby note markers
    public void getNearbyMarkers() {
        //String JsonURL = "http://192.168.1.10/geonotes/api/nearbyMarkers.php?lat="+lat+"&lng="+lng;
        String apiURL = getResources().getString(R.string.apiURL);
        String JsonURL = apiURL + "nearbyMarkers.php?lat=" + lat + "&lng=" + lng;

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

                            String listOfLocations = "";
                            dataModels.clear();

                            int id;
                            double markerLat;
                            double markerLng;
                            String note = "";
                            String timestamp = "";
                            JSONArray noteArray = new JSONArray(response.getString("notes"));
                            for (int i = 0; i < noteArray.length(); i++) {
                                JSONObject row = noteArray.getJSONObject(i);
                                id = row.getInt("id");
                                markerLat = row.getDouble("lat");
                                markerLng = row.getDouble("lng");
                                timestamp = row.getString("timestamp");
                                String username = row.getString("user");
                                note = row.getString("data");
                                //Log.v(TAG, "Adding marker id" + id + note);

                                listOfLocations = listOfLocations + id + " LAT: " + markerLat + "\n";

                                //LatLng sydney = new LatLng(markerLat, markerLng);
                                mMap.addMarker(new MarkerOptions().position(new LatLng(markerLat, markerLng)).title(username).snippet(note + "\n" + timestamp));
                                dataModels.add(new DataModel(username, timestamp, note, "Feature", markerLat, markerLng));

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


//        //Begin View Notes Stuff
//        listView=(ListView)findViewById(R.id.list);
//        dataModels= new ArrayList<>();
//        dataModels.add(new DataModel("Geoman", "2016-10-12", "1","September 23, 2008"));
//        dataModels.add(new DataModel("searchuser", "2016-10-5", "2","February 9, 2009"));


        adapter = new CustomAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel = dataModels.get(position);

//                Snackbar.make(view, dataModel.getName() + "\n" + dataModel.getTimestamp() + " API: " + dataModel.getNote(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();

                LatLng myLocation = new LatLng(dataModel.getDataLat(), dataModel.getDataLng());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

            }
        });
        //End view notes stuff
    }


    void updateUI() {
        txtOutputLat.setText(lat);
        txtOutputLng.setText(lng);

        //zoom in once I have my location
        LatLng myLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));


        mMap.addMarker(new MarkerOptions().position(myLocation).title("You are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));



        getNearbyMarkers();
    }
}
