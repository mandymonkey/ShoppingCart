package com.example.module_android_bluedemo_532ant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.bth.api.cls.BlueTooth4_C.BLEServices;
import com.bth.api.cls.CommBlueDev;
import com.bth.api.cls.Comm_Bluetooth;
import com.function.SPconfig;
import com.function.myAdapter;
import com.silionmodule.HardWareDetector;
import com.silionmodule.ParamNames;
import com.silionmodule.Reader;
import com.silionmodule.ReaderException;
import com.silionmodule.StatusEventListener;
import com.silionmodule.ReaderType.AntTypeE;
import com.tool.log.LogD;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Sub1TabActivity extends Activity {

	Button button_search, button_connect, button_disconnect,
			button_setblueframe;
	CheckBox cb_blue, cb_ble, cb_rebo,cb_readmode;
	ListView listview;
	TextView grid_1, grid_2, grid_3, grid_4;
	TabHost tabHost_connect;

	Map<String, CommBlueDev> Devaddrs = new LinkedHashMap<String, CommBlueDev>();

	private Spinner spinner_conectway, spinner_antports, spinner_module,
			spinner_bluetype;
	private ArrayAdapter arradp_cetway, arradp_antports, arradp_module,
			arradp_bluetype;

	String[] strbltp = { "CJJRJG", "HMSoft", "DXYBT021", "CWBLUE", "no special" };
	String[] pdaatpot = { "一天线", "双天线", "三天线", "四天线" };
	String[] strconectway = { "被动式", "主动式" };
	String[] strmodule = { "5100", "MT100" };
	MyApplication myapp;

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				// 取出参数更新控件
				showlist();
			}
			}
		}
	};

	private Comm_Bluetooth.SearchCallback Cbscallback = new Comm_Bluetooth.SearchCallback() {

		@Override
		public void OnSearch(CommBlueDev device) {
			// TODO Auto-generated method stub
			System.out.println("search:" + device.getName() + " "
					+ device.getAddress());
			// if(!Devaddrs.containsKey(device.getAddress()))
			// {
			System.out.println("To add it");
			synchronized (MainActivity.class) {
				Devaddrs.put(device.getAddress(), device);
			}
			// showlist();
			Iterator<Entry<String, CommBlueDev>> iesb = Devaddrs.entrySet()
					.iterator();
			int i = 1;
			while (iesb.hasNext()) {
				CommBlueDev bd = iesb.next().getValue();
				System.out.println("Devaddrs " + String.valueOf(i++) + " "
						+ bd.getName() + " " + bd.getAddress());
			}

			Message msg = new Message();
			msg.what = 0;
			Bundle bundle = new Bundle();
			msg.setData(bundle);
			handler.sendMessage(msg);

			// }

		}

	};

	private View createIndicatorView(Context context, TabHost tabHost,
			String title) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View tabIndicator = inflater.inflate(R.layout.tab_indicator_vertical,
				tabHost.getTabWidget(), false);
		final TextView titleView = (TextView) tabIndicator
				.findViewById(R.id.tv_indicator);
		titleView.setText(title);

		return tabIndicator;
	}

	String[] Coname = new String[] { "sort", "blue", "type", "rssi" };

	private synchronized void showlist() {

		List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();
		synchronized (MainActivity.class) {
			Iterator<Entry<String, CommBlueDev>> iesb = Devaddrs.entrySet()
					.iterator();
			int j = 1;
			while (iesb.hasNext()) {
				CommBlueDev bd = iesb.next().getValue();
				Map<String, String> m = new HashMap<String, String>();
				m.put(Coname[0], String.valueOf(j));
				j++;
				// System.out.println("List:"+bd.getName()+" "+bd.getAddress());
				m.put(Coname[1], bd.getName() + "\r\n" + bd.getAddress());
				String typen = bd.getType() == 1 ? "2.0" : "BLE";
				m.put(Coname[2], typen);
				m.put(Coname[3], String.valueOf(bd.RssiValue()));
				list.add(m);
			}
		}

		ListAdapter adapter = new myAdapter(this, list,
				R.layout.listitemview_blue, Coname, new int[] { R.id.View_sort,
						R.id.View_bluetooth, R.id.View_type, R.id.View_rssi });

		// layout为listView的布局文件，包括三个TextView，用来显示三个列名所对应的值
		// ColumnNames为数据库的表的列名
		// 最后一个参数是int[]类型的，为view类型的id，用来显示ColumnNames列名所对应的值。view的类型为TextView
		listview.setAdapter(adapter);
		System.out.println("listcount:" + listview.getCount());
	}

	/*
	 * protected void dialog() { AlertDialog.Builder builder= new
	 * Builder(Sub1TabActivity.this); builder.setMessage("确认退出吗？");
	 * builder.setTitle("提示"); builder.setPositiveButton("确认", new
	 * OnClickListener(){
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * dialog.dismiss(); Sub1TabActivity.this.finish(); } });
	 * builder.setNegativeButton("取消", new OnClickListener(){
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which){
	 * dialog.dismiss(); } }); builder.create().show(); }
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab1_tablelayout_connect);

		Application app = getApplication();
		myapp = (MyApplication) app;
		myapp.CommBth = new Comm_Bluetooth(this);
		myapp.spf = new SPconfig(this);

		/*
		 * // 获取TabHost对象 // 得到TabActivity中的TabHost对象 tabHost_connect =
		 * (TabHost) findViewById(R.id.tabhost1);
		 * 
		 * // 如果没有继承TabActivity时，通过该种方法加载启动tabHost tabHost_connect.setup();
		 * 
		 * tabHost_connect.getTabWidget().setOrientation(LinearLayout.VERTICAL);
		 * //tabHost2.addTab(tabHost2.newTabSpec("tab1").setIndicator("初始化",
		 * //getResources
		 * ().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab11));
		 * 
		 * //tabHost2.addTab(tabHost2.newTabSpec("tab1").setIndicator(
		 * createIndicatorView(this, tabHost2, "1111")) //
		 * .setContent(R.id.tab11));
		 * tabHost_connect.addTab(tabHost_connect.newTabSpec
		 * ("tab1").setIndicator(createIndicatorView(this, tabHost_connect,
		 * "连接")) .setContent(R.id.tab1_sub_connect));
		 * tabHost_connect.addTab(tabHost_connect
		 * .newTabSpec("tab2").setIndicator(createIndicatorView(this,
		 * tabHost_connect, "通信")) .setContent(R.id.tab1_sub_debug));
		 * 
		 * TabWidget tw=tabHost_connect.getTabWidget();
		 * tw.getChildAt(0).setBackgroundColor(Color.BLUE);
		 */

		// View layout =
		// getLayoutInflater().inflate(R.layout.tab1_tablelayout_power, null);
		// spinner_pdatype= (Spinner)layout.findViewById(R.id.spinner_pdatype);

		grid_1 = (TextView) this.findViewById(R.id.textView_sort);
		grid_2 = (TextView) this.findViewById(R.id.textView_bluetooth);
		grid_3 = (TextView) this.findViewById(R.id.textView_type);
		grid_4 = (TextView) this.findViewById(R.id.textView_rssi);

		grid_1.setBackgroundColor(Color.BLACK);
		grid_2.setBackgroundColor(Color.BLACK);
		grid_3.setBackgroundColor(Color.BLACK);
		grid_4.setBackgroundColor(Color.BLACK);

		grid_1.setTextColor(Color.WHITE);
		grid_2.setTextColor(Color.WHITE);
		grid_3.setTextColor(Color.WHITE);
		grid_4.setTextColor(Color.WHITE);

		cb_blue = (CheckBox) this.findViewById(R.id.checkBox_bth2);
		cb_ble = (CheckBox) this.findViewById(R.id.checkBox_bthble);
		
		cb_readmode=(CheckBox) this.findViewById(R.id.checkBox_readmode);
		cb_rebo = (CheckBox) this.findViewById(R.id.checkBox_rematch);
		listview = (ListView) this.findViewById(R.id.listView_params);

		spinner_antports = (Spinner) findViewById(R.id.spinner_antports);
		arradp_antports = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, pdaatpot);
		arradp_antports
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_antports.setAdapter(arradp_antports);

		spinner_bluetype = (Spinner) findViewById(R.id.spinner_bluetype);
		arradp_bluetype = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, strbltp);
		arradp_bluetype
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_bluetype.setAdapter(arradp_bluetype);

		spinner_conectway = (Spinner) findViewById(R.id.spinner_connectway);
		arradp_cetway = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, strconectway);
		arradp_cetway
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_conectway.setAdapter(arradp_cetway);

		spinner_module = (Spinner) findViewById(R.id.spinner_module);
		arradp_module = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, strmodule);
		arradp_module
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_module.setAdapter(arradp_module);

		button_search = (Button) this.findViewById(R.id.button_search);
		button_connect = (Button) this.findViewById(R.id.button_connect);
		button_disconnect = (Button) this.findViewById(R.id.button_disconnect);
		button_disconnect.setEnabled(false);

		String classsearch = myapp.spf.GetString("CLASSSEARCH");
		if(classsearch!=null&&classsearch.equals("1"))
			cb_blue.setChecked(true);
		else 
			cb_blue.setChecked(false);
		String blesearch = myapp.spf.GetString("BLESEARCH");
		if(blesearch!=null&&blesearch.equals("1"))
			cb_ble.setChecked(true);
		else
			cb_blue.setChecked(false);
		
		String modulestr = myapp.spf.GetString("MODULE");

		String conwaystr = myapp.spf.GetString("CONWAY");

		String bluetype = myapp.spf.GetString("BLTYPE");
		
		String anttype = myapp.spf.GetString("ANTYPE");
		
		String val= myapp.spf.GetString("Read_mode");
		if(val!=null&&val.equals("1"))
			cb_readmode.setChecked(true);
		else if(val!=null&&val.equals("0"))
			cb_readmode.setChecked(false);
		
		if (!anttype.equals("")) {
			spinner_antports.setSelection(Integer.valueOf(anttype));
		}
		spinner_antports.setEnabled(false);
		if (!modulestr.equals("")) {
			spinner_module.setSelection(Integer.valueOf(modulestr));
		}
		if (!conwaystr.equals("")) {
			spinner_conectway.setSelection(Integer.valueOf(conwaystr));
		}
		if (!bluetype.equals("")) {
			spinner_bluetype.setSelection(Integer.valueOf(bluetype));
		}
		button_search.setOnClickListener(new OnClickListener() {
			@Override
			public synchronized void onClick(View arg0) {
				// TODO Auto-generated method stub
				String txt = button_search.getText().toString().trim();
				if (txt.equals("搜索")) {
					synchronized (MainActivity.class) {
						Devaddrs.clear();
					}
					showlist();
					int selectoption = 0;
					if (cb_blue.isChecked())
						selectoption |= Comm_Bluetooth.SearchOption.Blue
								.value();
					if (cb_ble.isChecked())
						selectoption |= Comm_Bluetooth.SearchOption.BLE.value();
					if (selectoption == 0) {
						Toast.makeText(Sub1TabActivity.this, "请选择要搜索的蓝牙设备",
								Toast.LENGTH_SHORT).show();
						return;
					}

					myapp.spf.SaveString("CLASSSEARCH",cb_blue.isChecked()?"1":"0");

					myapp.spf.SaveString("BLESEARCH",cb_ble.isChecked()?"1":"0");

					int re = myapp.CommBth.StartSearch(selectoption,
							Cbscallback);

					if (re == 0) {
						Toast.makeText(Sub1TabActivity.this, "开始搜索蓝牙 成功",
								Toast.LENGTH_SHORT).show();

					} else if (re == 1) {
						Toast.makeText(Sub1TabActivity.this, "开始搜索蓝牙:1 失败",
								Toast.LENGTH_SHORT).show();

					} else if (re == 2) {
						Toast.makeText(Sub1TabActivity.this, "开始搜索蓝牙:2 失败",
								Toast.LENGTH_SHORT).show();

					} else if (re == 3) {
						Toast.makeText(Sub1TabActivity.this,
								"开始搜索蓝牙:1,2 失败，将重启蓝牙，请等待重启完成",
								Toast.LENGTH_SHORT).show();
						myapp.CommBth.Disable();
						myapp.CommBth.Enable();
						myapp.CommBth = new Comm_Bluetooth(getParent());
						return;
					}

					button_search.setText("停止");

				} else {
					myapp.CommBth.StopSearch();
					button_search.setText("搜索");

				}

			}
		});

		button_connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				myapp.CommBth.StopSearch();
				button_search.setText("搜索");
				
				if (myapp.m == null) {
					Toast.makeText(Sub1TabActivity.this, "请扫描并选中一个蓝牙读写器",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String mes = myapp.m.get("blue");
				String[] messit = mes.split("\r\n");

				String typestr = myapp.m.get("type");
				myapp.Address = messit[1];
				LogD.LOGD("connect:"+messit[0]+" "+messit[1]);
				if (typestr == "2.0") {
					if (cb_rebo.isChecked()) {
						boolean re = myapp.CommBth.CancleMatch(messit[1]);
						Toast.makeText(Sub1TabActivity.this,
								"取消蓝牙设备匹配:" + String.valueOf(re),
								Toast.LENGTH_SHORT).show();
					}

					if (myapp.CommBth.ToMatch(messit[1]) == BluetoothDevice.BOND_BONDED) {
						// /*
						int re = myapp.CommBth.Connect(messit[1], 2);

						// 连接成功
						if (re == 0) {
							ConnectEvent();
						} else
							Toast.makeText(Sub1TabActivity.this,
									"连接蓝牙设备失败:" + String.valueOf(re),
									Toast.LENGTH_SHORT).show();
						// */
					} else
						Toast.makeText(Sub1TabActivity.this, "匹配蓝牙设备失败",
								Toast.LENGTH_SHORT).show();
				} else {
					LogD.LOGD("connect_2_2");
					int re = -1;

					do {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} while (myapp.CommBth.ConnectState() != Comm_Bluetooth.DISCONNECTED);
					if (myapp.CommBth.ConnectState() == Comm_Bluetooth.DISCONNECTED
							&& myapp.CommBth.GetConnectAddr() != messit[1])
						re = myapp.CommBth.Connect(messit[1], 4);
					else
						re = 0;
					LogD.LOGD("connect_3");
					// 连接成功
					if (re == 0) {
						String Addr = "", Uuid = "", Uuid_read = "", Uuid_write = "", Uuid_pass = "", Val_pass = "";
						boolean needpwd = false;
						Addr = myapp.spf.GetString("Blue_Address");
						if (spinner_bluetype.getSelectedItemPosition() == 4) {
							Uuid = myapp.spf.GetString("Uuid");
							Uuid_read = myapp.spf.GetString("Uuid_read");
							Uuid_write = myapp.spf.GetString("Uuid_write");
							Uuid_pass = myapp.spf.GetString("Uuid_pass");
							Val_pass = myapp.spf.GetString("Val_pass");
						} else {
							if (spinner_bluetype.getSelectedItemPosition() == 0
									|| spinner_bluetype
											.getSelectedItemPosition() == 1) {
								Uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
								Uuid_read = "0000ffe1-0000-1000-8000-00805f9b34fb";
								Uuid_write = "0000ffe1-0000-1000-8000-00805f9b34fb";
							} else if (spinner_bluetype
									.getSelectedItemPosition() == 2) {
								Uuid = "0000fea0-0000-1000-8000-00805f9b34fb";
								Uuid_read = "0000fea2-0000-1000-8000-00805f9b34fb";
								Uuid_write = "0000fea1-0000-1000-8000-00805f9b34fb";
								Uuid_pass = "0000ffa0-0000-1000-8000-00805f9b34fb";
								Val_pass = "0800313131313131";
							} else if (spinner_bluetype
									.getSelectedItemPosition() == 3) {
								Uuid = "00001C00-D102-11E1-9B23-000efB0000A5";
								Uuid_read = "00001C0f-D102-11E1-9B23-000efB0000A5";
								Uuid_write = "00001C01-D102-11E1-9B23-000efB0000A5";
							}

						}

						/*
						 * if(spinner_bluetype.getSelectedItemPosition()==4&&!
						 * Uuid_pass.equals("")&&Val_pass.equals(""))
						 * needpwd=true;
						 * 
						 * boolean issame=false;
						 * 
						 * if(spinner_bluetype.getSelectedItemPosition()==4&&
						 * Addr.equals(myapp.Address)) issame=true;
						 */

						if (Uuid.equals("")
								&& Uuid_read.equals("")
								&& Uuid_write.equals("")
								|| spinner_bluetype.getSelectedItemPosition() == 4) {

							myapp.BackResult = 0;
							Intent intent = new Intent(Sub1TabActivity.this,
									OprationBLEActivity.class);
							startActivityForResult(intent, 0);
						} else {
							LogD.LOGD("connect_4_2");
							List<BLEServices> lbe = myapp.CommBth
									.FindServices(8000);
							if (lbe != null && lbe.size() > 0) {
								if (!Uuid_pass.equals(""))
									{
									myapp.CommBth.SetServiceUUIDs(Uuid,Uuid_pass);

									if (myapp.CommBth.ToMatch(Val_pass) != 0) {
										Toast.makeText(Sub1TabActivity.this,
												"密码匹配失败", Toast.LENGTH_SHORT)
												.show();
										return;
									}
								}
								myapp.CommBth.SetServiceUUIDs(Uuid,
										Uuid_read, Uuid_write);
								
								Toast.makeText(Sub1TabActivity.this,
										"连接蓝牙设备成功,将连接读写器", Toast.LENGTH_SHORT)
										.show();
								myapp.BackResult = 1;
								ConnectEvent();
								return;
							} else {
								myapp.CommBth.DisConnect();
								Toast.makeText(Sub1TabActivity.this,
										"连接蓝牙设备服务失败", Toast.LENGTH_SHORT)
										.show();
								return;
							}
						}

						Toast.makeText(Sub1TabActivity.this, "连接蓝牙设备成功",
								Toast.LENGTH_SHORT).show();
					} else
						Toast.makeText(Sub1TabActivity.this,
								"连接蓝牙设备失败:" + String.valueOf(re),
								Toast.LENGTH_SHORT).show();
				}
			}
		});
		button_disconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LogD.LOGD("disconnect_1");
				myapp.CommBth.StopSearch();
				button_search.setText("搜索");
				if (myapp.Mreader != null) {
					LogD.LOGD("disconnect_2");
					myapp.Mreader.DisConnect();
				}
				DisConnectHandleUI();
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				@SuppressWarnings({ "unchecked" })
				Map<String, String> m = (Map<String, String>) listview
						.getItemAtPosition(arg2);
				myapp.CommBth.StopSearch();
				button_search.setText("搜索");
				myapp.m = m;

				String mes = m.get("blue");
				String[] messit = mes.split("\r\n");
				TextView ev = (TextView) findViewById(R.id.TextView_adr);
				ev.setText(messit[1]);
				// /*
				arg1.setBackgroundColor(Color.YELLOW);

				for (int i = 0; i < listview.getCount(); i++) {
					if (i != arg2) {
						View v = listview.getChildAt(i);
						ColorDrawable cd = (ColorDrawable) v.getBackground();
						if (Color.YELLOW == cd.getColor()) {
							int[] colors = { Color.WHITE,
									Color.rgb(219, 238, 244) };// RGB颜色
							v.setBackgroundColor(colors[i % 2]);// 每隔item之间颜色不同
						}
					}
				}
				// */
			}
		});

		/*
		 * tabHost_connect.setOnTabChangedListener(new OnTabChangeListener(){
		 * 
		 * @Override public void onTabChanged(String arg0) { // TODO
		 * Auto-generated method stub int j=tabHost_connect.getCurrentTab();
		 * TabWidget tabIndicator=tabHost_connect.getTabWidget(); View
		 * vw=tabIndicator.getChildAt(j); vw.setBackgroundColor(Color.BLUE); int
		 * tc=tabHost_connect.getTabContentView().getChildCount(); for(int
		 * i=0;i<tc;i++) { if(i!=j) { View vw2=tabIndicator.getChildAt(i);
		 * vw2.setBackgroundColor(Color.TRANSPARENT); } }
		 * 
		 * }
		 * 
		 * });
		 */
	}

	private void ConnectEvent() {
		// /*
		boolean isok = false;
		if (myapp.CommBth.getRemoveType() == 4) {
			isok = myapp.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED
					&& myapp.CommBth.IssetUUID() == true
					&& myapp.BackResult == 1;
		} else
			isok = true;

		if (isok) {

			try {
				myapp.Mode = spinner_conectway.getSelectedItemPosition();
				myapp.CommBth.Comm_SetParam(ParamNames.Communication_mode,myapp.Mode
						);
				String scount, stime;
				scount = myapp.spf.GetString("Blue_FrameCount");
				stime = myapp.spf.GetString("Blue_FrameTime");
				if (!scount.equals("") && !stime.equals("")) {
					try {
						int fcount = Integer.valueOf(scount);
						int ftime = Integer.valueOf(stime);
						myapp.CommBth.SetFrameParams(fcount, ftime);
					} catch (Exception ex) {
						Toast.makeText(Sub1TabActivity.this, "读写器创建失败",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}

				if (myapp.Mode == 1) {
					if (spinner_module.getSelectedItemPosition() == 0)
						myapp.CommBth.Comm_SetParam(ParamNames.Communication_module,
								HardWareDetector.Module_Type.MODOULE_SLR5100
								);
					else
						myapp.CommBth.Comm_SetParam(ParamNames.Communication_module,
								HardWareDetector.Module_Type.MODOULE_R902_M1S
								);
				}
				myapp.CommBth.Comm_SetParam(ParamNames.Communication_Gpioctrants,4);
				myapp.CommBth.Comm_SetParam(ParamNames.Communication_isGpioCtrant, true);
						
				int antc = spinner_antports.getSelectedItemPosition() + 1;
				myapp.Rparams.antportc = antc;
				
				myapp.Mreader = Reader.Create(AntTypeE.valueOf(antc),
						myapp.CommBth);
				
				ConnectHandleUI();

			} catch (Exception ex) {
				myapp.CommBth.DisConnect();
				Toast.makeText(Sub1TabActivity.this, "读写器创建失败",
						Toast.LENGTH_SHORT).show();

			}

		} else {
			myapp.CommBth.DisConnect();
			Toast.makeText(Sub1TabActivity.this, "连接蓝牙设备服务失败",
					Toast.LENGTH_SHORT).show();
		}
		// */
	}

	private void ConnectHandleUI() {
		 if(cb_readmode.isChecked())
         {
			 myapp.CommBth.SetFrameParams(20, 0);
			try {
				Integer ieg = (Integer) myapp.Mreader
						.paramGet(ParamNames.InitMode_READ_MODE);
				if (myapp.Mode == 1) {
					if (ieg != 0) {
						try {
							myapp.Mreader.paramSet(
									ParamNames.InitMode_READ_MODE, "0");
						} catch (ReaderException e) {
							// TODO Auto-generated catch block
							Toast.makeText(getApplicationContext(), "配置模式失败",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else if (myapp.Mode == 0) {
					if (ieg == 0) {
						try {
							int val = 0xa5a55a5a;
							myapp.Mreader.paramSet(
									ParamNames.InitMode_READ_MODE,
									String.valueOf(val));
						} catch (ReaderException e) {
							// TODO Auto-generated catch block
							Toast.makeText(getApplicationContext(), "配置模式失败",
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			} catch (ReaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String scount, stime;
			scount = myapp.spf.GetString("Blue_FrameCount");
			stime = myapp.spf.GetString("Blue_FrameTime");
			int fcount,ftime;
			if (!scount.equals("") && !stime.equals("")) {
				  fcount = Integer.valueOf(scount);
				  ftime = Integer.valueOf(stime);
				
			}
			fcount=0;ftime=0;
			myapp.CommBth.SetFrameParams(fcount, ftime);
		}
		myapp.spf.SaveString("Read_mode", cb_readmode.isChecked()?"1":"0");
		this.button_search.setEnabled(false);
		this.button_connect.setEnabled(false);
		this.button_disconnect.setEnabled(true);
		if(myapp.CommBth.UUID_SERVICE.equals("0000fea0-0000-1000-8000-00805f9b34fb"))
		{
		myapp.CommBth.SetUUID("0000fea0-0000-1000-8000-00805f9b34fb",
				"0000fea3-0000-1000-8000-00805f9b34fb", false);
		myapp.CommBth.SetUUID("0000fea0-0000-1000-8000-00805f9b34fb",
				"0000fea4-0000-1000-8000-00805f9b34fb", false);
		myapp.CommBth.SetUUID("0000fea0-0000-1000-8000-00805f9b34fb",
				"0000fea5-0000-1000-8000-00805f9b34fb", false);
		myapp.CommBth.SetUUID("0000fea0-0000-1000-8000-00805f9b34fb",
				"0000fea6-0000-1000-8000-00805f9b34fb", false);
		myapp.CommBth.SetUUID("0000fea0-0000-1000-8000-00805f9b34fb",
				"0000fea7-0000-1000-8000-00805f9b34fb", false);
		myapp.spf.SaveString("Blue_Address", myapp.Address);
		}
		UUID uuid;
		uuid = myapp.CommBth.GetUUID(myapp.CommBth.UUID_SERVICE);
		if (uuid != null)
			myapp.spf.SaveString("Uuid", uuid.toString());
		else
			myapp.spf.SaveString("Uuid", "");

		uuid = myapp.CommBth.GetUUID(myapp.CommBth.UUID_READ);
		if (uuid != null)
			myapp.spf.SaveString("Uuid_read", uuid.toString());
		else
			myapp.spf.SaveString("Uuid_read", "");

		uuid = myapp.CommBth.GetUUID(myapp.CommBth.UUID_WRITE);
		if (uuid != null)
			myapp.spf.SaveString("Uuid_write", uuid.toString());
		else
			myapp.spf.SaveString("Uuid_write", "");

		uuid = myapp.CommBth.GetUUID(myapp.CommBth.UUID_PASS);
		if (uuid != null)
			myapp.spf.SaveString("Uuid_pass", uuid.toString());
		else
			myapp.spf.SaveString("Uuid_pass", "");

		myapp.spf.SaveString("Val_pass", myapp.bluepassword);

		myapp.spf.SaveString("MODULE",
				String.valueOf(spinner_module.getSelectedItemPosition()));
		myapp.spf.SaveString("CONWAY",
				String.valueOf(spinner_conectway.getSelectedItemPosition()));
		myapp.spf.SaveString("BLTYPE",
				String.valueOf(spinner_bluetype.getSelectedItemPosition()));
		myapp.spf.SaveString("ANTYPE",
				String.valueOf(spinner_antports.getSelectedItemPosition()));

		TabWidget tw = myapp.tabHost.getTabWidget();
		if (myapp.Mode == 0) {
			myapp.tabHost.addTab(MainActivity.tab3);
			myapp.tabHost.addTab(MainActivity.tab4_1);
			// tw.getChildAt(3).setEnabled(true);
			// tw.getChildAt(4).setEnabled(false);
		} else {
			// myapp.tabHost.addTab(MainActivity.tab3);
			myapp.tabHost.addTab(MainActivity.tab4_2);
			// tw.getChildAt(3).setEnabled(false);
			// tw.getChildAt(4).setEnabled(true);
		}

		tw.getChildAt(1).setVisibility(View.VISIBLE);

		myapp.tabHost.setCurrentTab(1);
	}

	private void DisConnectHandleUI() {
		this.button_search.setEnabled(true);
		button_disconnect.setEnabled(false);
		button_connect.setEnabled(true);
		int r = 1;
		if (myapp.Mode == 0)
			r = 2;
		// myapp.tabHost.getTabWidget().removeViews(2,r);

		myapp.tabHost.clearAllTabs();
		myapp.tabHost.setCurrentTab(0);
		myapp.tabHost.addTab(MainActivity.tab1);
		myapp.tabHost.addTab(MainActivity.tab2);
		TabWidget tw = myapp.tabHost.getTabWidget();
		tw.getChildAt(1).setVisibility(View.INVISIBLE);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ConnectEvent();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - myapp.exittime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				myapp.exittime = System.currentTimeMillis();
			} else {
				myapp.CommBth.StopSearch();
				finish();

			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onStop() {
		super.onStop();
	}
}
