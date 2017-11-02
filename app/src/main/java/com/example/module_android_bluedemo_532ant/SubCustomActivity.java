package com.example.module_android_bluedemo_532ant;

import com.silionmodule.Custom;
import com.silionmodule.Custom.CustomCmdType;
import com.silionmodule.Custom.KUNRUI_CustomParam;
import com.silionmodule.Custom.KUNRUI_CustomResult;
import com.silionmodule.Functional;
import com.silionmodule.ReaderException;
import com.silionmodule.Gen2.Gen2Password;
import com.silionmodule.Gen2.Gen2TagFilter;
import com.silionmodule.Gen2.MemBankE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

public class SubCustomActivity extends Activity {
	MyApplication myapp;
	String[] UserArea={"0","1","2","3"};
	String[] cusfbank={"EPC区","TID区","用户区"};
	String[] cusreadwrite={"读操作","写操作"};
	String[] cuslockunlock={"锁","解锁"};
	Button button_chanageareapwd,button_changearealock,button_changearealocknopwd;
	Spinner spinner_userarea,spinner_cusfbank,
			spinner_readwrite_nopwd,spinner_lockunlock_nopwd,
	spinner_readwrite,spinner_lockunlock;
	private ArrayAdapter arradp_uarea,arradp_fbank,arradp_readwrite,arradp_lockunlock;
	Gen2TagFilter g2tf=null;
	RadioGroup  gr_customatch,gr_customenablefil;
	CheckBox cb_maskread,cb_maskwirte,cb_actionread,cb_actionwrite;
	TabHost tabHost_custom_lunrui;
		private View createIndicatorView(Context context, TabHost tabHost, String title) {
			  LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			  View tabIndicator = inflater.inflate(R.layout.tab_indicator_vertical, tabHost.getTabWidget(), false);
			  final TextView titleView = (TextView) tabIndicator.findViewById(R.id.tv_indicator);
			  titleView.setText(title);
			  return tabIndicator;
			 }
		
