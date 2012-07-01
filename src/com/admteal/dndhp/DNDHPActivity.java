//git repository: "git@github.com:admiralteal/dnd-hp-tracker.git"
package com.admteal.dndhp;

import java.util.ArrayList;

import com.admteal.dndhp.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Bundle;

public class DNDHPActivity extends Activity {
	PlayerDataSQLHelper playerData;
	public ArrayList<Player> players = new ArrayList<Player>();
	
	public int CURRENT_PLAYER;
	// Some string literals we'll use for clarity's sake
	private static String PLUS, MINUS, INPUT;
	private static String BLANK = "";

	// This will let me treat dialogs as ints using a switch
	private final int DIALOG_EXTENDED_REST = 0;
	private final int DIALOG_NEW_CUSTOM_PLAYER = 1;

	private int currentEntry;

	// The various status toggles
	private ToggleButton toggleBlinded, toggleDazed, toggleDominated,
			toggleGrabbed, toggleMarked, toggleProne, toggleStunned,
			toggleWeakened;
	
	// This button displays current HS
	private Button inputHS;

	// This button displays current ongoing/regen
	private Button inputOngo;
	
	// This button displays current death saves
	private Button inputDS;

	// This button displays current surges
	private Button inputSurges;
	
	//This button allows the user to click to undo
	private Button inputUndo;

	// The views we'll be using for presentation
	private TextView currentEntryView, currentHPView, currentTHPView;
	private ScrollView showWorkScroller;
	private LinearLayout showWorkLayout;

	// Used in place of setting dozens of OnClickListeners
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.toggleBlinded:
			if (toggleBlinded.isChecked()) {
				players.get(CURRENT_PLAYER).blind();
			} else {
				players.get(CURRENT_PLAYER).unblind();
			}
			break;
		case R.id.toggleDazed:
			if (toggleDazed.isChecked()) {
				players.get(CURRENT_PLAYER).daze();
			} else {
				players.get(CURRENT_PLAYER).undaze();
			}
			break;
		case R.id.toggleDominated:
			if (toggleDominated.isChecked()) {
				players.get(CURRENT_PLAYER).dominate();
			} else {
				players.get(CURRENT_PLAYER).undominate();
			}
			break;
		case R.id.toggleGrabbed:
			if (toggleGrabbed.isChecked()) {
				players.get(CURRENT_PLAYER).grab();
			} else {
				players.get(CURRENT_PLAYER).ungrab();
			}
			break;
		case R.id.toggleMarked:
			if (toggleMarked.isChecked()) {
				players.get(CURRENT_PLAYER).mark();
			} else {
				players.get(CURRENT_PLAYER).unmark();
			}
			break;
		case R.id.toggleProne:
			if (toggleProne.isChecked()) {
				players.get(CURRENT_PLAYER).knockProne();
			} else {
				players.get(CURRENT_PLAYER).getUp();
			}
			break;
		case R.id.toggleStunned:
			if (toggleStunned.isChecked()) {
				players.get(CURRENT_PLAYER).stun();
			} else {
				players.get(CURRENT_PLAYER).unstun();
			}
			break;
		case R.id.toggleWeakened:
			if (toggleStunned.isChecked()) {
				players.get(CURRENT_PLAYER).weaken();
			} else {
				players.get(CURRENT_PLAYER).unweaken();
			}
			break;
		case R.id.inputTHP:
			tempHPUpdater(currentEntry);
			clearEntry();
			break;
		case R.id.inputAdd:
			showWorkUpdater(currentEntry);
			break;
		case R.id.inputSub:
			showWorkUpdater(-currentEntry);
			break;
		case R.id.inputExtendedRest:
			showDialog(DIALOG_EXTENDED_REST);
			break;
		case R.id.inputClear:
			clearEntry();
			break;
		case R.id.inputUndo:
			undoLastOp();
			break;
		case R.id.inputHS:
			showWorkUpdater(players.get(CURRENT_PLAYER).getHS());
			break;
		case R.id.ongoAdd:
			ongoUpdater(PLUS);
			break;
		case R.id.ongoSub:
			ongoUpdater(MINUS);
			break;
		case R.id.inputOngo:
			// Stores the value backwards, so invert
			showWorkUpdater(-players.get(CURRENT_PLAYER).getOngo());
			break;
		case R.id.surgesAdd:
			surgesUpdater(PLUS);
			break;
		case R.id.surgesSub:
			surgesUpdater(MINUS);
			break;
		case R.id.inputSurges:
			if (players.get(CURRENT_PLAYER).getSurges() == 0) {
				return;
			}
			showWorkUpdater(players.get(CURRENT_PLAYER).getHS());
			surgesUpdater(MINUS);
			break;
		case R.id.DSAdd:
			DSUpdater(PLUS);
			break;
		case R.id.DSSub:
			DSUpdater(MINUS);
			break;
		default: break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		CURRENT_PLAYER = 0;
		
