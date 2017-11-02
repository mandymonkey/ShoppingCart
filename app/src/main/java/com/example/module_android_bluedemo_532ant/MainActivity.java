package com.example.module_android_bluedemo_532ant;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.bth.api.cls.Comm_Bluetooth;
import com.silionmodule.DataListener;
import com.silionmodule.Functional;
import com.silionmodule.ParamNames;
import com.silionmodule.ReaderException;
import com.silionmodule.SimpleReadPlan;
import com.silionmodule.StatusEventListener;
import com.silionmodule.TAGINFO;
import com.silionmodule.TagProtocol.TagProtocolE;
import com.silionmodule.TagReadData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends TabActivity { // ActionBarActivity

	private final int ThreadMODE = 1;
	// 读线程：
	private Thread runThread;
	boolean isrun;
	TextView tv_once, tv_state, tv_tags;
	ExpandableListView tab4_left, tab4_right;

	Button button_read, button_stop, button_pay,button_confirm,button_connect,button_send;
	private ListView listView;

	Map<String, TAGINFO> TagsMap= new LinkedHashMap<String, TAGINFO>();// 有序
	private Handler handler = new Handler();
	private MyApplication myapp;
	private SoundPool soundPool;
	boolean isreading;
	RadioGroup gr_match;
	public static TabHost tabHost;
	public static TabSpec tab1, tab2, tab3, tab4_1, tab4_2,tab5;

	private PermissionsChecker mPermissionsChecker;
	static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
	private static final int REQUEST_PERMISSION = 4;

	boolean isReceivingMsgReady;
	Socket clientSocket;
	BufferedReader mReader;
	BufferedWriter mWriter;
	EditText etMessage,etUserId,etIp,etPort;
	String friendId;
	TextView purchasenum;
	int tagNum;
	 Lock lockobj = new ReentrantLock();// 锁

	public class MyEpListAdapter extends ArrayAdapter {

		public MyEpListAdapter(Context context, int resource,
				int textViewResourceId, List objects) {
			super(context, resource, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
		}

	}

	public enum Region_Conf {
		RG_NONE(0x0), RG_NA(0x01), RG_EU(0x02), RG_EU2(0X07), RG_EU3(0x08), RG_KR(
				0x03), RG_PRC(0x06), RG_PRC2(0x0A), RG_OPEN(0xFF);

		int p_v;

		Region_Conf(int v) {
			p_v = v;
		}

		public int value() {
			return this.p_v;
		}

		public static Region_Conf valueOf(int value) { // 手写的从int到enum的转换函数
			switch (value) {
			case 0:
				return RG_NONE;
			case 1:
				return RG_NA;
			case 2:
				return RG_EU;
			case 7:
				return RG_EU2;
			case 8:
				return RG_EU3;
			case 3:
				return RG_KR;
			case 6:
				return RG_PRC;
			case 0x0A:
				return RG_PRC2;
			case 0xff:
				return RG_OPEN;
			}
			return null;
		}
	}

	private void startPermissionsActivity() {
		PermissionsActivity.startActivityForResult(this, REQUEST_PERMISSION,
				PERMISSIONS);
	}

	public Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				showlist();
				Bundle bd = msg.getData();
				TextView et = (TextView) findViewById(R.id.textView_readoncecnt);
				et.setText(String.valueOf(bd.get("OnceCount")));
				TextView et2 = (TextView) findViewById(R.id.textView_readallcnt);
				et2.setText(String.valueOf(TagsMap.size()));
//				final TextView textView_price = (TextView)findViewById(R.id.textView_price);
//				textView_price.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View view) {
//						textView_price.setText("金额：" + totalPrice);
//					}
//				});
//				final Button button_pay = (Button)findViewById(R.id.button_pay);
//				button_pay.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View view) {
//						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//						builder.setTitle("支付");
//						builder.setMessage("是否确认支付？");
//						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialogInterface, int i) {
//
//							}
//						});
//						builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialogInterface, int i) {
//								DBHelper dbHelper = new DBHelper(MainActivity.this,"intelligentshoppingcart",null,1);
//								SQLiteDatabase db = dbHelper.getReadableDatabase();
//								String sql_ispaid = "update shoppingcart set IsPaid = 1 where cartId = 'CJJ'";
//								db.execSQL(sql_ispaid);
//							}
//						});
//						AlertDialog dialog = builder.create();
//						dialog.show();
//					}
//				});
			}
			case 1: {
				Bundle bd = msg.getData();

				TextView et = (TextView) findViewById(R.id.textView_invstate);
				if (et != null)
					et.setText(" " + bd.get("Msg"));
				if (myapp.CommBth.ConnectState() != Comm_Bluetooth.CONNECTED) {
					myapp.CommBth.ReConnect();
				}
			}
			}
		}
	};

	public Handler handler3 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bd = msg.getData();
			switch (msg.what) {
			case 0: {
				String count = bd.get("Msg_cnt").toString();
				tv_once.setText(count);
				tv_tags.setText(bd.get("Msg_all").toString());

				break;

			}
			case 1: {
				button_read.setText("读");
				tv_state.setText(bd.get("Msg_error_1").toString());
				break;
			}
			case 2: {
				tv_state.setText(bd.get("Msg_error_2").toString());
				break;
			}

			}
		}
	};

	StatusEventListener SL = new StatusEventListener() {

		@Override
		public void StatusCatch(Object t) {
			// TODO Auto-generated method stub

			Message msg = new Message();
			msg.what = 1;
			Bundle bundle = new Bundle();
			bundle.putString("Msg", (String) t);
			msg.setData(bundle);
			// 发送消息到Handler
			handler2.sendMessage(msg);
		}

	};

	DataListener DL = new DataListener() {

		@Override
		public void ReadData(TagReadData[] t) {
			// TODO Auto-generated method stub

			TagReadData[] trds = t;
			if (trds != null && trds.length > 0) {
				soundPool.play(1, 1, 1, 0, 0, 1);
				for (int i = 0; i < trds.length; i++) {
					if (!TagsMap.containsKey(trds[i].EPCHexstr())) {
						TAGINFO Ti = new TAGINFO();
						Ti.AntennaID = (byte) trds[i].Antenna();
						Ti.CRC = trds[i].CRC();
						Ti.EmbededData = trds[i].AData();
						Ti.EmbededDatalen = (short) trds[i].AData().length;
						Ti.EpcId = trds[i].EPCbytes();
						Ti.Epclen = (short) trds[i].EPCbytes().length;
						Ti.Frequency = trds[i].Frequency();
						Ti.PC = trds[i].PC();
						Ti.protocol = -1;
						Ti.ReadCnt = trds[i].ReadCount();
						Ti.RSSI = trds[i].RSSI();
						Ti.TimeStamp = (int) trds[i].Time().getTime();
						TagsMap.put(trds[i].EPCHexstr(), Ti);
					} else {
						TAGINFO tf = TagsMap.get(trds[i].EPCHexstr());
						tf.ReadCnt += trds[i].ReadCount();
						tf.RSSI = trds[i].RSSI();
						tf.Frequency = trds[i].Frequency();
					}
				}
			}

			Message msg = new Message();
			msg.what = 0;
			Bundle bundle = new Bundle();
			bundle.putInt("OnceCount", trds.length);
			msg.setData(bundle);
			// 发送消息到Handler
			handler2.sendMessage(msg);
		}

	};

	MyEpListAdapter mttab1adp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);

		soundPool.load(this, R.raw.beep, 1);

		tabHost = (TabHost) findViewById(android.R.id.tabhost);

		tabHost.setup();
		tab1 = tabHost
				.newTabSpec("tab1")
				.setIndicator("连接",
						getResources().getDrawable(R.drawable.ic_launcher))
				.setContent(new Intent(this, Sub1TabActivity.class));
		tabHost.addTab(tab1);
		tab2 = tabHost.newTabSpec("tab2").setIndicator("购物")
				.setContent(R.id.tab2);
		tabHost.addTab(tab2);
		tab3 = tabHost
				.newTabSpec("tab3")
				.setIndicator(
						"读写锁",
						getResources().getDrawable(
								android.R.drawable.arrow_down_float))
				.setContent(new Intent(this, Sub3TabActivity.class));

		tab4_1 = tabHost
				.newTabSpec("tab4")
				.setIndicator(
						"被动设置",
						getResources().getDrawable(
								android.R.drawable.arrow_down_float))
				.setContent(new Intent(this, Sub4TabActivity.class));
		tab4_2 = tabHost
				.newTabSpec("tab5")
				.setIndicator(
						"主动设置",
						getResources().getDrawable(
								android.R.drawable.arrow_down_float))
				.setContent(new Intent(this, SubBlueSetTabActivity.class));
		 
				 
		tabHost.setCurrentTab(0);
		TabWidget tw = tabHost.getTabWidget();
		tw.getChildAt(1).setVisibility(View.INVISIBLE);

		/*
		 * Region_Conf rcf1=Region_Conf.valueOf(Integer.valueOf("8")); byte[]
		 * data=new byte[1]; data[0]=(byte)((Region_Conf)rcf1).value();
		 * System.out.println(String.valueOf(data[0]));
		 */

		Application app = getApplication();
		myapp = (MyApplication) app;

		myapp.Rparams = myapp.new ReaderParams();
		 
		myapp.tabHost = tabHost;
		/*
		 * spinner_opbank= (Spinner)findViewById(R.id.spinner_opfbank);
		 * arradp_opbank = new
		 * ArrayAdapter<String>(this,android.R.layout.simple_spinner_item
		 * ,spibank); arradp_opbank.setDropDownViewResource(android.R.layout.
		 * simple_spinner_dropdown_item);
		 * spinner_opbank.setAdapter(arradp_opbank);
		 */

		button_read = (Button) findViewById(R.id.button_start);
		button_stop = (Button) findViewById(R.id.button_stop);
		button_stop.setEnabled(false);
		button_pay = (Button) findViewById(R.id.button_pay);

		button_confirm = (Button)findViewById(R.id.btn_confirm);
		//button_send = (Button)findViewById(R.id.btn_send);
		button_connect = (Button)findViewById(R.id.btn_start);

		listView = (ListView) findViewById(R.id.listView_epclist);
		gr_match = (RadioGroup) findViewById(R.id.radioGroup_opmatch);

		tv_once = (TextView) findViewById(R.id.textView_readoncecnt);
		tv_state = (TextView) findViewById(R.id.textView_invstate);
		tv_tags = (TextView) findViewById(R.id.textView_readallcnt);
		//etMessage = (EditText)findViewById(R.id.et_message);
		etUserId = (EditText)findViewById(R.id.et_user_id);

		//purchasenum = (TextView)findViewById(R.id.purchasenum);

		etIp = (EditText)findViewById(R.id.et_ip);
		etPort = (EditText)findViewById(R.id.et_port);

		for (int i = 0; i < Coname.length; i++)
			h.put(Coname[i], Coname[i]);

		button_connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isReceivingMsgReady){
					initSocket();
				}
			}
		});

