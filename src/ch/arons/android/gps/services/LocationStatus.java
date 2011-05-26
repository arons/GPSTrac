package ch.arons.android.gps.services;

import ch.arons.android.gps.GPSTracActivity;
import android.location.Location;

public class LocationStatus {
	
	public static Location lastLoc;
	
	private static GPSTracActivity activity;
	
	public static void register(GPSTracActivity activity){
		LocationStatus.activity = activity;
	}
	
	public static void unregeister(){
		LocationStatus.activity = null;
	}
	
	public static void notifyUpdate(){
		if(activity == null) return;
		activity.notifyLocationUpdate();
	}
}
