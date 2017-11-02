package com.example.module_android_bluedemo_532ant;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
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
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.silionmodule.Functional;
import com.silionmodule.Gen2;
import com.silionmodule.Gen2.Gen2LockAction;
import com.silionmodule.Gen2.Gen2Password;
import com.silionmodule.Gen2.Gen2TagFilter;
import com.silionmodule.Gen2.MemBankE;
import com.silionmodule.ParamNames;
import com.silionmodule.ReaderException;
import com.silionmodule.TagData;

public class Sub3TabActivity extends Activity {

	String[] spibank={"保留区","EPC区","TID区","用户区"};
	String[] spifbank={"EPC区","TID区","用户区"};
	String[] spilockbank={"访问密码","销毁密码","EPCbank","TIDbank","USERbank"};
	String[] spilocktype={"解锁定","暂时锁定","永久锁定"};

	Button button_readop,button_writeop,button_writepc,button_lockop,button_kill;
	Spinner spinner_opbank,spinner_bankr,spinner_bankw,
	spinner_lockbank,spinner_locktype;
	TabHost tabHost_op;
	 RadioGroup gr_opant,gr_match,gr_enablefil;
	 CheckBox cb_pwd;
	// Switch swt_fil;
	private ArrayAdapter arradp_bank,arradp_fbank,arradp_lockbank,arradp_locktype;
	MyApplication myapp;
	Gen2TagFilter g2tf=null;

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

	 private void SetOpant() throws ReaderException
	 {

			int opa=SortGroup(gr_opant)+1;
			myapp.Mreader.paramSet(ParamNames.Reader_Tagop_Antenna,opa);

	 }

	 private void SetFiler() throws ReaderException
	 {
		 if(SortGroup(gr_enablefil)==1)
		 {
			 byte[] fdata;
			 EditText et=(EditText)findViewById(R.id.editText_opfilterdata);
			 fdata = Functional.hexstr_Bytes(et.getText().toString());
			 int it=spinner_opbank.getSelectedItemPosition()+1;
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
			 EditText etadr=(EditText)findViewById(R.id.editText_opfilsadr);

			 g2tf = new Gen2TagFilter(mb,
					 Integer.valueOf(etadr.getText().toString()),
					 fdata, fdata.length*8);
			 int ma=SortGroup(gr_match);
			 if(ma==1)
				 g2tf.SetInvert(true);
		 }
		 else
			 g2tf=null;
	 }

