package com.example.reena.twittermap;

import android.app.Activity;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.example.reena.twittermap.JavaClasses.MainJson;

import android.location.Location;

import java.util.logging.Handler;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Created by natesh on 2/5/15.
 */
public class GetTweetsOnMap extends AppCompatActivity implements OnMapReadyCallback {
    private String[] current_location;
    private String TAG = "GetTweetsOnMap";
    String url;
    private Location currentLocation;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.

    private boolean mLocationPermissionGranted = false;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;


    private GoogleMap googleMap;
    Handler handler;
    public String l;
    public String m;
    double latitude;
    double longitude;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        //Test Code
        //double latitude =28.55952093;
        //double longitude =77.0016868;
        //LatLng posi =new LatLng(latitude,longitude);
        //mMap.addMarker(new MarkerOptions().position(posi).title("Marker in Location"));



        if (mLocationPermissionGranted) {
            m = getDeviceLocation();
            if (m != null) {
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);


        getLocationPermission();

        /*
        /
        if(currentLocation!=null) {
            double latitude = currentLocation.getLatitude();
            double longitude = currentLocation.getLongitude();
            l = String.valueOf(latitude) + ":" + String.valueOf(longitude);
            current_location = l.split(":");
            Log.d(TAG, "Latitude: " + current_location[0] + " Longitude: " + current_location[1]);
            try {
                // Loading map
                initializeMap();
            } catch (Exception e) {
                Log.d(TAG, "Map error: " + e);
                e.printStackTrace();
            }
            //Setting map to currrent postion
            /*LatLng current = new LatLng(Double.parseDouble(current_location[0]), Double.parseDouble(current_location[1]));
            //To use zoom effect when clicked on map
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    current).zoom(13f).build();
            //Animate map from the zoom set above at cameraPostion
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            //Generating URL
            url = "https://api.twitter.com/1.1/search/tweets.json?q=&geocode=" + current_location[0] + "," + current_location[1] + ",1.6km&lang=en&result_type=recent";
            Log.d(TAG, "URL: " + url);
            //Fetch past tweets
            new ShowTweets().execute();
            //For upcoming tweets using bounding box
            streamIt();
        }
        */
         // Specify Central Delhi
latitude =28.5494489;
        longitude =77.2001368;
        l = String.valueOf(latitude) + ":" + String.valueOf(longitude);


        current_location = l.split(":");

        url = "https://api.twitter.com/1.1/search/tweets.json?q=&geocode=" + current_location[0] + "," + current_location[1] + ",1.6km&lang=en&result_type=recent";
        Log.d(TAG, "URL: " + url);

        //Fetch past tweets
        new ShowTweets().execute();
        //For upcoming tweets using bounding box
        streamIt();
        //Fetch past tweets



    }


    public String getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            currentLocation = (Location) task.getResult();
                            latitude =currentLocation.getLatitude();
                           longitude = currentLocation.getLongitude();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                            //For upcoming tweets using bounding box


                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(GetTweetsOnMap.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return l;
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
        return l;
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }
   /* private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                currentLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
*/