//		button_send.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				send();
//			}
//		});

		button_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!isConnected){
						Toast.makeText(MainActivity.this,"连接服务器失败", Toast.LENGTH_SHORT).show();
					}else{
						if(!TextUtils.isEmpty(etUserId.getText().toString().trim())){
							friendId=etUserId.getText().toString().trim();
						}else{
							Toast.makeText(MainActivity.this,"请输入对方的用户名id", Toast.LENGTH_SHORT).show();
						}
					}
			}
		});


		button_read.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
lc=-1;
					if (myapp.Mode == 0) {
						
						try {
							
							// srp=new SimpleReadPlan(new
							// int[]{4},TagProtocol.TagProtocolE.Gen2);
							// reader.paramSet(ParamNames.Reader_Read_Plan,
							// srp);

							/* 取消检测天线 */
							myapp.Mreader.paramSet(
									ParamNames.Reader_Antenna_CheckPort, false);
							myapp.Mreader.addStatusListener(SL);
 
							if(ThreadMODE==0)
								handler.postDelayed(runnable_MainActivity,0); 
								else
								{
									handler3.postDelayed(runnable_refreshlist, 0);
									isrun=true;
									runThread=new Thread(runnable_alone);
									runThread.start();
								}
			 
							isreading = true;
							myapp.isread = true;
							ReadHandleUI();

						} catch (ReaderException e) {
							// TODO Auto-generated catch block
							Toast.makeText(MainActivity.this, "设置失败",
									Toast.LENGTH_SHORT).show();
							return;
						}
					} else if (myapp.Mode == 1) {
						try {
							myapp.Mreader.addStatusListener(SL);
							myapp.Mreader.addDataListener(DL);
							myapp.Mreader.StartTagEvent();
							myapp.isread = true;
							isreading = true;
							ReadHandleUI();
						} catch (ReaderException e) {
							// TODO Auto-generated catch block
							return;
						}
					}
				} catch (Exception ex) {

				}
			}

		});

		button_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				StopHandle();
				myapp.isread = false;
				
			}
		});


		this.listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				arg1.setBackgroundColor(Color.YELLOW);

				EditText et = (EditText) findViewById(R.id.editText_opfilterdata);
				EditText et2 = (EditText) findViewById(R.id.editText_opfilsadr);
				HashMap<String, String> hm = (HashMap<String, String>) listView
						.getItemAtPosition(arg2);
				String epc = hm.get("EPC ID");
				myapp.Rparams.Curepc = epc;
				// et.setText(epc);
				// et2.setText("32");
				// gr_match.check(gr_match.getChildAt(0).getId());
				// spinner_opbank.setSelection(1);

				for (int i = 0; i < listView.getCount(); i++) {
					if (i != arg2) {
						View v = listView.getChildAt(i);
						ColorDrawable cd = (ColorDrawable) v.getBackground();
						if (Color.YELLOW == cd.getColor()) {
							int[] colors = { Color.WHITE,
									Color.rgb(219, 238, 244) };// RGB颜色
							v.setBackgroundColor(colors[i % 2]);// 每隔item之间颜色不同
						}
					}
				}
			}

		});
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
				finish();
				// System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	void StopHandle() {
		isreading = false;
		if (myapp.Mode == 0) {
			if(ThreadMODE==0)
				handler.removeCallbacks(runnable_MainActivity);
				else
				{
					isrun=false;
					try {
						if(runThread!=null)
						runThread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handler3.removeCallbacks(runnable_refreshlist);
				}
			StopHandleUI();
		} else {
			try {
				myapp.Mreader.EndTagEvent();
				myapp.Mreader.removeDataListener(DL);
				myapp.Mreader.removeStatusListener(SL);
				StopHandleUI();
			} catch (ReaderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	String[] Coname = new String[] { "序号", "EPC ID", "商品名称", "价格", "协议", "RSSI",
			"频率", "附加数据" };

	String[] goodsId = new String[] {"E2000016340A00191200A1A8","E2000016340A00201200A1AD","E2000016340A002012109F7D",
			"E2000016340A002112109F85","E2000016340A002212109F7E","E2000016340A002312109F86",
			"E2000016340A00251200A1B7","E2000016340A00271200A1B8","E2000016340A00281200A1BD","E2000016340A00301200A1BE",
			"E2000016340A003512109F98","E2000016340A003612109F9D","E2000016340A00411200A1D7","E2000016340A00421200A1D0",
			"E2000016340A004812109FAF","E2000016340A004912109FB7","E2000016340A00501200A1E0","E2000016340A005012109FB0",
			"E2000016340A005112109FB8","E2000016340A00611200A205"};
	String[] goodsname = new String[] {"apple","book","candy","dish","egg","fish","girl","hot","ice","juice","kick","lemon","mount","num","opti","pork","qqq","rice","sussage","time"};
	int[] goodsprice = new int[] {1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2,3,1,2};

	String goodsName = null;
	int goodsPrice = 0;
	String[] purchase = new String[100];

	Map<String, String> h = new HashMap<String, String>();
	private void showlist() {
		List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();
		 Map<String, TAGINFO> mst=new LinkedHashMap<String, TAGINFO>();// 有序
		 synchronized (this) {
		 mst.putAll(TagsMap);
		 }// 释放锁
		Iterator<Entry<String, TAGINFO>> iesb = mst.entrySet().iterator();
		int j = 1;
		int totalPrice = 0;

		list.add(h);


		while (iesb.hasNext()) {
			TAGINFO bd = iesb.next().getValue();
			Map<String, String> m = new HashMap<String, String>();
			m.put(Coname[0], String.valueOf(j));
			j++;

			m.put(Coname[1], Functional.bytes_Hexstr(bd.EpcId));
			String id = new String(Functional.bytes_Hexstr(bd.EpcId));
			int c = Arrays.asList(goodsId).indexOf(id);

			purchase[j-1]= id;

			goodsName = goodsname[c];
			goodsPrice = goodsprice[c];

			totalPrice += goodsPrice;

			String cs = m.get("次数");
			if (cs == null)
				cs = "0";
			int isc = Integer.parseInt(cs) + bd.ReadCnt;

//			m.put(Coname[2], String.valueOf(isc));
//			m.put(Coname[3], String.valueOf(bd.AntennaID));

			m.put(Coname[2], goodsName);
			m.put(Coname[3], String.valueOf(goodsPrice));

			m.put(Coname[4], "");
			m.put(Coname[5], String.valueOf(bd.RSSI));
			m.put(Coname[6], String.valueOf(bd.Frequency));

			if (bd.EmbededDatalen > 0)
				m.put(Coname[7], Functional.bytes_Hexstr(bd.EmbededData));
			else
				m.put(Coname[7], "                 ");

			list.add(m);
		}

		final int finalTotalPrice = totalPrice;
		final int finalJ = j;
		button_pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				synchronized (this) {
//				TagsMap.clear();
//				}
//				showlist();
//
//				TextView et = (TextView) findViewById(R.id.textView_readoncecnt);
//				et.setText("0");
//
//				TextView et2 = (TextView) findViewById(R.id.textView_readallcnt);
//				et2.setText("0");
//
//				TextView et3 = (TextView) findViewById(R.id.textView_invstate);
//				et3.setText("...");
//
//				myapp.Rparams.Curepc = "";

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("支付");
				builder.setMessage("是否确认支付" + finalTotalPrice + "元？");
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				});
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						send(listView.getCount() - 1,finalTotalPrice);
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		// /*
		ListAdapter adapter = new MyAdapter(this, list,
				R.layout.listitemview_inv, Coname, new int[] {
						R.id.textView_readsort, R.id.textView_readepc,
						R.id.textView_readcnt, R.id.textView_readant,
						R.id.textView_readpro, R.id.textView_readrssi,
						R.id.textView_readfre, R.id.textView_reademd });

		// layout为listView的布局文件，包括三个TextView，用来显示三个列名所对应的值
		// ColumnNames为数据库的表的列名
		// 最后一个参数是int[]类型的，为view类型的id，用来显示ColumnNames列名所对应的值。view的类型为TextView
		listView.setAdapter(adapter);
		// */
	}

	private StringBuffer sb = new StringBuffer();
	boolean isConnected = false;
	private Handler handler5=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {

				case 1:
					String msgContent=(String) msg.obj;
					sb.append("我："+msgContent+"\n");
					break;

				case 2:
					String message = (String) msg.obj;
					if(message.contains("用户名")){
						isConnected=true;
					}else{
						JSONObject json;
						try {
							json = new JSONObject(message);
							sb.append(json.getString("from")+":" +json.getString("msg")+"   " + "\n");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					break;
			}
		};
	};


	private void send(final int a, final int b) {

		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... params) {
				sendMsg(a,b);
				return null;
			}

		}.execute();

	}

	/**
	 * 向服务器发送消息
	 */
	protected void sendMsg(int a,int b) {
		try {
			//根据clientSocket.getOutputStream得到BufferedWriter对象，从而从输出流中获取数据
			mWriter=new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"utf-8"));

			//String msgContent=etMessage.getText().toString();
			String msgContent_num = String.valueOf(a);
			String msgContent_price = String.valueOf(b);

			//封装成json
			JSONObject json = new JSONObject();
			json.put("to", Integer.parseInt(friendId));
			json.put("msg_num", msgContent_num);
			json.put("msg_price",msgContent_price);
			//通过BufferedWriter对象向服务器写数据
			mWriter.write(json.toString()+"\n");
			//一定要调用flush将缓存中的数据写到服务器
			mWriter.flush();
			Message msg = handler5.obtainMessage();
			msg.what=1;
			msg.obj=msgContent_num + " " + msgContent_price;
			handler5.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initSocket() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				String ip=etIp.getText().toString();
				int port=Integer.parseInt(etPort.getText().toString());

				try {
					isReceivingMsgReady=true;
					//在子线程中初始化Socket对象
					clientSocket=new Socket(ip,port);
					//根据clientSocket.getInputStream得到BufferedReader对象，从而从输入流中获取数据
					mReader=new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"utf-8"));
					//根据clientSocket.getOutputStream得到BufferedWriter对象，从而从输出流中获取数据
					mWriter=new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"utf-8"));
					while(isReceivingMsgReady){
						if(mReader.ready()){
							Message msg = handler5.obtainMessage();
							msg.what=2;
							msg.obj=mReader.readLine();
							handler5.sendMessage(msg);
						}
						Thread.sleep(200);
					}
					mWriter.close();
					mReader.close();
					clientSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private Runnable runnable_MainActivity = new Runnable() {
		public void run() {

			synchronized (this) {
				if (isreading
						&& myapp.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED) {

					try {
						TagReadData[] trds = myapp.Mreader
								.Read(myapp.Rparams.readtime);

						if (trds != null && trds.length > 0) {
							lockobj.lock();
							soundPool.play(1, 1, 1, 0, 0, 1);
							for (int i = 0; i < trds.length; i++) {
								if (!TagsMap.containsKey(trds[i].EPCHexstr())) {
									TAGINFO Ti = new TAGINFO();
									Ti.AntennaID = (byte) trds[i].Antenna();
									Ti.CRC = trds[i].CRC();
									Ti.EmbededData = trds[i].AData();
									Ti.EmbededDatalen = (short) trds[i].AData().length;
									Ti.EpcId = trds[i].EPCbytes();
									Ti.Epclen = (short) trds[i].EPCbytes().length;
									Ti.Frequency = trds[i].Frequency();
									Ti.PC = trds[i].PC();
									Ti.protocol = -1;
									Ti.ReadCnt = trds[i].ReadCount();
									Ti.RSSI = trds[i].RSSI();
									Ti.TimeStamp = (int) trds[i].Time()
											.getTime();
									TagsMap.put(trds[i].EPCHexstr(), Ti);
								} else {
									TAGINFO tf = TagsMap.get(trds[i]
											.EPCHexstr());
									tf.ReadCnt += trds[i].ReadCount();
									tf.RSSI = trds[i].RSSI();
									tf.Frequency = trds[i].Frequency();
								}
							}
							lockobj.unlock();
						}

						TextView et = (TextView) findViewById(R.id.textView_readoncecnt);
						et.setText(String.valueOf(trds.length));

					} catch (ReaderException rex) {
						TextView et = (TextView) findViewById(R.id.textView_invstate);
						et.setText("error:" + rex.GetMessage());
						handler.postDelayed(this, myapp.Rparams.sleep);
						return;
					} catch (Exception ex) {
						TextView et = (TextView) findViewById(R.id.textView_invstate);
						et.setText("error:" + ex.toString() + ex.getMessage());
						handler.postDelayed(this, myapp.Rparams.sleep);
						return;
					}

//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//						if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
//							startPermissionsActivity();
//						} else {
//								showlist();
//						}
//					} else {
							showlist();
//					}

				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				TextView et = (TextView) findViewById(R.id.textView_readallcnt);
				et.setText(String.valueOf(listView.getCount() - 1));
				tagNum = listView.getCount() - 1;
				handler.postDelayed(this, myapp.Rparams.sleep);
			}
		}
	};

	private Runnable runnable_refreshlist = new Runnable() {
		public void run() {
					showlist();
			handler.postDelayed(this, 1000);
		}
	};
    private int lc=-1;
	private Runnable runnable_alone = new Runnable() {
		public void run() {

			while (isrun) {
				synchronized (this) {
					if (isreading
							&& myapp.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED) {
						TagReadData[] trds = null;
						try {
							
							if(lc<myapp.Rparams.uants.length-1)
								    ++lc;
								    else
								    	lc=0;
							SimpleReadPlan srp = new SimpleReadPlan(new int[]{myapp.Rparams.uants[lc]});
							if (myapp.Rparams.To == null)
								myapp.Mreader.paramSet(
										ParamNames.Reader_Read_Plan, srp);
							else {

								srp = new SimpleReadPlan(new int[]{myapp.Rparams.uants[lc]},
										TagProtocolE.Gen2, null,
										myapp.Rparams.To, 10);
								myapp.Mreader.paramSet(
										ParamNames.Reader_Read_Plan, srp);
							}
							trds = myapp.Mreader.Read(myapp.Rparams.readtime);

							if (trds != null && trds.length > 0) {
								 
								soundPool.play(1, 1, 1, 0, 0, 1);
								for (int i = 0; i < trds.length; i++) {
									if (!TagsMap.containsKey(trds[i]
											.EPCHexstr())) {
										TAGINFO Ti = new TAGINFO();
										Ti.AntennaID = (byte) trds[i].Antenna();
										Ti.CRC = trds[i].CRC();
										Ti.EmbededData = trds[i].AData();
										Ti.EmbededDatalen = (short) trds[i]
												.AData().length;
										Ti.EpcId = trds[i].EPCbytes();
										Ti.Epclen = (short) trds[i].EPCbytes().length;
										Ti.Frequency = trds[i].Frequency();
										Ti.PC = trds[i].PC();
										Ti.protocol = -1;
										Ti.ReadCnt = trds[i].ReadCount();
										Ti.RSSI = trds[i].RSSI();
										Ti.TimeStamp = (int) trds[i].Time()
												.getTime();
										TagsMap.put(trds[i].EPCHexstr(), Ti);
									} else {
										TAGINFO tf = TagsMap.get(trds[i]
												.EPCHexstr());
										tf.ReadCnt += trds[i].ReadCount();
										tf.RSSI = trds[i].RSSI();
										tf.Frequency = trds[i].Frequency();
										tf.AntennaID=(byte) trds[i].Antenna();
									}
								}
								 
							}
						} catch (ReaderException rex) {
							  Message msg2 = new Message(); 
				              msg2.what = 1;  
				              Bundle bundle2 = new Bundle(); 
				              bundle2.putString("Msg_error_1", "error:"+rex.GetMessage());
				              msg2.setData(bundle2);  
				              handler3.sendMessage(msg2);
							  isrun=false;
							  
						} catch (Exception ex) {
							 
							  Message msg = new Message(); 
			 				  msg.what = 2;  
			 				  Bundle bundle = new Bundle(); 
				              bundle.putString("Msg_error_2", "error:" + ex.toString()
										+ ex.getMessage());
				              msg.setData(bundle); 
				              handler3.sendMessage(msg);
				              try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				              continue;
							 
						}
 
						if (trds != null && trds.length > 0) {
							Message msg = new Message();
							Bundle bundle = new Bundle();
							msg.what = 0;
							bundle.putString("Msg_cnt",
									(String.valueOf(trds.length)));
							synchronized (this) {
								bundle.putString("Msg_all",
										(String.valueOf(TagsMap.size())));
							}
							msg.setData(bundle);
							handler3.sendMessage(msg);
						}
						try {
							Thread.sleep(myapp.Rparams.sleep);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (myapp.isread) {
			Toast.makeText(MainActivity.this, "请先停止扫描", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		int id = item.getItemId();

		if (id == R.id.action_debug) {

			if (myapp.m != null
					&& myapp.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED) {
				Intent intent = new Intent(MainActivity.this,
						SubDebugActivity.class);
				startActivityForResult(intent, 0);
				return true;
			}
			Toast.makeText(MainActivity.this, "请扫描并选中一个蓝牙读写器,并且完成连接",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (id == R.id.action_system) {
			if (myapp.m != null) {
				Intent intent = new Intent(MainActivity.this,
						SubSystemActivity.class);
				startActivityForResult(intent, 0);
				return true;
			}

			Toast.makeText(MainActivity.this, "请扫描并选中一个蓝牙读写器",
					Toast.LENGTH_SHORT).show();
			return true;
		}
		 else if (id == R.id.action_custom) {
			 if (myapp.m != null
						&& myapp.CommBth.ConnectState() == Comm_Bluetooth.CONNECTED) {
					Intent intent = new Intent(MainActivity.this,
							SubCustomActivity.class);
					startActivityForResult(intent, 0);
					return true;
				}
				Toast.makeText(MainActivity.this, "请扫描并选中一个蓝牙读写器,并且完成连接",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	private void ReadHandleUI() {
		this.button_read.setEnabled(false);
		this.button_stop.setEnabled(true);
		TabWidget tw = myapp.tabHost.getTabWidget();
		tw.getChildAt(0).setEnabled(false);
		tw.getChildAt(2).setEnabled(false);
		if (myapp.Mode == 0)
			tw.getChildAt(3).setEnabled(false);

	}

	private void StopHandleUI() {
		button_read.setEnabled(true);
		button_stop.setEnabled(false);
		TabWidget tw = myapp.tabHost.getTabWidget();
		tw.getChildAt(0).setEnabled(true);
		if (tw.getChildCount() > 2)
			tw.getChildAt(2).setEnabled(true);
		if (tw.getChildCount() > 3 && myapp.Mode == 0)
			tw.getChildAt(3).setEnabled(true);
	}

	/*
	 * protected void onPause() {
	 * 
	 * long now=System.currentTimeMillis();
	 * if(!(myapp.exittime<now&&now-myapp.exittime<2000)) { myapp.exittime=now;
	 * Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
	 * return; }
	 * 
	 * super.onPause(); }
	 * 
	 * protected void onResume() { //this.setVisible(true);
	 * 
	 * super.onResume(); }
	 */

	protected void onDestroy() {

		if (button_read.isEnabled())
			StopHandle();

		if (myapp.Mreader != null)
			myapp.Mreader.DisConnect();
		// /*
		if (myapp.CommBth.getRemoveType() == 4
				&& myapp.CommBth.ConnectState() != Comm_Bluetooth.DISCONNECTED)
			myapp.CommBth.DisConnect();
		// */
		System.exit(0);
		super.onDestroy();
	}
}
