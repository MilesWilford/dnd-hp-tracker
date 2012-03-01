//git repository: "git@github.com:codonnell/dnd-hp-tracker.git"
package com.admteal.dndhp;

import java.util.ArrayList;

import com.admteal.dndhp.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
	public ArrayList<Player> player = new ArrayList<Player>();

	// Some variables for cleanliness's sake
	private static String PLUS, MINUS, INPUT, CURRENT_HP;
	private static String CURRENT_ENTRY_STRING = "currentEntry";
	private static String PLAYER_STRING = "player";
	private static String BLANK = "";

	private final int DIALOG_CLEAR = 0;
	private final int DIALOG_NEW_CUSTOM_PLAYER = 1;

	public int currentEntry;

	// Initialize the status toggles
	public ToggleButton toggleBlinded, toggleDazed, toggleDominated,
			toggleGrabbed, toggleMarked, toggleProne, toggleStunned,
			toggleWeakened;

	// Initialize the calculator function buttons
	public Button inputTHP, inputAdd, inputSub, inputClear, inputHS;

	// Initialize the ongoing function buttons
	public Button ongoAdd, ongoSub, inputOngo, inputDS;

	// Initialize the surges function buttons
	public Button surgesAdd, surgesSub, inputSurges;

	// Initialize the Death Saves function buttons
	public Button DSAdd, DSSub;

	// Initialize our important views
	public TextView currentEntryView, currentHPView, currentTHPView;
	public ScrollView showWorkScroller;
	public LinearLayout showWorkLayout;

	// Triggered in the XML in place of dozens of onClickListeners
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.toggleBlinded:
			if (toggleBlinded.isChecked()) {
				player.get(0).blind();
			} else {
				player.get(0).unblind();
			}
			break;
		case R.id.toggleDazed:
			if (toggleDazed.isChecked()) {
				player.get(0).daze();
			} else {
				player.get(0).undaze();
			}
			break;
		case R.id.toggleDominated:
			if (toggleDominated.isChecked()) {
				player.get(0).dominate();
			} else {
				player.get(0).undominate();
			}
			break;
		case R.id.toggleGrabbed:
			if (toggleGrabbed.isChecked()) {
				player.get(0).grab();
			} else {
				player.get(0).ungrab();
			}
			break;
		case R.id.toggleMarked:
			if (toggleMarked.isChecked()) {
				player.get(0).mark();
			} else {
				player.get(0).unmark();
			}
			break;
		case R.id.toggleProne:
			if (toggleProne.isChecked()) {
				player.get(0).knockProne();
			} else {
				player.get(0).getUp();
			}
			break;
		case R.id.toggleStunned:
			if (toggleStunned.isChecked()) {
				player.get(0).stun();
			} else {
				player.get(0).unstun();
			}
			break;
		case R.id.toggleWeakened:
			if (toggleStunned.isChecked()) {
				player.get(0).weaken();
			} else {
				player.get(0).unweaken();
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
			showWorkUpdater(player.get(0).getHS());
			break;
		case R.id.ongoAdd:
			ongoUpdater(PLUS);
			break;
		case R.id.ongoSub:
			ongoUpdater(MINUS);
			break;
		case R.id.inputOngo:
			// Stores the value backwards, so invert
			showWorkUpdater(-player.get(0).getOngo());
			break;
		case R.id.surgesAdd:
			surgesUpdater(PLUS);
			break;
		case R.id.surgesSub:
			surgesUpdater(MINUS);
			break;
		case R.id.inputSurges:
			if (player.get(0).getSurges() == 0) {
				return;
			}
			showWorkUpdater(player.get(0).getHS());
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

	// Called when the activity is first created.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Let's get the string literals used in this activity from strings.xml
		PLUS = getString(R.string.plus);
		MINUS = getString(R.string.minus);
		INPUT = getString(R.string.input);
		CURRENT_HP = getString(R.string.CURRENT_HP);

		// Create the new default player if there isn't one already
		if (savedInstanceState == null) {
			player.add(new Player());
		}

		// Set the toggle buttons to java elements and give them their
		// OnClickListeners
		toggleBlinded	= (ToggleButton) findViewById(R.id.toggleBlinded);
		toggleDazed		= (ToggleButton) findViewById(R.id.toggleDazed);
		toggleDominated	= (ToggleButton) findViewById(R.id.toggleDominated);
		toggleGrabbed	= (ToggleButton) findViewById(R.id.toggleGrabbed);
		toggleMarked	= (ToggleButton) findViewById(R.id.toggleMarked);
		toggleProne		= (ToggleButton) findViewById(R.id.toggleProne);
		toggleStunned	= (ToggleButton) findViewById(R.id.toggleStunned);
		toggleWeakened	= (ToggleButton) findViewById(R.id.toggleWeakened);

		/*
		 * Set the calculator number buttons and their OnClickListeners. Creates
		 * the 10 buttons at once
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

		inputClear	= (Button) findViewById(R.id.inputClear);
		inputClear.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				showDialog(DIALOG_CLEAR);
				return true; // stops click event from also being processed
			}
		});
		inputHS = (Button) findViewById(R.id.inputHS);
		/*
		 * Long click to set currentEntry into Healing surge value, and display
		 * it on the button
		 */
		inputHS.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				player.get(0).setHS(currentEntry);
				inputHS.setText(getString(R.string.hs) + ": "
						+ Integer.toString(currentEntry));
				clearEntry();
				return true; // stops click event from also being processed
			}
		});

		inputOngo	= (Button) findViewById(R.id.inputOngo);
		inputSurges	= (Button) findViewById(R.id.inputSurges);
		inputDS		= (Button) findViewById(R.id.inputDS);

		// A few other Views we need for displaying information
		currentEntryView = (TextView) findViewById(R.id.currentEntryView);
		showWorkScroller = (ScrollView) findViewById(R.id.showWorkScroller);
		showWorkLayout = (LinearLayout) findViewById(R.id.showWorkLayout);
		currentHPView = (TextView) findViewById(R.id.currentHPView);
		currentTHPView = (TextView) findViewById(R.id.currentTHPView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		relaunchWithPlayer(player.get(0));
		currentEntryView.setText(Integer.toString(currentEntry));
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putSerializable(PLAYER_STRING, player.get(0));
		savedInstanceState.putInt(CURRENT_ENTRY_STRING, currentEntry); 
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		player.add((Player) savedInstanceState.getSerializable(PLAYER_STRING));
		currentEntry = savedInstanceState.getInt(CURRENT_ENTRY_STRING);
	}

	// Generate my alert dialogs
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		// DIALOG_CLEAR comes from long-pressing the C(lear) button
		case DIALOG_CLEAR:
			return new AlertDialog.Builder(this)
					.setMessage(R.string.DIALOG_CLEAR_msg)
					.setCancelable(true)
					.setPositiveButton(getString(R.string.yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									clearEntry();
									player.get(0).extendedRest();
									relaunchWithPlayer(player.get(0));
								}
							}).setNegativeButton(getString(R.string.no), null)
					.create();
			// DIALOG_NEW_CUSTOM_PLAYER comes from pressing the Custom Player menu/actionbar button
		case DIALOG_NEW_CUSTOM_PLAYER:
			// See dialog_custom_player.get(0).xml
			/*
			 * TODO: Clean up this code. Not sure the best way to do this. Might
			 * actually better to make this dialog its own class, for several
			 * reasons
			 */
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
											player.set(0, new Player(
													Integer.parseInt(customPlayerNewMaxHP),
													Integer.parseInt(customPlayerNewMaxSurges),
													Integer.parseInt(customPlayerNewHS)));
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.TOAST_madeFullHPPlayer),
													Toast.LENGTH_SHORT).show();
											relaunchWithPlayer(player.get(0));
										} else {
											player.set(0, new Player(
													Integer.parseInt(customPlayerNewMaxHP),
													Integer.parseInt(customPlayerNewCurrentHP),
													Integer.parseInt(customPlayerNewMaxSurges),
													Integer.parseInt(customPlayerNewCurrentSurges),
													Integer.parseInt(customPlayerNewHS)));
											Toast.makeText(
													getApplicationContext(),
													getString(R.string.TOAST_madePartialHPPlayer),
													Toast.LENGTH_SHORT).show();
											relaunchWithPlayer(player.get(0));
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
		player.set(0, newPlayer);
		updateAll();
		/*
		 * Because of the way they're created, player.get(0).changeHistory and
		 * player.get(0).HPHistory are *always* the same length. First,
		 * removeAllViews just in case any were already here.
		 */
		showWorkLayout.removeAllViews();
		for (int i = 0; i < player.get(0).getChangeHistory().size(); i++) {
			showWorkViewMaker(player.get(0).getChangeHistory().get(i), player
					.get(0).getHPHistory().get(i));
		}
	}

	// This method updates all fields, such as when re-launching with a new player
	public void updateAll() {
		currentHPView.setText(Integer.toString(player.get(0).getHP()));
		ongoUpdater();
		DSUpdater();
		surgesUpdater();
		togglesUpdater();
		tempHPUpdater();

	}

	// Sets all toggle buttons to the states from the Player class
	public void togglesUpdater() {
		toggleBlinded.setChecked(player.get(0).isBlinded());
		toggleDazed.setChecked(player.get(0).isDazed());
		toggleDominated.setChecked(player.get(0).isDominated());
		toggleGrabbed.setChecked(player.get(0).isGrabbed());
		toggleMarked.setChecked(player.get(0).isMarked());
		toggleProne.setChecked(player.get(0).isProne());
		toggleStunned.setChecked(player.get(0).isStunned());
		toggleWeakened.setChecked(player.get(0).isWeakened());
	}

	/*
	 * Method injures or heals the player based on input, then calls for the
	 * view maker to show work
	 */
	public void showWorkUpdater(int value) {
		// First we must pick our operation.
		if (value > 0) {
			player.get(0).heal(value);
		} else if (value < 0) {
			player.get(0).injure(value);
		} else {
			clearEntry();
			return;
		}
		showWorkViewMaker(value);
		clearEntry();

		currentHPView.setText(Integer.toString(player.get(0).getHP()));
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
			/*
			 * if we do not clear operation at this point, an extra "-" will
			 * appear
			 */
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

		if (hpToList == player.get(0).getMaxHP() && !player.get(0).isDefaultPlayer()) {
			sum.setTextColor(Color.GREEN);
		} else if (player.get(0).isBloodied() && !player.get(0).isDefaultPlayer()) {
			sum.setTextColor(Color.YELLOW);
		} else if (player.get(0).isDying()) {
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
		showWorkViewMaker(value, player.get(0).getHP());
	}

	// Controls adding temporary HP to player class
	public void tempHPUpdater(int value) {
		/*
		 *  No need to check for negatives since app doesn't 
		 *  allow negative numbers
		 */
		player.get(0).addTHP(value);
		currentTHPView.setTextColor(Color.GREEN);
		if (player.get(0).getTHP() > 0) {
			currentTHPView.setText("(" + player.get(0).getTHP() + ")");
		} else {
			currentTHPView.setText(BLANK);
		}
	}

	public void tempHPUpdater() {
		tempHPUpdater(0); // Update, but do not change the value
	}

	/*
	 * Controls adding and removing death saves to player and updating the
	 * button's text to reflect changes
	 */
	public void DSUpdater(String how) {
		if (how.equals(PLUS)) {
			player.get(0).addDeathSave();
		} else if (how.equals(MINUS)) {
			player.get(0).remDeathSave();
		}
		DSUpdater();
	}

	// Update the button's text without changing the stored number
	public void DSUpdater() {
		inputDS.setText(getResources().getString(R.string.ds) + ": "
				+ Integer.toString(player.get(0).getDeathSaves()));
	}

	/*
	 * Controls adding and removing healing surges to player and updating the
	 * button's text to reflect changes
	 */
	public void surgesUpdater(String how) {
		if (how.equals(PLUS)) {
			player.get(0).addSurge();
		} else if (how.equals(MINUS)) {
			player.get(0).remSurge();
		}
		surgesUpdater();
	}

	// Update the button's text without changing the stored number
	public void surgesUpdater() {
		inputSurges.setText(getResources().getString(R.string.surges) + ": "
				+ Integer.toString(player.get(0).getSurges()));
	}

	/*
	 * Controls adding and removing ongoing damage to the player and updating
	 * the button's text to reflect changes
	 */
	public void ongoUpdater(String how) {
		// Pick operation and adjust currentOngo number
		if (how.equals(PLUS)) {
			player.get(0).addOngo();
		} else if (how.equals(MINUS)) {
			player.get(0).remOngo();
		}
		ongoUpdater();
	}

	// Update the button's text without changing the stored values
	public void ongoUpdater() {
		String dotOrHot, valueToUse;
		// It's a regen if it is under 0, otherwise it is ongoing
		if (player.get(0).getOngo() < 0) {
			dotOrHot = getResources().getString(R.string.regen);
			valueToUse = Integer.toString(player.get(0).getRegen());
		} else {
			dotOrHot = getResources().getString(R.string.ongoing);
			valueToUse = Integer.toString(player.get(0).getOngo());
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

	// Turns a pixel size (int) to a dpi size.
	public float dpi(int px) {
		return px * getResources().getDisplayMetrics().density + 0.5f;
	}
}