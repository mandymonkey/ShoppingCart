package com.example.module_android_bluedemo_532ant;

import com.bth.api.cls.Comm_Bluetooth;
import com.communication.inf.CommunicationException;
import com.function.SPconfig;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SubDebugActivity  extends Activity {

	
	MyApplication myapp;
	Button button_clear,button_send1,button_send2,button_send3,
	button_revd,button_app;
	CheckBox checkhex1,checkhex2,checkhex3,checkrhex;
	private Handler handler = new Handler( );
	private Handler handler2 = new Handler( );
	private Thread revThread;
	private boolean isrun=false,isrun2=false;
	private Runnable runnable2 = new Runnable( ) {
 		public void run ( ) {

			  // check connect
			 TextView et1=(TextView)findViewById(R.id.textView_state);
			int bl=myapp.CommBth.ConnectState();
			if(bl==Comm_Bluetooth.DISCONNECTED)
			{
				et1.setText("已经断开,正在重新连接");
				if(isrun2)
					myapp.CommBth.Connect(myapp.CommBth.GetConnectAddr(), myapp.CommBth.getRemoveType());
				
				handler2.postDelayed(this,1000); 
			}
			else if(bl==Comm_Bluetooth.CONNECTED)
			{
				if(et1.getText().toString().equals("已经断开,正在重新连接"))
					et1.setText("已经连接");
			}
			else if(bl==Comm_Bluetooth.CONNECTING)
			{
					et1.setText("正在连接......");
			}
			handler2.postDelayed(this,1000); 
 			return;
 		}
	};
	
	private Runnable runnable = new Runnable( ) {
	 		public void run ( ) {
	 			
	 			//while(isrun)
	 			//{
	 			  byte[] revdata=new byte[256];
	 			  int offset=0;
	 			
	 			int re=0;
				try {
					re = myapp.CommBth.Comm_Read(revdata, offset, 0);
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 			 
	 			if(re>0)
	 			{
	 				TextView et2=(TextView)findViewById(R.id.textView_revddata);
					String btext=et2.getText().toString();
					String rdata="";
					if(checkrhex.isChecked())
						rdata=Comm_Bluetooth.bytes_Hexstr(revdata,re);
					else
						rdata=String.valueOf(revdata);
					et2.setText(btext+rdata+"\n");
	 			}
	 			
	 			//}
	 			handler.postDelayed(this,0); 
	 			return;
	 		}
	 };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab1_tablelayout_debug);
		
		Application app=getApplication();
		myapp=(MyApplication) app;  
		 
		myapp.spf=new SPconfig(this);
		 
				
				button_clear=(Button) this.findViewById(R.id.button_clear);
				button_send1=(Button) this.findViewById(R.id.button_send1);
				button_send2=(Button) this.findViewById(R.id.button_send2);
				button_send3=(Button) this.findViewById(R.id.button_send3);
				button_revd=(Button)this.findViewById(R.id.button_revd);
				 
				checkrhex=(CheckBox) this.findViewById(R.id.checkBox_rhex);
				checkhex1=(CheckBox) this.findViewById(R.id.checkBox_hex1);
				checkhex2=(CheckBox) this.findViewById(R.id.checkBox_hex2);
				checkhex3=(CheckBox) this.findViewById(R.id.checkBox_hex3);
				TextView et3=(TextView)findViewById(R.id.textView_revddata);
				et3.setText("");
				//revThread=new Thread(runnable);
				isrun2=true;
				handler2.postDelayed(runnable2,0); 
				button_revd.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						String txt=button_revd.getText().toString().trim();
						if(txt.equals("接收"))
						{   
							isrun=true;
							handler.postDelayed(runnable,0); 
							//isrun=true;
							//revThread.start();
							button_revd.setText("停止");
						}
						else
						{
							/*isrun=false;
							try {
								revThread.join(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							isrun=false;
							handler.removeCallbacks(runnable);
							button_revd.setText("接收");
						}
					}}
				
				);
				 
				button_clear.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						TextView et2=(TextView)findViewById(R.id.textView_revddata);
						et2.setText("");
					}}
				
				);
				button_send1.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						byte[] buffer = null;
						if(checkhex1.isChecked())
						{
							//CommBth.
							EditText ets1=(EditText)findViewById(R.id.editText_s1);
							String data=ets1.getText().toString();
							buffer=new byte[data.length()/2];
							int re=Comm_Bluetooth.Str2Hex(data,data.length(),buffer);
							if(re==-1)
								Toast.makeText(SubDebugActivity.this, "数据长度不对",
										Toast.LENGTH_SHORT).show();
							if(re==1)
								Toast.makeText(SubDebugActivity.this, "请输入16进制字符",
										Toast.LENGTH_SHORT).show();
							 
						}
						else
						{
							EditText ets1=(EditText)findViewById(R.id.editText_s1);
							String data=ets1.getText().toString();
						    buffer=data.getBytes();
							 
						}
						
						try {
							if(buffer!=null)
								myapp.CommBth.Comm_Write(buffer, 0, buffer.length);
							 
						} catch (CommunicationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}}
				
				);
				button_send2.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						byte[] buffer = null;
						if(checkhex2.isChecked())
						{
							//CommBth.
							EditText ets1=(EditText)findViewById(R.id.editText_s2);
							String data=ets1.getText().toString();
							buffer=new byte[data.length()/2];
							int re=Comm_Bluetooth.Str2Hex(data,data.length(),buffer);
							if(re==-1)
								Toast.makeText(SubDebugActivity.this, "数据长度不对",
										Toast.LENGTH_SHORT).show();
							if(re==1)
								Toast.makeText(SubDebugActivity.this, "请输入16进制字符",
										Toast.LENGTH_SHORT).show();
						}
						else
						{
							EditText ets1=(EditText)findViewById(R.id.editText_s2);
							String data=ets1.getText().toString();
						    buffer=data.getBytes();
							 
						}
						
						try {
							if(buffer!=null)
								myapp.CommBth.Comm_Write(buffer, 0, buffer.length);
						} catch (CommunicationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}}
				
				);
				button_send3.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						byte[] buffer = null;
						if(checkhex3.isChecked())
						{
							//CommBth.
							EditText ets1=(EditText)findViewById(R.id.editText_s3);
							String data=ets1.getText().toString();
							buffer=new byte[data.length()/2];
							int re=Comm_Bluetooth.Str2Hex(data,data.length(),buffer);
							if(re==-1)
								Toast.makeText(SubDebugActivity.this, "数据长度不对",
										Toast.LENGTH_SHORT).show();
							if(re==1)
								Toast.makeText(SubDebugActivity.this, "请输入16进制字符",
										Toast.LENGTH_SHORT).show();
						}
						else
						{
							EditText ets1=(EditText)findViewById(R.id.editText_s3);
							String data=ets1.getText().toString();
						    buffer=data.getBytes();
							 
						}
						
						try {
							if(buffer!=null)
								myapp.CommBth.Comm_Write(buffer, 0, buffer.length);
						} catch (CommunicationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}}
				
				);
				
				TextView et1=(TextView)findViewById(R.id.textView_blueset);
				et1.setText(myapp.CommBth.GetConnectAddr());
				TextView et2=(TextView)findViewById(R.id.textView_state);
				et2.setText("已经连接");

	}
	protected void onStop()
	{
		isrun2=false;
		 handler2.removeCallbacks(runnable2);
		 if(runnable!=null&&handler!=null)
		 {
			 isrun=false;
			 handler.removeCallbacks(runnable);
		 }
		 super.onStop();
	}
	 
}
