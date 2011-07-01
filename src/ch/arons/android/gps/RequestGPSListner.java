package ch.arons.android.gps;

import android.view.View;
import android.view.View.OnClickListener;

public class RequestGPSListner implements OnClickListener{

	GPSTracActivity activity;
	
	public RequestGPSListner(GPSTracActivity activity){
		this.activity = activity;
	}
	
	@Override
    public void onClick(View v) {
		activity.requestGPS();
    }

}
