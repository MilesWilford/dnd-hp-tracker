package com.admteal.dndhp;

import java.util.ArrayList;

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
	
	public void updatePlayer(Player player) {
		String playerName = player.getName();
		byte[] serializedPlayer = Player.serliazeObject(player);
		database.execSQL("INSERT OR IGNORE INTO "
				+ PlayerDBSQLiteHelper.TABLE_PLAYERS + " ("
				+ PlayerDBSQLiteHelper.COLUMN_PLAYER_NAME + ") VALUES ('"
				+ playerName + "')");
		Log.v("1st SQL", "Inserting " + playerName);
		database.execSQL("UPDATE " + PlayerDBSQLiteHelper.TABLE_PLAYERS
				+ " SET " + PlayerDBSQLiteHelper.COLUMN_SERIALIZED_PLAYER
				+ " = '" + serializedPlayer + "' WHERE "
				+ PlayerDBSQLiteHelper.COLUMN_PLAYER_NAME + " = '" + playerName
				+ "'");
		Log.v("2nd SQL", "Inserting " + serializedPlayer);
	}
	
	public void deletePlayer(Player player) {
		String playerName = player.getName();
		deletePlayer(playerName);
	}	
	
	public void deletePlayer(String playerName) {
		Log.w(PlayersDataSource.class.getName(), "Deleting player with playerName " + playerName);
		database.delete(PlayerDBSQLiteHelper.TABLE_PLAYERS, PlayerDBSQLiteHelper.COLUMN_PLAYER_NAME + " = " + playerName, null);
	}
	
	public ArrayList<Player> getAllPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();
		Cursor cursor = database.query(PlayerDBSQLiteHelper.TABLE_PLAYERS, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Player player = cursorToPlayer(cursor);
			players.add(player);
			cursor.moveToNext();
			Log.v("PlayersDataSource.getAllPlayers()", "Player scan " + player.getName());
		}
		cursor.close();
		return players;
	}
	
	private Player cursorToPlayer(Cursor cursor) {
		Player player = (Player) Player.deseriaizeObject(cursor.getBlob(2));
		return player;
	}
}
