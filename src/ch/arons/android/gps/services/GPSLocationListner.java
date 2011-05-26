package ch.arons.android.gps.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GPSLocationListner implements LocationListener {
	
	LowBatteryLocationService service;
	
	public GPSLocationListner(LowBatteryLocationService service){
		this.service = service;
	}

	@Override
    public void onLocationChanged(Location location) {
		service.onGPSLocationChanged(location);
    }

	@Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

	@Override
    public void onProviderEnabled(String provider) {
    }

	@Override
    public void onProviderDisabled(String provider) {
    }

}
