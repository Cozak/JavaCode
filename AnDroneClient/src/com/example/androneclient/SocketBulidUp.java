package com.example.androneclient;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SocketBulidUp extends Activity {
	private Button sock_but = null;
	private Button sock_cancel = null;
	private EditText sock_text = null;
	private String sock_IP = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_socket_build_up);
		Sock_Init();
	}
	private void Sock_Init() {
		this.sock_but = (Button)findViewById(R.id.socket_but_submit);
		this.sock_cancel = (Button)findViewById(R.id.socket_but_cancel);
		this.sock_text = (EditText)findViewById(R.id.socket_edit_ip);
		this.sock_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SocketBulidUp.this.sock_IP = SocketBulidUp.this.sock_text.getText().toString();
				int fb = NetWorkLayer.LaunchSocket(SocketBulidUp.this, SocketBulidUp.this.sock_IP);
				if (fb == 0) {
					SocketBulidUp.this.finish();
				} else {
					Toast.makeText(SocketBulidUp.this, "Oops~~Error Code: "+fb, Toast.LENGTH_SHORT).show();
				}
			}
		});
		this.sock_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SocketBulidUp.this.finish();
			}
		});
	}
}
