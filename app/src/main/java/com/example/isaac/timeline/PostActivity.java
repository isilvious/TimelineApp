package com.example.isaac.timeline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    SharedPreferences sharedPref;

    TextView dateTime;
    TextView content;
    CheckBox includeLocation;
    ScrollView sv;

    boolean newPost = true;
    Post currPost;
    LocServBroadcastReceiver br;
    LatLng loc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.animation_move, R.anim.slide_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        sharedPref = getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);

        dateTime = (TextView) findViewById(R.id.edtx_post_date);
        content = (TextView) findViewById(R.id.edtx_post_message);
        includeLocation = (CheckBox) findViewById(R.id.cb_post_include_loc);
        Toolbar tb = (Toolbar) findViewById(R.id.tb_post);
        sv = (ScrollView) findViewById(R.id.sv_post);

        //Back button on toolbar
        tb.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Create BroadcastReceiver
        br = new LocServBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(LocServ.LOCATION_SERVER_PERMISSION_GRANTED);
        filter.addAction(LocServ.LOCATION_SERVER_PERMISSION_DENIED);
        filter.addAction(LocServ.LOCATION_SERVER_LAST_KNOW_LOCATION);
        registerReceiver(br, filter);

        LocServ.init(this);
        LocServ.requestPermissions();

        if(getIntent().hasExtra("post")){
            //Editing an old post
            currPost = getIntent().getExtras().getParcelable("post");
            if(currPost != null) {
                dateTime.setText(currPost.getDate());
                content.setText(currPost.getContent());
                newPost = false;
                includeLocation.setVisibility(View.INVISIBLE);
            }
            LocalDB.closeDB();
        } else {
            //Making a new post
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("M/d/yyyy H:mm");
            String formattedDate = df.format(c.getTime());
            dateTime.setText(formattedDate);
        }

    }

    public void post(View view) {
        LocalDB.openDB(view.getContext());
        Post n;
        if (newPost) {
            if(includeLocation.isChecked() && loc != null) {
                if (LocalDB.addPost(n = new Post(dateTime.getText().toString(),
                        content.getText().toString(),
                        sharedPref.getString(getString(R.string.current_user_email), ""),
                        loc.latitude, loc.longitude)) == 0) {
                    //Toast.makeText(view.getContext(), "is checked and loc !is null", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(view.getContext(), "Error creating post", Toast.LENGTH_LONG).show();
                }
            //This is used as debug!
            /*}else if(includeLocation.isChecked() && loc == null) {
                Toast.makeText(view.getContext(), "is checked and loc is null", Toast.LENGTH_SHORT).show();
            }else if(!includeLocation.isChecked() && loc == null) {
                Toast.makeText(view.getContext(), "!is checked and loc is null", Toast.LENGTH_SHORT).show();
            }else if(!includeLocation.isChecked() && loc != null) {
                Toast.makeText(view.getContext(), "!is checked and loc !is null", Toast.LENGTH_SHORT).show();
            */
            }else{
                if (LocalDB.addPost(n = new Post(dateTime.getText().toString(),
                        content.getText().toString(),
                        sharedPref.getString(getString(R.string.current_user_email), ""), 0, 0)) == 0) { //DEFAULT  Loc == 0, 0
                    Toast.makeText(view.getContext(), "Location not included", Toast.LENGTH_SHORT).show();//TODO remove error checking
                    finish();
                } else {
                    Toast.makeText(view.getContext(), "Error creating post", Toast.LENGTH_LONG).show();
                }
            }
        } else{
            if (LocalDB.addPost(n = new Post(dateTime.getText().toString(),
                    content.getText().toString(),
                    sharedPref.getString(getString(R.string.current_user_email), ""),
                    currPost.getLat(), currPost.getLon())) == 0) {
                LocalDB.deletePost(Integer.toString(currPost.get_id())); //Delete the post this one is replacing
                finish();
            } else {
                Toast.makeText(view.getContext(), "Error creating post", Toast.LENGTH_LONG).show();
            }
        }


        LocalDB.closeDB();
    }

    public void deletePost(View view){
        if(!newPost) {
            LocalDB.openDB(view.getContext());
            LocalDB.deletePost(Integer.toString(currPost.get_id()));
            Toast.makeText(view.getContext(), "Deleted post _id: " + currPost.get_id(), Toast.LENGTH_SHORT).show();
            LocalDB.closeDB();
        }
        finish();
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
                includeLocation.setChecked(true);
                LocServ.requestLastKnownLocation();
            }
            else if (action.equals(LocServ.LOCATION_SERVER_PERMISSION_DENIED)) {
                Log.d(TAG, "permission denied");
                Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show();
            }
            else if (action.equals(LocServ.LOCATION_SERVER_LAST_KNOW_LOCATION)) {
                Log.d(TAG, "location received");
                loc = intent.getParcelableExtra("location");
            }
        }
    }
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }

    //Inflate back button on toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_post_viewer, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        if(res_id == R.id.menu_post_viewer_edit){
            Intent postActivity = new Intent(getApplicationContext(), PostActivity.class);
            startActivity(postActivity);
        }

        return true;
    }
}
