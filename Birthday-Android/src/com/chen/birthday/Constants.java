package com.chen.birthday;

public class Constants {
	// app direction
	public static enum DIRECTION {
		LEFT_TO_RIGHT,
		RIGHT_TO_LEFT
	}
	public static final DIRECTION APP_DIRECTION = DIRECTION.RIGHT_TO_LEFT;
	// refresh time
	public static final int FONT_SIZE = 10;
	public static final int REFRESH_TIME = 5; // milliseconds
	public static final int CHECK_DATE_CHANGE_INTERVAL_TIME = 10*60;//30*60; // 10 minutes
	
	// ID
	public static final int ID_ITEM_OFFSET = 100;
	
	// ticket count
	public static final int TODAY_TICKET_COUNT = 4;
	public static final int WEEK_TICKET_COUNT = 5;
	public static final int MONTH_TICKET_COUNT = 6;
	public static final int NEW_BORN_TICKET_COUNT = 6;
	public static final int REST_TICKET_COUNT = 12;
	
	public static final int ALL_TICKET_COUNT = TODAY_TICKET_COUNT+WEEK_TICKET_COUNT+MONTH_TICKET_COUNT+NEW_BORN_TICKET_COUNT+REST_TICKET_COUNT;

	// extra keys
	public static final String EXTRA_NAME = "name";
	public static final String EXTRA_FAMILIY = "family";
	public static final String EXTRA_ADDRESS = "address";
	public static final String EXTRA_BIRTHDAY = "birthday";
	public static final String EXTRA_NEXT_BIRTHDAY = "next_birthday";
	
	public static final String EXTRA_TODAY = "today";
	public static final String EXTRA_FONT_SIZE = "font_size";
	public static final String EXTRA_REFRESH_TIME = "refresh_time";
}
