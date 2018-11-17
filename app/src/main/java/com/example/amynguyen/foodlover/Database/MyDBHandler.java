package com.example.amynguyen.foodlover.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.amynguyen.foodlover.Adapters.BusinessLineItemAdapter;
import com.example.amynguyen.foodlover.Models.Business;

import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {
    Business biz;


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Restaurant.db";
    private static final String TABLE_FAVORITE_NAME = "favorite_restaurant";
    private static final String TABLE_RECENT_NAME = "recent_restaurant";

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableFavorite = "CREATE TABLE " + TABLE_FAVORITE_NAME +
                "(name TEXT, address TEXT, category TEXT, rating FLOAT, imgURL TEXT)";
        sqLiteDatabase.execSQL(createTableFavorite);

        String createTableRecent = "CREATE TABLE " + TABLE_RECENT_NAME +
                "(name TEXT, address TEXT, category TEXT, rating FLOAT, imgURL TEXT)";
        sqLiteDatabase.execSQL(createTableRecent);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addToFavorite(BusinessLineItemAdapter myAdapter, int i) {
        biz = (Business) myAdapter.getItem(i);
        ContentValues val = new ContentValues();
        val.put("name", biz.getName());
        val.put("address", biz.getAddress());
        val.put("category", biz.getCategory());
        val.put("rating", biz.getRating());
        val.put("imgURL", biz.getImgURL());

        long result;
        SQLiteDatabase db = this.getWritableDatabase();
        result = db.insert(TABLE_FAVORITE_NAME, null, val);
        db.close();
    }

    public void addToRecent(BusinessLineItemAdapter myAdapter, int i) {
        biz = (Business) myAdapter.getItem(i);
        ContentValues val = new ContentValues();
        val.put("name", biz.getName());
        val.put("address", biz.getAddress());
        val.put("category", biz.getCategory());
        val.put("rating", biz.getRating());
        val.put("imgURL", biz.getImgURL());

        long result;
        SQLiteDatabase db = this.getWritableDatabase();
        result = db.insert(TABLE_RECENT_NAME, null, val);
        db.close();
    }

    public String loadFavorite()    {
        String result = "";
        String query = "SELECT * FROM " + TABLE_FAVORITE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            result = cursor.getString(0) + cursor.getString(1)
                    +cursor.getString(2) + cursor.getInt(3) + cursor.getString(4)+
                    System.getProperty("line.separator");
        }
        cursor.close();
        db.close();
        return result;
    }
}
