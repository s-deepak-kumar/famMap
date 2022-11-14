package com.sdeepakkumar.fammap.sqlitedatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hitanshu on 9/8/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "famMap.db";
    public static final String RECENT_SEARCH = "search_recent";

    public static final String ID = "id";
    public static final String ADDRESS = "address";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String queryCreateSearchRecentTable = "CREATE TABLE " + RECENT_SEARCH + " ( "
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ADDRESS + " TEXT)";

        sqLiteDatabase.execSQL(queryCreateSearchRecentTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
