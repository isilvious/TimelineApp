package com.example.isaac.timeline;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;


public class LocServ implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LOC-SERV";

    public static final int M_REQUEST_LOCATION_ON_FOR_APP = 1;
    public static final int M_REQUEST_LOCATION_ON_FOR_DEVICE = 2;

    public static final String LOCATION_SERVER_PERMISSION_GRANTED = "permission granted";
    public static final String LOCATION_SERVER_PERMISSION_DENIED = "permission denied";
    public static final String LOCATION_SERVER_LAST_KNOW_LOCATION = "last known location";

    private static Activity activeActivity;
    private static GoogleApiClient mGoogleClient;

    private static Location mLastKnownLocation = null;
    private static boolean appHasLocationEnabled = false;
    private static boolean deviceHasLocationEnabled = false;

    private static LocServ INSTANCE = null;

    private LocServ(Activity activity) {
        activeActivity = activity;
        if (mGoogleClient == null) {
            mGoogleClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleClient.connect();
    }

    public static void init(Activity activity) {
        INSTANCE = new LocServ(activity);
    }

    public void close() {
        mGoogleClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /*******************************************************
     * User methods
     *******************************************************/

    public static void requestPermissions() {
        Log.d(TAG, "Received permissions check request.");
        if (!deviceHasLocationEnabled) {
            requestDeviceLocationPermissions(activeActivity);
        }
        else if (!appHasLocationEnabled) {
            requestAppLocationPermissions(activeActivity);
        }
        else {
            Intent intent = new Intent();
            intent.setAction(LOCATION_SERVER_PERMISSION_GRANTED);
            activeActivity.sendBroadcast(intent);
        }
    }

    public static void requestLastKnownLocation() {
        if (!deviceHasLocationEnabled || !appHasLocationEnabled) {
            Intent intent = new Intent();
            intent.setAction(LOCATION_SERVER_PERMISSION_DENIED);
            activeActivity.sendBroadcast(intent);
            return;
        }

        try {
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);
        } catch (SecurityException e) {
            Log.d(TAG, "Failed to get last known location");
            return;
        }

        if (mLastKnownLocation != null) {
            LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            Intent intent = new Intent();
            intent.setAction(LOCATION_SERVER_LAST_KNOW_LOCATION);
            intent.putExtra("location", latLng);
            activeActivity.sendBroadcast(intent);
        }
    }

    /*******************************************************
     * Check DEVICE Location Permissions
     *******************************************************/

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
    }

    /*******************************************************
     * Check DEVICE Location Permissions
     *******************************************************/

    private static void requestDeviceLocationPermissions(final Activity activity) {
        // Check if device has proper Location Settings
        activeActivity = activity;

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "Device already has permission");
                        deviceHasLocationEnabled = true;

                        if (!appHasLocationEnabled) {
                            requestAppLocationPermissions(activity);
                        }
                        else {
                            Intent intent = new Intent();
                            intent.setAction(LOCATION_SERVER_PERMISSION_GRANTED);
                            activeActivity.sendBroadcast(intent);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "Device does not have permission - requesting permission");
                        deviceHasLocationEnabled = false;

                        try {
                            // Request permission and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity,
                                    M_REQUEST_LOCATION_ON_FOR_DEVICE);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }

    // Activity forwards result back here
    public static void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == M_REQUEST_LOCATION_ON_FOR_DEVICE) {
            if (resultCode == activeActivity.RESULT_OK )  {
                Log.d(TAG, "Device granted permissions");
                deviceHasLocationEnabled = true;

                if (!appHasLocationEnabled) {
                    requestAppLocationPermissions(activeActivity);
                }
                else {
                    Intent intent = new Intent();
                    intent.setAction(LOCATION_SERVER_PERMISSION_GRANTED);
                    activeActivity.sendBroadcast(intent);
                }
            }
            else if (resultCode == activeActivity.RESULT_CANCELED) {
                Log.d(TAG, "Device not granted permissions");
                deviceHasLocationEnabled = false;
            }
        }
    }

    /*******************************************************
     * Check APP Location Permissions
     *******************************************************/

    private static void requestAppLocationPermissions(Activity activity) {
        activeActivity = activity;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            appHasLocationEnabled = true;
            Log.d(TAG, "App already has permission");
            if(!deviceHasLocationEnabled) {
                requestDeviceLocationPermissions(activity);
            }
            else {
                Intent intent = new Intent();
                intent.setAction(LOCATION_SERVER_PERMISSION_GRANTED);
                activeActivity.sendBroadcast(intent);
            }
        }
        else {
            appHasLocationEnabled = false;
            Log.d(TAG, "App does not have permission - requesting permission");
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    M_REQUEST_LOCATION_ON_FOR_APP);
        }
    }

    // Activity forwards result back here
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == M_REQUEST_LOCATION_ON_FOR_APP) {
            Log.d(TAG, "Received result for app location permission");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                appHasLocationEnabled = true;
                Log.d(TAG, "App granted permissions");

                if (!deviceHasLocationEnabled){
                    requestDeviceLocationPermissions(activeActivity);
                }
                else {
                    Intent intent = new Intent();
                    intent.setAction(LOCATION_SERVER_PERMISSION_GRANTED);
                    activeActivity.sendBroadcast(intent);
                }
            }
            else {
                Log.d(TAG, "App not granted permissions");
                appHasLocationEnabled = false;
            }
        }
    }


}