		// helper for our SQLite database
		playerData = new PlayerDataSQLHelper(this);

		// Get the string literals used in this activity from strings.xml
		PLUS		= getString(R.string.plus);
		MINUS		= getString(R.string.minus);
		INPUT		= getString(R.string.input);

		// Make sure at least one player loaded to prevent any crashes on first boot
		if (players.size() == 0) {
			players.add(new Player());
		}

		// Will allow us to act on the ToggleButtons directly
		toggleBlinded	= (ToggleButton) findViewById(R.id.toggleBlinded);
		toggleDazed		= (ToggleButton) findViewById(R.id.toggleDazed);
		toggleDominated	= (ToggleButton) findViewById(R.id.toggleDominated);
		toggleGrabbed	= (ToggleButton) findViewById(R.id.toggleGrabbed);
		toggleMarked	= (ToggleButton) findViewById(R.id.toggleMarked);
		toggleProne		= (ToggleButton) findViewById(R.id.toggleProne);
		toggleStunned	= (ToggleButton) findViewById(R.id.toggleStunned);
		toggleWeakened	= (ToggleButton) findViewById(R.id.toggleWeakened);

		/*
		 * Create OnClickListeners for the number buttons without having to hard code each button
		 */
		for (int i = 0; i < 10; i++) {
			String buttonID = INPUT + Integer.toString(i);
			int resourceID = getResources().getIdentifier(buttonID, "id",
					getString(R.string.packageName));
			Button b = (Button) findViewById(resourceID);
			final int j = i;
			b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(j);
				}
			});
		}

		// These buttons must be loaded so they can display stuff
		inputOngo	= (Button) findViewById(R.id.inputOngo);
		inputSurges	= (Button) findViewById(R.id.inputSurges);
		inputDS		= (Button) findViewById(R.id.inputDS);
		inputHS		= (Button) findViewById(R.id.inputHS);
		inputUndo 	= (Button) findViewById(R.id.inputUndo);

		// These views are all part of presentation
		currentEntryView	= (TextView) findViewById(R.id.currentEntryView);
		showWorkScroller	= (ScrollView) findViewById(R.id.showWorkScroller);
		showWorkLayout		= (LinearLayout) findViewById(R.id.showWorkLayout);
		currentHPView		= (TextView) findViewById(R.id.currentHPView);
		currentTHPView		= (TextView) findViewById(R.id.currentTHPView);   
		
		/*
		String[] playerNames = {players.get(CURRENT_PLAYER).getName()}; //TODO something better than this for generating array of names
		SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, playerNames);

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(spinnerAdapter, new ActionBar.OnNavigationListener() {
			
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				addPlayerToDB(players.get(CURRENT_PLAYER));
				// TODO: This stuff is stopping my compatibility.  
				// I am sticking with one player for now, but later on it will be important to track multiple players.
				//
				Toast.makeText(getApplicationContext(), Integer.toString(itemPosition), Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		*/
	}
		
	@Override 
	protected void onDestroy() {
		super.onDestroy();
		playerData.close();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		addPlayerToDB(players.get(CURRENT_PLAYER));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		players = getPlayersFromDB();
		relaunchWithPlayer(players.get(CURRENT_PLAYER));
		currentEntryView.setText(Integer.toString(currentEntry));
	}

	// Generate my alert dialogs
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_EXTENDED_REST:
			return new AlertDialog.Builder(this)
					.setMessage(R.string.DIALOG_EXTENDED_REST_msg)
					.setCancelable(true)
					.setPositiveButton(getString(R.string.yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									clearEntry();
									players.get(CURRENT_PLAYER).extendedRest();
									relaunchWithPlayer(players.get(CURRENT_PLAYER));
								}
							}).setNegativeButton(getString(R.string.no), null)
					.create();
		case DIALOG_NEW_CUSTOM_PLAYER:
			// See dialog_custom_player.xml
			// TODO: This is a mess
			final View customizePlayerView = View.inflate(this,
					R.layout.dialog_custom_player, null);
			final EditText newMaxHPEdit = (EditText) customizePlayerView
					.findViewById(R.id.maxHPEdit);
			final EditText newCurrentHPEdit = (EditText) customizePlayerView
					.findViewById(R.id.currentHPEdit);
			final EditText newMaxSurgesEdit = (EditText) customizePlayerView
					.findViewById(R.id.maxSurgesEdit);
			final EditText newCurrentSurgesEdit = (EditText) customizePlayerView
					.findViewById(R.id.currentSurgesEdit);
			final EditText newCurrentHSEdit = (EditText) customizePlayerView
					.findViewById(R.id.currentHSEdit);

			return new AlertDialog.Builder(this)
					.setTitle(R.string.DIALOG_CUSTOM_PLAYER_title)
					.setCancelable(true)
					.setView(customizePlayerView)
					.setPositiveButton(R.string.savePlayer,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									String customPlayerNewMaxHP = newMaxHPEdit
											.getText().toString();
									String customPlayerNewCurrentHP = newCurrentHPEdit
											.getText().toString();
									String customPlayerNewMaxSurges = newMaxSurgesEdit
											.getText().toString();
									String customPlayerNewCurrentSurges = newCurrentSurgesEdit
											.getText().toString();
									String customPlayerNewHS = newCurrentHSEdit
											.getText().toString();

									if (customPlayerNewMaxHP.equals(BLANK)
											|| customPlayerNewMaxSurges
													.equals(BLANK)) {
										Toast.makeText(
												getApplicationContext(),
												getString(R.string.TOAST_couldNotMakePlayer),
												Toast.LENGTH_SHORT).show();
										return;
									} else {
										if (customPlayerNewHS.equals(BLANK)) {
											int customPlayerNewHSInt = Integer
													.parseInt(customPlayerNewMaxHP) / 2;
											customPlayerNewHSInt = (customPlayerNewHSInt / 2) / 2;
											customPlayerNewHS = Integer
													.toString(customPlayerNewHSInt);
										}
										if (customPlayerNewCurrentHP
												.equals(BLANK)
												|| customPlayerNewCurrentSurges
														.equals(BLANK)) {
											players.set(CURRENT_PLAYER, new Player(
													Integer.parseInt(customPlayerNewMaxHP),
													Integer.parseInt(customPlayerNewMaxSurges),
													Integer.parseInt(customPlayerNewHS)));
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.TOAST_madeFullHPPlayer),
													Toast.LENGTH_SHORT).show();
											relaunchWithPlayer(players.get(CURRENT_PLAYER));
										} else {
											players.set(CURRENT_PLAYER, new Player(
													Integer.parseInt(customPlayerNewMaxHP),
													Integer.parseInt(customPlayerNewCurrentHP),
													Integer.parseInt(customPlayerNewMaxSurges),
													Integer.parseInt(customPlayerNewCurrentSurges),
													Integer.parseInt(customPlayerNewHS)));
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.TOAST_madePartialHPPlayer),
													Toast.LENGTH_SHORT).show();
											relaunchWithPlayer(players.get(CURRENT_PLAYER));
										}
									}

								}
							}).setNegativeButton(R.string.abandonPlayer, null)
					.create();
		default:
			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// When user clicks the New Player menu/actionbar button
		case R.id.default_player:
			relaunchWithPlayer(new Player());
			Toast.makeText(getApplicationContext(),
					getString(R.string.TOAST_madeDefaultPlayer),
					Toast.LENGTH_SHORT).show();
			return true;
		// When user clicks the Custom Player menu.actionbar button
		case R.id.newCustomPlayer:
			showDialog(DIALOG_NEW_CUSTOM_PLAYER);
			return true;
		case R.id.setHealingSurge:
			if (currentEntry == 0) {
				Toast.makeText(getApplicationContext(),
						getString(R.string.TOAST_HSWasZero), 
						Toast.LENGTH_SHORT).show();
			}
			players.get(CURRENT_PLAYER).setHS(currentEntry);
			inputHS.setText(getString(R.string.hs) + ": "
					+ Integer.toString(currentEntry));
			clearEntry();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * This method accepts a new Player class and uses it to set all the data
	 * for display in the app.
	 */
	public void relaunchWithPlayer(Player newPlayer) {
		players.set(CURRENT_PLAYER, newPlayer);
		addPlayerToDB(newPlayer);
		updateAll();
		/*
		 * Because of the way they're created, players.get(CURRENT_PLAYER).changeHistory and
		 * players.get(CURRENT_PLAYER).HPHistory are *always* the same length. First,
		 * removeAllViews just in case any were already here.
		 */
		showWorkLayout.removeAllViews();
		for (int i = 0; i < players.get(CURRENT_PLAYER).getChangeHistory().size(); i++) {
			showWorkViewMaker(players.get(CURRENT_PLAYER).getChangeHistory().get(i), players
					.get(0).getHPHistory().get(i));
		}
	}

	// This method updates all views
	public void updateAll() {
		currentHPView.setText(Integer.toString(players.get(CURRENT_PLAYER).getHP()));
		inputUndo.setEnabled(players.get(CURRENT_PLAYER).canUndo());
		ongoUpdater();
		DSUpdater();
		togglesUpdater();
		tempHPUpdater();
		surgesUpdater();
		surgeValueUpdater();
	}

	public void togglesUpdater() {
		Player player = players.get(CURRENT_PLAYER);
		togglesUpdater(player);
	}
	// Sets all toggle buttons to the states from the Player class
	public void togglesUpdater(Player player) {
		toggleBlinded.setChecked(player.isBlinded());
		toggleDazed.setChecked(player.isDazed());
		toggleDominated.setChecked(player.isDominated());
		toggleGrabbed.setChecked(player.isGrabbed());
		toggleMarked.setChecked(player.isMarked());
		toggleProne.setChecked(player.isProne());
		toggleStunned.setChecked(player.isStunned());
		toggleWeakened.setChecked(player.isWeakened());
	}

	/*
	 * Method injures or heals the player based on input, then calls for the
	 * view maker to show work
	 */
	public void showWorkUpdater(int value) {
		// First we must pick our operation.
		if (value > 0) {
			players.get(CURRENT_PLAYER).heal(value);
		} else if (value < 0) {
			players.get(CURRENT_PLAYER).injure(value);
		} else {
			clearEntry();
			return;
		}
		showWorkViewMaker(value);
		clearEntry();
		inputUndo.setEnabled(players.get(CURRENT_PLAYER).canUndo());

		currentHPView.setText(Integer.toString(players.get(CURRENT_PLAYER).getHP()));
	}

	/*
	 * Method which adds the red or green numbers and new current HP values to
	 * showWorkView
	 */
	public void showWorkViewMaker(int value, int hpToList) {
		String operation = BLANK;
		TextView adjustment = new TextView(this);
		TextView sum = new TextView(this);
		if (value > 0) {
			adjustment.setTextColor(Color.GREEN);
		} else if (value < 0) {
			adjustment.setTextColor(Color.RED);
			// get rid of an extra "-" by clearing operation
			operation = BLANK;
		}

		// First line shows how much was added or subtracted as +n or -n
		adjustment.setText(operation + Integer.toString(value));
		adjustment.setGravity(Gravity.RIGHT);
		adjustment.setTextSize(dpi(14));

		// Second line shows new total number
		sum.setText(Integer.toString(hpToList));
		sum.setGravity(Gravity.LEFT);
		sum.setTextSize(dpi(14));

		if (hpToList == players.get(CURRENT_PLAYER).getMaxHP() && !players.get(CURRENT_PLAYER).isDefaultPlayer()) {
			sum.setTextColor(Color.GREEN);
		} else if (players.get(CURRENT_PLAYER).isBloodied() && !players.get(CURRENT_PLAYER).isDefaultPlayer()) {
			sum.setTextColor(Color.YELLOW);
		} else if (players.get(CURRENT_PLAYER).isDying()) {
			sum.setTextColor(Color.RED);
		}

		// Now commit those lines to the view
		showWorkLayout.addView(adjustment);
		showWorkLayout.addView(sum);
		showWorkScroller.post(new Runnable() {
			public void run() {
				/*
				 * I tried a .fullScroll(ScrollView.FOCUS_DOWN), but this failed
				 * to scroll ALL the way. This solution provides an alternative.
				 */
				showWorkScroller.scrollTo(showWorkLayout.getMeasuredWidth(),
						showWorkLayout.getMeasuredHeight());
			}
		});
		tempHPUpdater();
	}

	/*
	 * Use the player's HP if an hpToList was not specified. Generally, this
	 * will be the case
	 */
	public void showWorkViewMaker(int value) {
		showWorkViewMaker(value, players.get(CURRENT_PLAYER).getHP());
	}

	// Controls adding temporary HP to player class
	public void tempHPUpdater() {
		tempHPUpdater(0); // Update, but do not change the value
	}

	public void tempHPUpdater(int value) {
		players.get(CURRENT_PLAYER).addTHP(value);
		currentTHPView.setTextColor(Color.GREEN);
		if (players.get(CURRENT_PLAYER).getTHP() > 0) {
			currentTHPView.setText("(" + players.get(CURRENT_PLAYER).getTHP() + ")");
		} else {
			currentTHPView.setText(BLANK);
		}
	}

	/*
	 * Controls adding and removing death saves to player and updating the
	 * button's text to reflect changes
	 */
	public void DSUpdater(String how) {
		if (how.equals(PLUS)) {
			players.get(CURRENT_PLAYER).addDeathSave();
		} else if (how.equals(MINUS)) {
			players.get(CURRENT_PLAYER).remDeathSave();
		}
		DSUpdater();
	}

	// Update the button's text without changing the stored number
	public void DSUpdater() {
		inputDS.setText(getResources().getString(R.string.ds) + ": "
				+ Integer.toString(players.get(CURRENT_PLAYER).getDeathSaves()));
	}

	/*
	 * Controls adding and removing healing surges to player and updating the
	 * button's text to reflect changes
	 */
	public void surgesUpdater(String how) {
		if (how.equals(PLUS)) {
			players.get(CURRENT_PLAYER).addSurge();
		} else if (how.equals(MINUS)) {
			players.get(CURRENT_PLAYER).remSurge();
		}
		surgesUpdater();
	}

	// Update the button's text without changing the stored number
	public void surgesUpdater() {
		inputSurges.setText(getResources().getString(R.string.surges) + ": "
				+ Integer.toString(players.get(CURRENT_PLAYER).getSurges()));
	}
	
	public void surgeValueUpdater() {
		inputHS.setText(getString(R.string.hs) + ": "
				+ Integer.toString(players.get(CURRENT_PLAYER).getHS()));
	}

	/*
	 * Controls adding and removing ongoing damage to the player and updating
	 * the button's text to reflect changes
	 */
	public void ongoUpdater(String how) {
		// Pick operation and adjust currentOngo number
		if (how.equals(PLUS)) {
			players.get(CURRENT_PLAYER).addOngo();
		} else if (how.equals(MINUS)) {
			players.get(CURRENT_PLAYER).remOngo();
		}
		ongoUpdater();
	}

	// Update the button's text without changing the stored values
	public void ongoUpdater() {
		String dotOrHot, valueToUse;
		// It's a regen if it is under 0, otherwise it is ongoing
		if (players.get(CURRENT_PLAYER).getOngo() < 0) {
			dotOrHot = getResources().getString(R.string.regen);
			valueToUse = Integer.toString(players.get(CURRENT_PLAYER).getRegen());
		} else {
			dotOrHot = getResources().getString(R.string.ongoing);
			valueToUse = Integer.toString(players.get(CURRENT_PLAYER).getOngo());
		}
		// Change the button text to reflect variable
		inputOngo.setText(dotOrHot + ": " + valueToUse);
	}

	/*
	 * Displays the numbers that represent the current entry based on key
	 * presses.
	 */
	public void currentEntryViewUpdater(int updateWith) {
		if (currentEntry * 10 > 999) {
			return;
		}
		// Max 3 digit number in currentEntry
		currentEntry = (currentEntry * 10) + updateWith;
		currentEntryView.setText(Integer.toString(currentEntry));
	}

	// Empties currentEntryView, such as when an operator is selected
	public void clearEntry() {
		currentEntry = 0;
		currentEntryView.setText(Integer.toString(currentEntry));
	}
	
	public void undoLastOp() {
		players.get(CURRENT_PLAYER).undoLast();
		relaunchWithPlayer(players.get(CURRENT_PLAYER));
	}
	
	// TODO: should database actions be in their own class?
	// Add the player to the database
	public void addPlayerToDB(Player player) {
		SQLiteDatabase db = playerData.getWritableDatabase();
		// First delete any same-named players from the database
		String sql = "DELETE FROM " + PlayerDataSQLHelper.TABLE + " WHERE "
				+ PlayerDataSQLHelper.COL_NAME + " = '" + player.getName()
				+ "';";
		db.execSQL(sql);
		ContentValues values = new ContentValues();
		values.put(PlayerDataSQLHelper.COL_NAME, player.getName());
		values.put(PlayerDataSQLHelper.COL_SERPLAYER,
				Player.serliazeObject(player));
		db.insert(PlayerDataSQLHelper.TABLE, null, values);
		Log.d("DNDHPActivity", "addPlayerToDB");
	}

	// Pull all the players out of the database
	public ArrayList<Player> getPlayersFromDB() {
		SQLiteDatabase db = playerData.getWritableDatabase();
		Cursor cursor = db.query(PlayerDataSQLHelper.TABLE, null, null, null,
				null, null, null);
		startManagingCursor(cursor);
		while (cursor.moveToNext()) {
			players = new ArrayList<Player>();
			players.add((Player) Player.deseriaizeObject(cursor.getBlob(2)));
		}
		Log.d("DNDHPActivity", "getPlayerFromDB");
		return players;
	}

	// Turns a pixel size (int) to a dpi size.
	public float dpi(int px) {
		return px * getResources().getDisplayMetrics().density + 0.5f;
	}
}