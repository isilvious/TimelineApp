package com.example.isaac.timeline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Isaac on 4/5/2017.
 */

public class LocalDB {
    //ERROR logging tag
    private static final String TAG = "LocalDB";
    //DB info
    private static final String DATABASE_NAME = "Timeline_DB";
    private static final int DATABASE_VERSION = 2;
    //DB objects
    private static DatabaseHelper dBHelper = null;
    private static SQLiteDatabase db = null;
    //Return codes for user methods
    public static final int FAILURE = -1;
    public static final int SUCCESS = 0;
    public static final int EMAIL_ALREADY_EXISTS = 1;

    public static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(Users_T.CREATE_TABLE);
            _db.execSQL(Post_T.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version "
                    + oldVersion + " to " + newVersion
                    + ", which will destroy all old data!");
            _db.execSQL(Users_T.DELETE_TABLE);
            _db.execSQL(Post_T.DELETE_TABLE);
            onCreate(_db);
        }
    }

    public static void openDB(Context c){
        if (db == null) {
            dBHelper = new DatabaseHelper(c);
            db = dBHelper.getWritableDatabase();
        }
    }

    public static void closeDB(){
        db = null;
        dBHelper.close();
    }

    /*User_T */
    public static int addUser(User user){
        assert(db != null);
        if (emailExists(user.getEmail()))
            return EMAIL_ALREADY_EXISTS;
        ContentValues values = new ContentValues(3);
        values.put(Users_T.USERNAME, user.getUsername());
        values.put(Users_T.EMAIL, user.getEmail());
        values.put(Users_T.PASSWORD, user.getPassword());
        long results = db.insert(Users_T.TABLE_NAME, null, values);
        if (results == -1)
            return FAILURE;
        else
            return SUCCESS;
    }

    public static int deleteUser(String email){
        assert(db != null);
        Log.d(TAG, "Deleting: " + email);
        int res = db.delete(Users_T.TABLE_NAME, Users_T.EMAIL + " = '" + email + "'", null);
        if (res == 0) {
            Log.d(TAG, "no rows deleted from excursion table");
        }
        return res;
    }

    public static User getUser(String email){
        assert(db != null);
        String query = Users_T.GET_USER;
        String[] data = {email};
        Cursor c = db.rawQuery(query, data);
        if (c == null || c.getCount() == 0) {
            return null;
        }
        c.moveToFirst();
        String username = c.getString(c.getColumnIndex(Users_T.USERNAME));
        String password = c.getString(c.getColumnIndex(Users_T.PASSWORD));
        return new User(username, password, email);
    }

    public static boolean emailExists(String email)
    {
        assert(db != null);
        String query = Users_T.GET_USER;
        String[] data = {email};
        Cursor c = db.rawQuery(query, data);
        if (c == null || c.getCount() == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    /*Post_T */
    public static int addPost(Post post){
        assert(db != null);
        ContentValues values = new ContentValues(3);
        values.put(Post_T.DATE, post.getDate());
        values.put(Post_T.CONTENT, post.getContent());
        values.put(Post_T.USER, post.getUid());
        values.put(Post_T.LAT, post.getLat());
        values.put(Post_T.LON, post.getLon());
        long results = db.insert(Post_T.TABLE_NAME, null, values);
        if (results == -1)
            return FAILURE;
        else
            return SUCCESS;
    }

    public static int deletePost(String id){
        assert(db != null);
        Log.d(TAG, "Deleting post: " + id);
        int res = db.delete(Post_T.TABLE_NAME, Post_T._ID + " = '" + id + "'", null);
        if (res == 0) {
            Log.d(TAG, "no rows deleted from excursion table");
        }
        return res;
    }

    public static Post getPost(String id){
        assert(db != null);
        String query = Post_T.GET_POST;
        String[] data = {id};
        Cursor c = db.rawQuery(query, data);
        if (c == null || c.getCount() == 0) {
            return null;
        }
        c.moveToFirst();
        String date = c.getString(c.getColumnIndex(Post_T.DATE));
        String content = c.getString(c.getColumnIndex(Post_T.CONTENT));
        String user = c.getString(c.getColumnIndex(Post_T.USER));
        double lat = c.getDouble(c.getColumnIndex(Post_T.LAT));
        double lon = c.getDouble(c.getColumnIndex(Post_T.LON));
        int _id = c.getInt(c.getColumnIndex(Post_T._ID));
        return new Post(date, content, user, _id, lat, lon);
    }

    public static ArrayList<Post> getUserPosts(String uid){
        assert(db != null);
        ArrayList<Post> posts = new ArrayList<>();
        String query = Post_T.GET_USER_POSTS;
        String[] data = {uid};
        Cursor c = db.rawQuery(query, data);
        if(c == null || c.getCount() == 0) {
            return null;
        }

        Post n;
        String date;
        String content;
        int _id;
        double lat;
        double lon;
        if(c.getCount() >= 1) {
            while (c.moveToNext()) {
                date = c.getString(c.getColumnIndex(Post_T.DATE));
                content = c.getString(c.getColumnIndex(Post_T.CONTENT));
                _id = c.getInt(c.getColumnIndex(Post_T._ID));
                lat = c.getDouble(c.getColumnIndex(Post_T.LAT));
                lon = c.getDouble(c.getColumnIndex(Post_T.LON));
                n = new Post(date, content, uid, _id, lat, lon);
                posts.add(n);
            }
        }

        return posts;
    }

}

