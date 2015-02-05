package net.khughes88.tapedeckfree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.khughes88.tapedeckfree.R;

import android.R.integer;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class tapedeckservice extends Service {
	static MediaPlayer mp;
	BroadcastsHandler receiver;
	TelephonyManager mgr;
	static int trackError=0;
	static ArrayList<HashMap<String, String>> svc_nowplaying = new ArrayList<HashMap<String, String>>();
	static int svc_current_position;
	static Notification notification;
	static NotificationManager mManager;
	public static final String PREF_FILE_NAME = "pocketPrefs";
	PhoneStateListener phoneStateListener;
	
	SharedPreferences prefs;
	Notification notice;
	static int shuffle=0;
	static int switchtape=0;
	public void onCreate() {
		
		phoneStateListener = new PhoneStateListener() {
		    @Override
		    public void onCallStateChanged(int state, String incomingNumber) {
		        if (state == TelephonyManager.CALL_STATE_RINGING) {
		        	try{
		        		if(mp.isPlaying()){
		        			mp.pause();
		        			
		        		}
		        		
		        		}catch(Exception e){}
		        } else if(state == TelephonyManager.CALL_STATE_IDLE) {
		        	//mp.start();
		        } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
		        	//mp.pause();
		        }
		        super.onCallStateChanged(state, incomingNumber);
		    }
		};
		
		mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if(mgr != null) {
		    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		}
		//Toast.makeText(pocket.this, "service oncreate", Toast.LENGTH_SHORT).show();	
		SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME,
				MODE_PRIVATE);
		shuffle=prefs.getInt("PREFS_SHUFFLE", 0);
		mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification.Builder builder = new Notification.Builder(this);

		Intent intent = new Intent(this, deck2.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent.getActivity(this, 0, intent, 0);

		builder.setContentIntent(pendIntent)
	    .setSmallIcon(R.drawable.icon)
	    .setLargeIcon(null)
	    .setTicker(null)
	    .setOnlyAlertOnce(true)                 
	    .setWhen(System.currentTimeMillis())
	    .setContentTitle("Retro Tape Deck")
	    .setOngoing(true)
	    .setContentText("Click to open");
		
		notice=builder.getNotification();
		//This constructor is deprecated. Use Notification.Builder instead
		//notice = new Notification(R.drawable.icon1, "Retro Tape Deck", System.currentTimeMillis());

		//This method is deprecated. Use Notification.Builder instead.
		//notice.setLatestEventInfo(this, "Retro Tape Deck", "Click to open", pendIntent);

		//notice.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(8888, notice);
		
		
		//notification = new Notification(R.drawable.icon, "",
		//		System.currentTimeMillis());
		//notification.setLatestEventInfo(pocket.this, "Retro Tape Deck", "Playing - click to open",
		//		PendingIntent.getActivity(this.getBaseContext(), 0, intent,
		//				PendingIntent.FLAG_CANCEL_CURRENT));
		//notification.flags = Notification.FLAG_ONGOING_EVENT;
		//Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
		//mManager.notify(8888, notification);
		//this.startForeground(8888, notification);
		receiver=new BroadcastsHandler();
		
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_HEADSET_PLUG));
		
	}

	
	public static boolean load(ArrayList<HashMap<String,String>> nowplaying,int position) throws IllegalArgumentException, IllegalStateException, IOException{
		
		svc_nowplaying=nowplaying;
		svc_current_position=position;
		if (mp==null){
		mp = new MediaPlayer();
		
		mp.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer arg0) {
				if (svc_nowplaying.size() > 0) {
				try{nextSong();
				tapedeckservice.mp.start();
				}catch(Exception e){}
				} 
			}
		});
		
		}
		
		loadNewTrack();
		return true;
		// Setup listener so next song starts automatically
		
	}
	
	
	static String getTitle() {
		
		String Artist= svc_nowplaying.get(svc_current_position).get("line2").toString();
	   	String Song =  svc_nowplaying.get(svc_current_position).get("line1").toString();
		return Artist +"-"+ Song;
	
	}


	public static void trackbar(Integer progpercent) {

		Integer dur = mp.getDuration();
		mp.seekTo((dur / 100) * progpercent);

	}


	public static void loadNewTrack() throws IllegalArgumentException, IllegalStateException, IOException{
	try{	
		mp.reset();
		mp.setDataSource(svc_nowplaying.get(svc_current_position).get("line4").toString());
		mp.prepare();
		//if(isPlaying()){mp.start();}
		trackError=0;
	}catch(Exception e){
		if (trackError<5) {
		trackError+=1;
		nextSong();
		}
	}
		
		
	}
	
	public static void playpause() {
		try{
		if(mp.isPlaying()){
			mp.pause();
			
		}
		else
		{
			mp.start();
			

			}
		}catch(Exception e){}
	
	}

	
	
	public static void nextSong() throws IllegalArgumentException, IllegalStateException, IOException {
		if (shuffle==1) {
			Random randomGenerator = new Random();
			Integer rand = randomGenerator.nextInt(svc_nowplaying.size());
			svc_current_position=rand;
		}	
		else{	
			if(svc_current_position==svc_nowplaying.size()-1)	{
				svc_current_position=0;	
			}
			else
			{
				svc_current_position+=1;
			}
		}
		
		
		loadNewTrack();
	}
	
	public static void previousSong() throws IllegalArgumentException, IllegalStateException, IOException {
		if(svc_current_position==0)	{
			svc_current_position=svc_nowplaying.size()-1;	
		}
		else
		{
			svc_current_position=svc_current_position-1;
		}
		
		loadNewTrack();
	}
	

	
	@Override
	public void onDestroy(){
		
		//Toast.makeText(pocket.this, "stopping", Toast.LENGTH_SHORT).show();
		stopForeground(true);
		mManager.cancelAll();
		
		this.stopForeground(true);
		
		unregisterReceiver(receiver);
		SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor spe = prefs.edit();

		spe.putInt("PREFS_SHUFFLE", shuffle);
		spe.commit();
		
		mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if(mgr != null) {
		    mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		
		
		super.onDestroy();
	}

	public class LocalBinder extends Binder {
		tapedeckservice getService() {
			return tapedeckservice.this;
		}
	}

	
	
	private final IBinder binder = new LocalBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}


	public class BroadcastsHandler extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equalsIgnoreCase(Intent.ACTION_HEADSET_PLUG))
			{
				if (intent.hasExtra("state")){
					if (intent.getIntExtra("state", 0) == 1){
						
					}
					else
					{
					if(isPlaying()){
					Toast.makeText(tapedeckservice.this, "headphone unplugged", Toast.LENGTH_SHORT).show();
					playpause();		
					}
					}
				}
			}
			//if (headsetConnected && intent.getIntExtra("state", 0) == 0){ }

			// if (headsetevent1==true){
			// if (MusicDroid.mp.isPlaying()){
			// MusicDroid.mp.pause();
			// }
			// }
			// headsetevent1=true;
			// }
			// }
			// else
			// {
			//setlabel();
			// }
			// }
			// else if (!headsetConnected && intent.getIntExtra("state", 0) ==
			// 1){
			// headsetConnected = true;

			// }
		} // hasstate

	}// on receive

	
	
	
	
	
	public static boolean isReady(){
		
		return true;
		
	}
	
	
	public static int shuffle(){
		return shuffle;
	}
	
	
	public static boolean isPlaying() {
		if(mp!=null){
		if(mp.isPlaying()){
			return true;
		}
		else
		{
		return false;
		}
		}
		else
		{
		return false;
		}
	}

	
	
}