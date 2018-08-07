package com.chen.birthday.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class DateUtil {
	@SuppressLint("SimpleDateFormat")
	
	public static String dateStringToOtherDateString(String value, String inFormat, String outFormat) {
		SimpleDateFormat format = new SimpleDateFormat(inFormat);
		SimpleDateFormat toformat = new SimpleDateFormat(outFormat);
		Date date;
		String returnValue = null;
		try {  
			date = format.parse(value);
			returnValue = toformat.format(date);
		} catch (Exception e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();
		}
		return returnValue;
	}
	
	/*
	 * Date -> yyyyMMdd
	 * Date -> MMdd
	 * Date -> yyyy/MM/dd
	 * Date -> dd/MM/yyyy
	 */
	@SuppressLint("SimpleDateFormat")
	public static String dateToString(Date date, String strformat) {
		SimpleDateFormat format = new SimpleDateFormat(strformat);
		return format.format(date);
	}
	
	/*
	 * Date -> yyyyMMdd
	 * Date -> MMdd
	 * Date -> yyyy/MM/dd
	 * Date -> dd/MM/yyyy
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date stringToDate(String strDate, String strformat) {
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat(strformat);
		try {
			date = format.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
}
