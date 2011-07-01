package ch.arons.android.gps;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import ch.arons.android.gps.widget.wheel.NumericWheelAdapter;
import ch.arons.android.gps.widget.wheel.WheelView;

public class SettingActivity extends Activity {

	WheelView gpsPollingMin;
	WheelView gpsMaxTryMin;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        gpsPollingMin = (WheelView) findViewById(R.id.gps_polling_data);
        gpsPollingMin.setAdapter(new NumericWheelAdapter(1, 60,"%02d"));
        gpsPollingMin.setCurrentItem(Preferences.GPS_POLLING_MIN -1);
        
        gpsMaxTryMin = (WheelView) findViewById(R.id.gps_max_try_data);
        gpsMaxTryMin.setAdapter(new NumericWheelAdapter(1, 60,"%02d"));
        gpsMaxTryMin.setCurrentItem(Preferences.GPS_MAX_TRY_MIN-1);
    }
	
	
	@Override
	protected void onStop() {
	    super.onStop();
	    saveSetting();
	}
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    saveSetting();
	}
	@Override
	protected void onPause() {
	    super.onPause();
	    saveSetting();
	}
	
	
	@Override
	protected void onRestart() {
		System.out.println("onRestart");
	    super.onRestart();
	    restoreSetting();
	}
	
	@Override
	protected void onResume() {
		System.out.println("onResume");
	    super.onResume();
	    restoreSetting();
	}
	
	private void saveSetting(){
		Preferences.GPS_POLLING_MIN = gpsPollingMin.getCurrentItem() +1;
		Preferences.GPS_MAX_TRY_MIN = gpsMaxTryMin.getCurrentItem()+1;
		SharedPreferences settings = getSharedPreferences(Preferences.PREFS_NAME,0);
		Preferences.saveSetting(settings);
	}
	private void restoreSetting(){
		SharedPreferences settings  = getSharedPreferences(Preferences.PREFS_NAME,0);
		Preferences.readSetting(settings);
	}
}
