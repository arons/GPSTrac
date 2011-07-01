package ch.arons.android.gps;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import ch.arons.android.gps.services.LocationStatus;
import ch.arons.android.gps.services.LowBatteryLocationService;

public class GPSTracActivity extends Activity implements OnCheckedChangeListener {

	private static final String COMPONENT = "GPSTracActivity";
    
	private static final String TIMESTAMP_FORMAT = "dd.MM.yyyy HH:mm:ss";
	private SimpleDateFormat df = new SimpleDateFormat(TIMESTAMP_FORMAT);
	private TextView provider_data;
	private TextView latitude_data;
	private TextView longitude_data;
	private TextView elevation_data;
	private TextView accuracy_data;
	private TextView date_data;
	private Button button_request_gps;
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(COMPONENT, "GPSTracActivity start");
		
		setContentView(R.layout.main);
		
//		View view = (View) findViewById(R.layout.);
//		int orientation = getResources().getConfiguration().orientation;
//		if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
//		    view.setBackgroundResource (R.drawable.background_land);
//		} else {
//		    view.setBackgroundResource (R.drawable.background_port);
//		}

		
		
		readSetting();

		CompoundButton btn = (ToggleButton) findViewById(R.id.toggleButton_startService);
		btn.setChecked(isMyServiceRunning());
		btn.setOnCheckedChangeListener(this);
		
		
		button_request_gps = (Button) findViewById(R.id.button_request_gps);
		button_request_gps.setOnClickListener(new RequestGPSListner(this));
		
		provider_data = (TextView)findViewById(R.id.provider_data);
		latitude_data = (TextView)findViewById(R.id.latitude_data);
		longitude_data = (TextView)findViewById(R.id.longitude_data);
		elevation_data = (TextView)findViewById(R.id.elevation_data);
		accuracy_data = (TextView)findViewById(R.id.accuracy_data);
		date_data = (TextView)findViewById(R.id.date_data);
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
	


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.isChecked()) {
			startService();
		} else {
			stopService();
		}
	}
	
	/******************************************************************************************************************************/
	
	public void notifyLocationUpdate() {
		if(LocationStatus.lastLoc == null) return;
		provider_data.setText(LocationStatus.lastLoc.getProvider());
		latitude_data.setText(""+LocationStatus.lastLoc.getLatitude());
		longitude_data.setText(""+LocationStatus.lastLoc.getLongitude());
		elevation_data.setText(LocationStatus.lastLoc.hasAltitude() ? ""+LocationStatus.lastLoc.getAltitude() : "-");
		accuracy_data.setText(LocationStatus.lastLoc.hasAccuracy() ? ""+LocationStatus.lastLoc.getAccuracy() : "-");
		date_data.setText(df.format(new Date(LocationStatus.lastLoc.getTime())));
    }
	
	
	/******************************************************************************************************************************/
	

	public void requestGPS() {
		if(isMyServiceRunning()){
			Log.d(COMPONENT, "userRequestGPS");
			bindService(new Intent(this, LowBatteryLocationService.class), 
			            new ServiceConnection(){
						    public void onServiceConnected(ComponentName name, IBinder service) {
								Log.d(COMPONENT, "service connected: userRequestGPS");
								LowBatteryLocationService mBoundService = ((LowBatteryLocationService.LocalBinder)service).getService();
								mBoundService.userRequestGPS();
						    }
						    public void onServiceDisconnected(ComponentName name) {
						    }
						}, 
						0);
		}else{
			Toast.makeText(this, "Start Service First", Toast.LENGTH_SHORT).show();
		}
    }
	
	
	/******************************************************************************************************************************/
	//Menu
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	// Handle item selection
        switch (item.getItemId()) {
        case R.id.MenuSettings:
        	Intent menuSettingsIntent = new Intent(getBaseContext(), SettingActivity.class);
            startActivityForResult(menuSettingsIntent, 0);
            break;
        case R.id.MenuFile:
        	Intent menuFileIntent = new Intent(getBaseContext(), FileActivity.class);
            startActivityForResult(menuFileIntent, 0);
            break;
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
        case R.id.MenuSettings:
        	Intent menuSettingsIntent = new Intent(getBaseContext(), SettingActivity.class);
            startActivityForResult(menuSettingsIntent, 0);
            break;
        case R.id.MenuFile:
        	Intent menuFileIntent = new Intent(getBaseContext(), FileActivity.class);
            startActivityForResult(menuFileIntent, 0);
            break;
        }
        return true;
    }
    
    /******************************************************************************************************************************/
	
    private void readSetting(){
		SharedPreferences settings  = getSharedPreferences(Preferences.PREFS_NAME,0);
		Preferences.readSetting(settings);
	}

    
    
    
    

}