package com.example.isaac.timeline;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.isaac.timeline.PostFragment.currPost;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostLocationFragment extends Fragment implements OnMapReadyCallback {

    private final String TAG = "PostLocationFragment";
    LocServBroadcastReceiver br;
    GoogleMap mMap;
    public PostLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_location, container, false);

        // Create BroadcastReceiver
        br = new LocServBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(LocServ.LOCATION_SERVER_PERMISSION_GRANTED);
        filter.addAction(LocServ.LOCATION_SERVER_PERMISSION_DENIED);
        filter.addAction(LocServ.LOCATION_SERVER_LAST_KNOW_LOCATION);
        getActivity().registerReceiver(br, filter);

        LocServ.init(getActivity());
        LocServ.requestPermissions();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        return rootView;
    }


    //BroadcastReceiver for LocServ
    private class LocServBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "BROADCAST-RECEIVER";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);
            if (action.equals(LocServ.LOCATION_SERVER_PERMISSION_GRANTED)) {
                Log.d(TAG, "permission granted");
                //Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show();
            }
            else if (action.equals(LocServ.LOCATION_SERVER_PERMISSION_DENIED)) {
                Log.d(TAG, "permission denied");
                Toast.makeText(context, "Location permissions denied", Toast.LENGTH_SHORT).show();
            }
            else if (action.equals(LocServ.LOCATION_SERVER_LAST_KNOW_LOCATION)) {
                Log.d(TAG, "location received");
                LatLng loc = intent.getParcelableExtra("location");

            }
        }
    }
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(br);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(currPost != null) {
            LatLng location = new LatLng(currPost.getLat(), currPost.getLon());
            if(location.equals(new LatLng(0,0)) == false) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(19).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.addMarker(new MarkerOptions().position(location).title(currPost.getDate()));
            }
            mMap.setBuildingsEnabled(true);
            try {
                mMap.setMyLocationEnabled(true);
            }catch(SecurityException e){
                Toast.makeText(getActivity(), "Cannot find current location", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getActivity(), "no location", Toast.LENGTH_SHORT).show();
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "Received permission results for app - sending to LocServ");
        if (requestCode == LocServ.M_REQUEST_LOCATION_ON_FOR_APP) {
            LocServ.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Received permission results for device -sending to LocServ");
        if (requestCode == LocServ.M_REQUEST_LOCATION_ON_FOR_DEVICE) {
            LocServ.onActivityResult(requestCode, resultCode, data);
        }
    }

}
