package com.chen.birthday;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.chen.birthday.db.DBManager;
import com.chen.birthday.db.DBOpenHelper;
import com.chen.birthday.util.DateUtil;
import com.chen.birthday.util.SDCardUtil;
import com.chen.birthday.util.Utils;
import com.hipmob.gifanimationdrawable.GifAnimationDrawable;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MainActivity extends Activity implements OnClickListener {

	@SuppressWarnings("deprecation")
	private static Date TODAY;

	// ui
	private TextView txtToday, txtWeek, txtMonth, txtNewBorn, txtRest;
	private LinearLayout todayLayout, weekLayout, monthLayout, newbornLayout, restLayout1, restLayout2;

	// values
	public static MainActivity instance = null;
	private ArrayList<PupilInfo> mAllPupilList = null;
	private ArrayList<PupilInfo> mTodayPupilList = null;
	private ArrayList<PupilInfo> mWeekPupilList = null;
	private ArrayList<PupilInfo> mMonthPupilList = null;
	private ArrayList<PupilInfo> mNewBornPupilList = null;
	private ArrayList<PupilInfo> mRestPupilList = null;

	private int ticketCount = 0;
	private int curTodayTicketIndex = 0;
	private int curWeekTicketIndex = 0;
	private int curMonthTicketIndex = 0;
	private int curNewBornTicketIndex = 0;
	private int curRestTicketIndex = 0;

	private int app_font_size = Constants.FONT_SIZE;
	private int app_refresh_time = Constants.REFRESH_TIME;

	private static String CSV_SAVE_PATH = "";
	
	private static int ticket_layout_resid = R.layout.ticket_rtl;

	// database
	private DBManager mdbcontrol;

	// timer
	private Timer timerUpdataShowInfo = null;
	private Timer timerCheckDateChange = null;

	// request code
	public static int REQ_CONFIRM_EXIT = 0;
	public static int REQ_SETTING = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (Constants.APP_DIRECTION == Constants.DIRECTION.LEFT_TO_RIGHT) {
			setContentView(R.layout.activity_main);
			ticket_layout_resid = R.layout.ticket;
			
		} else {
			setContentView(R.layout.activity_main_rtl);
		}
		
		instance = this;

		// initialize value for UI and Layout
		initUIandLayout();

		// initialize
		initialize_app();
	}

	private void initUIandLayout() {
		// for ui
		txtToday = (TextView)findViewById(R.id.txt_today);
		txtWeek = (TextView)findViewById(R.id.txt_week);
		txtMonth = (TextView)findViewById(R.id.txt_month);
		txtNewBorn = (TextView)findViewById(R.id.txt_newborn);
		txtRest = (TextView)findViewById(R.id.txt_rest);
		
		todayLayout = (LinearLayout)findViewById(R.id.today_layout);
		weekLayout = (LinearLayout)findViewById(R.id.week_layout);
		monthLayout = (LinearLayout)findViewById(R.id.month_layout);
		newbornLayout = (LinearLayout)findViewById(R.id.newborn_layout);
		restLayout1 = (LinearLayout)findViewById(R.id.rest_layout1);
		restLayout2 = (LinearLayout)findViewById(R.id.rest_layout2);
		Button btn_setting = (Button)findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(this);
				
		ticketCount = 0;
		// today layout
		for (int i = 0; i < Constants.TODAY_TICKET_COUNT; i++) {
			View view = LayoutInflater.from(this).inflate(ticket_layout_resid, null);
			Utils.setFontSizeAllView((ViewGroup)view, app_font_size);

			// set id of ticket view and sub views
			if (Constants.APP_DIRECTION == Constants.DIRECTION.LEFT_TO_RIGHT) {
				setId(view, ticketCount);
				ticketCount++;
			} else {
				setId(view,  Constants.TODAY_TICKET_COUNT-i-1);
				ticketCount++;
			}

			// set layout parameter
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
			params.setMargins(2, 0, 2, 2);
			view.setPadding(12, 8, 12, 8);

			// add view
			todayLayout.addView(view, params);

			// add animation
			addAnimationView(view, R.drawable.cartoon);
		}
		// week layout
		for (int i = 0; i < Constants.WEEK_TICKET_COUNT; i++) {
			View view = LayoutInflater.from(this).inflate(ticket_layout_resid, null);
			Utils.setFontSizeAllView((ViewGroup)view, app_font_size);

			// set id of ticket view and sub views
			if (Constants.APP_DIRECTION == Constants.DIRECTION.LEFT_TO_RIGHT) {
				setId(view, ticketCount);
				ticketCount++;
			} else {
				setId(view,  Constants.TODAY_TICKET_COUNT+Constants.WEEK_TICKET_COUNT-i-1);
				ticketCount++;
			}

			// set layout parameter
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
			params.setMargins(2, 0, 2, 2);
			view.setPadding(12, 8, 12, 8);
			if (i < 2) {
				view.setBackgroundResource(R.drawable.weekly_left);
			} else if (i == 2) {
				view.setBackgroundResource(R.drawable.weekly_middle);
			} else if (i > 2) {
				view.setBackgroundResource(R.drawable.weekly_right);
			}

			// add view
			weekLayout.addView(view, params);

			// add animation
			addAnimationView(view, R.drawable.candle);
		}

		// month layout
		for (int i = 0; i < Constants.MONTH_TICKET_COUNT; i++) {
			View view = LayoutInflater.from(this).inflate(ticket_layout_resid, null);
			Utils.setFontSizeAllView((ViewGroup)view, app_font_size);

			// set id of ticket view and sub views
			if (Constants.APP_DIRECTION == Constants.DIRECTION.LEFT_TO_RIGHT) {
				setId(view, ticketCount);
				ticketCount++;
			} else {
				setId(view,  Constants.TODAY_TICKET_COUNT+Constants.WEEK_TICKET_COUNT+Constants.MONTH_TICKET_COUNT-i-1);
				ticketCount++;
			}

			// set layout parameter
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
			params.setMargins(2, 0, 2, 2);
			view.setPadding(12, 8, 12, 8);
			if (i < 3) {
				view.setBackgroundResource(R.drawable.monthly_left);
			} else {
				view.setBackgroundResource(R.drawable.monthly_right);
			}

			// add view
			monthLayout.addView(view, params);

			// add animation
			addAnimationView(view, R.drawable.cartoon);
		}

		// new born layout
		for (int i = 0; i < Constants.NEW_BORN_TICKET_COUNT; i++) {
			View view = LayoutInflater.from(this).inflate(ticket_layout_resid, null);
			Utils.setFontSizeAllView((ViewGroup)view, app_font_size);

			// set id of ticket view and sub views
			if (Constants.APP_DIRECTION == Constants.DIRECTION.LEFT_TO_RIGHT) {
				setId(view, ticketCount);
				ticketCount++;
			} else {
				setId(view,  Constants.TODAY_TICKET_COUNT+Constants.WEEK_TICKET_COUNT+Constants.MONTH_TICKET_COUNT+Constants.NEW_BORN_TICKET_COUNT-i-1);
				ticketCount++;
			}

			// set layout parameter
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
			params.setMargins(2, 0, 2, 2);
			view.setPadding(12, 8, 12, 8);
			if (i < 3) {
				view.setBackgroundResource(R.drawable.monthly_left);
			} else {
				view.setBackgroundResource(R.drawable.monthly_right);
			}

			// add view
			newbornLayout.addView(view, params);

			// add animation
			addAnimationView(view, R.drawable.candle);
		}

		// rest layout1
		for (int i = 0; i < Constants.REST_TICKET_COUNT/2; i++) {
			View view = LayoutInflater.from(this).inflate(ticket_layout_resid, null);
			Utils.setFontSizeAllView((ViewGroup)view, app_font_size);

			// set id of ticket view and sub views
			if (Constants.APP_DIRECTION == Constants.DIRECTION.LEFT_TO_RIGHT) {
				setId(view, ticketCount);
				ticketCount++;
			} else {
				setId(view,  Constants.TODAY_TICKET_COUNT+Constants.WEEK_TICKET_COUNT+Constants.MONTH_TICKET_COUNT+Constants.NEW_BORN_TICKET_COUNT+Constants.REST_TICKET_COUNT/2-i-1);
				ticketCount++;
			}

			// set layout parameter
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
			params.setMargins(2, 0, 2, 2);
			view.setPadding(12, 8, 12, 8);
			if (i < 3) {
				view.setBackgroundResource(R.drawable.monthly_left);
			} else {
				view.setBackgroundResource(R.drawable.monthly_right);
			}

			// add view
			restLayout1.addView(view, params);

			// add animation
			addAnimationView(view, R.drawable.cartoon);
		}

		// rest layout2
		for (int i = 0; i < Constants.REST_TICKET_COUNT/2; i++) {
			View view = LayoutInflater.from(this).inflate(ticket_layout_resid, null);
			Utils.setFontSizeAllView((ViewGroup)view, app_font_size);

			// set id of ticket view and sub views
			if (Constants.APP_DIRECTION == Constants.DIRECTION.LEFT_TO_RIGHT) {
				setId(view, ticketCount);
				ticketCount++;
			}  else {
				setId(view,  Constants.TODAY_TICKET_COUNT+Constants.WEEK_TICKET_COUNT+Constants.MONTH_TICKET_COUNT+Constants.NEW_BORN_TICKET_COUNT+Constants.REST_TICKET_COUNT-i-1);
				ticketCount++;
			}

			// set layout parameter
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
			params.setMargins(2, 0, 2, 2);
			view.setPadding(12, 8, 12, 8);

			if (i < 3) {
				view.setBackgroundResource(R.drawable.monthly_left);
			} else {
				view.setBackgroundResource(R.drawable.monthly_right);
			}

			// add view
			restLayout2.addView(view, params);

			// add animation
			addAnimationView(view, R.drawable.cartoon);
		}
	}
	
	/*
	 * set unique id of ticket view and sub text view,
	 * so we can get id of every ticket view and sub text view later by ticket count
	 */
	private void setId(View view, int curIndex) {
		// set id
		view.setId(curIndex*Constants.ID_ITEM_OFFSET);
		View txt_name_family = view.findViewById(R.id.txt_name_family);
		txt_name_family.setId(curIndex*Constants.ID_ITEM_OFFSET+1);
		View txt_address = view.findViewById(R.id.txt_address);
		txt_address.setId(curIndex*Constants.ID_ITEM_OFFSET+2);
		View txt_hebraw_birthday = view.findViewById(R.id.txt_hebraw_birthday);
		txt_hebraw_birthday.setId(curIndex*Constants.ID_ITEM_OFFSET+3);
		View txt_birthday = view.findViewById(R.id.txt_birthday);
		txt_birthday.setId(curIndex*Constants.ID_ITEM_OFFSET+4);
		View txt_new_birthday = view.findViewById(R.id.txt_next_birthday);
		txt_new_birthday.setId(curIndex*Constants.ID_ITEM_OFFSET+5);
	}

	/*
	 * showing animation
	 * Error: after 2 minutes, APP will crash
	 */
	private void addAnimationView(View view, final int animatedGifResId) {
		try {
			GifAnimationDrawable gifDrawable = new GifAnimationDrawable(getResources().openRawResource(animatedGifResId));
			gifDrawable.setOneShot(false);
			
			ImageView imgView = (ImageView) view.findViewById(R.id.img_view);
			if(imgView.getDrawable() == null) {
				imgView.setImageDrawable(gifDrawable);
				gifDrawable.setVisible(true, true);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void initialize_app() {
		mdbcontrol = new DBManager(this);

		if (!SDCardUtil.detectIsAvailable()) {
			// show SD card waring dialog
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.sdcard_warning)
			.setMessage(R.string.sderror)
			.setCancelable(false)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int paramInt) {
					dialog.cancel();
					MainActivity.instance.finish();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			})
			.show();

		} else {
			
			// init value
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			TODAY = cal.getTime();
			
			// get CSV Path
			File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			CSV_SAVE_PATH = downloadDir.getAbsolutePath()+"/birthday";
			File csv_save_dir = new File(CSV_SAVE_PATH);
			if (!csv_save_dir.exists()) {
				csv_save_dir.mkdir();
			}

			// initialize SQL DB
			DBOpenHelper.createDB(this.getApplicationContext());

			// initialize Configuration
			ConfigMgr.initialize(this.getApplicationContext());

			// get data from database, first time, did not need to save CSV file
			resetApp(false);

			// create time for checking new message
			timerUpdataShowInfo = new Timer(); 
			timerUpdataShowInfo.scheduleAtFixedRate(new UpdateShowingInfoTask(), 0, app_refresh_time*1000);
			
			// create time for checking date changing
			timerCheckDateChange = new Timer();
			timerCheckDateChange.scheduleAtFixedRate(new CheckDateChangeTask(), 0, Constants.CHECK_DATE_CHANGE_INTERVAL_TIME*1000);
		}
	}
	
	private void resetApp(boolean bSavetoCSV) {
		// reset current index
		ticketCount = 0;
		curTodayTicketIndex = 0;
		curWeekTicketIndex = 0;
		curMonthTicketIndex = 0;
		curNewBornTicketIndex = 0;
		curRestTicketIndex = 0;
		
		// reset info of every ticket
		for (int i = 0; i < Constants.ALL_TICKET_COUNT; i++) {
			TextView txt_name_family = (TextView) findViewById(i*Constants.ID_ITEM_OFFSET+1);
			TextView txt_address = (TextView) findViewById(i*Constants.ID_ITEM_OFFSET+2);
			TextView txt_hebraw_birthday = (TextView) findViewById(i*Constants.ID_ITEM_OFFSET+3);
			TextView txt_birthday = (TextView) findViewById(i*Constants.ID_ITEM_OFFSET+4);
			TextView txt_next_birthday = (TextView) findViewById(i*Constants.ID_ITEM_OFFSET+5);
			txt_name_family.setText("");
			txt_address.setText("");
			txt_hebraw_birthday.setText("");
			txt_birthday.setText("");
			txt_next_birthday.setText("");
		}

		// get data from database
		mdbcontrol.clearExcludeIDList();
		
		mAllPupilList = mdbcontrol.getAllPupilInfo();
		mTodayPupilList = mdbcontrol.getTodayPupilInfo(TODAY);
		mWeekPupilList = mdbcontrol.getWeekPupilInfo(TODAY);
		mMonthPupilList = mdbcontrol.getMonthPupilInfo(TODAY);
		mNewBornPupilList = mdbcontrol.getNewBornPupilInfo(TODAY);
		mRestPupilList = mdbcontrol.getRestPupilInfo();
		
		// change ui
		txtToday.setText("Todays Birthdays : " + mTodayPupilList.size());
		txtWeek.setText("This Week Birthdays : " + mWeekPupilList.size());
		txtMonth.setText("This Month Birthdays : " + mMonthPupilList.size());
		txtNewBorn.setText("New Born Birthdays : " + mNewBornPupilList.size());
		txtRest.setText("Rest of Year : " + mRestPupilList.size());
		
		// save to CSV file
		if (bSavetoCSV) {
			try {
				doExportToCSV();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Task and Handle for Update Ticket Showing
	 */
	private class UpdateShowingInfoTask extends TimerTask { 
		@Override
		public void run() {
			updateShowingInfoHandler.sendEmptyMessage(0); 
		}
	}

	private final Handler updateShowingInfoHandler = new Handler() { 
		@Override 
		public void handleMessage(Message msg) {
			updateTodayInfo();
			updateWeekInfo();
			updateMonthInfo();
			updateNewBornInfo();
			updateRestInfo();
		}
	}; 
	
	/*
	 * Task and Handle for Checking date changed
	 */
	private class CheckDateChangeTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Date oldday = TODAY;
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			TODAY = cal.getTime();
			
			// if date changes
			if (!oldday.equals(TODAY)) {
				updateDataAndCSVfile.sendEmptyMessage(0);
			}
		}
		
	}
	
	private final Handler updateDataAndCSVfile = new Handler() {
		public void handleMessage(Message msg) {
			// update data and save to csv file
			resetApp(true);
			
			// update showing data
			updateShowingInfoHandler.sendEmptyMessage(0);
		};
	};

	/*
	 * show one ticket:
	 * 2 - show this week ticket
	 * 3 - show this month ticket
	 * 4 - show new born ticket
	 * 5 - show rest of year ticket
	 */
	private void showTicketInfo(PupilInfo info, int titlecolorResourceid) {
		Date birthday = DateUtil.stringToDate(info.birthday, "yyyy-MM-dd");
		Calendar cal_startday = Calendar.getInstance();
		cal_startday.setTime(TODAY);
		cal_startday.add(Calendar.YEAR, -1);
		Date oneyearago = cal_startday.getTime(); // 1 year ago
		
		boolean is_new_born = false;
		if (birthday.compareTo(oneyearago) > 0) {
			is_new_born = true;
		}
		
		TextView txt_name_family = (TextView) findViewById(ticketCount*Constants.ID_ITEM_OFFSET+1);
		TextView txt_address = (TextView) findViewById(ticketCount*Constants.ID_ITEM_OFFSET+2);
		TextView txt_hebrew_birthday = (TextView) findViewById(ticketCount*Constants.ID_ITEM_OFFSET+3);
		TextView txt_birthday = (TextView) findViewById(ticketCount*Constants.ID_ITEM_OFFSET+4);
		TextView txt_next_birthday = (TextView) findViewById(ticketCount*Constants.ID_ITEM_OFFSET+5);
		
		// set value
		txt_name_family.setText(info.name + " " + info.family);
		txt_address.setText(info.address);
		String strbirthday = DateUtil.dateStringToOtherDateString(info.birthday, "yyyy-MM-dd", "dd/MM/yyyy");
		String strNextbirthday = DateUtil.dateStringToOtherDateString(info.next_birthday, "yyyy-MM-dd", "dd/MM/yyyy");
		txt_birthday.setText(strbirthday);
		txt_next_birthday.setText(strNextbirthday);
		
		// set color
		if (is_new_born) {
			txt_name_family.setTextColor(getResources().getColor(R.color.yellow));
		} else {
			txt_name_family.setTextColor(getResources().getColor(R.color.white));
		}
		txt_address.setTextColor(getResources().getColor(R.color.white));
		txt_birthday.setTextColor(getResources().getColor(R.color.white));
		txt_next_birthday.setTextColor(getResources().getColor(R.color.white));
		
		ticketCount++;
	}

	private LinearLayout getCurrentTicketLayout() {
		return (LinearLayout) findViewById(ticketCount*Constants.ID_ITEM_OFFSET);
	}
	/*
	 * show today ticket
	 * if count < 4 then show ticket once
	 * if count > 4 then show ticket in a loop
	 * 
	 * it will be same in show this week/this month/new born/rest of year
	 */
	private void updateTodayInfo() {
		ticketCount = 0;

		if (mTodayPupilList.size() == 0) {
		} else if (mTodayPupilList.size() == 1) {
			getCurrentTicketLayout().setBackgroundResource(R.drawable.dayly_1);
			getCurrentTicketLayout().setVisibility(View.VISIBLE);
			showTicketInfo(mTodayPupilList.get(0), R.color.white);
			getCurrentTicketLayout().setVisibility(View.GONE);
			ticketCount++;
			getCurrentTicketLayout().setVisibility(View.GONE);
			ticketCount++;
			getCurrentTicketLayout().setVisibility(View.GONE);
			ticketCount++;

		} else if (mTodayPupilList.size() == 2) {
			getCurrentTicketLayout().setBackgroundResource(R.drawable.dayly_2_left);
			getCurrentTicketLayout().setVisibility(View.VISIBLE);
			showTicketInfo(mTodayPupilList.get(0), R.color.white);
			getCurrentTicketLayout().setBackgroundResource(R.drawable.dayly_2_right);
			getCurrentTicketLayout().setVisibility(View.VISIBLE);
			showTicketInfo(mTodayPupilList.get(1), R.color.white);
			getCurrentTicketLayout().setVisibility(View.GONE);
			ticketCount++;
			getCurrentTicketLayout().setVisibility(View.GONE);
			ticketCount++;

		} else if (mTodayPupilList.size() == 3) {
			getCurrentTicketLayout().setBackgroundResource(R.drawable.dayly_3_left);
			getCurrentTicketLayout().setVisibility(View.VISIBLE);
			showTicketInfo(mTodayPupilList.get(0), R.color.white);
			getCurrentTicketLayout().setBackgroundResource(R.drawable.dayly_3_middle);
			getCurrentTicketLayout().setVisibility(View.VISIBLE);
			showTicketInfo(mTodayPupilList.get(1), R.color.white);
			getCurrentTicketLayout().setBackgroundResource(R.drawable.dayly_3_right);
			getCurrentTicketLayout().setVisibility(View.VISIBLE);
			showTicketInfo(mTodayPupilList.get(2), R.color.white);
			getCurrentTicketLayout().setVisibility(View.GONE);
			ticketCount++;

		} else if (mTodayPupilList.size() >= 4) {
			for (int i = 0; i < 4; i++) {
				getCurrentTicketLayout().setVisibility(View.VISIBLE);
				if (i < 2) {
					getCurrentTicketLayout().setBackgroundResource(R.drawable.dayly_4_left);
				} else  {
					getCurrentTicketLayout().setBackgroundResource(R.drawable.dayly_4_right);
				}
				showTicketInfo(mTodayPupilList.get(curTodayTicketIndex++), R.color.white);
				if (curTodayTicketIndex == mTodayPupilList.size()) {
					curTodayTicketIndex = 0;
				}
			}
		}
	}

	private void updateWeekInfo() {
		if (mWeekPupilList.size() <= Constants.WEEK_TICKET_COUNT) {
			for (int i = 0; i < mWeekPupilList.size(); i++) {
				showTicketInfo(mWeekPupilList.get(i), R.color.white);
			}
			for (int i = mWeekPupilList.size(); i < Constants.WEEK_TICKET_COUNT; i++) {
				ticketCount++;
			}

		} else {
			for (int i = 0; i < Constants.WEEK_TICKET_COUNT; i++) {
				showTicketInfo(mWeekPupilList.get(curWeekTicketIndex), R.color.white);
				curWeekTicketIndex++;
				if (curWeekTicketIndex == mWeekPupilList.size()) {
					curWeekTicketIndex = 0;
				}
			}
		}
	};

	private void updateMonthInfo() {
		if (mMonthPupilList.size() <= Constants.MONTH_TICKET_COUNT) {
			for (int i = 0; i < mMonthPupilList.size(); i++) {
				showTicketInfo(mMonthPupilList.get(i), R.color.white);
			}
			for (int i = mMonthPupilList.size(); i < Constants.MONTH_TICKET_COUNT; i++) {
				ticketCount++;
			}

		} else {
			for (int i = 0; i < Constants.MONTH_TICKET_COUNT; i++) {
				showTicketInfo(mMonthPupilList.get(curMonthTicketIndex), R.color.white);
				curMonthTicketIndex++;
				if (curMonthTicketIndex == mMonthPupilList.size()) {
					curMonthTicketIndex = 0;
				}
			}
		}
	}

	private void updateNewBornInfo() {
		if (mNewBornPupilList.size() <= Constants.NEW_BORN_TICKET_COUNT) {
			for (int i = 0; i < mNewBornPupilList.size(); i++) {
				showTicketInfo(mNewBornPupilList.get(i), R.color.yellow);
			}

		} else {
			for (int i = 0; i < Constants.NEW_BORN_TICKET_COUNT; i++) {
				showTicketInfo(mNewBornPupilList.get(curNewBornTicketIndex), R.color.yellow);
				curNewBornTicketIndex++;
				if (curNewBornTicketIndex == mNewBornPupilList.size()) {
					curNewBornTicketIndex = 0;
				}
			}
		}
	}

	private void updateRestInfo() {
		int numberOfOnNewborn = Constants.NEW_BORN_TICKET_COUNT - mNewBornPupilList.size();
		if (numberOfOnNewborn < 0) {
			numberOfOnNewborn = 0;
		}
		int numberOfRestTicket = Constants.REST_TICKET_COUNT + numberOfOnNewborn;
		if (mRestPupilList.size() <= numberOfRestTicket) {
			for (int i = 0; i < mRestPupilList.size(); i++) {
				showTicketInfo(mRestPupilList.get(i), R.color.white);
			}
			for (int i = mRestPupilList.size(); i < numberOfRestTicket; i++) {
				ticketCount++;
			}

		} else {
			for (int i = 0; i < numberOfRestTicket; i++) {
				showTicketInfo(mRestPupilList.get(curRestTicketIndex), R.color.white);
				curRestTicketIndex++;
				if (curRestTicketIndex == mRestPupilList.size()) {
					curRestTicketIndex = 0;
				}
			}
		}
	}

	/*
	 * GIF View
	 */
	private static class GIFView extends View{

		Movie movie;
		InputStream is = null;
		long moviestart;

		int view_width = 0;
		int view_height = 0;
		int img_width = 0;
		int img_height = 0;

		public GIFView(Context context, int ResId, int view_width, int view_height) {
			super(context);
			is = context.getResources().openRawResource(ResId);
			movie = Movie.decodeStream(is);
			this.view_width = view_width;
			this.view_height = view_height;
			img_width = movie.width();
			img_height = movie.height();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(0x00ffffff);
			super.onDraw(canvas);

			long now = android.os.SystemClock.uptimeMillis();
			//System.out.println("now="+now);
			if (moviestart == 0) {   // first time
				moviestart = now;
			}
			//System.out.println("\tmoviestart="+moviestart);
			int relTime = (int)((now - moviestart) % movie.duration()) ;
			//System.out.println("time="+relTime+"\treltime="+movie.duration());
			movie.setTime(relTime);
			movie.draw(canvas, (view_width - img_width)/2, (view_height - img_height)/2);
			this.invalidate();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_setting:
			showSettingDlg();
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this)
		.setTitle("Do you really want to EXIT?")
		.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				MainActivity.super.onBackPressed();
			}
		})
		.setNegativeButton(R.string.btn_cancel, null)
		.show();
	}

	public void AddNewPupilInfo(Bundle bundle) {
		if (bundle == null)
			return;

		String name = bundle.getString(Constants.EXTRA_NAME);
		String family = bundle.getString(Constants.EXTRA_FAMILIY);
		String address = bundle.getString(Constants.EXTRA_ADDRESS);
		String birthday = bundle.getString(Constants.EXTRA_BIRTHDAY);
		String next_birthday = bundle.getString(Constants.EXTRA_NEXT_BIRTHDAY);

		PupilInfo info = new PupilInfo(name, family, address, birthday, next_birthday, false);
		boolean success = mdbcontrol.AddNewPupilInfo(info);

		// update data
		if (success) {
			updateDataAndCSVfile.sendEmptyMessage(0);
		}
	}

	private void showSettingDlg() {
		Bundle bundle = new Bundle();

		String strToday = DateUtil.dateToString(TODAY, "dd/MM/yyyy");

		bundle.putString(Constants.EXTRA_TODAY, strToday);
		bundle.putInt(Constants.EXTRA_FONT_SIZE, app_font_size);
		bundle.putInt(Constants.EXTRA_REFRESH_TIME, app_refresh_time);

		Intent intent = new Intent(instance, SettingDlg.class);
		intent.putExtras(bundle);

		startActivityForResult(intent, REQ_SETTING);
		overridePendingTransition(R.anim.in_down, R.anim.out_down);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			if (requestCode == REQ_CONFIRM_EXIT) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						MainActivity.super.onBackPressed();
					}
				}, 500);

			} else if (requestCode == REQ_SETTING) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					settingOnResult(bundle);
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void settingOnResult(Bundle bundle) {
		// get value from bundle
		String strToday = bundle.getString(Constants.EXTRA_TODAY);
		Date oldDate = TODAY;
		TODAY = DateUtil.stringToDate(strToday, "dd/MM/yyyy");
		if (TODAY == null) {
			TODAY = oldDate;
		}
		app_font_size = bundle.getInt(Constants.EXTRA_FONT_SIZE);
		app_refresh_time = bundle.getInt(Constants.EXTRA_REFRESH_TIME);

		// update data
		if (!oldDate.equals(TODAY)) {
			updateDataAndCSVfile.sendEmptyMessage(0);
		}

		// set font size
		for (int i = 0; i < Constants.ALL_TICKET_COUNT; i++) {
			ViewGroup vg = (ViewGroup) findViewById(i*Constants.ID_ITEM_OFFSET);
			Utils.setFontSizeAllView(vg, app_font_size);
		}

		// set timer
		if (timerUpdataShowInfo != null) {
			timerUpdataShowInfo.cancel();
			timerUpdataShowInfo = null;
			timerUpdataShowInfo = new Timer();
			timerUpdataShowInfo.scheduleAtFixedRate(new UpdateShowingInfoTask(), 0, app_refresh_time*1000);
		}
	}

	private List<String[]> parseToCSVData(ArrayList<PupilInfo> pupil_list) {
		List<String[]> retList = new ArrayList<String[]>();
		for (PupilInfo info : pupil_list) {
			String[] row = new String[6];
			for (int i = 0; i < 6; i++)
				row[i] = "";

			row[0] = info.id;
			row[1] = info.name;
			row[2] = info.family;
			row[3] = info.address;
			row[4] = DateUtil.dateStringToOtherDateString(info.birthday, "yyyy-MM-dd", "dd/MM/yyyy");
			row[5] = DateUtil.dateStringToOtherDateString(info.next_birthday, "yyyy-MM-dd", "dd/MM/yyyy");

			retList.add(row);
		}

		if (!retList.isEmpty())
			retList.add(0, new String[]{"ID", "Name", "Family", "Address", "Birthday", "Next Birthday"});

		return retList;
	}

	public void doExportToCSV() throws IOException {
		CSVWriter csvWrite;
		File file;
		List<String[]> outData;

		// all data
		file = new File(CSV_SAVE_PATH + "/0_all_backup.csv");
		if (file.exists())
			file.delete();
		csvWrite = new CSVWriter(new FileWriter(file), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
		outData = parseToCSVData(mAllPupilList);
		csvWrite.writeAll(outData);
		csvWrite.close();

		// today
		file = new File(CSV_SAVE_PATH + "/1_today.csv");
		if (file.exists())
			file.delete();
		csvWrite = new CSVWriter(new FileWriter(file), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
		outData = parseToCSVData(mTodayPupilList);
		csvWrite.writeAll(outData);
		csvWrite.close();

		// this week
		file = new File(CSV_SAVE_PATH + "/2_this_week.csv");
		if (file.exists())
			file.delete();
		csvWrite = new CSVWriter(new FileWriter(file), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
		outData = parseToCSVData(mWeekPupilList);
		csvWrite.writeAll(outData);
		csvWrite.close();

		// this month
		file = new File(CSV_SAVE_PATH + "/3_this_month.csv");
		if (file.exists())
			file.delete();
		csvWrite = new CSVWriter(new FileWriter(file), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
		outData = parseToCSVData(mMonthPupilList);
		csvWrite.writeAll(outData);
		csvWrite.close();

		// new born
		file = new File(CSV_SAVE_PATH + "/4_new_born.csv");
		if (file.exists())
			file.delete();
		csvWrite = new CSVWriter(new FileWriter(file), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
		outData = parseToCSVData(mNewBornPupilList);
		csvWrite.writeAll(outData);
		csvWrite.close();

		// rest of year
		file = new File(CSV_SAVE_PATH + "/5_rest_of_year.csv");
		if (file.exists())
			file.delete();
		csvWrite = new CSVWriter(new FileWriter(file), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
		outData = parseToCSVData(mRestPupilList);
		csvWrite.writeAll(outData);
		csvWrite.close();

		Toast.makeText(getApplicationContext(), "Saved Successfully : " + CSV_SAVE_PATH, Toast.LENGTH_LONG).show();
	}

	private int parseFromCSVData(List<String[]> inputData, ArrayList<PupilInfo> pupil_list) {
		for (String[] row : inputData) {
			if (row.length != 6)
				return 0;

			PupilInfo info = new PupilInfo(row[1], row[2], row[3], row[4], row[5], false);
			pupil_list.add(info);
		}

		return 1;
	}

	public void doImportFromCSV() throws IOException {
		ArrayList<PupilInfo> pupil_list = new ArrayList<PupilInfo>();
		File file = new File(CSV_SAVE_PATH + "/0_all.csv");
		if (!file.exists()) {
			Toast.makeText(this, "there is no file: "+ file.getAbsolutePath(), Toast.LENGTH_LONG).show();
			return;
		}

		//CSVReader csvReader = new CSVReader(new FileReader(file.getAbsolutePath()));
		CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "UTF-8"));
		List<String[]> content = csvReader.readAll();
		if (content.size() > 0) {
			content.remove(0);
		}
		csvReader.close();

		int parseRes = parseFromCSVData(content, pupil_list);
		if (parseRes != 1) {
			Toast.makeText(this, "there is error in file: "+ file.getAbsolutePath(), Toast.LENGTH_LONG).show();
			return;
		}

		mdbcontrol.deleteAllPupilInfo();
		boolean success = mdbcontrol.AddNewPupilInfo(pupil_list);

		// update data
		if (success) {
			updateDataAndCSVfile.sendEmptyMessage(0);
		}
	}
}
