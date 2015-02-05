package net.khughes88.tapedeckfree;



import java.io.IOException;

import net.khughes88.tapedeckfree.R;


import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View.OnClickListener;

public class settings extends Activity  {
	

	public static final String PREF_FILE_NAME = "deckPrefs";
	int int_screenon;
	int int_shuffle;
	int int_switchtape;
	
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
     setContentView(R.layout.settings);
    
    loadprefs();
    
	}

	private void loadprefs() {
		SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
		
        int_screenon=prefs.getInt("OPT_SCREENON",0);
        CheckBox opt_screenon = (CheckBox) findViewById(R.id.opt_screenon);
        if (int_screenon==1){opt_screenon.setChecked(true);}else {opt_screenon.setChecked(false);}
        
        int_shuffle=prefs.getInt("PREFS_SHUFFLE",0);
        CheckBox opt_shuffle = (CheckBox) findViewById(R.id.opt_shuffle);
        if (int_shuffle==1){opt_shuffle.setChecked(true);}else {opt_shuffle.setChecked(false);}
        
        int_switchtape=prefs.getInt("PREFS_SWITCHTAPE",0);
        CheckBox opt_switchtape = (CheckBox) findViewById(R.id.opt_switchtape);
        if (int_switchtape==1){opt_switchtape.setChecked(true);}else {opt_switchtape.setChecked(false);}
	}

	@Override
	public void onPause() {
	super.onPause();
	saveprefs();
	}
	
	
	public void onStop() {
	super.onStop();
	saveprefs();
	}
	
	@Override
	public void onResume() {
	super.onResume();
	loadprefs();
	}
	
	
	private void saveprefs() {
		SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor spe = prefs.edit();
    	
        CheckBox opt_screenon = (CheckBox) findViewById(R.id.opt_screenon);
        int int_screenon;
		if (opt_screenon.isChecked()){int_screenon=1;}else {int_screenon=0;}
        spe.putInt("OPT_SCREENON", int_screenon); 
        
        CheckBox opt_shuffle = (CheckBox) findViewById(R.id.opt_shuffle);
        int int_shuffle;
		if (opt_shuffle.isChecked()){int_shuffle=1;tapedeckservice.shuffle=1;}else {int_shuffle=0;tapedeckservice.shuffle=0;}
        spe.putInt("PREFS_SHUFFLE", int_shuffle); 
        
        
        CheckBox opt_switchtape = (CheckBox) findViewById(R.id.opt_switchtape);
        int int_switchtape;
		if (opt_switchtape.isChecked()){int_switchtape=1;}else {int_switchtape=0;}
        spe.putInt("PREFS_SWITCHTAPE", int_switchtape); 
        
        
        spe.commit();
	}


	 public void clickHandler(View v) throws IllegalStateException, IOException{
	
		 if (v.getId() == R.id.opt_screenon) {
			 CheckBox opt_screenon = (CheckBox) findViewById(R.id.opt_screenon);
			 if (opt_screenon.isChecked()){
	        	int_screenon=1;}
	        else {int_screenon=0;}
		 }
		 if (v.getId() == R.id.opt_shuffle) {
			 CheckBox opt_shuffle = (CheckBox) findViewById(R.id.opt_shuffle);
			 if (opt_shuffle.isChecked()){
		        	int_shuffle=1;}
		        else {int_shuffle=0;}
		 } 
	
	 }
	 
	 
	 
}
	

	


	





