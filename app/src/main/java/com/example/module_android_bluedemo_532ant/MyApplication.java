package com.example.module_android_bluedemo_532ant;

import java.util.Map;

import com.bth.api.cls.Comm_Bluetooth;
import com.function.SPconfig;
import com.silionmodule.Reader;
import com.silionmodule.TagFilter;
import com.silionmodule.TagOp;

import android.app.Application;
import android.widget.TabHost;

public class MyApplication extends Application{

	/*
	 * 公共变量   
	 */
	 public Comm_Bluetooth CommBth;
	 
	 public int Mode;
	 public Map<String, String> m;
	 
	    public Reader Mreader;
		public String Address;
	    public SPconfig spf;
		public ReaderParams Rparams;
		public long exittime;
		public  TabHost tabHost;
		public boolean isread;
		public String bluepassword;
		public int BackResult;
		public class ReaderParams
		{
			public int antportc;
			public int sleep;
			public int[] uants;
			public int readtime;
			public String Curepc;
			public int Bank;
		
			public TagOp To;
			public TagFilter Tf;
			
			public ReaderParams()
			{
				sleep=0;
				readtime=100;
				uants=new int[1];
				uants[0]=1;
				
			}
		}
}