	 private void SetPassword() throws ReaderException
	 {
		    EditText et=(EditText)findViewById(R.id.editText_password);
		   // String temp=et.getText().toString();
		    if(cb_pwd.isChecked())
		    {
		    	Gen2Password g2pw = new Gen2Password(et.getText().toString());
				myapp.Mreader.paramSet(ParamNames.Reader_Gen2_AccessPassword, g2pw);

		    }
		    else
		    {
		    	Gen2Password g2pw = new Gen2Password("00000000");
		    	myapp.Mreader.paramSet(ParamNames.Reader_Gen2_AccessPassword, g2pw);
		    }
	 }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab3_tablelayout);
		Application app=getApplication();
		myapp=(MyApplication) app;

				// 获取TabHost对象
				 // 得到TabActivity中的TabHost对象
				tabHost_op = (TabHost) findViewById(R.id.tabhost3);
				// 如果没有继承TabActivity时，通过该种方法加载启动tabHost
				tabHost_op.setup();
				tabHost_op.getTabWidget().setOrientation(LinearLayout.VERTICAL);
				//tabHost2.addTab(tabHost2.newTabSpec("tab1").setIndicator("初始化",
				//getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab11));
			    //tabHost2.addTab(tabHost2.newTabSpec("tab1").setIndicator(createIndicatorView(this, tabHost2, "1111"))
			    	//	.setContent(R.id.tab11));

				tabHost_op.addTab(tabHost_op.newTabSpec("tab1").setIndicator(createIndicatorView(this, tabHost_op, "读标签"))
						.setContent(R.id.tab3_sub_read));
				tabHost_op.addTab(tabHost_op.newTabSpec("tab2").setIndicator(createIndicatorView(this, tabHost_op, "写标签"))
						.setContent(R.id.tab3_sub_write));
				tabHost_op.addTab(tabHost_op.newTabSpec("tab3").setIndicator(createIndicatorView(this, tabHost_op, "锁与销毁"))
						.setContent(R.id.tab3_sub_lockkill));

				TabWidget tw=tabHost_op.getTabWidget();
				tw.getChildAt(0).setBackgroundColor(Color.BLUE);

				spinner_bankr = (Spinner) findViewById(R.id.spinner_bankr);
				// View layout = getLayoutInflater().inflate(R.layout.tab3_tablelayout_write, null);
				spinner_bankw= (Spinner)findViewById(R.id.spinner_bankw);
				spinner_opbank= (Spinner)findViewById(R.id.spinner_opfbank);
				arradp_bank = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spibank);
				arradp_bank.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner_bankr.setAdapter(arradp_bank);
				spinner_bankw.setAdapter(arradp_bank);
				spinner_opbank.setAdapter(arradp_bank);

				spinner_lockbank = (Spinner) findViewById(R.id.spinner_lockbank);
				 //将可选内容与ArrayAdapter连接起来
				arradp_lockbank = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spilockbank);
				 //设置下拉列表的风格
				arradp_lockbank.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				 //将adapter 添加到spinner中
				spinner_lockbank.setAdapter(arradp_lockbank);

				spinner_locktype = (Spinner) findViewById(R.id.spinner_locktype);
				arradp_locktype = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spilocktype);
				arradp_locktype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner_locktype.setAdapter(arradp_locktype);

				button_readop = (Button) findViewById(R.id.button_read);
				button_writeop = (Button) findViewById(R.id.button_write);
				button_writepc = (Button) findViewById(R.id.button_wepc);
				button_lockop=(Button)findViewById(R.id.button_lock);
				button_kill=(Button)findViewById(R.id.button_kill);
				gr_opant=(RadioGroup)findViewById(R.id.radioGroup_opant);
				gr_match=(RadioGroup)findViewById(R.id.radioGroup_opmatch);
				gr_enablefil=(RadioGroup)findViewById(R.id.radioGroup_enableopfil);
				cb_pwd=(CheckBox)findViewById(R.id.checkBox_opacepwd);
				 EditText et=(EditText)findViewById(R.id.editText_opfilterdata);
				 et.setText(myapp.Rparams.Curepc);

				 spinner_opbank.setSelection(1);
				 spinner_bankr.setSelection(1);
				 spinner_bankw.setSelection(1);
				 spinner_lockbank.setSelection(2);
				 spinner_locktype.setSelection(1);

				button_readop.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						try {
							EditText etdr=(EditText)findViewById(R.id.editText_datar);
							etdr.setText("");
							SetOpant();
							SetPassword();
							SetFiler();

							EditText etcount=(EditText)findViewById(R.id.editText_opcountr);
							EditText etadr=(EditText)findViewById(R.id.editText_startaddr);
							EditText etdrw=(EditText)findViewById(R.id.editText_dataw);
							short[] epddata =null;

							int trycount=3;
							do{
								try{
									epddata=myapp.Mreader.ReadTagMemWords(g2tf,
										MemBankE.values()[spinner_bankr.getSelectedItemPosition()],
										Integer.valueOf(etadr.getText().toString()),
										Integer.valueOf(etcount.getText().toString()));
									break;
								}catch(ReaderException ex)
								{ trycount--;

								if(trycount<1)
									throw ex;
								 continue;
								}

							}while(epddata==null);

							if(epddata!=null)
							{etdr.setText(Functional.shorts_HexStr(epddata));
							etdrw.setText(Functional.shorts_HexStr(epddata));
							}
							else
							{
								etdr.setText("");
								etdrw.setText("");
							}
						} catch (ReaderException e) {
							// TODO Auto-generated catch block
							Toast.makeText(Sub3TabActivity.this, "读取失败:"+e.GetMessage(),
									Toast.LENGTH_SHORT).show();
						}
						catch (Exception e) {
							// TODO Auto-generated catch block
							Toast.makeText(Sub3TabActivity.this, "读失败:"+e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}

					}

				});

				button_writeop.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						try {
							SetOpant();
							SetPassword();
							SetFiler();

							//EditText etcount=(EditText)findViewById(R.id.editText_opcountw);
							EditText etadr=(EditText)findViewById(R.id.editText_startaddrw);
							EditText etdr=(EditText)findViewById(R.id.editText_dataw);
							if(etdr.getText().toString().equals(""))
							{
								Toast.makeText(Sub3TabActivity.this, "无数据",
										Toast.LENGTH_SHORT).show();
							}
							int trycount=3;
							do{
								try{
									myapp.Mreader.WriteTagMemWords(g2tf,
											MemBankE.values()[spinner_bankw.getSelectedItemPosition()],
											Integer.valueOf(etadr.getText().toString()),
											Functional.hexstr_Short16s(etdr.getText().toString()));

									Toast.makeText(Sub3TabActivity.this, "写成功",
											Toast.LENGTH_SHORT).show();
									break;
								}catch(ReaderException ex)
								{ trycount--;

								if(trycount<1)
									throw ex;
								 continue;
								}

							}while(true);

						} catch (ReaderException re) {
							// TODO Auto-generated catch block
							Toast.makeText(Sub3TabActivity.this, "写失败:"+re.GetMessage(),
									Toast.LENGTH_SHORT).show();
						}
						catch (Exception e) {
							// TODO Auto-generated catch block
							Toast.makeText(Sub3TabActivity.this, "写失败:"+e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}


					}

				});

				button_writepc.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						try {
							SetOpant();
							SetPassword();
							SetFiler();

							EditText etadr=(EditText)findViewById(R.id.editText_startaddrw);
							EditText etdr=(EditText)findViewById(R.id.editText_dataw);

							int trycount=3;
							do{
								try{

									myapp.Mreader.InitTag(g2tf,new TagData(etdr.getText().toString()));
									Toast.makeText(Sub3TabActivity.this, "写成功",
											Toast.LENGTH_SHORT).show();
									break;
								}catch(ReaderException ex)
								{ trycount--;

								if(trycount<1)
									throw ex;
								 continue;
								}

							}while(true);

						} catch (ReaderException e) {
							// TODO Auto-generated catch block
							Toast.makeText(Sub3TabActivity.this, "写失败:"+e.GetMessage(),
									Toast.LENGTH_SHORT).show();
							return;
						}catch (Exception e) {
							// TODO Auto-generated catch block
							Toast.makeText(Sub3TabActivity.this, "写失败:"+e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}


					}

				});

				button_lockop.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						try {
							SetOpant();
							SetPassword();
							SetFiler();

							/*定义锁类型，epc临时锁*/
							Gen2LockAction g2la = null;
							int lbank=spinner_lockbank.getSelectedItemPosition();
							int ltype=spinner_locktype.getSelectedItemPosition();
							if(lbank==0)
							{
								if(ltype==0)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.ACCESS_UNLOCK);
								else if(ltype==1)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.ACCESS_LOCK);
								else if(ltype==2)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.ACCESS_PERMALOCK);

							}
							else if(lbank==1)
							{
								if(ltype==0)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.KILL_UNLOCK);
								else if(ltype==1)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.KILL_LOCK);
								else if(ltype==2)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.KILL_PERMALOCK);
							}
							else if(lbank==2)
							{
								if(ltype==0)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.EPC_UNLOCK);
								else if(ltype==1)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.EPC_LOCK);
								else if(ltype==2)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.EPC_PERMALOCK);
							}
							else if(lbank==3)
							{
								if(ltype==0)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.TID_UNLOCK);
								else if(ltype==1)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.TID_LOCK);
								else if(ltype==2)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.TID_PERMALOCK);
							}
							else if(lbank==4)
							{
								if(ltype==0)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.USER_UNLOCK);
								else if(ltype==1)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.USER_LOCK);
								else if(ltype==2)
									g2la = new Gen2LockAction(Gen2.Gen2LockAction.USER_PERMALOCK);
							}

							int trycount=3;
							do{
								try{
									myapp.Mreader.LockTag(g2tf, g2la);
									Toast.makeText(Sub3TabActivity.this, "锁成功",
											Toast.LENGTH_SHORT).show();
									break;
								}catch(ReaderException ex)
								{ trycount--;

								if(trycount<1)
									throw ex;
								 continue;
								}

							}while(true);

						} catch (ReaderException e) {
							// TODO Auto-generated catch block
							Toast.makeText(Sub3TabActivity.this, "锁失败:"+e.GetMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}
				});

				button_kill.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						try {
							 SetOpant();
							 SetFiler();
							 EditText et=(EditText)findViewById(R.id.editText_password);

							 int trycount=3;
							do{
								try{
									myapp.Mreader.KillTag(g2tf, Integer.valueOf(et.getText().toString()));
									Toast.makeText(Sub3TabActivity.this, "KILL成功",
											Toast.LENGTH_SHORT).show();
									break;
								}catch(ReaderException ex)
								{ trycount--;

								if(trycount<1)
									throw ex;
								 continue;
								}

							}while(true);

						} catch (ReaderException e) {
							// TODO Auto-generated catch block
							Toast.makeText(Sub3TabActivity.this, "销毁失败:"+e.GetMessage(),
									Toast.LENGTH_SHORT).show();
						}

					}

				});

				tabHost_op.setOnTabChangedListener(new OnTabChangeListener(){

					@Override
					public void onTabChanged(String arg0) {
						// TODO Auto-generated method stub
						int j=tabHost_op.getCurrentTab();
						TabWidget tabIndicator=tabHost_op.getTabWidget();
						View vw=tabIndicator.getChildAt(j);
						vw.setBackgroundColor(Color.BLUE);
						int tc=tabHost_op.getTabContentView().getChildCount();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
	        if((System.currentTimeMillis()-myapp.exittime) > 2000){
	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
	            myapp.exittime = System.currentTimeMillis();
	        } else {
	            finish();

	        }
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	protected void onResume()
	{

		 super.onResume();
	}
	protected void onStop()
	{
		 super.onStop();
	}
}
