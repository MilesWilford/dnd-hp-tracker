//git repository: "git@github.com:codonnell/dnd-hp-tracker.git"
package com.admteal.dndhp;

import com.admteal.dndhp.R;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;	
import android.os.Bundle;
		
public class DNDHPActivity extends Activity {
	//Create the new default player
	public Player player = new Player();
	
	//Some variables for cleanliness's sake
	public final String PLUS = "+";
	public final String MINUS = "-";
	public final String BLANK = "";
	public final String COLON_SPACE = ": ";
	
	public int currentEntry;
	
	//Create the calculator function buttons
	public Button inputAdd, inputSub, inputClear, inputHS;

	//Create the ongoing function buttons
	public Button ongoAdd, ongoSub, inputOngo, inputDS;

	//Create the surges function buttons
	public Button surgesAdd, surgesSub, inputSurges;

	//Create the Death Saves function buttons
	public Button DSAdd, DSSub;
	
	//public Button inputDS	= (Button) findViewById(R.id.inputDS); //This button does nothing right now
	
	public TextView currentEntryView, currentHPView;
	public LinearLayout showWorkLayout;
	public ScrollView showWorkScroller;
	
    //Called when the activity is first created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
    	
        //Set the calculator number buttons and their onClickListeners.  Creates the 10 buttons dynamically
        for (int i = 0; i < 10; i++) {
        	String buttonID = "input" + Integer.toString(i);
        	int resourceID = getResources().getIdentifier(buttonID, "id", "com.admteal.dndhp");
            Button b = (Button) findViewById(resourceID);
            final int j = i; //allows passing i into a new View.OnClickListener's onClick
            b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(j);					
				}
			});
        }

    	//Create the calculator function buttons
    	inputAdd	= (Button) findViewById(R.id.inputAdd);
    	inputAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showWorkUpdater(currentEntry);
			}
		});
    	inputSub	= (Button) findViewById(R.id.inputSub);
    	inputSub.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showWorkUpdater(-currentEntry);
			}
		});
    	inputClear	= (Button) findViewById(R.id.inputClear);
    	inputClear.setOnLongClickListener(new View.OnLongClickListener() {			
			public boolean onLongClick(View v) {	
				clearEntry();
				showWorkLayout.removeAllViews();				
				return true; //stops click event from also being processed
			}
		});
    	inputClear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {	
				clearEntry();
			}
		});
    	inputHS		= (Button) findViewById(R.id.inputHS);
    	inputHS.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				showWorkUpdater(player.getHS());
			}
		});
    	// Long click to set currentEntry into Healing surge value, and display it on the button
    	inputHS.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				player.setHS(currentEntry);
				inputHS.setText(getResources().getString(R.string.hs) + COLON_SPACE 
						+ Integer.toString(currentEntry));
				clearEntry();
				return true;
			}
		});

    	//Create the ongoing function buttons
    	ongoAdd	= (Button) findViewById(R.id.ongoAdd);
    	ongoAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ongoUpdater(PLUS);
			}
		});
    	ongoSub	= (Button) findViewById(R.id.ongoSub);
    	ongoSub.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ongoUpdater(MINUS);
			}
		});
    	inputOngo	= (Button) findViewById(R.id.inputOngo);
    	inputOngo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showWorkUpdater(-player.getOngo()); //current ongo stores the number backwards, so it is inverted first
			}
		});

    	//Create the surges function buttons
    	surgesAdd	= (Button) findViewById(R.id.surgesAdd);
    	surgesAdd.setOnClickListener (new View.OnClickListener() {			
			public void onClick(View v) {
				surgesUpdater(PLUS);
			}
		});
    	surgesSub	= (Button) findViewById(R.id.surgesSub);
    	surgesSub.setOnClickListener (new View.OnClickListener() {			
			public void onClick(View v) {
				surgesUpdater(MINUS);
			}
		});
    	inputSurges = (Button) findViewById(R.id.inputSurges);
    	inputSurges.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (player.getSurges() == 0) {
					return;
				}
				showWorkUpdater(player.getHS());
				surgesUpdater(MINUS);
			}
		});

    	//Create the Death Saves function buttons
    	DSAdd		= (Button) findViewById(R.id.DSAdd);
    	DSAdd.setOnClickListener (new View.OnClickListener() {			
			public void onClick(View v) {
				DSUpdater(PLUS);
			}
		});
    	DSSub		= (Button) findViewById(R.id.DSSub);
    	DSSub.setOnClickListener (new View.OnClickListener() {			
			public void onClick(View v) {
				DSUpdater(MINUS);
			}
		});
    	
    	inputDS		= (Button) findViewById(R.id.inputDS);
    	
    	currentEntryView= (TextView) findViewById(R.id.currentEntryView);
    	showWorkScroller= (ScrollView) findViewById(R.id.showWorkScroller);
    	showWorkLayout	= (LinearLayout) findViewById(R.id.showWorkLayout);
    	currentHPView	= (TextView) findViewById(R.id.currentHPView); 
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    	currentEntryView.setText(Integer.toString(currentEntry));
    	currentHPView.setText(Integer.toString(player.getHP()));
    	inputSurges.setText(getResources().getString(R.string.surges) + COLON_SPACE 
    			+ Integer.toString(player.getSurges()));
    	ongoUpdater(BLANK);
    	DSUpdater(BLANK);
    	surgesUpdater(BLANK);
		inputHS.setText(getResources().getString(R.string.hs) + COLON_SPACE 
				+ Integer.toString(player.getHS()));
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putInt("currentEntry", currentEntry);
    	savedInstanceState.putSerializable("Player", player);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	currentEntry = savedInstanceState.getInt("currentEntry");
    	player = (Player) savedInstanceState.getSerializable("Player");
    }
    
    public void currentEntryViewUpdater(int updateWith) {
    	if (currentEntry * 10 > 999) {return;} //Max 3 digit number in currentEntry
    	currentEntry = (currentEntry * 10) + updateWith;
    	currentEntryView.setText(Integer.toString(currentEntry));
    }
    
    public void showWorkUpdater(int value) {
    	String operation = BLANK;
    	TextView adjustment = new TextView(this);   
    	TextView sum = new TextView(this); 	
    	//First we must pick our operation.
    	if (value > 0) {
    		operation = PLUS;
        	adjustment.setTextColor(Color.GREEN);
        	player.heal(value);
    	} else if (value < 0) {
        	adjustment.setTextColor(Color.RED);
        	player.injure(-value);
    	}
    	//First line shows how much was added or subtracted as +n or -n
    	adjustment.setText(operation + Integer.toString(value));
    		adjustment.setGravity(Gravity.RIGHT);
    		adjustment.setTextSize(14 * getResources().getDisplayMetrics().density + 0.5f); //14 px converted to 16 dip
    	//Second line shows new total number
    	sum.setText(Integer.toString(player.getHP()));
    		sum.setGravity(Gravity.LEFT);
    		sum.setTextSize(14 * getResources().getDisplayMetrics().density + 0.5f);  //14 px converted to 16 dip
    	//Now commit those lines to the view
    	showWorkLayout.addView(adjustment);
    	showWorkLayout.addView(sum);
    	clearEntry();
    	
    	currentHPView.setText(Integer.toString(player.getHP()));
    }
    
    public void DSUpdater(String how) {
    	if (how.equals(PLUS)) {
    		player.addDeathSave();
    	} else if (how.equals(MINUS)) {
    		player.remDeathSave();
    	}
    	inputDS.setText(getResources().getString(R.string.ds) + COLON_SPACE
    			+ Integer.toString(player.getDeathSaves()));
    }
    
    public void surgesUpdater(String how) {
    	if (how.equals(PLUS)) {
    		player.addSurge();
    	} else if (how.equals(MINUS)) {
    		player.remSurge();
    	}
    	inputSurges.setText(getResources().getString(R.string.surges) + COLON_SPACE 
    			+ Integer.toString(player.getSurges()));
    }
    
    public void ongoUpdater(String how) {
    	String dotOrHot, valueToUse;
    	//Pick operation and adjust currentOngo number
    	if (how.equals(PLUS)) {
    		player.addOngo();
    	} else if (how.equals(MINUS)) {
    		player.remOngo();
    	}
    	//It's a regen if it is under 0, otherwise it is ongoing
    	if (player.getOngo() < 0) {
    		dotOrHot = getResources().getString(R.string.regen);
    		valueToUse = Integer.toString(player.getRegen());
    	} else {
    		dotOrHot = getResources().getString(R.string.ongoing);
    		valueToUse = Integer.toString(player.getOngo());
    	}
    	//Change the button text to reflect variable
    	dotOrHot+= COLON_SPACE; //Add colon space to the end of our word
    	inputOngo.setText(dotOrHot + valueToUse);
    }
    
    public void clearEntry() {
		currentEntry = 0;
		currentEntryView.setText(Integer.toString(currentEntry));
    }
}
