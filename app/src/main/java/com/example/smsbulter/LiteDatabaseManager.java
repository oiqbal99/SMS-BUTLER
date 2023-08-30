package com.example.smsbulter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.lang.UScript;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LiteDatabaseManager extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteInteraction";
    private static final String TABLE_NAME = "MESSAGES_TABLE";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_MESSAGES = "MESSAGE";
    private static final String COLUMN_ACTIVE = "ACTIVE";
    private static final int COL_ID = 0;
    private static final int COL_MSG = 1;
    private static final int COL_ACTIVE = 2;

    public LiteDatabaseManager(@Nullable Context context) {
        super(context, "smsbutler.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_MESSAGES + " TEXT, " + COLUMN_ACTIVE + " BOOL)";
            db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
    }

    public boolean addDataToDatabase(UserResponses response){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(response.isActive()){updateActiveResponse(db);}
        contentValues.put(COLUMN_MESSAGES, response.getMessage());
        contentValues.put(COLUMN_ACTIVE, response.isActive());
        Log.d(TAG, "addDataToDatabase: Adding " + response.getMessage() + " to " + " database");
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public UserResponses getActiveMessage(){
        UserResponses activeMessage = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ACTIVE + " = 1";
        Cursor data = db.rawQuery(query, null);
        if(data.moveToFirst()){
            String msg = data.getString(COL_MSG);
            boolean active = data.getInt(COL_ACTIVE) == 1;
            activeMessage = new UserResponses(msg, active);
        }
        db.close();
        data.close();
      return activeMessage;
    }

    public void editChosenResponse(int itemID, String oldResponse, UserResponses newResponse){
        SQLiteDatabase db = this.getWritableDatabase();
        //Check if new response is active and the old response is not active
        if(newResponse.isActive()){
            updateActiveResponse(db);
        }
        Log.d(TAG, "editChosenResponse: itemID: " + itemID + " oldResponse: " + oldResponse + " New response: " + newResponse.getMessage() + " State: " + newResponse.isActive()) ;
        String query = "UPDATE " + TABLE_NAME + " SET " + COLUMN_MESSAGES + "= '" + newResponse.getMessage() + "', " + COLUMN_ACTIVE + " = " + newResponse.isActive() + " WHERE " +
                COLUMN_ID + " = " + itemID + " AND " + COLUMN_MESSAGES + " = '" + oldResponse +"'";
        db.execSQL(query);
        db.close();
    }

    public boolean isInActiveState(int itemID, String response, SQLiteDatabase liteDatabase){
        String query = "SELECT " + COLUMN_ACTIVE + " FROM " + TABLE_NAME + " WHERE " + COLUMN_MESSAGES + " = '" + response + "'"
                    + " AND " + COLUMN_ID + " = " + itemID;
        Cursor data = liteDatabase.rawQuery(query,null);
        if(data.moveToFirst()){
            return data.getInt(COL_ACTIVE) == 1;
        }
        data.close();
        return false;
    }

    public void changeActiveState(int itemID, UserResponses responses){
          SQLiteDatabase db = this.getWritableDatabase();
          if(responses.isActive()){
              updateActiveResponse(db);
          }
          String query = "UPDATE " + TABLE_NAME + " SET " + COLUMN_ACTIVE + " = " + responses.isActive()
                   + " WHERE " + COLUMN_ID + " = " + itemID  + " AND " +  COLUMN_MESSAGES  + " = '" + responses.getMessage() +"'";
          db.execSQL(query);
          db.close();
    }

    private void updateActiveResponse(SQLiteDatabase databaseLite){
        String query = "UPDATE " + TABLE_NAME + " SET " + COLUMN_ACTIVE + " = 0";
        databaseLite.execSQL(query);
    }

    public int getItemID(UserResponses mMsg){
        int result = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_MESSAGES + " ='" + mMsg.getMessage() +"'";
        Cursor data = db.rawQuery(query, null);
        if(data.moveToFirst()){
            Log.d(TAG, "getItemID: database id sent: " + data.getInt(COL_ID));
            result = data.getInt(COL_ID);
        }

        data.close();
        db.close();
        return result;
    }



    public void deleteOneItem(int itemID){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + itemID;
        db.execSQL(query);
        db.close();
    }

    public void deleteMultipleItems(ArrayList<Integer> IDs){
        SQLiteDatabase db  = this.getWritableDatabase();
        for(int i = 0; i < IDs.size(); i++){
            String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + IDs.get(i);
            db.execSQL(query);
        }
        db.close();
    }

    public ArrayList<UserResponses> getAllMessages(){
        ArrayList<UserResponses> result = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery(query, null);
        if(data.getCount() != 0){
            while(data.moveToNext()){
                String textMessage = data.getString(COL_MSG);
                boolean activeResponse = data.getInt(COL_ACTIVE) == 1;
                UserResponses newResponse = new UserResponses(textMessage, activeResponse);
                result.add(newResponse);
            }
        }
        data.close();
        db.close();
        return result;
    }
}
