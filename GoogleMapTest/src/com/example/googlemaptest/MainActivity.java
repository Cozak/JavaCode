package com.example.googlemaptest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;

public class MainActivity extends Activity {
	private MapView mapView;
	private AMap aMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// R ��Ҫ���ð� import com.amapv2.apis.R;
		setContentView(R.layout.activity_main);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// ����Ҫд
		init();
	}

	/**
	 * ��ʼ�� AMap ����
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,1,1,"Normal");
		menu.add(0,2,2,"Satellite");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 1: {
				aMap.setMapType(AMap.MAP_TYPE_NORMAL);
				break;
			}
			case 2: {
				aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// ���ǵ�ͼģʽ
				break;
			}
			default: {
				aMap.setMapType(AMap.MAP_TYPE_NORMAL);
			}
		}
		return true;
	}
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		
	}
	
	/**
	 * ����������д
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * �˷�����Ҫ��
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * �˷�����Ҫ��
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * �˷�����Ҫ��
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
}