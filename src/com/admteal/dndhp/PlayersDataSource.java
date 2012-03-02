package com.admteal.dndhp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PlayersDataSource {
	
	// Database fields
	private SQLiteDatabase database;
	private PlayerDBSQLiteHelper databaseHelper;
	private String[] allColumns = { PlayerDBSQLiteHelper.COLUMN_ID, 
			PlayerDBSQLiteHelper.COLUMN_PLAYER_NAME, 
			PlayerDBSQLiteHelper.COLUMN_SERIALIZED_PLAYER };
	
	public PlayersDataSource(Context context) {
		databaseHelper = new PlayerDBSQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = databaseHelper.getWritableDatabase();
	}
	
	public void close() {
		databaseHelper.close();
	}
	
	public void createPlayer(String playerName, byte[] player) {
		ContentValues values = new ContentValues();
		values.put(PlayerDBSQLiteHelper.COLUMN_PLAYER_NAME, playerName);
		values.put(PlayerDBSQLiteHelper.COLUMN_SERIALIZED_PLAYER, player);
		long insertId = database.insert(PlayerDBSQLiteHelper.TABLE_PLAYERS, null, values);
		// Do I need this?
		Cursor cursor = database.query(PlayerDBSQLiteHelper.TABLE_PLAYERS,
				allColumns, PlayerDBSQLiteHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		//return cursorToPlayer(cursor);
	}
	
	public void updatePlayer(Player player) {
		String playerName = player.getName();
		byte[] serializedPlayer = Player.serliazeObject(player);
		String whereClause = PlayerDBSQLiteHelper.COLUMN_PLAYER_NAME + " = " + playerName;
		database.execSQL("INSERT OR IGNORE INTO "
				+ PlayerDBSQLiteHelper.TABLE_PLAYERS + " ("
				+ PlayerDBSQLiteHelper.COLUMN_PLAYER_NAME + ") VALUES ('"
				+ playerName + "')");
		ContentValues values = new ContentValues();
		values.put(PlayerDBSQLiteHelper.COLUMN_PLAYER_NAME, playerName);
		values.put(PlayerDBSQLiteHelper.COLUMN_SERIALIZED_PLAYER,
				serializedPlayer);
		database.update(PlayerDBSQLiteHelper.TABLE_PLAYERS, values, whereClause, null);
	}
	
	public void deletePlayer(Player player) {
		String playerName = player.getName();
		deletePlayer(playerName);
	}	
	
	public void deletePlayer(String playerName) {
		Log.w(PlayersDataSource.class.getName(), "Deleting player with playerName " + playerName);
		database.delete(PlayerDBSQLiteHelper.TABLE_PLAYERS, PlayerDBSQLiteHelper.COLUMN_PLAYER_NAME + " = " + playerName, null);
	}

	/*private Player cursorToPlayer(Cursor cursor) {
		Player player = new Player();
		player.setId(cursor.getLong(0));
		player.setComment(cursor.getString(1));
		return player;
	}*/
}
