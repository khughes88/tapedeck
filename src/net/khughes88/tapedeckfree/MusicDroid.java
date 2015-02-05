package net.khughes88.tapedeckfree;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MusicDroid extends ListActivity {
	int svcretry;
	public String currentArtist="All";
	public String currentAlbum="All";
	String playingArtist;
	String playingSong;
	String playingPath;
	String currentOrder;
	Intent svc;
	SimpleAdapter sng_notes;
	SimpleAdapter now_playing;
	SimpleAdapter alb_notes;
	SimpleAdapter pl_notes;
	ArrayAdapter art_notes;
	List<String> artistlist = new ArrayList<String>();
	List<String> albumlist = new ArrayList<String>();
	List<String> artistlist_unique = new ArrayList<String>();
	static ArrayList<HashMap<String, String>> songhashlist = new ArrayList<HashMap<String, String>>();
	static ArrayList<HashMap<String, String>> nowplaying = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> albumhashlist = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> plhashlist = new ArrayList<HashMap<String, String>>();
	public static String searchLevel;
	public String storageState;
	static int currentPosition;
	static int serviceConnected=0;
	static Integer randtape = 0;
	public static final String PREF_FILE_NAME = "deckPrefs";
	SharedPreferences prefs;
	
	ServiceConnection mServerConn;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		setContentView(R.layout.tracklist);
		
	
		
		TextView notice = (TextView) findViewById(R.id.empty);
		Button player = (Button) findViewById(R.id.player);
		Button playlists = (Button) findViewById(R.id.lists);
		Button albums = (Button) findViewById(R.id.albums);
		Button artists = (Button) findViewById(R.id.artists);
		Button songs = (Button) findViewById(R.id.songs);
		
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		double scr_height = (double) dm.heightPixels;
		double scr_width = (double) dm.widthPixels;
		int scrwidth=(int) (scr_width * 1);
		int scrheight=(int) (scr_height * 1);
		
		int fontsize=scrwidth/65;
		playlists.setTextSize(fontsize);
		player.setTextSize(fontsize);
		albums.setTextSize(fontsize);
		artists.setTextSize(fontsize);
		songs.setTextSize(fontsize);
	
		
		mServerConn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
			
				
			}
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
					} 
		  }; 
		  
		  
		  
		  svc = new Intent(this, tapedeckservice.class);
		  startService(svc);
		  
		  
		  svcretry=0;
		  while(!tapedeckservice.isReady() && svcretry<10){
			  try {
				Thread.sleep(1000);
				Log.w("tapedeck","waited");
				svcretry+=1;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		
		resumetask();
		 
	}
	
	private void resumetask() {
		
		// TODO Auto-generated method stub
		loadprefs();	
		if (checkSD() == true) {
			if (currentAlbum=="All"&&currentArtist!="All"){
				ListArtistsSongs(currentArtist);
			}
			else{
				ListSongs(currentArtist,currentAlbum);}	
	}
		}// checksd 
	

	private boolean checkSD() {
		TextView notice = (TextView) findViewById(R.id.empty);
		storageState = (Environment.getExternalStorageState());
		if (storageState == null) {
			storageState = "";
		}

		if (storageState.contains("mounted")) {
			// notice.setText("");
			return true;

		} else {
			notice.setText("SD Card Unavailable");
			return false;
		}

	}

	public void saveprefs() {
		prefs = getSharedPreferences(PREF_FILE_NAME,
				MODE_PRIVATE);
		SharedPreferences.Editor spe = prefs.edit();
		spe.putString("PREFS_ART", currentArtist);
		spe.putString("PREFS_ALB", currentAlbum);
		spe.commit();
	}

	public void loadprefs() {
		prefs = getSharedPreferences(PREF_FILE_NAME,
				MODE_PRIVATE);
		currentArtist=prefs.getString("PREFS_ART", null);
		currentAlbum=prefs.getString("PREFS_ALB", null);
		
		
		if(currentArtist==null){currentArtist="All";}
		if(currentArtist==null){currentAlbum="All";}
		
	}
	

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void ListAllArtists() {
		
		try {

			searchLevel = "artists";
			TextView notice = (TextView) findViewById(R.id.empty);
			artistlist.clear();
			artistlist_unique.clear();
			String[] proj = { MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DATA,MediaStore.Audio.Artists.ARTIST };
			Cursor tempCursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proj, null,null, null);
			tempCursor.moveToFirst(); // reset the cursor
			int col_index_artist = -1;
			int currentNum = 0;
			col_index_artist = tempCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
			do {
				String artist = tempCursor.getString(col_index_artist);
				artistlist.add(artist);
				currentNum += 1;
			} while (tempCursor.moveToNext());

			HashSet<String> hashSet = new HashSet<String>(artistlist);
			artistlist_unique = new ArrayList<String>(hashSet);
			try {
				Collections.sort(artistlist_unique);
			} catch (Exception e) {
			}
			;
			art_notes = new ArrayAdapter(this, R.layout.artist_item,artistlist_unique);
			setListAdapter(art_notes);
			Integer numArtists = artistlist_unique.size();
			notice.setText("Artists :" + numArtists.toString());

		} catch (Exception e) {
			TextView notice = (TextView) findViewById(R.id.empty);
			notice.setText("Error reading Music from SD card");
		}
	}

	public void ListAlbums(String sArt) {
		
		try {

			if (sArt.equals("All")) {
				searchLevel = "allalbums";
			} else {
				searchLevel = "albums";
			}

			TextView notice = (TextView) findViewById(R.id.empty);
			String sArtist;
			String sAlbum;
			String sNum;
			String sAlbart;

			alb_notes = new SimpleAdapter(this, albumhashlist,
					R.layout.album_item, new String[] { "line1", "line2",
							"line4" }, new int[] { R.id.text1, R.id.text2,
							R.id.albicon });

			albumhashlist.clear();

			String[] proj = { MediaStore.Audio.Albums._ID,
					MediaStore.Audio.Albums.ALBUM,
					MediaStore.Audio.Albums.ARTIST,
					MediaStore.Audio.Albums.NUMBER_OF_SONGS,
					MediaStore.Audio.Albums.ALBUM_ART };
			Cursor tempCursor = managedQuery(
					MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, proj, null,
					null, "ALBUM");

			tempCursor.moveToFirst(); // reset the cursor
			int col_index_artist = -1;
			int col_index_nos = -1;
			int col_index_album = -1;
			int col_index_art = -1;
			Integer currentNum = 0;

			col_index_artist = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
			col_index_album = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
			col_index_nos = tempCursor
					.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
			col_index_art = tempCursor
					.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
			do {

				sArtist = tempCursor.getString(col_index_artist);
				sNum = tempCursor.getString(col_index_nos);
				sAlbum = tempCursor.getString(col_index_album);
				sAlbart = tempCursor.getString(col_index_art);
				if (sAlbum == null) {
					sAlbum = "Unknown";
				}
				if (sArtist == null) {
					sArtist = "Unknown";
				}
				
				
				
				if (sArt.equals("All") || sArt.contains(sArtist)) {

					HashMap<String, String> item = new HashMap<String, String>();
					item.put("line1", sAlbum);
					item.put("line2", sArtist);
					// item.put("line3",sNum+" tracks");
					item.put("line4", sAlbart);
					albumhashlist.add(item);
					alb_notes.notifyDataSetChanged();
				}

				currentNum += 1;
			} while (tempCursor.moveToNext());
			Integer numAlbs = albumhashlist.size();
			if(numAlbs==0){
				ListArtistsSongs(sArt);
			}
			else
			{
			setListAdapter(alb_notes);
			if (currentArtist.equals("All")){
				notice.setText("Albums(" + albumhashlist.size()+")");
			}
			else{
				notice.setText(sArt);
			}
			}
		} catch (Exception e) {
			TextView notice = (TextView) findViewById(R.id.empty);
			notice.setText("Error reading Music from SD card");
		}

	}

	public void ListPlayLists() {
		
		try {

			searchLevel = "playlists";

			String sPl;
			Integer sPid;
			pl_notes = new SimpleAdapter(this, plhashlist, R.layout.album_item,
					new String[] { "line1" }, new int[] { R.id.text1 });
			plhashlist.clear();

			String[] proj = { MediaStore.Audio.Playlists._ID,
					MediaStore.Audio.Playlists.NAME

			};
			Cursor tempCursor = managedQuery(
					MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, proj,
					null, null, null);

			tempCursor.moveToFirst(); // reset the cursor

			int col_index_pl = -1;
			int col_index_pid = -1;
			Integer currentNum = 0;

			col_index_pl = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);
			col_index_pid = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID);
			do {

				sPl = tempCursor.getString(col_index_pl);
				sPid = tempCursor.getInt(0);

				HashMap<String, String> item = new HashMap<String, String>();
				item.put("line1", sPl);
				item.put("line2", sPid.toString());

				plhashlist.add(item);
				pl_notes.notifyDataSetChanged();

				currentNum += 1;
			} while (tempCursor.moveToNext());

			setListAdapter(pl_notes);
			TextView notice = (TextView) findViewById(R.id.empty);
			Integer numPls = plhashlist.size();
			notice.setText("Playlists : " + numPls.toString());

		} catch (Exception e) {
			TextView notice = (TextView) findViewById(R.id.empty);
			notice.setText("Error reading Music from SD card");
		}

	}

	public void ListSongs(String sArt,String sAlb) {
		
		//try {
			if (sArt.equals("All")) {
				searchLevel = "allsongs";	
			}
			 else {
				searchLevel = "songs";
			}

			String artist;
			String fName;
			String sTitle;
			Integer isMusic;
			String sDur;
			Integer Duration;
			String sId;
			String sAlbum;
			SimpleDateFormat df = new SimpleDateFormat("mm':'ss");
			sng_notes = new SimpleAdapter(this, songhashlist,
					R.layout.song_item, new String[] { "line1", "line2",
							"line3" }, new int[] { R.id.text1, R.id.text2,
							R.id.text3 });
			songhashlist.clear();

			String[] proj = { MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media.DISPLAY_NAME,
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.IS_MUSIC,
					MediaStore.Audio.Media.DURATION,
					MediaStore.Audio.Media.ALBUM, 
					MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Artists.ARTIST };
			Cursor tempCursor;
			if (searchLevel == "allsongs") {
				tempCursor = managedQuery(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj,
						null, null, "TITLE");
			} else {
				tempCursor = managedQuery(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj,
						null, null, null);
			}

			tempCursor.moveToFirst(); // reset the cursor
			int col_index_artist = -1;
			int col_index_trackname = -1;
			int col_index_filename = -1;
			int col_index_issong = -1;
			int col_index_dur = -1;
			int col_index_id = -1;
			int col_index_album = -1;
			Integer currentNum = 0;
			col_index_dur = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

			col_index_artist = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
			col_index_filename = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
			col_index_trackname = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
			col_index_album = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
			col_index_issong = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC);
			col_index_id = tempCursor
					.getColumnIndex(MediaStore.Audio.Media.DATA);
			do {

				artist = tempCursor.getString(col_index_artist);
				fName = tempCursor.getString(col_index_filename);
				sTitle = tempCursor.getString(col_index_trackname);
				isMusic = tempCursor.getInt(col_index_issong);
				sDur = tempCursor.getString(col_index_dur);
				if (sDur!=null){
				Duration = Integer.parseInt(sDur);
				}
				else
				{
				Duration = 0;
				}
				sId = tempCursor.getString(col_index_id);
				sAlbum = tempCursor.getString(col_index_album);

				if (isMusic == 1) {

					HashMap<String, String> item = new HashMap<String, String>();
					if (sArt.equals("All") || sArt.contains(artist)) {
						if (sArt.equals("All") || sAlb.contains(sAlbum)) {
							item.put("line1", sTitle);
							item.put("line2", artist);
							item.put("line3", df.format(new Date(Duration)));
							item.put("line4", sId);
							songhashlist.add(item);
							sng_notes.notifyDataSetChanged();
						}
					}

				}
				currentNum += 1;
			} while (tempCursor.moveToNext());

			// .sort(sng_notes);
			setListAdapter(sng_notes);
			TextView notice = (TextView) findViewById(R.id.empty);
			if (sArt.equals("All")) {
				notice.setText("All Songs (" + songhashlist.size()+")");
			}
			 else {
				 if(sAlb.equals("All")){
				 notice.setText(sArt+" "+ songhashlist.size()+" songs");
				 } else {
					 notice.setText(sAlb+" "+ songhashlist.size()+" songs"); 
				 }
			}
			

		//} catch (Exception e) {
		//	TextView notice = (TextView) findViewById(R.id.empty);
		//	notice.setText("Error reading Music from SD card");
		//}

	}

	
	public void ListArtistsSongs(String sArt) {
		//try {
			searchLevel = "artistssongs";
			currentAlbum = "";
			String artist;
			String fName;
			String sTitle;
			Integer isMusic;
			String sDur;
			Integer Duration;
			String sId;
			String sAlbum;
			SimpleDateFormat df = new SimpleDateFormat("mm':'ss");
			sng_notes = new SimpleAdapter(this, songhashlist,
					R.layout.song_item, new String[] { "line1", "line2",
							"line3" }, new int[] { R.id.text1, R.id.text2,
							R.id.text3 });
			songhashlist.clear();

			String[] proj = { MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media.DISPLAY_NAME,
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.IS_MUSIC,
					MediaStore.Audio.Media.DURATION,
					MediaStore.Audio.Media.ALBUM, 
					MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Artists.ARTIST };
			Cursor tempCursor;
			if (searchLevel == "allsongs") {
				tempCursor = managedQuery(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj,
						null, null, "TITLE");
			} else {
				tempCursor = managedQuery(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj,
						null, null, null);
			}

			tempCursor.moveToFirst(); // reset the cursor
			int col_index_artist = -1;
			int col_index_trackname = -1;
			int col_index_filename = -1;
			int col_index_issong = -1;
			int col_index_dur = -1;
			int col_index_id = -1;
			int col_index_album = -1;
			Integer currentNum = 0;
			col_index_dur = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

			col_index_artist = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
			col_index_filename = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
			col_index_trackname = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
			col_index_album = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
			col_index_issong = tempCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC);
			col_index_id = tempCursor
					.getColumnIndex(MediaStore.Audio.Media.DATA);
			do {

				artist = tempCursor.getString(col_index_artist);
				fName = tempCursor.getString(col_index_filename);
				sTitle = tempCursor.getString(col_index_trackname);
				isMusic = tempCursor.getInt(col_index_issong);
				sDur = tempCursor.getString(col_index_dur);
				if (sDur!=null){
				Duration = Integer.parseInt(sDur);
				}
				else
				{
				Duration = 0;
				}
				sId = tempCursor.getString(col_index_id);
				sAlbum = tempCursor.getString(col_index_album);

				if (isMusic == 1) {

					HashMap<String, String> item = new HashMap<String, String>();
					if (sArt.contains(artist)) {
						
							item.put("line1", sTitle);
							item.put("line2", artist);
							item.put("line3", df.format(new Date(Duration)));
							item.put("line4", sId);
							songhashlist.add(item);
							sng_notes.notifyDataSetChanged();
						
					}

				}
				currentNum += 1;
			} while (tempCursor.moveToNext());

			// .sort(sng_notes);
			setListAdapter(sng_notes);
			TextView notice = (TextView) findViewById(R.id.empty);

			notice.setText(sArt+ " " + songhashlist.size()+" songs");

		//} catch (Exception e) {
		//	TextView notice = (TextView) findViewById(R.id.empty);
		//	notice.setText("Error reading Music from SD card");
		//}

	}
	
	
	
	
	public void ListPlSongs(String sPname, Integer sPid) {
		try {
			searchLevel = "plsongs";

			TextView notice = (TextView) findViewById(R.id.empty);
			songhashlist.clear();
		
		
			sng_notes = new SimpleAdapter(this, songhashlist,
					R.layout.song_item, new String[] {"line1", "line2",
							"line3" }, new int[] { R.id.text1, R.id.text2,
							R.id.text3 });

			String[] proj = { MediaStore.Audio.Playlists.Members._ID,
					MediaStore.Audio.Playlists.Members.TITLE,
					MediaStore.Audio.Playlists.Members.ARTIST,
					MediaStore.Audio.Playlists.Members.DATA, };
			Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri(
					"external", (long) sPid);
			Cursor membersCursor = managedQuery(membersUri, proj, null, null,
					null);
			Integer col_index_data = membersCursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			membersCursor.moveToFirst();
			for (int s = 0; s < membersCursor.getCount(); s++, membersCursor
					.moveToNext()) {
				HashMap<String, String> item = new HashMap<String, String>();
				item.put("line1", membersCursor.getString(membersCursor
						.getColumnIndex("title")));
				item.put("line2", membersCursor.getString(membersCursor
						.getColumnIndex("artist")));
				String dir = String.valueOf(membersCursor
						.getString(col_index_data));
				item.put("line4", dir);
				songhashlist.add(item);
				sng_notes.notifyDataSetChanged();

			}
			membersCursor.close();

			setListAdapter(sng_notes);

			notice.setText(sPname);

		} catch (Exception e) {
			TextView notice = (TextView) findViewById(R.id.empty);
			notice.setText("Error reading Music from SD card");
		}

	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		//TextView notice = (TextView) findViewById(R.id.empty);
		//notice.setText(searchLevel);
		//if(pocket.isReady()){
		if (searchLevel.equals("artists")) {
			String sArtist = artistlist_unique.get(position).toString(); 
			ListAlbums(sArtist);
		}
		
		else if (searchLevel.equals("artistssongs") ){
			 currentAlbum = "All";
				currentArtist = songhashlist.get(position).get("line2").toString();
				
				try {
					
					nowplaying.clear();
					//if (songhashlist.size() == 1) {
					//	nowplaying.add(songhashlist.get(0));
					//} else {
						for (int s = 0; s < songhashlist.size(); s++) {
							nowplaying.add(songhashlist.get(s));
						}
						//songhashlist.clear();
						
						
						boolean go=tapedeckservice.load(nowplaying,position);
						tapedeckservice.mp.start();
						
						
						
					
				} catch (Exception e) {}
				startActivity(new Intent(getApplicationContext(), deck2.class));
				overridePendingTransition(R.anim.right_in, R.anim.left_in);
				
		}
		 else if (searchLevel.equals("songs") || searchLevel.equals("allsongs")
				|| searchLevel.equals("plsongs")  ){
			 
			 
			try {
				
				nowplaying.clear();
				//if (songhashlist.size() == 1) {
				//	nowplaying.add(songhashlist.get(0));
				//} else {
					for (int s = 0; s < songhashlist.size(); s++) {
						nowplaying.add(songhashlist.get(s));
					}
					//songhashlist.clear();
					
					
					boolean go=tapedeckservice.load(nowplaying,position);
					tapedeckservice.mp.start();
					
					
					
				
			} catch (Exception e) {
				
				
			}
			startActivity(new Intent(getApplicationContext(), deck2.class));
			overridePendingTransition(R.anim.right_in, R.anim.left_in);

		} else if (searchLevel == "albums" || searchLevel == "allalbums") {
			currentArtist = albumhashlist.get(position).get("line2").toString();
			currentAlbum = albumhashlist.get(position).get("line1").toString();

		ListSongs(currentArtist,currentAlbum);
		} else if (searchLevel == "playlists") {
			Integer sPid = Integer.parseInt(plhashlist.get(position).get(
					"line2"));
			String sPname = plhashlist.get(position).get("line1");
			ListPlSongs(sPname, sPid);
		}
		}


	
	public void onStop() {
		super.onStop();
		saveprefs();
		
		//getListView().setVisibility(View.GONE);
	}
	
	
	public void onPause() {
		super.onPause();
		
		saveprefs();
		
	}

	public void onResume() {
		super.onResume();
		
		
		resumetask();
		
		//getListView().setVisibility(View.VISIBLE);
	}// resume

	public void clickHandler(View v) {

		if (v.getId() == R.id.songs) {
		
			if (checkSD() == true) {
				try {
					ListSongs("All","All");
				} catch (Exception e) {
					startActivity(new Intent(getApplicationContext(),
							MusicDroid.class));
				}
			}
		}
		if (v.getId() == R.id.artists) {
			if (checkSD() == true) {
				try {
					ListAllArtists();
				} catch (Exception e) {
					startActivity(new Intent(getApplicationContext(),
							MusicDroid.class));
				}
			}
		}
		if (v.getId() == R.id.albums) {
			if (checkSD() == true) {
				try {
					currentArtist="All";
					ListAlbums("All");
				} catch (Exception e) {
					startActivity(new Intent(getApplicationContext(),
							MusicDroid.class));
				}
			}
		}
		if (v.getId() == R.id.lists) {
			if (checkSD() == true) {
				try {
					ListPlayLists();
				} catch (Exception e) {
					startActivity(new Intent(getApplicationContext(),
							MusicDroid.class));
				}
			}
		}
		if (v.getId() == R.id.player) {
			try {
				startActivity(new Intent(getApplicationContext(), deck2.class));
				overridePendingTransition(R.anim.right_in, R.anim.left_in);
			} catch (Exception e) {
				startActivity(new Intent(getApplicationContext(),
						MusicDroid.class));
			}
		}
	}

	public void onBackPressed() {
		if (searchLevel == "songs") {
			if (alb_notes != null) {
				alb_notes = new SimpleAdapter(this, albumhashlist,
						R.layout.album_item, new String[] { "line1", "line2",
								"line4" }, new int[] { R.id.text1, R.id.text2,
								R.id.albicon });
				setListAdapter(alb_notes);
				searchLevel = "albums";
				TextView notice = (TextView) findViewById(R.id.empty);
				if (currentArtist.equals("All")){
					notice.setText("Albums(" + albumhashlist.size()+")");
				}
				else{
					notice.setText(currentArtist);
				}
				
			}
		} else if (searchLevel == "albums") {
			searchLevel = "artists";
			ListAllArtists();
		} else if (searchLevel == "artistssongs") {
			searchLevel = "artists";
			ListAllArtists();
		} else if (searchLevel == "plsongs") {
			searchLevel = "playlists";
			if (pl_notes != null) {
				ListPlayLists();
			}
		}

		return;
	}

	public static void setlabel() {
		if (MusicDroid.nowplaying.size() > 0) {
			String Artist = MusicDroid.nowplaying
					.get(MusicDroid.currentPosition).get("line2").toString();
			String Song = MusicDroid.nowplaying.get(MusicDroid.currentPosition)
					.get("line1").toString();
			deck2.label.setText(Artist + "-" + Song);
		}
	}


	public boolean onCreateOptionsMenu(android.view.Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.menu, menu);

		return true;

	}

	// Handles when menu options are selected
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, settings.class));
			return true;
		case R.id.exit: 
			
			
			//unbindService(mServerConn);
			
			
			 
			try {
				if (tapedeckservice.isPlaying()){
				nowplaying.clear();
				tapedeckservice.mp.stop();}
				stopService(svc);
				this.finish();
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
			} catch (Throwable e) {
				
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

}

class Mp3Filter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		return (name.endsWith(".mp3"));
	}
}