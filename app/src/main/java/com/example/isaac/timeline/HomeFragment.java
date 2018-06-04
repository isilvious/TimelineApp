package com.example.isaac.timeline;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public ArrayList<Post> posts;
    public SharedPreferences sharedPref;

    public HomeFragment() {
        // Required empty public constructor
    }

    ListView listview;
    View rootView;
    int numPost = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        listview = (ListView) rootView.findViewById(R.id.postListView);

        sharedPref = rootView.getContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        //Retrieve items from DB Post_T
        LocalDB.openDB(rootView.getContext());
        posts = LocalDB.getUserPosts(sharedPref.getString(getString(R.string.current_user_email), ""));
        Collections.reverse(posts);
        LocalDB.closeDB();

        //populate postListView
        if(posts != null) {
            PostListAdapter adapter = new PostListAdapter(getContext(), android.R.layout.simple_list_item_1, posts);

            listview.setAdapter(adapter);

            //On click
            AdapterView.OnItemClickListener handler = new AdapterView.OnItemClickListener() { //TODO This doesn't ever seem to happen
                @Override
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    Intent i = new Intent(v.getContext(), PostViewer.class);
                    i.putExtra("post", posts.get(position));
                    startActivity(i);
                }
            };

            listview.setOnItemClickListener(handler);
        }
    }

}
