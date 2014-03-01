package com.example.farm;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;

public class BaseAvtivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
	}
	public boolean isConnectInternet() {
		ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}

		return false;
	}

	public boolean isGpsEnabled() {
		LocationManager locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}
	public boolean isFirstOpen()
	{
		return true;
	}
	
}
