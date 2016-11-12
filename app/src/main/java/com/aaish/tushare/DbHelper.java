package com.aaish.tushare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaishsindwani on 30/09/16.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "My_Downloads.db";
    private static final int DATABASE_VERSION = 1;

    // Definition of table and column names of Products table
    public static final String TABLE_NAME = "File_Downloads";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "File_Name";
    public static final String COLUMN_URI = "Uri";
    public static final String COLUMN_SUBJECT = "Subject";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_USERNAME = "Username";

    // Definition of table and column names of Transactions table
    /*public static final String TABLE_TRANSACTIONS = "Transactions";
    public static final String COLUMN_PRODUCT_ID = "ProductId";
    public static final String COLUMN_AMOUNT = "Amount";*/

    // Create Statement for Products Table
    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE " + TABLE_NAME + "  (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_URI + " TEXT, " +
            COLUMN_SUBJECT + " TEXT, " +
            COLUMN_TYPE + " TEXT, " +
            COLUMN_USERNAME + " TEXT " +
            ");";

    // Create Statement for Transactions Table
    /*private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TABLE_TRANSACTIONS + "  (" +
            COLUMN_ID + " INTEGER PRIMARY KEY," +
            COLUMN_PRODUCT_ID + " INTEGER," +
            COLUMN_AMOUNT + " INTEGER," +
            " FOREIGN KEY (" + COLUMN_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_ID + ")" +
            ");";*/

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate should always create your most up to date database
        // This method is called when the app is newly installed
        db.execSQL(CREATE_TABLE_PRODUCT);
        //db.execSQL(CREATE_TABLE_TRANSACTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addFile(Downloaded downloaded) {
        // You need a writable database to insert data
        final SQLiteDatabase database = getWritableDatabase();

// Create a ContentValues instance which contains the data for each column
// You do not need to specify a value for the PRIMARY KEY column.
// Unique values for these are automatically generated.
        final ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, downloaded.getFilename());
        values.put(COLUMN_URI, String.valueOf(downloaded.getFileuri()));
        values.put(COLUMN_SUBJECT, downloaded.getSubject());
        values.put(COLUMN_TYPE, downloaded.getType());
        values.put(COLUMN_USERNAME, downloaded.getUsername());
// This call performs the update
// The return value is the rowId or primary key value for the new row!
// If this method returns -1 then the insert has failed.
        Log.e("I am in ", " DBHelper add file");
        database.insert(TABLE_NAME, null, values);
        database.close();
    }

    public void deleteFile(String filename) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + "=\"" + filename + "\";");
        db.close();
    }

    public ArrayList<Downloaded> getAllLabels() {
        ArrayList<Downloaded> labels = new ArrayList<Downloaded>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        final int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
        final int uriIndex = cursor.getColumnIndex(COLUMN_URI);
        final int subjectIndex = cursor.getColumnIndex(COLUMN_SUBJECT);
        final int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);
        final int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Downloaded db_downloaded = new Downloaded();
                db_downloaded.setFilename(cursor.getString(nameIndex));
                db_downloaded.setFileuri(Uri.parse(cursor.getString(uriIndex)));
                db_downloaded.setSubject(cursor.getString(subjectIndex));
                db_downloaded.setType(cursor.getString(typeIndex));
                db_downloaded.setUsername(cursor.getString(usernameIndex));
                labels.add(db_downloaded);
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }
}




