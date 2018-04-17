package com.example.mkmnim.socialize.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mkmnim.socialize.Models.Message;

import java.util.ArrayList;
import java.util.List;



public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MessageManager";
    private static final String TABLE_MESSAGES = "MessageTable";
    private static final String KEY_ID = "id";
    private static final String KEY_MESSAGE_CONTENT = "Message_Text";
    private static final String KEY_FROM = "From_";
    private static final String KEY_TO = "To_";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_MESSAGE_TABLE = "CREATE TABLE " + TABLE_MESSAGES + " ("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MESSAGE_CONTENT + " TEXT, "
                + KEY_FROM + " TEXT," + KEY_TO + " TEXT"+")";
        db.execSQL(CREATE_MESSAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        // Create tables again
        onCreate(db);
    }
    public void addMessage(Message message)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_CONTENT, message.getMessage()); //
        values.put(KEY_FROM, message.getFrom()); //
        values.put(KEY_TO,message.getReceiver());
        values.put(KEY_ID, getMessagesCount()+1);

        // Inserting Row
        db.insert(TABLE_MESSAGES, null, values);
        //2nd argument is String containing nullColumnHack

//        db.close(); // Closing database connection
    }

    public int getMessagesCount()
    {
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();

        // return count
        return cursor.getCount();
    }

    public List<Message> getAllMessages()
    {
        List<Message> messageList = new ArrayList<Message>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(2)
                );

                // Adding message to list
                messageList.add(message);
            } while (cursor.moveToNext());
        }

        // return contact list
        return messageList;
    }

    public void deleteAllMessages()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_MESSAGES);
    }

}