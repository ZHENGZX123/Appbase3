package com.zwhd.appbase.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Xml;
/**
 * 字符串操作
 * @author YI
 * */
public class StringUtil {
	/**
	 * MD5加密
	 * */
	public static String MD5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			plainText = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plainText.toLowerCase(Locale.getDefault());
	}

	/**
	 * 字符串验证
	 * */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);

	}

	public static String timeToNow(long time) {
		if (time <= 0l)
			return "";
		time *= 1000;
		long now = System.currentTimeMillis();
		long nt = now - time;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		if (nt < 0)
			return format.format(new Date(time));
		if (nt < 15 * 1000)
			return "just";
		else if (nt < 60 * 1000)
			return nt / 1000 + " s ago";
		else if (nt < 60 * 60 * 1000)
			return nt / (60 * 1000) + "minute ago";
		else if (nt < 24 * 60 * 60 * 1000)
			return nt / (60 * 60 * 1000) + "hour ago";
		else
			return format.format(new Date(time));
	}

	/**
	 * 时间戳转换为 **之前
	 * */
	public static String getStandardDate(long timeStr) {

		StringBuffer sb = new StringBuffer();

		long t = timeStr;
		long time = System.currentTimeMillis() - (t * 1000);
		long mill = (long) Math.ceil(time / 1000);// 秒前
		long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前
		long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时
		if (hour - 1 > 0) {
			if (hour >= 24) {
				sb.append(" 1 day ");
			} else {
				sb.append(hour + " hour ");
			}
		} else if (minute - 1 > 0) {
			if (minute == 60) {
				sb.append(" 1 hour ");
			} else {
				sb.append(minute + " minute ");
			}
		} else if (mill - 1 > 0) {
			if (mill == 60) {
				sb.append(" 1 minute ");
			} else {
				sb.append(mill + "mill");
			}
		} else {
			sb.append(" just ");
		}
		if (!sb.toString().equals("just")) {
			sb.append(" ago");
		}
		return sb.toString();
	}

	/**
	 * 数据的格式化
	 * */
	public static String format(String format, Object... strings) {
		return String.format(format, strings);

	}

	public static int toInt(String str) {
		try {
			if (isEmpty(str))
				return 0;
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * 寻找索引值
	 * */
	public static int findStrIndex(List<String> list, String str) {
		if (str == null)
			return 0;
		int idx = list.indexOf(str);
		return idx == -1 ? 0 : idx;

	}

	public static int findStrIndexs(List<String> list, String str) {
		if (str == null)
			return -1;
		for (int i = 0, leg = list.size(); i < leg; i++) {
			if (list.get(i).equals(str))
				return i;

		}
		return -1;

	}

	/**
	 * 字符串索引
	 * */
	public static int findStrIndexs(String pat, String str) {
		try {
			return pat.indexOf(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static String getPicPath(Uri uri, ContentResolver contentResolver) {
		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = contentResolver.query(uri, projection, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(projection[0]);
			cursor.moveToFirst();
			String str = cursor.getString(column_index);
			cursor.close();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String[] split(String splitStr, String str) {
		return str.split(splitStr);
	}

	public static String getStrings(String[] strings, int idx) {
		if (strings == null)
			return null;
		if (strings.length - 1 < idx)
			return null;
		return strings[idx];

	}

	public static String toString(List<String> list, String split) {
		StringBuffer buffer = new StringBuffer();
		for (String str : list) {
			buffer.append(str).append(split);
		}
		buffer.delete(buffer.length() - split.length(), buffer.length());
		return buffer.toString();
	}

	public static String toString(String split, String... list) {
		StringBuffer buffer = new StringBuffer();
		for (String str : list) {
			buffer.append(str).append(split);
		}
		buffer.delete(buffer.length() - split.length(), buffer.length());
		return buffer.toString();
	}

	/**
	 * 根据年份以及月份获取天数
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份 1~12
	 * */
	public static ArrayList<String> getDaysByYM(String year, String month) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, Integer.parseInt(year));
			calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
			int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			ArrayList<String> days = new ArrayList<String>();
			for (int i = 1; i < maxDay + 1; i++) {
				days.add("" + i);

			}
			return days;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String dateFormat(String str, int filed) {

		String s = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		try {
			if (isNotEmpty(str)) {
				Date date = format.parse(str);
				calendar.setTime(date);
			} else {
				calendar.setTime(new Date());
			}

			switch (filed) {
			case 0:
				s = "" + calendar.get(Calendar.YEAR);
				break;

			case 1:
				s = "" + (1 + calendar.get(Calendar.MONTH));
				break;
			case 2:
				s = "" + calendar.get(Calendar.DAY_OF_MONTH);
				break;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String getDateField(long time, int filed) {

		String s = null;
		Date date = new Date(time);
		SimpleDateFormat sdf;
		try {

			switch (filed) {
			case 0:
				sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 1:
				sdf = new SimpleDateFormat("MM", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 2:
				sdf = new SimpleDateFormat("dd", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 3:
				sdf = new SimpleDateFormat("MM.yyyy", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 4:
				sdf = new SimpleDateFormat("dd-MM HH:mm", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 5:
				sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 6:
				sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 7:
				sdf = new SimpleDateFormat(" HH:mm MM-dd", Locale.getDefault());
				s = sdf.format(date);
				break;
			case 8:
				sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
				s = sdf.format(date);
				break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String getAge(String year) {
		Calendar calendar = Calendar.getInstance();
		return "" + Math.abs(calendar.get(Calendar.YEAR) - Integer.parseInt(year));
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static int bytesToInt(byte[] src, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (src[i + offset] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}

	/**
	 * 电话号码验证
	 * 
	 * */
	public static boolean isTelNum(String num) {
		if (isEmpty(num))
			return false;
		num = num.trim();
		Pattern pattern = Pattern.compile("^[0-9]{11}$");
		Matcher matcher = pattern.matcher(num);
		return matcher.find();
	}

	/**
	 * 固定集合转化为活动集合
	 * 
	 * @param <T>
	 * */
	public static <T> List<T> changeList(List<T> list) {
		List<T> lst = new ArrayList<T>();
		for (int i = 0, leg = list.size(); i < leg; i++) {
			lst.add(list.get(i));
		}
		return lst;
	}

	/**
	 * 日期转时分秒
	 * */
	public static String dateToHms() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
		return sdf.format(new Date());

	}
	/**
	 * 获取视频目录
	 * **/
	public static String getVideoPath(Uri uri, ContentResolver contentResolver) {
		try {
			String[] projection = { MediaStore.Video.Media.DATA };
			Cursor cursor = contentResolver.query(uri, projection, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(projection[0]);
			cursor.moveToFirst();
			String str = cursor.getString(column_index);
			cursor.close();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
