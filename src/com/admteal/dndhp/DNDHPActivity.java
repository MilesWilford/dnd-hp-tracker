//git repository: "git@github.com:admiralteal/dnd-hp-tracker.git"
package com.admteal.dndhp;

import java.util.ArrayList;

import com.admteal.dndhp.R;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Bundle;

public class DNDHPActivity extends Activity {
	PlayerDataSQLHelper playerData;
	public ArrayList<Player> players = new ArrayList<Player>();

	// Some string literals we'll use for clarity's sake
	private static String PLUS, MINUS, INPUT;
	private static String BLANK = "";

	private final int DIALOG_EXTENDED_REST = 0;
	private final int DIALOG_NEW_CUSTOM_PLAYER = 1;

	private int currentEntry;

	// The various status toggles
	private ToggleButton toggleBlinded, toggleDazed, toggleDominated,
			toggleGrabbed, toggleMarked, toggleProne, toggleStunned,
			toggleWeakened;

	// This button has a long click for taking extended rests
	// TODO: long clicking this button is bad UX
	private Button inputClear;
	
	// This button has a long click for setting healing surge value
	// TODO: long clicking this button is bad UX
	private Button inputHS;

	// This button displays current ongoing/regen
	private Button inputOngo;
	
	// This button displays current death saves
	private Button inputDS;

	// This button displays current surges
	private Button inputSurges;

	// The views we'll be using for presentation
	private TextView currentEntryView, currentHPView, currentTHPView;
	private ScrollView showWorkScroller;
	private LinearLayout showWorkLayout;

