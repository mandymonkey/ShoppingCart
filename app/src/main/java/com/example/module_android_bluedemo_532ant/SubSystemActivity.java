package com.example.module_android_bluedemo_532ant;

import com.bth.api.cls.Comm_Bluetooth;
import com.function.SPconfig;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SubSystemActivity extends Activity {
	
	Button button_setblueframe;
	
	 
	MyApplication myapp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.systemlayout);
		
		Application app=getApplication();
		myapp=(MyApplication) app;  
		  
		EditText ev=(EditText)findViewById(R.id.editText_framec);
	 
		EditText ev2=(EditText)findViewById(R.id.editTexta_framet);
		
		ev.setText(myapp.spf.GetString("Blue_FrameCount"));
		ev2.setText(myapp.spf.GetString("Blue_FrameTime"));
 
		button_setblueframe=(Button)this.findViewById(R.id.button_setblueframe);
		
		button_setblueframe.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(myapp.CommBth!=null)
				{
					try{
					EditText ev=(EditText)findViewById(R.id.editText_framec);
					int fcount=Integer.valueOf(ev.getText().toString());
					EditText ev2=(EditText)findViewById(R.id.editTexta_framet);
					int ftime=Integer.valueOf(ev2.getText().toString());
					myapp.CommBth.SetFrameParams(fcount, ftime);
					myapp.spf.SaveString("Blue_FrameCount",ev.getText().toString());
					myapp.spf.SaveString("Blue_FrameTime",ev2.getText().toString());
				}
				catch(Exception ex)
				{
					Toast.makeText(SubSystemActivity.this, "设置失败:"+ex.getMessage(),
							Toast.LENGTH_SHORT).show();

				}
			}
			}
		});
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		 
	}
}
