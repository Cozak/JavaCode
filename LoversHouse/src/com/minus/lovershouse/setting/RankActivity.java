package com.minus.lovershouse.setting;

import java.net.URL;
import java.util.ArrayList;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class RankActivity extends Activity{

	private WebView webview;

	private String url="search?q=";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		int i = 0;
		webview = new WebView(this);  
        //设置WebView属性，能够执行Javascript脚本  
        webview.getSettings().setJavaScriptEnabled(true);  
		
        //设置Web视图  
        setContentView(webview);
        PackageManager pm = RankActivity.this.getPackageManager();
        ArrayList<PackageInfo> list = (ArrayList<PackageInfo>) pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for(i = 0;i<list.size();i++)
        {
         //会打印出手机里安装的所有的程序的包名
                if (list.get(i).packageName.equals("com.minus.lovershouse")) {
//                    url = url.substring(url.lastIndexOf("id=")+3);
                    Uri uri = Uri.parse("market://search?q="+ "com.minus.lovershouse"); 
                    Intent it   = new Intent(Intent.ACTION_VIEW,uri); 
                    startActivity(it);
                   
                 }
        }
        if(i==list.size()){
        	Intent viewIntent = new Intent(Intent.ACTION_VIEW , Uri.parse("market://search"));

        	startActivity(viewIntent);
        }
        PushAgent.getInstance(this).onAppStart();
	}

	@Override
	protected void onResume() {
		super.onResume();

		MobclickAgent.onResume(this);
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK)
			finish();
			return super.onKeyDown(keyCode, event);
	}
}
