package ch.arons.android.gps.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class PassiveLocationListner implements LocationListener {
	
	LowBatteryLocationService service;
	
	public PassiveLocationListner(LowBatteryLocationService service){
		this.service = service;
	}

	@Override
    public void onLocationChanged(Location location) {
		service.onPassiveLocationChanged(location);
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
