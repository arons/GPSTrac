
package ch.arons.android.gps;

import android.content.SharedPreferences;

public class Preferences {

	
	public static final String PREFS_NAME = "BeastMakerPrefsFile";
	
	public static int GPS_MAX_TRY_MIN = 3;
	public static int GPS_POLLING_MIN = 20;
	
	
	
	public static  void readSetting(SharedPreferences settings){
		synchronized (PREFS_NAME) {
			GPS_MAX_TRY_MIN = settings.getInt("GPS_MAX_TRY_MIN",GPS_MAX_TRY_MIN);
			GPS_POLLING_MIN = settings.getInt("GPS_POLLING_MIN",GPS_POLLING_MIN);
        }
		
	}
	
	public static void saveSetting(SharedPreferences settings){
		synchronized (PREFS_NAME) {
			 SharedPreferences.Editor editor = settings.edit();
			    editor.putInt("GPS_MAX_TRY_MIN", GPS_MAX_TRY_MIN);
			    editor.putInt("GPS_POLLING_MIN", GPS_POLLING_MIN);
			    // Commit the edits!
			    editor.commit();
        }
	}
}
