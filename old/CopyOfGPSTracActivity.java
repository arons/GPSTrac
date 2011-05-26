package ch.arons.android.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import ch.arons.android.gps.services.LowBatteryLocationService;

public class CopyOfGPSTracActivity extends Activity {

	private static final String COMPONENT = "GPSTracActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		CompoundButton btn = (ToggleButton) findViewById(R.id.toggleButton_startService);
		btn.setChecked(true);
		/*
		 * LocationManager.NETWORK_PROVIDER; LocationManager.GPS_PROVIDER;
		 * LocationManager.PASSIVE_PROVIDER;
		 */
/*
		final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Log.d(COMPONENT, "retrieve location manager");

		List<String> providersName = locationManager.getProviders(true);
		Log.d(COMPONENT, "providers:" + providersName.size());

		for (String pn : providersName) {
			Log.d(COMPONENT, "----------------");
			Log.d(COMPONENT, "retrieve provider name:" + pn);
			// LocationProvider provider = locationManager.getProvider(pn);

			Location lastLoc = locationManager.getLastKnownLocation(pn);
			Log.d(COMPONENT, "lastLoc:" + lastLoc);

		}

		Log.d(COMPONENT, "create listner");
		// Define a listener that responds to location updates
		MyLocationListener locationListener = new MyLocationListener(locationManager);

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		*/
		Log.d(COMPONENT, "start service");
		Intent intent = new Intent(this, LowBatteryLocationService.class);
		startService(intent);

	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    
	    try {
	        stopService(new Intent(this, LowBatteryLocationService.class));
	      } catch (SecurityException e) {
	        Log.e(COMPONENT, "Encountered a security exception when trying to stop service.", e);
	      }
	}
}