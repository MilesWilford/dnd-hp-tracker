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
	
	//Create the calculator number buttons
	public Button[] inputNum = new Button[10];
	
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
	public ScrollView showWorkView;
	public int showWorkLineId = 1000; //set to an arbitrary large number so 
										//that they won't overlap with anything because 
										//I don't understand why it needs to be an int.
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	
        //Set the calculator number buttons and their onClickListeners
        /*for (int i = 0; i < 10; i++) {
        	String buttonID = "input" + Integer.toString(i);
        	int resourceID = getResources().getIdentifier(buttonID, "id", "com.admteal.dndhp");
        	numStorer = i;
        	inputNum[i] = (Button) findViewById(resourceID);
        	inputNum[i].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(numStorer);					
				}
			});
        } */
        	inputNum[0] = (Button) findViewById(R.id.input0);
        	inputNum[0].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(0);
				}
			});
        	inputNum[1] = (Button) findViewById(R.id.input1);
        	inputNum[1].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(1);
				}
			});
        	inputNum[2] = (Button) findViewById(R.id.input2);
        	inputNum[2].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(2);
				}
			});
        	inputNum[3] = (Button) findViewById(R.id.input3);
        	inputNum[3].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(3);
				}
			});
        	inputNum[4] = (Button) findViewById(R.id.input4);
        	inputNum[4].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(4);
				}
			});
        	inputNum[5] = (Button) findViewById(R.id.input5);
        	inputNum[5].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(5);
				}
			});
        	inputNum[6] = (Button) findViewById(R.id.input6);
        	inputNum[6].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(6);
				}
			});
        	inputNum[7] = (Button) findViewById(R.id.input7);
        	inputNum[7].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(7);
				}
			});
        	inputNum[8] = (Button) findViewById(R.id.input8);
        	inputNum[8].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(8);
				}
			});
        	inputNum[9] = (Button) findViewById(R.id.input9);
        	inputNum[9].setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					currentEntryViewUpdater(9);
				}
			});
        
    	//Create the calculator function buttons
    	inputAdd	= (Button) findViewById(R.id.inputAdd);
    	inputAdd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showWorkUpdater("add");
			}
		});
    	inputSub	= (Button) findViewById(R.id.inputSub);
    	inputClear	= (Button) findViewById(R.id.inputClear);
    	inputClear.setOnLongClickListener(new View.OnLongClickListener() {			
			public boolean onLongClick(View v) {
				currentEntry = 0;
				currentEntryView.setText(Integer.toString(currentEntry));
				showWorkView.removeAllViews();
				return true; //stops click event from also being processed
			}
		});
    	inputClear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				currentEntry = 0;
				currentEntryView.setText(Integer.toString(currentEntry));				
			}
		});
    	inputHS		= (Button) findViewById(R.id.inputHS);
    	inputHS.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				currentHS = currentEntry;
				inputHS.setText("HS: " + Integer.toString(currentEntry));
				currentEntry = 0;
				currentEntryView.setText(Integer.toString(currentEntry));
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
    	showWorkView	= (ScrollView) findViewById(R.id.showWorkView);
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
    	TextView view = new TextView(this);
    	view.setText(Integer.toString(currentEntry));
    	view.setId(showWorkLineId);
    	showWorkLineId++;
    	
    	showWorkView.addView(view);
    }
}