package com.admteal.dndhp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlayerDBSQLiteHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "players.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_PLAYERS = "players";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PLAYER_NAME = "playerName";
	public static final String COLUMN_SERIALIZED_PLAYER = "serializedPlayer";
	
	//SQL statement for declaring the database
	public static final String DB_CREATE = "create table " 
			+ TABLE_PLAYERS + "( "
				+ COLUMN_ID + " integer primary key autoincrement," 
				+ COLUMN_PLAYER_NAME + " text unique not null,"
				+ COLUMN_SERIALIZED_PLAYER + " blob not null"
			+");";
	
	public PlayerDBSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DB_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// For now we'll just toss an old database rather than trying to update
		Log.w(PlayerDBSQLiteHelper.class.getName(),
				"Upgrading DB from version " + oldVersion + " to version " + newVersion + " and wiping out old data.");
		database.execSQL("DROP TABLE IF EXISTS" + TABLE_PLAYERS);
		onCreate(database);
	}

}