	 private int SortGroup(RadioGroup rg)
		{
			 int check1=rg.getCheckedRadioButtonId();
			    if(check1!=-1)
			    {
			    	for(int i=0;i<rg.getChildCount();i++)
			    	{ 
			    	  View vi=rg.getChildAt(i);
			    	  int vv=vi.getId();
			    	  if(check1==vv)
			    	  {
			    		  return i;
			    	  }
			    	}
			    	
			    	return -1;
			    }
			    else
			    	return check1;
		}
	private void SetFiler() throws ReaderException
	 { 
		 if(SortGroup(gr_customenablefil)==1)
		 {  
			 byte[] fdata;
			 EditText et=(EditText)findViewById(R.id.editText_customfilterdata);
			 fdata = Functional.binarystr_Bytes(et.getText().toString());
			 int it=spinner_cusfbank.getSelectedItemPosition()+1;
			 MemBankE mb=null;
			 switch(it)
				{
				case 0: 
					mb=MemBankE.RESERVED;
					break;
				case 1:
					mb=MemBankE.EPC;
					break;
				case 2:
					mb=MemBankE.TID;
					break;
				case 3:
					mb=MemBankE.USER;
					break;
				}
			 EditText etadr=(EditText)findViewById(R.id.editText_customfilsadr);
			 
			 g2tf = new Gen2TagFilter(mb, 
					 Integer.valueOf(etadr.getText().toString()), 
					 fdata, et.getText().toString().length());//按二进制数据过滤
			 int ma=SortGroup(gr_customatch);
			 if(ma==1)
				 g2tf.SetInvert(true);
		 }
		 else
			 g2tf=null;
	 }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab5_tablelayout);
		
		Application app=getApplication();
		myapp=(MyApplication) app;
		
		tabHost_custom_lunrui = (TabHost) findViewById(R.id.tabhost4); 
		// 如果没有继承TabActivity时，通过该种方法加载启动tabHost 
		tabHost_custom_lunrui.setup(); 
		tabHost_custom_lunrui.getTabWidget().setOrientation(LinearLayout.VERTICAL);
		//tabHost2.addTab(tabHost2.newTabSpec("tab1").setIndicator("初始化",  
		//getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab11)); 
	    //tabHost2.addTab(tabHost2.newTabSpec("tab1").setIndicator(createIndicatorView(this, tabHost2, "1111"))
	    	//	.setContent(R.id.tab11)); 
	   
		tabHost_custom_lunrui.addTab(tabHost_custom_lunrui.newTabSpec("tab1").setIndicator(createIndicatorView(this, tabHost_custom_lunrui, "改密码"))
				.setContent(R.id.tab5_1)); 
		tabHost_custom_lunrui.addTab(tabHost_custom_lunrui.newTabSpec("tab2").setIndicator(createIndicatorView(this, tabHost_custom_lunrui, "带密码锁")) 
				.setContent(R.id.tab5_2)); 
		tabHost_custom_lunrui.addTab(tabHost_custom_lunrui.newTabSpec("tab3").setIndicator(createIndicatorView(this, tabHost_custom_lunrui, "不带密码锁"))
				.setContent(R.id.tab5_3)); 
		
		TabWidget tw=tabHost_custom_lunrui.getTabWidget();
		tw.getChildAt(0).setBackgroundColor(Color.BLUE);
		
		gr_customenablefil=(RadioGroup)findViewById(R.id.radioGroup_enablecustomfil);
		gr_customatch=(RadioGroup)findViewById(R.id.radioGroup_customatch);
		//cb_maskread=(CheckBox) findViewById(R.id.checkBox_customaskread);
		//cb_maskwirte=(CheckBox) findViewById(R.id.checkBox_customaskwrite);
		//cb_actionread=(CheckBox) findViewById(R.id.checkBox_customactionread);
		//cb_actionwrite=(CheckBox) findViewById(R.id.checkBox_customactionwrite);
		spinner_userarea = (Spinner) findViewById(R.id.spinner_customuserarea); 
		// View layout = getLayoutInflater().inflate(R.layout.tab3_tablelayout_write, null);
		arradp_uarea = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,UserArea); 
		arradp_uarea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		spinner_userarea.setAdapter(arradp_uarea);
		 
		spinner_cusfbank= (Spinner)findViewById(R.id.spinner_customfbank);
		arradp_fbank = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cusfbank); 
		arradp_fbank.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		spinner_cusfbank.setAdapter(arradp_fbank);
		
		spinner_lockunlock_nopwd= (Spinner)findViewById(R.id.spinner_lockunlock_nopwd);
		spinner_lockunlock= (Spinner)findViewById(R.id.spinner_lockunlock_pwd);
		arradp_lockunlock = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cuslockunlock); 
		arradp_lockunlock.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		spinner_lockunlock.setAdapter(arradp_lockunlock);
		spinner_lockunlock_nopwd.setAdapter(arradp_lockunlock);
		
		spinner_readwrite= (Spinner)findViewById(R.id.spinner_readwrite_pwd);
		spinner_readwrite_nopwd= (Spinner)findViewById(R.id.spinner_readwrite_nopwd);
		arradp_readwrite = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cusreadwrite); 
		arradp_readwrite.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		spinner_readwrite.setAdapter(arradp_readwrite);
		spinner_readwrite_nopwd.setAdapter(arradp_readwrite);
 
		button_chanageareapwd = (Button) findViewById(R.id.button_changegareapwd);
		button_changearealock = (Button) findViewById(R.id.button_changearealock);
		button_changearealocknopwd = (Button) findViewById(R.id.button_changearealocknopwd);
		
		myapp.CommBth.SetFrameParams(20, 0);
		
		button_chanageareapwd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					
					SetFiler();
					KUNRUI_CustomParam kuricp=new Custom().new KUNRUI_CustomParam();
					EditText timestr=(EditText)findViewById(R.id.editText_customtime);
					if(timestr.getText().toString().equals(""))
					{
						Toast.makeText(SubCustomActivity.this, "请设置超时时间",
								Toast.LENGTH_SHORT).show();
						return;
					}
					EditText et=(EditText)findViewById(R.id.editText_customapwd);
					EditText et2=(EditText)findViewById(R.id.editText_customnowpwd);
					EditText et3=(EditText)findViewById(R.id.editText_customnewpwd);
					if(et2.getText().toString().equals("")||et3.getText().toString().equals(""))
					{
						Toast.makeText(SubCustomActivity.this, "填入当前密码与新密码",
								Toast.LENGTH_SHORT).show();
						return;
					}
					if(spinner_userarea.getSelectedItemPosition()==-1)
					{
						Toast.makeText(SubCustomActivity.this, "请选择区域",
								Toast.LENGTH_SHORT).show();
						return;
					}
					   // String temp=et.getText().toString();
					
				    Gen2Password g2pw = new Gen2Password(et.getText().toString());
				    Gen2Password nowpw = new Gen2Password(et2.getText().toString());
				    Gen2Password newpw = new Gen2Password(et3.getText().toString());
					    
					kuricp.Set_ChangeAreaPwd(Short.parseShort(timestr.getText().toString()), g2pw.GetPasswordBytes(), 
							(byte)spinner_userarea.getSelectedItemPosition(), newpw.GetPasswordBytes(), nowpw.GetPasswordBytes(), g2tf);
					 
					KUNRUI_CustomResult cr=(KUNRUI_CustomResult) myapp.Mreader.CustomCmd(g2tf, CustomCmdType
	.KUNRUI_ChangeAreaPwd, kuricp);
					if(cr!=null)
					Toast.makeText(SubCustomActivity.this, "epc:"+Functional.bytes_Hexstr(cr.epcid),
							Toast.LENGTH_SHORT).show();
				}
				catch (ReaderException e) {
					// TODO Auto-generated catch block
					Toast.makeText(SubCustomActivity.this, "操作失败:"+e.GetMessage(),
							Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		button_changearealock.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					SetFiler();
					KUNRUI_CustomParam kuricp=new Custom().new KUNRUI_CustomParam();
					EditText timestr=(EditText)findViewById(R.id.editText_customtime);
					if(timestr.getText().toString().equals(""))
					{
						Toast.makeText(SubCustomActivity.this, "请设置超时时间",
								Toast.LENGTH_SHORT).show();
						return;
					}
					EditText et=(EditText)findViewById(R.id.editText_customapwd);
					EditText et2=(EditText)findViewById(R.id.editText_customnowpwd_lock);
					if(et2.getText().toString().equals(""))
					{
						Toast.makeText(SubCustomActivity.this, "填入当前密码",
								Toast.LENGTH_SHORT).show();
						return;
					}
					if(spinner_userarea.getSelectedItemPosition()==-1)
					{
						Toast.makeText(SubCustomActivity.this, "请选择区域",
								Toast.LENGTH_SHORT).show();
						return;
					}
					   // String temp=et.getText().toString();
				    Gen2Password g2pw = new Gen2Password(et.getText().toString());
				    Gen2Password nowpw = new Gen2Password(et2.getText().toString());
				 
					    
				    byte mask=0,action=0;
				    short payload = 0;
		 
				    /* if(cb_maskread.isChecked())
				     	mask|=0x02;
				      if(cb_maskwirte.isChecked())
				     	 mask|=0x01;
				     
				   
				     if(cb_actionread.isChecked())
				    	 action|=0x02;
				     if(cb_actionwrite.isChecked())
				    	 action|=0x01;
				    	  */
				    if(spinner_readwrite.getSelectedItemPosition()==0)
				    	mask|=0x02;
				    else if(spinner_readwrite.getSelectedItemPosition()==1)
				    	mask|=0x01;
				    
				    if(spinner_readwrite.getSelectedItemPosition()==0
				    		&&spinner_lockunlock.getSelectedItemPosition()==0)
				    	action|=0x02;
				    else if(spinner_readwrite.getSelectedItemPosition()==0
				    		&&spinner_lockunlock.getSelectedItemPosition()==1)
				    	action|=0x00;
				    else if(spinner_readwrite.getSelectedItemPosition()==1
				    		&&spinner_lockunlock.getSelectedItemPosition()==0)
				    	action|=0x01;
				    else if(spinner_readwrite.getSelectedItemPosition()==1
				    		&&spinner_lockunlock.getSelectedItemPosition()==1)
				    	action|=0x00;

				     payload=(short) (mask<<8|action);
					kuricp.Set_ChangeAreaLock(Short.parseShort(timestr.getText().toString()), g2pw.GetPasswordBytes(), 
							(byte)spinner_userarea.getSelectedItemPosition(), payload, nowpw.GetPasswordBytes(), g2tf);
					 
					KUNRUI_CustomResult cr=(KUNRUI_CustomResult) myapp.Mreader.CustomCmd(g2tf, CustomCmdType
	.KUNRUI_ChangeAreaLock, kuricp);
					Toast.makeText(SubCustomActivity.this, "epc:"+Functional.bytes_Hexstr(cr.epcid),
							Toast.LENGTH_SHORT).show();
					
				}
				catch (ReaderException e) {
					// TODO Auto-generated catch block
					Toast.makeText(SubCustomActivity.this, "操作失败:"+e.GetMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		button_changearealocknopwd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					SetFiler();
					KUNRUI_CustomParam kuricp=new Custom().new KUNRUI_CustomParam();
					EditText timestr=(EditText)findViewById(R.id.editText_customtime);
					if(timestr.getText().toString().equals(""))
					{
						Toast.makeText(SubCustomActivity.this, "请设置超时时间",
								Toast.LENGTH_SHORT).show();
						return;
					}
					EditText et=(EditText)findViewById(R.id.editText_customapwd);
					if(spinner_userarea.getSelectedItemPosition()==-1)
					{
						Toast.makeText(SubCustomActivity.this, "请选择区域",
								Toast.LENGTH_SHORT).show();
						return;
					}
					   // String temp=et.getText().toString();
				    Gen2Password g2pw = new Gen2Password(et.getText().toString());
				   
				    byte mask=0,action=0;
				    //cb_maskread.setChecked(false);
		    	    //cb_maskwirte.setChecked(false);
		    	    
				     /*if(cb_actionread.isChecked())
				    	 action|=0x02;
				     if(cb_actionwrite.isChecked())
				    	 action|=0x01;
				     */
				     if(spinner_readwrite.getSelectedItemPosition()==0
					    		&&spinner_lockunlock.getSelectedItemPosition()==0)
					    	action|=0x02;
					    else if(spinner_readwrite.getSelectedItemPosition()==0
					    		&&spinner_lockunlock.getSelectedItemPosition()==1)
					    	action|=0x00;
					    else if(spinner_readwrite.getSelectedItemPosition()==1
					    		&&spinner_lockunlock.getSelectedItemPosition()==0)
					    	action|=0x01;
					    else if(spinner_readwrite.getSelectedItemPosition()==1
					    		&&spinner_lockunlock.getSelectedItemPosition()==1)
					    	action|=0x00;
 
					kuricp.Set_ChangeAreaLockwithoutPwd(Short.parseShort(timestr.getText().toString()),g2pw.GetPasswordBytes(),
							(byte)spinner_userarea.getSelectedItemPosition(), action, g2tf);
					 
					KUNRUI_CustomResult cr=(KUNRUI_CustomResult) myapp.Mreader.CustomCmd(g2tf, CustomCmdType
	.KUNRUI_ChangeAreaLockWithoutPwd, kuricp);
					Toast.makeText(SubCustomActivity.this, "epc:"+Functional.bytes_Hexstr(cr.epcid),
							Toast.LENGTH_SHORT).show();
					
				}
				catch (ReaderException e) {
					// TODO Auto-generated catch block
					Toast.makeText(SubCustomActivity.this, "操作失败:"+e.GetMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		tabHost_custom_lunrui.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String arg0) {
				// TODO Auto-generated method stub
				int j=tabHost_custom_lunrui.getCurrentTab();
				TabWidget tabIndicator=tabHost_custom_lunrui.getTabWidget();
				View vw=tabIndicator.getChildAt(j);
				vw.setBackgroundColor(Color.BLUE);
				int tc=tabHost_custom_lunrui.getTabContentView().getChildCount();
				for(int i=0;i<tc;i++)
				{
					if(i!=j)
					{
						View vw2=tabIndicator.getChildAt(i);
						vw2.setBackgroundColor(Color.TRANSPARENT);
					}
				}
				 
			}
			
		});
	}
	
	@Override
	protected void onDestroy()
	{
		 String fc= myapp.spf.GetString("Blue_FrameCount");
		 if(!fc.equals(""))
		 myapp.CommBth.SetFrameParams(Integer.parseInt(fc), 0);
		 else
			 myapp.CommBth.SetFrameParams(0, 0);
		 super.onDestroy();
	}
}
