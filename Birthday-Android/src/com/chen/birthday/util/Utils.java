package com.chen.birthday.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Utils {
	// font
	public static final String FONT_YAHEI_BOLD = "fonts/msyhbd.ttf";
	public static final String FONT_YAHEI = "fonts/DroidSansFallback.ttf";

	public static Typeface font_yahei_bold = null;
	public static Typeface font_yahei = null;

	public static Typeface getYaheiBoldFont(Context context) {
		if (font_yahei_bold != null)
			return font_yahei_bold;

		font_yahei_bold = Typeface.createFromAsset(context.getAssets(), FONT_YAHEI_BOLD);
		return font_yahei_bold;

	}

	public static Typeface getYaheiFont(Context context) {
		if (font_yahei != null)
			return font_yahei;

		font_yahei = Typeface.createFromAsset(context.getAssets(), FONT_YAHEI);
		return font_yahei;

	}

	/*
	 * 
	 * set font type to all view of viewgroup  
	 */
	public static void setTypefaceAllView(ViewGroup vg, Typeface face) {

		for (int i = 0; i < vg.getChildCount(); ++i) {

			View child = vg.getChildAt(i);

			if (child instanceof ViewGroup) {

				setTypefaceAllView((ViewGroup) child, face);

			} else if (child != null) {
				if (child instanceof TextView) {
					TextView textView = (TextView) child;
					textView.setTypeface(face);
				}
			}
		}
	}

	public static void setFontSizeAllView(ViewGroup vg, float size) {
		if (vg == null)
			return;

		for (int i = 0; i < vg.getChildCount(); ++i) {

			View child = vg.getChildAt(i);

			if (child instanceof ViewGroup) {
				setFontSizeAllView((ViewGroup) child, size);

			} else if (child != null) {
				if (child instanceof TextView) {
					TextView view = (TextView) child;
					Object tag = view.getTag();
					if (tag != null && tag.toString().equals("name_family")) {
						view.setTextSize(size+2);
						view.setTypeface(null, Typeface.BOLD_ITALIC);
					} else {
						view.setTextSize(size);
					}
				}
			}
		}
	}

	private static final String DATE_PATTERN = "(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)";

	private static Pattern pattern;
	private static Matcher matcher;
	/**
	 * Validate date format with regular expression
	 * @param date date address for validation
	 * @return true valid date format, false invalid date format
	 */
	public boolean date_validate(final String date) {

		matcher = pattern.matcher(date);

		if(matcher.matches()) {
			matcher.reset();

			if(matcher.find()) {
				String day = matcher.group(1);
				String month = matcher.group(2);
				int year = Integer.parseInt(matcher.group(3));

				if (day.equals("31") && 
						(month.equals("4") || month .equals("6") || month.equals("9") ||
								month.equals("11") || month.equals("04") || month .equals("06") ||
								month.equals("09"))) {
					return false; // only 1,3,5,7,8,10,12 has 31 days
				} 

				else if (month.equals("2") || month.equals("02")) {
					//leap year
					if(year % 4==0){
						if(day.equals("30") || day.equals("31")){
							return false;
						}
						else{
							return true;
						}
					}
					else{
						if(day.equals("29")||day.equals("30")||day.equals("31")){
							return false;
						}
						else{
							return true;
						}
					}
				}

				else{               
					return true;                
				}
			}

			else{
				return false;
			}        
		}
		else{
			return false;
		}              
	}
}
