package com.example.shara.assignment5;

public class DatabaseContent {

    public static final String TAG = "databasecontent";

    public static final String TABLE_NAME = "HOMETOWN";
    public static final String PRIMARY_KEY = "PRIMARY KEY";
    public static final String COMMA_SEP = ",";
    public static final String TEXT_ID_TYPE = "TEXT";
    public static final String REAL_ID_TYPE="REAL";

    public static final String ID = "Id";
    public static final String NICKNAME = "nickname";
    public static final String  COUNTRY= "country";
    public static final String  STATE= "state";
    public static final String  CITY= "city";
    public static final String  YEAR= "year";
    public static final String  LATITUDE="latitude";
    public static final String  LONGITUDE="longitude";


    public static final String SQL_CREATE_HOMETOWN_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    ID + " " + TEXT_ID_TYPE + " " +PRIMARY_KEY+COMMA_SEP +
                    NICKNAME + " " + TEXT_ID_TYPE + COMMA_SEP +
                    COUNTRY + " " + TEXT_ID_TYPE + COMMA_SEP +
                    STATE + " " + TEXT_ID_TYPE + COMMA_SEP +
                    CITY + " " + TEXT_ID_TYPE + COMMA_SEP +
                    YEAR + " " + TEXT_ID_TYPE + COMMA_SEP +
                    LATITUDE + " " + TEXT_ID_TYPE + COMMA_SEP +
                    LONGITUDE + " " + TEXT_ID_TYPE +
                    ")";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
