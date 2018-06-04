package com.example.isaac.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Isaac on 3/21/2017.
 */

public class PostListAdapter extends ArrayAdapter<Post> {

    private Context context;
    private int resource;
    private ArrayList<Post> posts; // local reference

    public PostListAdapter(Context context, int resource, ArrayList<Post> posts){

        super(context, resource, posts);
        this.context = context;
        this.resource = resource;
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View view = convertView;

        if(view == null){
            /*Create a new View object */
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.post_view, null);
        }

        /*Populate the View object*/
        Post post = posts.get(position);
        if(post != null){
            TextView v = (TextView) view.findViewById(R.id.date);
            v.setText(post.getDate() + ": ");

            v = (TextView) view.findViewById(R.id.content);
            v.setText(post.getContent());
        }

        /*return the view object*/
        return view;
    }
}
