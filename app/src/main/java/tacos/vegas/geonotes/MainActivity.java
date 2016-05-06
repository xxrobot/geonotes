package tacos.vegas.geonotes;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    TextView txtOutputLat, txtOutputLng;
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lng;
    String TAG = "GeoNotes MainActivity";

    int PERMISSION_ACCESS_FINE_LOCATION;




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
        Intent myIntent = new Intent(this, viewNotes.class);
        myIntent.putExtra("lat", lat); //Optional parameters
        myIntent.putExtra("lng", lng); //Optional parameters
        this.startActivity(myIntent);
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

//        public void requestLocationFromGoogle {
//            mLocationRequest = LocationRequest.create();
//            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            mLocationRequest.setInterval(600000); // Update location every minute
//        }



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


        }else{
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
        //String JsonURL = "http://192.168.1.10/geonotes/api/nearbyMarkers.php?lat="+lat+"&lng="+lng;

        String apiURL = getResources().getString(R.string.apiURL);
        String JsonURL = apiURL + "nearbyMarkers.php?lat=" + lat + "&lng=" + lng;


        //String JsonURL = "http://192.168.1.10/geonotes/api/add.php?user="+user+"&lat="+lat+"&lng="+lng+"&type="+type+"&data="+note;

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

                            int id;
                            double markerLat;
                            double markerLng;
                            String note = "";
                            String timestamp = "";
                            JSONArray array = new JSONArray(response.getString("notes"));
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject row = array.getJSONObject(i);
                                id = row.getInt("id");
                                markerLat = row.getDouble("lat");
                                markerLng = row.getDouble("lng");
                                timestamp = row.getString("timestamp");
                                note = row.getString("data");
                                //Log.v(TAG, "Adding marker id" + id + note);

                                listOfLocations = listOfLocations + id + " LAT: " + markerLat + "\n";

                                //LatLng sydney = new LatLng(markerLat, markerLng);
                                mMap.addMarker(new MarkerOptions().position(new LatLng(markerLat, markerLng)).title("N" + id + " " + note));
                            }

//
//                            mMap.addMarker(new MarkerOptions().position(new LatLng(36.17,-115.17)).title("mess"));
//                            mMap.addMarker(new MarkerOptions().position(new LatLng(36.07,-115.07)).title("mess2"));
//                            mMap.addMarker(new MarkerOptions().position(new LatLng(36.47,-115.47)).title("mess3"));

//                            TextView jsonResultsTextView = (TextView) findViewById(R.id.jsonResults);
//                            jsonResultsTextView.setText(listOfLocations);


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

    void updateUI() {
        txtOutputLat.setText(lat);
        txtOutputLng.setText(lng);

        //zoom in once I have my location
        LatLng myLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        getNearbyMarkers();
    }
}
