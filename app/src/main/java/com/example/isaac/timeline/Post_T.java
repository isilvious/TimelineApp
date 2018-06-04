package com.example.isaac.timeline;

import android.provider.BaseColumns;

/**
 * Created by Isaac.
 */

public class Post_T implements BaseColumns {
    private Post_T(){} //Makes class non-instantiable

    public static final String TABLE_NAME = "posts";

    /*Colums */

    //the BaseColumn interface provides a field named _ID
    public static final String DATE = "date";
    public static final String CONTENT = "content";
    public static final String USER = "user";
    public static final String LAT = "lat";
    public static final String LON = "lon";

    /*SQL Statements */
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY, "
            + DATE + " TEXT NOT NULL, "
            + CONTENT + " TEXT NOT NULL, "
            + USER + " TEXT NOT NULL, "
            + LAT + ", "
            + LON + ")";

    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String GET_POST = "SELECT * FROM " + TABLE_NAME
            + " WHERE " + _ID + "= ?";
    public static final String GET_USER_POSTS = "SELECT * FROM " +TABLE_NAME
            + " WHERE " + USER + "= ?";
}
