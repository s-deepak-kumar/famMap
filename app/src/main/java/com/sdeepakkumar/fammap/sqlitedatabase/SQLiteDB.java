package com.sdeepakkumar.fammap.sqlitedatabase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDB {

    //ADD RECENT SEARCH
    public static void addSearchRecent(Context context, String address) {
        if (address == null) return;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        if (isSearchRecent(context, address)) {
            removeSearchFromRecent(context, address);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.ADDRESS, address);
            database.insert(DatabaseHelper.RECENT_SEARCH, null, contentValues);
        }else {
            removeFirstRowFromSearchRecent(context);
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.ADDRESS, address);
            database.insert(DatabaseHelper.RECENT_SEARCH, null, contentValues);
        }
        database.close();
    }

    public static boolean isSearchRecent(Context context, String word) {
        if (word == null) return false;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        boolean isSearchReecent;
        String isSearchRecentQuery = "SELECT * FROM " + DatabaseHelper.RECENT_SEARCH+ " WHERE " + DatabaseHelper.ADDRESS + " = ?";
        Cursor cursor = database.rawQuery(isSearchRecentQuery, new String[] { word});
        if (cursor.getCount() == 1)
            isSearchReecent = true;
        else
            isSearchReecent = false;

        cursor.close();
        database.close();
        return isSearchReecent;
    }

    public static void removeSearchFromRecent(Context context, String word) {
        if (word == null) return;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        if (isSearchRecent(context, word)) {
            database.delete(DatabaseHelper.RECENT_SEARCH, DatabaseHelper.ADDRESS+ " = ?", new String[] { word});
        }
        database.close();
    }

    public static void removeFirstRowFromSearchRecent(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        if (isRecentSearchRowMoreThan10(context)) {
            String deleteRecentSearchFirstRowQuery = "DELETE FROM " + DatabaseHelper.RECENT_SEARCH + " WHERE " + DatabaseHelper.ID + " IN " +
                    "(SELECT " + DatabaseHelper.ID + " FROM " + DatabaseHelper.RECENT_SEARCH +
                    " ORDER BY " + DatabaseHelper.ID +" ASC LIMIT 1)";
            database.execSQL(deleteRecentSearchFirstRowQuery);
        }
        database.close();
    }

    public static boolean isRecentSearchRowMoreThan10(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        boolean isSearchRecentRowMoreThan10;
        long count = DatabaseUtils.queryNumEntries(database, DatabaseHelper.RECENT_SEARCH);

        if (count > 10)
            isSearchRecentRowMoreThan10 = true;
        else
            isSearchRecentRowMoreThan10 = false;

        database.close();
        return isSearchRecentRowMoreThan10;
    }

    public static void clearAllSearchRecent(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        String deleteAllRecentSearchQuery = "DELETE FROM " + DatabaseHelper.RECENT_SEARCH;
        database.execSQL(deleteAllRecentSearchQuery);

        database.close();
    }

    public static List<String> getSearchRecentBriefs(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        List<String> searchRecent = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.RECENT_SEARCH, null, null, null, null, null, DatabaseHelper.ID + " DESC");
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String word = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADDRESS));

            searchRecent.add(word);
        }
        cursor.close();
        database.close();
        return searchRecent;
    }
}
