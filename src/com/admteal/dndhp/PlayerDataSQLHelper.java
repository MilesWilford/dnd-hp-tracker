package com.admteal.dndhp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.database.sqlite.SQLiteOpenHelper;

public class PlayerDataSQLHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "players.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE = "players";
	
	public static final String COL_NAME = "playerName";
	public static final String COL_SERPLAYER = "serializedPlayer";
	
	public PlayerDataSQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("PlayerDataSQLHelper", "Before creating table");
		String sql = "CREATE TABLE " + TABLE + " (" 
				+ BaseColumns._ID + " integer primary key autoincrement, " 
				+ COL_NAME + " text not null, " 
				+ COL_SERPLAYER + " blob);";
		Log.d("PlayerDataSQLHelper", "onCreate: " + sql);
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// I'm not good with this, so for now an upgrade will simply delete the old db
		String sql = "DROP TABLE " + TABLE + ";";
		db.execSQL(sql);
		Log.d("PlayerDataSQLHelper", "onUpgrade: " + sql);
		onCreate(db);
	}

}

/*
 * players.db version 1
 * table players:
 * +--------+----------+-------------------------+
 * |   ID   |   NAME   |        SERPLAYER        |
 * +--------+----------+-------------------------+
 * |  auto  | assigned | Serialized Player class |
 * +--------+----------+-------------------------+
 */