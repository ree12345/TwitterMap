package com.example.reena.twittermap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity  {

    Button getTweets;
    private String TAG = "MainActivity";
    private ProgressDialog progressDialog;
    FusedLocationProviderClient mFusedLocationClient;
    // Google client to interact with Google API

    //Used to check google play services request code
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    //Used to retrieve last location object
    LocationManager locationManager;

    String locationt = null;
    double latitude;
    double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         getTweets = findViewById(R.id.bGetTweets);
         mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getTweets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDeviceLocationEnable()) {
                    //Check if play services are there
                    if (checkPlayServices()) {

                        Intent intentMap = new Intent(MainActivity.this,GetTweetsOnMap.class);
                        startActivity(intentMap);
                    }


                } else {
                    //Show setting alert dialog when location is not enabled.
                    showSettingsAlert();
                }
            }


        });
    }


    //Checking if location is enabled on device
    private boolean checkDeviceLocationEnable() {
        boolean isGPSEnabled;
        boolean isNetworkEnabled;

        //Get location service
        LocationManager locationManager = (LocationManager) MainActivity.this
                .getSystemService(LOCATION_SERVICE);
        //Check if gps is enabled
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //Check if mobile network is enabled
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            return false;
        } else {
            return true;
        }
    }

    //Check if play services are installed on device
    private boolean checkPlayServices() {
        //Checking result code if available
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            //If code is recoverable then show dialog
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog( this,resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG);
                Log.d(TAG, "Google Play Services not supported");
                finish();
            }
            return false;
        }
        return true;
    }

    //AlertDialog to be shown when location is not enabled.
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Location is settings");

        // Setting Dialog Message
        alertDialog.setMessage("Location is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

