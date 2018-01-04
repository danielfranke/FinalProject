package cst2335.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * CST2335 Android Final Project
 *
 * GROUP MEMBERS:
 * Daniel Franke (Nutrition Tracker)
 * Ahmed Mohammed
 * Alexandre Martin
 * Yunfeng Yu <-- NO CONTACT WITH THIS MEMBER!
 *
 * The database helper file for all activities
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // MASTER SETTINGS
    private static final String DATABASE_NAME = "projectDatabase.db";
    private static final int VERSION = 1; // the current database version
    public final static String ID = "_id";

    // VARIABLES BY ACTIVITY

    // ACTIVITY TRACKING

    // NUTRITION TRACKER by: Daniel Franke
    public final static String NUTRITION_TABLE = "nutrition_tracker";
    public final static String FOOD_ITEM = "food_item";
    public final static String CALORIES = "calories";
    public final static String FAT = "fat";
    public final static String CARBOHYDRATES = "carbohydrates";
    public final static String NUTRITION_ENTRY_DATE = "entry_date";

    // HOUSE THERMOSTAT

    // AUTOMOBILE

    // ---------------------------------------- /

    public DatabaseHelper(Context ctx){
        super(ctx, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        // CREATE DATABASE TABLES

        // NUTRITION TRACKER - create table
        String sqlNutrition = "CREATE TABLE " + NUTRITION_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FOOD_ITEM + " TEXT, " + CALORIES + " INT, " + FAT + " INT, " + CARBOHYDRATES + " INT, " + NUTRITION_ENTRY_DATE + " INT);";
        db.execSQL(sqlNutrition);
        Log.i("DataBase Helper", "Creating nutrition tracker database table");
    }

    @Override
    // Upgrade table
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + NUTRITION_TABLE);
        onCreate(db);
        Log.i("Database", "Upgrading table");

    }

    @Override
    // Downgrade table
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + NUTRITION_TABLE);
        onCreate(db);
        Log.i("Database", "Downgrading table");
    }

    @Override
    // onOpen
    public void onOpen(SQLiteDatabase db){
        Log.i("Database", "onOpen called");
    }
}