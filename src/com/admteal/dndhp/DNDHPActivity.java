//git repository: "git@github.com:codonnell/dnd-hp-tracker.git"
package com.admteal.dndhp;

import com.admteal.dndhp.R;
import android.app.Activity;
import android.view.View;
import android.widget.*;	
import android.os.Bundle;
		
public class DNDHPActivity extends Activity {
    public int currentHP, currentHS, currentOngo, currentSurgeCount, currentEntry, showWork = 0;
	public int currentSurges = 3;
	public int numStorer;
	
	//Create the calculator function buttons
	public Button inputAdd, inputSub, inputClear, inputHS;

	//Create the ongoing function buttons
	public Button ongoAdd, ongoSub, inputOngo;

	//Create the surges function buttons
	public Button surgesAdd, surgesSub, inputSurges;

	//Create the Death Saves function buttons
	public Button DSAdd, DSSub;
	
	//public Button inputDS	= (Button) findViewById(R.id.inputDS); //This button does nothing right now
	
	public TextView currentEntryView, currentHPView;
	public LinearLayout showWorkLayout;
	public ScrollView showWorkScroller;
	public int showWorkLineId = 1000; //set to an arbitrary large number so 
										//that they won't overlap with anything because 
										//I don't understand why it needs to be an int.
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	
        //Set the calculator number buttons and their onClickListeners
        for (int i = 0; i < 10; i++) {
        	String buttonID = "input" + Integer.toString(i);
        	int resourceID = getResources().getIdentifier(buttonID, "id", "com.admteal.dndhp");
            final Button b = (Button) findViewById(resourceID);
            final int j = i;
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
				showWorkUpdater("+");
			}
		});
    	inputSub	= (Button) findViewById(R.id.inputSub);
    	inputSub.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showWorkUpdater("-");
			}
		});
    	inputClear	= (Button) findViewById(R.id.inputClear);
    	inputClear.setOnLongClickListener(new View.OnLongClickListener() {			
			public boolean onLongClick(View v) {	
				clearEntry();
				showWorkLayout.removeAllViews();
				currentHP = 0;
				currentEntry = 0; 
				
				return true; //stops click event from also being processed
			}
		});
    	inputClear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {	
				clearEntry();
			}
		});
    	inputHS		= (Button) findViewById(R.id.inputHS);
    	// Long click to set currentEntry into Healing surge value, and display it on the button
    	inputHS.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				currentHS = currentEntry;
				inputHS.setText("HS: " + Integer.toString(currentEntry));
				return true;
			}
		});

    	//Create the ongoing function buttons
    	ongoAdd	= (Button) findViewById(R.id.ongoAdd);
    	ongoSub	= (Button) findViewById(R.id.ongoSub);
    	inputOngo	= (Button) findViewById(R.id.inputOngo);

    	//Create the surges function buttons
    	surgesAdd	= (Button) findViewById(R.id.surgesAdd);
    	surgesSub	= (Button) findViewById(R.id.surgesSub);
    	inputSurges = (Button) findViewById(R.id.inputSurges);

    	//Create the Death Saves function buttons
    	DSAdd		= (Button) findViewById(R.id.DSAdd);
    	DSSub		= (Button) findViewById(R.id.DSSub);
    	//final Button inputDS	= (Button) findViewById(R.id.inputDS); //This button does nothing right now
    	
    	currentEntryView= (TextView) findViewById(R.id.currentEntryView);
    	showWorkScroller= (ScrollView) findViewById(R.id.showWorkScroller);
    	showWorkLayout	= (LinearLayout) findViewById(R.id.showWorkLayout);
    	currentHPView	= (TextView) findViewById(R.id.currentHPView); 
    	
    	currentEntryView.setText(Integer.toString(currentEntry));
    	currentHPView.setText(Integer.toString(currentHP));
    }
    
    public void currentEntryViewUpdater(int updateWith) {
    	if (currentEntry * 10 > 999) {return;} //Max 3 digit number
    	currentEntry = currentEntry * 10 + updateWith;
    	currentEntryView.setText(Integer.toString(currentEntry));
    }
    
    public void showWorkUpdater(String how) {
    	//First line shows how much was added or subtracted as +n or -n
    	TextView adjustment = new TextView(this);    	
    	adjustment.setText(how + Integer.toString(currentEntry));
    	adjustment.setId(showWorkLineId);
    	showWorkLineId++;
    	//Second line shows the new total.  First we must pick our operation.
    	if (how == "+") {
    		currentHP += currentEntry;
    	} else if (how == "-") {
    		currentHP -= currentEntry;
    	}
    	TextView sum = new TextView(this);
    	sum.setText(Integer.toString(currentHP));
    	sum.setId(showWorkLineId);
    	showWorkLineId++;
    	showWorkLayout.addView(adjustment);
    	showWorkLayout.addView(sum);
    	clearEntry();
    }
    
    public void clearEntry() {
		currentEntry = 0;
		currentEntryView.setText(Integer.toString(currentEntry));
    }
}
