package ch.arons.android.gps.services;

import java.util.Timer;
import java.util.TimerTask;

import ch.arons.android.gps.io.file.GPXWriter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LowBatteryLocationService extends Service {
	private static final String COMPONENT = "LowBatteryLocationService";

	
	private int count = 0;
	private static final int SAVE_MIN = 5;
	
	private long gpsLastStart = -1L;
	private boolean gpsStarted = false;
	private static final int GPS_MAX_TRY_MIN = 5;
	private static final int GPS_POLLING_MIN = 15;
	
	
	//DO NOT CHANGE
	private static final int CLOCK_FREQ_MIN = 1;
	/** This timer invokes periodically the checkLocationListener timer task. */
	private final Timer clock = new Timer();
	/** The timer posts a runnable to the main thread via this handler. */
	private final Handler clockHandler = new Handler();
	/**
	 * Task invoked by a timer periodically to make sure the location listener
	 * is still registered.
	 */
	private TimerTask clockTask = new TimerTask() {
		@Override
		public void run() {
			// It's always safe to assume that if isRecording() is true, it
			// implies
			// that onCreate() has finished.
			clockHandler.post(new Runnable() {
				public void run() {
					step();
				}
			});
		}
	};
	
	
	
	

	private LocationManager locationManager;
	private PassiveLocationListner passiveLocationListner;
	private GPSLocationListner gpsLocationListner;
	
	
	private GPXWriter filewriter;

	@Override
	public void onCreate() {
		Log.d(COMPONENT, "onCreate");

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		passiveLocationListner = new PassiveLocationListner(this);
		gpsLocationListner = new GPSLocationListner(this);
		filewriter = new GPXWriter();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(COMPONENT, "onStartCommand");
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		count = 0;
		
		clock.schedule(clockTask, 0, 1000 * 60 * CLOCK_FREQ_MIN);

		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 60000L, 100, passiveLocationListner);
		
		filewriter.createNewFile();
		
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d(COMPONENT, "onDestroy");

		clock.cancel();
		clock.purge();

		locationManager.removeUpdates(passiveLocationListner);
		locationManager.removeUpdates(gpsLocationListner);
		
		filewriter.close();
		
		Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public LowBatteryLocationService getService() {
			return LowBatteryLocationService.this;
		}
	}

	// This is the object that receives interactions from clients. 
	// See RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	
	
	private void requestGPS(){
		if(gpsStarted || System.currentTimeMillis() - gpsLastStart < GPS_POLLING_MIN * 60000L){
			return;
		}
		Log.d(COMPONENT, "requestGPS");
		gpsLastStart = System.currentTimeMillis();
		gpsStarted = true;
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListner);
	}
	private void removeGPS(){
		if(gpsStarted && System.currentTimeMillis() - gpsLastStart > GPS_MAX_TRY_MIN *60000L){
			Log.d(COMPONENT, "removeGPS waiting min:"+(System.currentTimeMillis() - gpsLastStart)/60000L);
			locationManager.removeUpdates(gpsLocationListner);
			gpsStarted = false;
		}
	}
	
	public void onGPSLocationChanged(Location location) {
		Log.d(COMPONENT, "onGPSLocationChanged:" + location);
		locationManager.removeUpdates(gpsLocationListner);
		processLocationUpdate(location);
		
    }
	
	public void onPassiveLocationChanged(Location location) {
		Log.d(COMPONENT, "onPassiveLocationChanged:" + location);
		processLocationUpdate(location);
	}

	
	
	
	private synchronized void processLocationUpdate(Location location) {
		
		boolean needUpdate = false;
		long now = System.currentTimeMillis();
		
		if(LocationStatus.lastLoc == null){
			needUpdate= true;
		
		}else{
			Location lastLoc = LocationStatus.lastLoc;
			long locationTimeMS = lastLoc.getTime();
			
			needUpdate |= now - locationTimeMS > 5 * 60000L; // 5 min
			needUpdate |= (location.hasAccuracy() && !lastLoc.hasAccuracy());
			needUpdate |= location.hasAccuracy() && lastLoc.hasAccuracy() && location.getAccuracy() < lastLoc.getAccuracy();
		}
		
		
		if(needUpdate){
			Log.d(COMPONENT, "updateLoc:" + location);
			LocationStatus.lastLoc = location;
			LocationStatus.notifyUpdate();
		}
		
	}
	
	
	
	
	
	
	private void step() {
		Log.d(COMPONENT, "step:"+count);
		
		removeGPS();
		
		//Check location
		Location location = LocationStatus.lastLoc;
		if(location == null ){
			if(count > SAVE_MIN-1) requestGPS();
		}else{
			long now = System.currentTimeMillis();
			long locationTimeMS = location.getTime();
			//if not valid invoke GPS
			if(now - locationTimeMS > GPS_POLLING_MIN * 60000L){ 
				requestGPS();
			}
		}
		
        
		//save location on file
		if(count == 0){
			Log.d(COMPONENT, "write location");
			filewriter.writeLocation(location);
		}
		
		count = (count + 1) % SAVE_MIN;
    }

	
}
