package ch.arons.android.gps;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import ch.arons.android.gps.services.LocationStatus;
import ch.arons.android.gps.services.LowBatteryLocationService;

public class GPSTracActivity extends Activity implements OnCheckedChangeListener {

	private static final String COMPONENT = "GPSTracActivity";
    
	private TextView provider_data;
	private TextView latitude_data;
	private TextView longitude_data;
	private TextView elevation_data;
	private TextView accuracy_data;
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(COMPONENT, "GPSTracActivity start");
		
		setContentView(R.layout.main);

		CompoundButton btn = (ToggleButton) findViewById(R.id.toggleButton_startService);
		btn.setChecked(isMyServiceRunning());
		btn.setOnCheckedChangeListener(this);
		
		provider_data = (TextView)findViewById(R.id.provider_data);
		latitude_data = (TextView)findViewById(R.id.latitude_data);
		longitude_data = (TextView)findViewById(R.id.longitude_data);
		elevation_data = (TextView)findViewById(R.id.elevation_data);
		accuracy_data = (TextView)findViewById(R.id.accuracy_data);
		
		LocationStatus.register(this);
		notifyLocationUpdate();
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    LocationStatus.unregeister();
	}
	
	
	
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (LowBatteryLocationService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	
	private void startService(){
		Log.d(COMPONENT, "start service");
		Intent intent = new Intent(this, LowBatteryLocationService.class);
		startService(intent);
	}

	private void stopService() {
		try {
			Log.d(COMPONENT, "stop service");
			stopService(new Intent(this, LowBatteryLocationService.class));
		} catch (SecurityException e) {
			Log.e(COMPONENT, "Encountered a security exception when trying to stop service.", e);
		}
	}
	
	private LowBatteryLocationService mBoundService;
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        mBoundService = ((LowBatteryLocationService.LocalBinder)service).getService();

	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        mBoundService = null;
	    }
	};
	void doBindService() {
		if(isMyServiceRunning()){
		    // Establish a connection with the service.  We use an explicit
		    // class name because we want a specific service implementation that
		    // we know will be running in our own process (and thus won't be
		    // supporting component replacement by other applications).
		    bindService(new Intent(this, LowBatteryLocationService.class), mConnection, 0);
		}
	}
	void doUnbindService() {
		// Detach our existing connection.
		if(mBoundService != null)
			unbindService(mConnection);
	}




	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.isChecked()) {
			startService();
		} else {
			stopService();
		}
	}
	

	public void notifyLocationUpdate() {
		if(LocationStatus.lastLoc == null) return;
		provider_data.setText(LocationStatus.lastLoc.getProvider());
		latitude_data.setText(""+LocationStatus.lastLoc.getLatitude());
		longitude_data.setText(""+LocationStatus.lastLoc.getLongitude());
		elevation_data.setText(LocationStatus.lastLoc.hasAltitude() ? ""+LocationStatus.lastLoc.getAltitude() : "-");
		accuracy_data.setText(LocationStatus.lastLoc.hasAccuracy() ? ""+LocationStatus.lastLoc.getAccuracy() : "-");
    }
}