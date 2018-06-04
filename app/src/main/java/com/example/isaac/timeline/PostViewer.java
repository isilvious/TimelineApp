package com.example.isaac.timeline;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import static com.example.isaac.timeline.PostFragment.currPost;

public class PostViewer extends FragmentActivity {
    private static final String TAG = "PostViewer";

    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);

        mPager = (ViewPager) findViewById(R.id.post_view_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(0);

        Toolbar tb = (Toolbar) findViewById(R.id.tb_post_viewer);
        //Back button on toolbar
        tb.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            if(position == 0) {
                PostFragment pfrag = new PostFragment();
                pfrag.setArguments(getIntent().getExtras());
                return pfrag;
            }else {
                return new PostLocationFragment();
            }
        }

        @Override
        public int getCount(){
            return NUM_PAGES;
        }
    }
    public void edit(View view){
        Intent postActivity = new Intent(getApplicationContext(), PostActivity.class);
        postActivity.putExtra("post", currPost);
        startActivity(postActivity);
        finish();
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