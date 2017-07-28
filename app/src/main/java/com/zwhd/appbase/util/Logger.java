package com.zwhd.appbase.util;

import android.util.Log;

/**
 * 日志的打印
 * 
 * @author YI
 * */
public class Logger {
	public static  String TAG = "com.zwhd.appbase";
	public static final boolean log = true;

	public static void log(Object msg) {
		if (msg == null)
			return;
		if (log) {
			Log.v(TAG, msg.toString());
		}
	}
}