	// Used in place of setting dozens of OnClickListeners
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.toggleBlinded:
			if (toggleBlinded.isChecked()) {
				players.get(0).blind();
			} else {
				players.get(0).unblind();
			}
			break;
		case R.id.toggleDazed:
			if (toggleDazed.isChecked()) {
				players.get(0).daze();
			} else {
				players.get(0).undaze();
			}
			break;
		case R.id.toggleDominated:
			if (toggleDominated.isChecked()) {
				players.get(0).dominate();
			} else {
				players.get(0).undominate();
			}
			break;
		case R.id.toggleGrabbed:
			if (toggleGrabbed.isChecked()) {
				players.get(0).grab();
			} else {
				players.get(0).ungrab();
			}
			break;
		case R.id.toggleMarked:
			if (toggleMarked.isChecked()) {
				players.get(0).mark();
			} else {
				players.get(0).unmark();
			}
			break;
		case R.id.toggleProne:
			if (toggleProne.isChecked()) {
				players.get(0).knockProne();
			} else {
				players.get(0).getUp();
			}
			break;
		case R.id.toggleStunned:
			if (toggleStunned.isChecked()) {
				players.get(0).stun();
			} else {
				players.get(0).unstun();
			}
			break;
		case R.id.toggleWeakened:
			if (toggleStunned.isChecked()) {
				players.get(0).weaken();
			} else {
				players.get(0).unweaken();
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
		// NOTE THAT A LONG CLICK ALSO EXISTS IN ONCREATE
		case R.id.inputClear:
			clearEntry();
			break;
		// NOTE THAT A LONG CLICK ALSO EXISTS IN ONCREATE
		case R.id.inputHS:
			showWorkUpdater(players.get(0).getHS());
			break;
		case R.id.ongoAdd:
			ongoUpdater(PLUS);
			break;
		case R.id.ongoSub:
			ongoUpdater(MINUS);
			break;
		case R.id.inputOngo:
			// Stores the value backwards, so invert
			showWorkUpdater(-players.get(0).getOngo());
			break;
		case R.id.surgesAdd:
			surgesUpdater(PLUS);
			break;
		case R.id.surgesSub:
			surgesUpdater(MINUS);
			break;
		case R.id.inputSurges:
			if (players.get(0).getSurges() == 0) {
				return;
			}
			showWorkUpdater(players.get(0).getHS());
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
		
		playerData = new PlayerDataSQLHelper(this);

		// Get the string literals used in this activity from strings.xml
		PLUS		= getString(R.string.plus);
		MINUS		= getString(R.string.minus);
		INPUT		= getString(R.string.input);

		// Make sure at least one player loaded to prevent any crashes
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

		// Long click for "C" clear button (extended rest)
		// TODO: eliminate this in favor of another button
		inputClear	= (Button) findViewById(R.id.inputClear);
		inputClear.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				showDialog(DIALOG_EXTENDED_REST);
				return true; // stops click event from also being processed
			}
		});
		
		// Long click for "HS" healing surge button (set healing surge)
		// TODO: eliminate this in favor of a separate button/UI
		inputHS = (Button) findViewById(R.id.inputHS);
		inputHS.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				players.get(0).setHS(currentEntry);
				inputHS.setText(getString(R.string.hs) + ": "
						+ Integer.toString(currentEntry));
				clearEntry();
				return true; // stops click event from also being processed
			}
		});

		// These buttons must be loaded so we can display stuff on the,
		inputOngo	= (Button) findViewById(R.id.inputOngo);
		inputSurges	= (Button) findViewById(R.id.inputSurges);
		inputDS		= (Button) findViewById(R.id.inputDS);

		// These views are all part of presentation
		currentEntryView	= (TextView) findViewById(R.id.currentEntryView);
		showWorkScroller	= (ScrollView) findViewById(R.id.showWorkScroller);
		showWorkLayout		= (LinearLayout) findViewById(R.id.showWorkLayout);
		currentHPView		= (TextView) findViewById(R.id.currentHPView);
		currentTHPView		= (TextView) findViewById(R.id.currentTHPView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		addPlayerToDB(players.get(0));
	}
	
	@Override 
	protected void onDestroy() {
		super.onDestroy();
		playerData.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		players = getPlayersFromDB();
		relaunchWithPlayer(players.get(0));
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
									players.get(0).extendedRest();
									relaunchWithPlayer(players.get(0));
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
											players.set(0, new Player(
													Integer.parseInt(customPlayerNewMaxHP),
													Integer.parseInt(customPlayerNewMaxSurges),
													Integer.parseInt(customPlayerNewHS)));
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.TOAST_madeFullHPPlayer),
													Toast.LENGTH_SHORT).show();
											relaunchWithPlayer(players.get(0));
										} else {
											players.set(0, new Player(
													Integer.parseInt(customPlayerNewMaxHP),
													Integer.parseInt(customPlayerNewCurrentHP),
													Integer.parseInt(customPlayerNewMaxSurges),
													Integer.parseInt(customPlayerNewCurrentSurges),
													Integer.parseInt(customPlayerNewHS)));
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.TOAST_madePartialHPPlayer),
													Toast.LENGTH_SHORT).show();
											relaunchWithPlayer(players.get(0));
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
		case R.id.newPlayer:
			relaunchWithPlayer(new Player());
			Toast.makeText(getApplicationContext(),
					getString(R.string.TOAST_madeDefaultPlayer),
					Toast.LENGTH_SHORT).show();
			return true;
		// When user clicks the Custom Player menu.actionbar button
		case R.id.newCustomPlayer:
			showDialog(DIALOG_NEW_CUSTOM_PLAYER);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * This method accepts a new Player class and uses it to set all the data
	 * for display in the app.
	 */
	public void relaunchWithPlayer(Player newPlayer) {
		players.set(0, newPlayer);
		addPlayerToDB(newPlayer);
		updateAll();
		/*
		 * Because of the way they're created, players.get(0).changeHistory and
		 * players.get(0).HPHistory are *always* the same length. First,
		 * removeAllViews just in case any were already here.
		 */
		showWorkLayout.removeAllViews();
		for (int i = 0; i < players.get(0).getChangeHistory().size(); i++) {
			showWorkViewMaker(players.get(0).getChangeHistory().get(i), players
					.get(0).getHPHistory().get(i));
		}
	}

	// This method updates all views
	public void updateAll() {
		currentHPView.setText(Integer.toString(players.get(0).getHP()));
		ongoUpdater();
		DSUpdater();
		togglesUpdater();
		tempHPUpdater();
		surgesUpdater();
	}

	public void togglesUpdater() {
		Player player = players.get(0);
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
			players.get(0).heal(value);
		} else if (value < 0) {
			players.get(0).injure(value);
		} else {
			clearEntry();
			return;
		}
		showWorkViewMaker(value);
		clearEntry();

		currentHPView.setText(Integer.toString(players.get(0).getHP()));
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

		if (hpToList == players.get(0).getMaxHP() && !players.get(0).isDefaultPlayer()) {
			sum.setTextColor(Color.GREEN);
		} else if (players.get(0).isBloodied() && !players.get(0).isDefaultPlayer()) {
			sum.setTextColor(Color.YELLOW);
		} else if (players.get(0).isDying()) {
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
		showWorkViewMaker(value, players.get(0).getHP());
	}

	// Controls adding temporary HP to player class
	public void tempHPUpdater() {
		tempHPUpdater(0); // Update, but do not change the value
	}

	public void tempHPUpdater(int value) {
		players.get(0).addTHP(value);
		currentTHPView.setTextColor(Color.GREEN);
		if (players.get(0).getTHP() > 0) {
			currentTHPView.setText("(" + players.get(0).getTHP() + ")");
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
			players.get(0).addDeathSave();
		} else if (how.equals(MINUS)) {
			players.get(0).remDeathSave();
		}
		DSUpdater();
	}

	// Update the button's text without changing the stored number
	public void DSUpdater() {
		inputDS.setText(getResources().getString(R.string.ds) + ": "
				+ Integer.toString(players.get(0).getDeathSaves()));
	}

	/*
	 * Controls adding and removing healing surges to player and updating the
	 * button's text to reflect changes
	 */
	public void surgesUpdater(String how) {
		if (how.equals(PLUS)) {
			players.get(0).addSurge();
		} else if (how.equals(MINUS)) {
			players.get(0).remSurge();
		}
		surgesUpdater();
	}

	// Update the button's text without changing the stored number
	public void surgesUpdater() {
		inputSurges.setText(getResources().getString(R.string.surges) + ": "
				+ Integer.toString(players.get(0).getSurges()));
	}

	/*
	 * Controls adding and removing ongoing damage to the player and updating
	 * the button's text to reflect changes
	 */
	public void ongoUpdater(String how) {
		// Pick operation and adjust currentOngo number
		if (how.equals(PLUS)) {
			players.get(0).addOngo();
		} else if (how.equals(MINUS)) {
			players.get(0).remOngo();
		}
		ongoUpdater();
	}

	// Update the button's text without changing the stored values
	public void ongoUpdater() {
		String dotOrHot, valueToUse;
		// It's a regen if it is under 0, otherwise it is ongoing
		if (players.get(0).getOngo() < 0) {
			dotOrHot = getResources().getString(R.string.regen);
			valueToUse = Integer.toString(players.get(0).getRegen());
		} else {
			dotOrHot = getResources().getString(R.string.ongoing);
			valueToUse = Integer.toString(players.get(0).getOngo());
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