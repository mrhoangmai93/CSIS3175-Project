package com.example.amynguyen.foodlover.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.amynguyen.foodlover.Adapters.BusinessLineItemAdapter;
import com.example.amynguyen.foodlover.Models.Business;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {
    Business biz;
    List<Business> bizList = new ArrayList<>();


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Restaurant_DB.db";
    private static final String TABLE_FAVORITE_NAME = "restaurant_favorite";
    private static final String TABLE_RECENT_NAME = "restaurant_recent";
    private static MyDBHandler sInstance;

    // ...

    public static synchronized MyDBHandler getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new MyDBHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {



        String createTableFavorite = "CREATE TABLE " + TABLE_FAVORITE_NAME +
                "(businessID TEXT, name TEXT, address TEXT, category TEXT, rating FLOAT, imgURL TEXT)";
        sqLiteDatabase.execSQL(createTableFavorite);


        String createTableRecent = "CREATE TABLE " + TABLE_RECENT_NAME +
                "(businessID TEXT, name TEXT, address TEXT, category TEXT, rating FLOAT, imgURL TEXT)";
        sqLiteDatabase.execSQL(createTableRecent);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropTableFavorite = "DROP TABLE IF EXISTS " + TABLE_FAVORITE_NAME;
        String dropTableRecent = "DROP TABLE IF EXISTS " + TABLE_RECENT_NAME;
        sqLiteDatabase.execSQL(dropTableFavorite);
        sqLiteDatabase.execSQL(dropTableRecent);
        this.onCreate(sqLiteDatabase);
    }

    public void addToFavorite(BusinessLineItemAdapter myAdapter, int i) {

        biz = (Business) myAdapter.getItem(i);
        ContentValues val = new ContentValues();
        val.put("businessID", biz.getBusinessId());
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
        val.put("businessID", biz.getBusinessId());
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

    public List<Business> loadFavorite()    {
        //bizList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_FAVORITE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                biz.setBusinessId(cursor.getString(0));
                biz.setName(cursor.getString(1));
                biz.setAddress(cursor.getString(2));
                biz.setCategory(cursor.getString(3));
                biz.setRating(cursor.getDouble(4));
                biz.setImgURL(cursor.getString(5));

                bizList.add(biz);
            } while (cursor.moveToNext());
        }


        /*while (cursor.moveToNext()) {
            biz.setBusinessId(cursor.getString(0));
            biz.setName(cursor.getString(1));
            biz.setAddress(cursor.getString(2));
            biz.setCategory(cursor.getString(3));
            biz.setRating(cursor.getDouble(4));
            biz.setImgURL(cursor.getString(5));
            //System.out.println("Hien thi " + biz.getName());
            bizList.add(biz);

        }*/

        cursor.close();
        db.close();
        return bizList;
    }

    public boolean deleteFromFavorite(BusinessLineItemAdapter myAdapter, int i) {
        biz = (Business) myAdapter.getItem(i);
        String query = "SELECT * FROM " + TABLE_FAVORITE_NAME + " WHERE businessID = "
                + "'" +  String.valueOf(biz.getBusinessId()) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToFirst()) {
            db.delete(TABLE_FAVORITE_NAME, "businessID =?" , new String[] {String.valueOf(cursor.getString(0))});
            cursor.close();
            return true;
        }
        db.close();
        return false;
    }

    public boolean searchFromFavorite(BusinessLineItemAdapter myAdapter, int i) {
        biz = (Business) myAdapter.getItem(i);
        String query = "SELECT * FROM " + TABLE_FAVORITE_NAME + " WHERE businessID = "
                + "'" +  String.valueOf(biz.getBusinessId()) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToFirst()) {
            if(!cursor.getString(0).equals(""))
            cursor.close();
            return true;
        }
        db.close();
        return false;
    }
}
