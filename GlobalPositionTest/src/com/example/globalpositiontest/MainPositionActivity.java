package com.example.globalpositiontest;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainPositionActivity extends Activity {
	private Criteria criteria = null;
	private LocationManager locationManager = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_position);
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		this.criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setCostAllowed(false);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		String provider = locationManager.getBestProvider(criteria, true);
//		String provider = LocationManager.GPS_PROVIDER;
		this.updataWithLocation(locationManager.getLastKnownLocation(provider));
		Toast.makeText(this, provider, Toast.LENGTH_LONG).show();
		locationManager.requestLocationUpdates(provider, 3000, 1, locationListener);
		((Button)findViewById(R.id.but_pos)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				locationManager.requestLocationUpdates(
						locationManager.getBestProvider(
								MainPositionActivity.this.criteria, true
								), 3000, 1, locationListener);
			}
		});
	}
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			MainPositionActivity.this.updataWithLocation(location);
		}
	};
	
	private void updataWithLocation(Location loc) {
		TextView tv = (TextView)findViewById(R.id.pos_text);
		String str = "Unknown Location";
		if (loc != null) {
			str = "Lst: "+loc.getLatitude()+"\nLong: "+loc.getLongitude();
		}
		tv.setText(tv.getText()+"\n"+str);
		Toast.makeText(this, "Updata~", Toast.LENGTH_SHORT).show();
	}
}
