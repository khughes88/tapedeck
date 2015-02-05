package net.khughes88.tapedeckfree;

import java.io.IOException;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import java.util.Random;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class deck2 extends Activity implements OnGestureListener,
		AnimationListener, OnSeekBarChangeListener {

	private GestureDetector gDetector;
	public ImageView spinnerImage1;
	public ImageView spinnerImage2;
	public ImageView brownImage1;
	public ImageView brownImage2;
	public static ImageView tape;

	public static TextView label;
	public static ImageView blank;

	int trackingnow;

	public static Button play;
	public Button next;
	public Button prev;
	
	Animation spin1_anim;
	Animation spin2_anim;
	Animation rotatetext;
	static Animation quickanim;
	static Animation menuanim;
	static Animation menuback;
	boolean menu_up;
	int miniwheelleft;
	int miniwheeltop;
	int wheeldiam;
	int wheel1_center_x;
	int wheel2_center_x;
	int wheel1_center_y;
	int wheel2_center_y;

	int max_brown_diam;
	int min_brown_diam;
	int brown1_diam;
	int brown2_diam;
	int percent_complete; 
	int buttonheight1;
	int buttonwidth1;
	int buttonheight2;
	int buttonwidth2;
	Integer brown2_left;
	Integer brown2_top;
	Integer brown2_bottom;
	Integer brown2_right;
	Integer brown1_left;
	Integer brown1_top;
	Integer brown1_bottom;
	Integer brown1_right;
	static int int_screenon;
	static int int_switchtape;
	int layoutset = 0;
	int buttons_vib;
	Integer title_top;
	Integer title_right;
	Integer title_bottom;
	Integer title_left;
	Integer menubase;
	Vibrator myVib;
	static int currenttape = 1;
	static int intshuffle = 0;
	static int intrandtape = 0;
	static String warning_text = "";
	public static String storageState;
	public static final String PREF_FILE_NAME = "deckPrefs";
	boolean headsetevent1 = false;
	SeekBar trackbar;
	
	static Typeface alien;
	static Typeface amano;
	static Typeface messy;
	static Typeface cargo;
	static Typeface coffee;
	static Typeface vadem;
	static Typeface vanish;
	static Typeface viper;
	MediaPlayer mp;
	
	
	int nextx_in;
	int nexty;
	int prevx_in;
	int prevy;
	int nextx_out;
	int prevx_out;
	int svcretry;
	static Intent svc;
	static ServiceConnection mServerConn;
	static Double scr_width;
	static Double scr_height;

	@Override
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle Icicle) {

		
		super.onCreate(Icicle);
		//Log.w("tapedeck","begin");
		//mServerConn = new ServiceConnection() {
		//	@Override
		//	public void onServiceConnected(ComponentName name, IBinder service) {
		//		// TODO Auto-generated method stub
				
				//startService(svc);
		//	}
		//	@Override
		//	public void onServiceDisconnected(ComponentName name) {
		//		// TODO Auto-generated method stub
			
		//	} 
		//  }; 
		  
		  
		 // if(!pocket.isReady())
		 // {  svc = new Intent(this, pocket.class);
		//	bindService(svc, mServerConn, Context.BIND_AUTO_CREATE); 
		 // }	
		 // svcretry=0;
		 // while(!pocket.isReady() && svcretry<10){
		//	  try {
		//		Thread.sleep(1000);
		//		Log.w("tapedeck","waited");
		//		svcretry+=1;
		//	} catch (InterruptedException e) {
		//		// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}
		 // }
		  
		gDetector = new GestureDetector((OnGestureListener) this); 
		  
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		scr_height = (double) dm.heightPixels;
		scr_width = (double) dm.widthPixels;

	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.deck2);
		
		//set tape properties
		tape = (ImageView) findViewById(R.id.tape);
		int theight = (int) ((scr_width / 606) * 960);
		int twidth = (int) (scr_width * 1);
		int scrwidth=(int) (scr_width * 1);
		int scrheight=(int) (scr_height * 1);
		FrameLayout.LayoutParams tapeparams = new FrameLayout.LayoutParams(twidth, theight);
	    tape.setLayoutParams(tapeparams);
	    
	    //trackbar properties
	    trackbar = (SeekBar) findViewById(R.id.trackbar);
	   FrameLayout.LayoutParams tbparams = new FrameLayout.LayoutParams(twidth, 75);
	   tbparams.topMargin=scrheight-80;
	   trackbar.setLayoutParams(tbparams);
	    
	    
		//set label properties
	    label = (TextView) findViewById(R.id.label);
	    TextView topblock= (TextView) findViewById(R.id.topblock);
	    TextView bottomblock= (TextView) findViewById(R.id.bottomblock);
		LinearLayout labelholder=(LinearLayout) findViewById(R.id.labelholder);
		FrameLayout.LayoutParams labelholderparams = new FrameLayout.LayoutParams(scrheight, LayoutParams.FILL_PARENT);
		labelholder.setLayoutParams(labelholderparams);
		//the label holder is the portrait shape rotated 90 degrees
		//because the rotation happens around the center, the label position should be difference between w and h /2
		int gap=(int) ((scrheight)-(scrwidth));
		//the weight of the top block, above the label is calculated as a percentage of the height
		//leaving 3 % for the label
		float topblockweight=(float) (100/((float)scrheight/(float)gap))+6;
		//the bottom block is the remainder 
		float bottomblockweight=(float) (100-topblockweight)-10;
		Log.w("topblock",Float.toString(topblockweight));
		Log.w("bottomblock",Float.toString(bottomblockweight));
		Log.w("scrwidth",Integer.toString(scrwidth));
		Log.w("scrheight",Integer.toString(scrheight));
		
	    LinearLayout.LayoutParams topblockparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,topblockweight);
	    LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(scrheight, 22,(float) 3);
	    LinearLayout.LayoutParams bottomblockparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,bottomblockweight);
	    labelholder.setRotation(90);
	    int leftpad=(int) (scrheight*0.15);
	    int rightpad=(int) (scrheight*0.2);
	    labelholder.setPadding(leftpad, 0, rightpad, 0);
	    label.setLayoutParams(labelparams);
	    label.setLineSpacing(-5, 1);
	    label.setPadding(0, 3, 0, 0);
		topblock.setLayoutParams(topblockparams);
		bottomblock.setLayoutParams(bottomblockparams);
		
		//fonts
		amano = Typeface.createFromAsset(getAssets(), "Amano.ttf");
		alien = Typeface.createFromAsset(getAssets(), "Alien.ttf");
		messy = Typeface.createFromAsset(getAssets(), "messy.ttf");
		cargo = Typeface.createFromAsset(getAssets(), "cargo.ttf");
		coffee = Typeface.createFromAsset(getAssets(), "coffee.ttf");
		vadem = Typeface.createFromAsset(getAssets(), "vadem.ttf");
		vanish = Typeface.createFromAsset(getAssets(), "vanish.ttf");
		viper = Typeface.createFromAsset(getAssets(), "viper.ttf");
		
		
		//set wheel properties
		spinnerImage1 = (ImageView) findViewById(R.id.spinner1);
		spinnerImage2 = (ImageView) findViewById(R.id.spinner2);
		brownImage1 = (ImageView) findViewById(R.id.brown1);
		brownImage2 = (ImageView) findViewById(R.id.brown2);
		blank = (ImageView) findViewById(R.id.blank);
		
		wheeldiam = (int) (scr_width * 0.32);

		max_brown_diam = (int) (scr_width * 0.62);
		min_brown_diam = (int) (scr_width * 0.34);
		wheel1_center_y = (int) (scr_width * .46);
		wheel1_center_x = (int) (scr_width * .5375);
		wheel2_center_y = (int) (scr_width * 1.125);
		wheel2_center_x = (int) (scr_width * 0.5375);
		
		FrameLayout.LayoutParams spinner1params = new FrameLayout.LayoutParams(wheeldiam, wheeldiam);
		spinner1params.leftMargin=wheel1_center_x - (wheeldiam / 2);
		spinner1params.topMargin=wheel1_center_y - (wheeldiam / 2);
		spinnerImage1.setLayoutParams(spinner1params);
		FrameLayout.LayoutParams spinner2params = new FrameLayout.LayoutParams(wheeldiam, wheeldiam);
		spinner2params.leftMargin=wheel1_center_x - (wheeldiam / 2);
		spinner2params.topMargin=wheel2_center_y - (wheeldiam / 2);
		spinnerImage2.setLayoutParams(spinner2params);
		
		FrameLayout.LayoutParams brown1params = new FrameLayout.LayoutParams(min_brown_diam, min_brown_diam);
		brown1params.leftMargin=wheel1_center_x- (min_brown_diam / 2);
		brown1params.topMargin=wheel1_center_y- (min_brown_diam / 2);
		brownImage1.setLayoutParams(brown1params);
		FrameLayout.LayoutParams brown2params = new FrameLayout.LayoutParams(max_brown_diam, max_brown_diam);
		brown2params.leftMargin=wheel2_center_x- (max_brown_diam / 2);
		brown2params.topMargin=wheel2_center_y - (max_brown_diam / 2);
		brownImage2.setLayoutParams(brown2params);
	
		
		
		//set button properties

		
		
		prev = (Button) findViewById(R.id.prev);
		play = (Button) findViewById(R.id.play);
		next = (Button) findViewById(R.id.next);
		
		buttonwidth1 = (int) (scr_width * 0.15);
		buttonheight1 = (int) (scr_width * 0.15);
		buttonwidth2 = (int) (scr_width * 0.12);
		buttonheight2 = (int) (scr_width * 0.12);
		
		
		int playx = (int) (scr_height * 1) - buttonheight1 - 5;
		int playy = (int) (((scr_width / 5) * 3) - (scr_width / 5) + ((scr_width / 5) - buttonheight1) / 2);
		nextx_in = (int) (scr_height * 1) - buttonheight1 - 5;
		nextx_out = (int) (scr_height * 2) - buttonheight1 - 5;
		nexty = (int) (((scr_width / 5) * 2) - (scr_width / 5) + ((scr_width / 5) - buttonheight1) / 2);
		prevx_in = (int) (scr_height * 1) - buttonheight1 - 5;
		prevx_out = (int) (scr_height * 2) - buttonheight1 - 5;
		prevy = (int) (((scr_width / 5) * 4) - (scr_width / 5) + ((scr_width / 5) - buttonheight1) / 2);
		int swtapex = (int) (scr_height * 1) - buttonheight2;
		int swtapey = (int) (((scr_width / 5)) - (scr_width / 5) + ((scr_width / 5) - buttonheight2) / 2);
		
		
	
		FrameLayout.LayoutParams prevparams = new FrameLayout.LayoutParams(buttonwidth1, buttonheight1);
		prevparams.topMargin=(int) ((int) (theight/2-buttonwidth1/2)-buttonwidth1*.8);
		//prevparams.leftMargin= (int) ((int) scrwidth*.09);
		prev.setLayoutParams(prevparams);
		
		FrameLayout.LayoutParams playparams = new FrameLayout.LayoutParams(buttonwidth1, buttonheight1);
		playparams.topMargin=(int) (theight/2-buttonwidth1/2);
	
		play.setLayoutParams(playparams);
		
		FrameLayout.LayoutParams nextparams = new FrameLayout.LayoutParams(buttonwidth1, buttonheight1);
		nextparams.topMargin=(int) ((int) (theight/2-buttonwidth1/2)+buttonwidth1*.8);
		//nextparams.leftMargin= (int) ((int) scrwidth*.09);
		next.setLayoutParams(nextparams);
		
		
	
		

		spin1_anim = AnimationUtils.loadAnimation(this, R.anim.slow_spin_anim);
		spin1_anim.setAnimationListener(this);
		spin2_anim = AnimationUtils.loadAnimation(this, R.anim.fast_spin_anim);
		spin2_anim.setAnimationListener(this);
		quickanim = AnimationUtils.loadAnimation(this, R.anim.quick_anim);
		quickanim.setAnimationListener(this);

	
		initiate();
		if(int_screenon==1){getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}
		
		trackbar = (SeekBar) findViewById(R.id.trackbar);
		trackbar.setOnSeekBarChangeListener(this);
		
		
	}

	private void initiate() {
		setlabel();
		drawspinners();
		loadprefs();
	}

	public static void setlabel() {
		
		try{deck2.label.setText(tapedeckservice.getTitle());}catch(Exception e){deck2.label.setText("");}
		
		}
	
		


	public void saveprefs() {
		SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor spe = prefs.edit();
		spe.putInt("PREFS_TAPE", currenttape);
		spe.commit();
	}

	public void loadprefs() {
		SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME,
				MODE_PRIVATE);
		deck2.currenttape = prefs.getInt("PREFS_TAPE", 0);
		if (currenttape == 0) {
			currenttape = 1;
		}
		deck2.int_screenon = prefs.getInt("OPT_SCREENON", 0);
		switchtape(currenttape);
		
		deck2.int_switchtape=prefs.getInt("PREFS_SWITCHTAPE", 0);
			
		
		
	}

	
	
	static void drawspinners() {

		blank.startAnimation(quickanim);

	}

	public static boolean checkSD() {

		storageState = (Environment.getExternalStorageState());

		if (storageState == null) {
			storageState = "";
		}

		if (storageState.contains("mounted")) {
			warning_text = "";

			return true;

		} else {
			warning_text = "SD Card Unavailable";

			return false;
		}

	}

	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
	return gDetector.onTouchEvent(me);
	}
	
	public boolean onFling(MotionEvent start, MotionEvent finish, float xVelocity, float yVelocity) {
	        //swipe up
			if (start.getRawY() > finish.getRawY()+200) {
	        	if (currenttape==16){
					switchtape(1);
				}
				else {
				switchtape(currenttape+1);	
			}  
	        //swipe down
	        } else if (start.getRawY() < finish.getRawY()-200) {
	        	if (currenttape==1){
					switchtape(16);
				}
				else {
				switchtape(currenttape-1);	
			} 
	        	} 
			//swipe over to menu
	        else if (start.getRawX() < finish.getRawX()-50) {
	        	startActivity(new Intent(getApplicationContext(), MusicDroid.class));	
	        	overridePendingTransition(R.anim.right_out, R.anim.left_out);
	        }
	        return true;
	}

	
	
	

	@Override
	public void onPause() {
		super.onPause();
		
		saveprefs();
		//tape.destroyDrawingCache();
		//tape.setImageResource(0);
		System.gc();

	}

	@Override
	public void onStop() {
		super.onPause();

		saveprefs();
		tape.destroyDrawingCache();
		tape.setImageResource(0);
		System.gc();

	}

	@Override
	public void onResume() {
		super.onResume();
		initiate();
		loadprefs();
		

	}


	@Override
	@SuppressWarnings("deprecation")
	public void onAnimationEnd(Animation arg0) {
		setlabel();
		Double dur = getDuration();
		Double pos = getPosition();
		percent_complete=(int) ((100/dur)*pos);
		
		if (arg0.equals(quickanim) || arg0.equals(spin1_anim) || arg0.equals(spin2_anim)) {
			
			//deck2.label.setText(dur.toString() +"-"+ pos.toString());
			// the bit of tape around the wheel divided by the song, multiplied
			// by how much has passed - will increase
			
			brown2_diam = (int) (min_brown_diam + (((double) max_brown_diam - (double) min_brown_diam)
					/ (dur) * pos));
			brown1_diam = (int) (min_brown_diam
					+ ((double) max_brown_diam - min_brown_diam) - (((double) max_brown_diam - (double) min_brown_diam)
					/ (dur) * pos));

			
			FrameLayout.LayoutParams brown1params = new FrameLayout.LayoutParams(brown1_diam, brown1_diam);
			brownImage1.setLayoutParams(brown1params);
			brown1params.leftMargin=wheel1_center_x- (brown1_diam / 2);
			brown1params.topMargin=wheel1_center_y- (brown1_diam / 2);
			FrameLayout.LayoutParams brown2params = new FrameLayout.LayoutParams(brown2_diam, brown2_diam);
			brownImage2.setLayoutParams(brown2params);
			brown2params.leftMargin=wheel2_center_x- (brown2_diam / 2);
			brown2params.topMargin=wheel2_center_y - (brown2_diam / 2);
			
			
			Double tbprog = (100 / dur) * (pos);
			Integer prog = tbprog.intValue();
			if (trackingnow == 0) {
				trackbar.setProgress(prog);
			}
		}
		if(percent_complete>50){
			spin1_anim.setDuration(2500);
			spin2_anim.setDuration(4000);
		}
		else
		{
			spin1_anim.setDuration(4000);
			spin2_anim.setDuration(2500);
		}
			
		if (arg0.equals(spin1_anim) || arg0.equals(quickanim)) {
		spinnerImage1.startAnimation(spin1_anim);}
		if (arg0.equals(spin2_anim) || arg0.equals(quickanim)) {
		spinnerImage2.startAnimation(spin2_anim);}
		
		if ((isPlaying() == false)) {

			play.setBackgroundResource(R.drawable.button_play);
			spinnerImage1.clearAnimation();
			spinnerImage2.clearAnimation();
		} else {
			play.setBackgroundResource(R.drawable.button_pause);
		}
		
		
		if(percent_complete>99){
			if (int_switchtape==1){
				if (currenttape==16){
					switchtape(1);
				}
				else {
				switchtape(currenttape+1);	
			}
			}
		}
		
		
	}

	private boolean isPlaying() {
		//if (pocket.isReady()==false){
				//bindService(svc, mServerConn, Context.BIND_AUTO_CREATE); 
			return tapedeckservice.isPlaying();
		//} else {
		//	return false;	
		//}
	}

	private Double getPosition() {
		if (tapedeckservice.isReady() && tapedeckservice.mp!=null) {
				return (double) tapedeckservice.mp.getCurrentPosition();
			} else {
				return (double) 0;
			}
	}

	private Double getDuration() {
		if (tapedeckservice.isReady() && tapedeckservice.mp!=null) {
			
				return (double) tapedeckservice.mp.getDuration();
			}
			
			else 
			{
				return (double) 1;
			}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	public void onBackPressed() {
		if (buttons_vib == 1) {
			myVib.vibrate(50);
		}
		startActivity(new Intent(getApplicationContext(), MusicDroid.class));

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {

			startActivity(new Intent(this, settings.class));
			//startActivity(new Intent(getApplicationContext(),
				//	MusicDroid.class));
			//if (menu_up == false) {
				// panelx=(int)(((scr_width/10)*9)-buttonheight1);

				//next.setLayoutParams(new AbsoluteLayout.LayoutParams(
				//		buttonwidth1, buttonheight1, nextx_out, nexty));
		//		prev.setLayoutParams(new AbsoluteLayout.LayoutParams(
		//				buttonwidth1, buttonheight1, prevx_out, prevy));
				//.setLayoutParams(new AbsoluteLayout.LayoutParams(
				//		buttonwidth1, buttonheight1, nextx_in, nexty));
		//		settings.setLayoutParams(new AbsoluteLayout.LayoutParams(
		//				buttonwidth1, buttonheight1, prevx_in, prevy));

				//menu_up = true;

			//}

			//else {

			//	next.setLayoutParams(new AbsoluteLayout.LayoutParams(
			//			buttonwidth1, buttonheight1, nextx_in, nexty));
		//		prev.setLayoutParams(new AbsoluteLayout.LayoutParams(
		//				buttonwidth1, buttonheight1, prevx_in, prevy));
			//	exit.setLayoutParams(new AbsoluteLayout.LayoutParams(
			//			buttonwidth1, buttonheight1, nextx_out, nexty));
		//		settings.setLayoutParams(new AbsoluteLayout.LayoutParams(
		//				buttonwidth1, buttonheight1, prevx_out, prevy));
			//	menu_up = false;
			//}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void clickHandler(View v) throws IllegalStateException, IOException {
		
		if (v.getId() == R.id.prev) {
			if (tapedeckservice.isReady()) {
				if (tapedeckservice.svc_nowplaying.size() > 0) {
					if (tapedeckservice.isPlaying())
					{
						tapedeckservice.previousSong();
						tapedeckservice.mp.start();
						}
					else
					{
						tapedeckservice.previousSong();
					}
					setlabel();
				}
			}
		}
		if (v.getId() == R.id.play) {
			if (tapedeckservice.isReady()) {
				if (tapedeckservice.svc_nowplaying.size() > 0) {
				tapedeckservice.playpause();
				drawspinners();
				
				}
			}
		}
		if (v.getId() == R.id.next) {
			if (tapedeckservice.isReady()) {
				
				if (tapedeckservice.svc_nowplaying.size() > 0) {
					if (tapedeckservice.isPlaying())
					{
						tapedeckservice.nextSong();
						tapedeckservice.mp.start();
						}
					else
					{
						tapedeckservice.nextSong();
						
					}
					setlabel();
					if (int_switchtape==1)
					{if (currenttape==16){switchtape(1);}
						else {switchtape(currenttape+1);}
					}
			}
			}
		}
		}// if next

		
	

	//
	//

	private void stop() {
		// TODO Auto-generated method stub

	}

	static void switchtape(Integer newtape) {
		try {
			tape.destroyDrawingCache();
			tape.setImageResource(0);
			System.gc();
			switch (newtape) {
			case 1:
				tape.setImageResource(R.drawable.tape1);//big orange area
				label.setTextSize(23);
				label.setTextColor(R.color.blue);
				label.setTypeface(amano);
				
				
				currenttape = 1;
				break;
			case 2:
				tape.setImageResource(R.drawable.tape2);//android
				label.setTextSize(23);
				label.setTextColor(R.color.blue);
				label.setTypeface(amano);
				
				currenttape = 2;
				break;

				
			case 3:
				tape.setImageResource(R.drawable.tape3);//grey maxell
				label.setTextSize(23);
				label.setTextColor(R.color.blue);
				label.setTypeface(amano);
				
				
			
				currenttape = 3;
				break;
			case 4:
				tape.setImageResource(R.drawable.tape4);//leather
				label.setTextSize(20);
				label.setTextColor(R.color.black);
				label.setTypeface(alien);
				
	
				currenttape = 4;
				break;
			case 5:
				tape.setImageResource(R.drawable.tape5);
				label.setTextSize(16);
				label.setTextColor(R.color.blue);
				label.setTypeface(messy);
				
	
				currenttape = 5;
				break;
			case 6:
				tape.setImageResource(R.drawable.tape6);
				label.setTextSize(15);
				label.setTextColor(R.color.black);
				label.setTypeface(viper);
				

				currenttape = 6;
				break;
			case 7:
				tape.setImageResource(R.drawable.tape7);
				label.setTextSize(16);
				label.setTextColor(R.color.black);
				label.setTypeface(messy);
				
				
				label.setLineSpacing(-7, 1);
				currenttape = 7;
				break;
			case 8:
				tape.setImageResource(R.drawable.tape8);//pink one
				label.setTextSize(20);
				label.setTextColor(R.color.blue);
				label.setTypeface(amano);
				
				label.setLineSpacing(-17, 1);
				currenttape = 8;
				break;
			case 9:
				tape.setImageResource(R.drawable.tape9);//tdk
				label.setTextSize(22);
				label.setTextColor(R.color.blue);
				label.setTypeface(amano);
	
				currenttape = 9;
				break;
			case 10:
				tape.setImageResource(R.drawable.tape10);
				label.setTextSize(20);
				label.setTextColor(R.color.blue);
				label.setTypeface(amano);
				
			
				currenttape = 10;
				break;
			case 11:
				tape.setImageResource(R.drawable.tape11);
				label.setTextSize(17);
				label.setTextColor(R.color.blue);
				label.setTypeface(messy);
				
				label.setLineSpacing(-7, 1);
				currenttape = 11;
				break;
			case 12:
				tape.setImageResource(R.drawable.tape12);
				label.setTextSize(17);
				label.setTextColor(R.color.blue);
				label.setTypeface(messy);
				
				label.setLineSpacing(-7, 1);
				currenttape = 12;
				break;
			case 13:
				tape.setImageResource(R.drawable.tape13);
				label.setTextSize(20);
				label.setTextColor(R.color.black);
				label.setTypeface(amano);
				
				
				currenttape = 13;
				break;
			case 14:
				tape.setImageResource(R.drawable.tape14);
				label.setTextSize(20);
				label.setTextColor(R.color.blue);
				label.setTypeface(messy);
				
				label.setLineSpacing(-10, 1);
				currenttape = 14;
				break;
			case 15:
				tape.setImageResource(R.drawable.tape15);
				label.setTextSize(20);
				label.setTextColor(R.color.blue);
				label.setTypeface(amano);
				
				
				currenttape = 15;
				break;
			case 16:
				tape.setImageResource(R.drawable.tape16);
				label.setTextSize(20);
				label.setTextColor(R.color.red);
				label.setTypeface(amano);
				
				
				currenttape = 16;
				break;
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		trackingnow = 1;
		// TODO Auto-generated method stub
		if (isPlaying()) {
			mp = MediaPlayer.create(getBaseContext(), R.raw.scrobble);
			try {
				mp.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mp.seekTo(1);
			mp.setLooping(true);
			mp.start();
			spin1_anim.setDuration(400);
			spin2_anim.setDuration(400);
			spinnerImage1.startAnimation(spin1_anim);
			spinnerImage2.startAnimation(spin2_anim);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		trackingnow = 0;
		if (isPlaying()) {
			mp.pause();
			mp.release();
			mp=null;
			spin1_anim.setDuration(4000);
			spin2_anim.setDuration(4000);
			spinnerImage1.startAnimation(spin1_anim);
			spinnerImage2.startAnimation(spin2_anim);
		}
		Integer progpercent = trackbar.getProgress();
		try{tapedeckservice.trackbar(progpercent);}catch(Exception e){}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