    private class ShowTweets extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            //Make a service call on url
            ServiceHandler serviceHandler = new ServiceHandler();
            String response = serviceHandler.makeServiceCall(url);
            Log.d(TAG, "Response:" + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int i;
            Gson gson = new Gson();
            //Converting JSON to Java classes
            final MainJson mainJson = gson.fromJson(result, MainJson.class);
            //Check if statuses are equal to 100, remove 10 oldest tweets
            if (mainJson.statuses.length == 100) {
                Log.d(TAG, "Tweets more than 100");
                for (i = 0; i < mainJson.statuses.length - 10; i++) {
                    //Check if geo is not null
                    if (mainJson.statuses[i].geo != null) {
                        //Create a point for map
                        LatLng current = new LatLng(mainJson.statuses[i].geo.coordinates[0], mainJson.statuses[i].geo.coordinates[1]);
                        MarkerOptions curmarker = new MarkerOptions().position(current).title(mainJson.statuses[i].user.name + ": " + mainJson.statuses[i].text);
                        //Add marker
                        mMap.addMarker(curmarker);

                    } else if (mainJson.statuses[i].retweetedStatus.geo != null) {
                        //If geo is null, it may be retweeted status
                        LatLng current = new LatLng(mainJson.statuses[i].retweetedStatus.geo.coordinates[0], mainJson.statuses[i].retweetedStatus.geo.coordinates[1]);
                        MarkerOptions curmarker = new MarkerOptions().position(current).title(mainJson.statuses[i].user.name + ": " + mainJson.statuses[i].text);
                        //Add marker
                        mMap.addMarker(curmarker);
                    }
                }
            } else {
                Log.d(TAG, "Tweets less than 100");
                for (i = 0; i < mainJson.statuses.length; i++) {
                    //Check if geo is not null
                    if (mainJson.statuses[i].geo != null) {
                        //Create a point for map
                        LatLng current = new LatLng(mainJson.statuses[i].geo.coordinates[0], mainJson.statuses[i].geo.coordinates[1]);
                        MarkerOptions curmarker = new MarkerOptions().position(current).title(mainJson.statuses[i].user.name + ": " + mainJson.statuses[i].text);
                        //Add marker
                        mMap.addMarker(curmarker);

                    } /*else if (mainJson.statuses[i].retweetedStatus.geo != null && mainJson.statuses[i].retweetedStatus != null && mainJson.statuses[i] != null) {
                        //If geo is null, it may be retweeted status


                        LatLng current = new LatLng(mainJson.statuses[i].retweetedStatus.geo.coordinates[0], mainJson.statuses[i].retweetedStatus.geo.coordinates[1]);
                        MarkerOptions curmarker = new MarkerOptions().position(current).title(mainJson.statuses[i].user.name + ": " + mainJson.statuses[i].text);
                        //Add marker
                        mMap.addMarker(curmarker);

                    }*///--Commented on error cause - 27-05-2018
                }
            }


        }
    }

    private void initMap() {
        //Create map
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(GetTweetsOnMap.this);

    }


    public void streamIt() {
        //Set up twitter stream
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(ServiceHandler.consumerKeyStr)
                .setOAuthConsumerSecret(ServiceHandler.consumerSecretStr)
                .setOAuthAccessToken(ServiceHandler.accessTokenStr)
                .setOAuthAccessTokenSecret(ServiceHandler.accessTokenSecretStr);
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

        //Inject a query of bounding box, I am using box near "janakpuri", it can be set according to requirement
        FilterQuery fq = getBoundingBoxFilter();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(final Status status) {
                final GeoLocation gl = status.getGeoLocation();

                //Check if geo is not null
                if (gl != null) {
                    //Check if the distance inside bounding box is less than 1 mile from current location
                    if (distance(Double.parseDouble(current_location[0]), Double.parseDouble(current_location[1]), gl.getLatitude(), gl.getLongitude()) <= 1.6) {
                        //Update the UI
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Log.d(TAG, "Upcoming tweets: " + status.getUser().getName() + "lat:" + gl.getLatitude() + ", " + gl.getLongitude());
                                //Create a point
                                LatLng current = new LatLng(gl.getLatitude(), gl.getLongitude());
                                MarkerOptions curmarker = new MarkerOptions().position(current).title(status.getUser().getName() + ": " + status.getText());
                                //Add marker
                                mMap.addMarker(curmarker);
                            }
                        });

                    }
                } else {
                    if (status.getRetweetedStatus() != null) {
                        //If geo is null, it may be retweeted status
                        final GeoLocation gl_retweeted = status.getRetweetedStatus().getGeoLocation();
                        //Check if geo of retweetedStatus is not null

                        if (gl_retweeted != null) {
                            //Check if the distance inside bounding box is less than 1 mile from current location
                            if (distance(Double.parseDouble(current_location[0]), Double.parseDouble(current_location[1]), gl_retweeted.getLatitude(), gl_retweeted.getLongitude()) <= 1.6) {
                                //Update UI thread
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        // Create a point
                                        Log.d(TAG, "Upcoming tweets: " + status.getUser().getName() + "lat:" + gl_retweeted.getLatitude() + ", " + gl_retweeted.getLongitude());
                                        LatLng current = new LatLng(gl_retweeted.getLatitude(), gl_retweeted.getLongitude());
                                        MarkerOptions curmarker = new MarkerOptions().position(current).title(status.getUser().getName() + ": " + status.getText());
                                        //Add marker
                                        mMap.addMarker(curmarker);

                                    }
                                });
                            }
                        }
                    }

                }

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onException(Exception ex) {
                if (!(ex instanceof IllegalStateException)) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onStallWarning(StallWarning sw) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        //Add statusListener to twitterStream
        twitterStream.addListener(listener);
        if (fq != null) {
            twitterStream.filter(fq);
        }

    }

    //For filtering query
    private FilterQuery getBoundingBoxFilter() {
        // new "janakpuri, new delhi, India"
        //Note- bounding box may be wrong. If its not working, it can be set more.
        double lat = Double.parseDouble(current_location[0]);
        double lon = Double.parseDouble(current_location[1]);
        double lon1 = lon - 1;
        double lon2 = lon + 1;
        double lat1 = lat - 1;
        double lat2 = lat + 1;

        double bbox[][] = {{lon1, lat1}, {lon2, lat2}};
        FilterQuery filtro = new FilterQuery();
        return filtro.locations(bbox);
    }

    //For calculating distance between a pair of lat/long
    private double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;
        return (dist);

    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);

    }
}
