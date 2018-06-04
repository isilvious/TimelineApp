package com.example.isaac.timeline;

import android.provider.BaseColumns;

/**
 * Created by Isaac on 4/5/2017.
 */

public final class Users_T implements BaseColumns{
    private Users_T(){} //Makes class non-instantiable

    public static final String TABLE_NAME = "users";

    /*Colums */

    //the BaseColumn interface provides a field named _ID
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";

    /*SQL Statements */
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY, "
            +USERNAME + " TEXT NOT NULL, "
            + PASSWORD + " TEXT NOT NULL, "
            +EMAIL + " TEXT NOT NULL UNIQUE)";

    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String GET_PASSWORD = "SELECT " + PASSWORD
            +" FROM " + TABLE_NAME + " WHERE " + EMAIL + "= ?";
    public static final String GET_USER = "SELECT * FROM " + TABLE_NAME
            + " WHERE " + EMAIL + "= ?";
}
