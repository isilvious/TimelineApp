package com.example.isaac.timeline;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {


    public PostFragment() {
        // Required empty public constructor
    }

    public static Post currPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        TextView username = (TextView) rootView.findViewById(R.id.post_view_username);
        TextView dateTime = (TextView) rootView.findViewById(R.id.post_view_date);
        TextView content = (TextView) rootView.findViewById(R.id.post_view_content);

        //Open the Post and add thier contents to the view
        Bundle bundle = getArguments();
        currPost = (Post) bundle.get("post");
        if(currPost != null) {
            username.setText(currPost.getUid());
            dateTime.setText(currPost.getDate());
            content.setText(currPost.getContent());
        }


        return rootView;
    }

}
