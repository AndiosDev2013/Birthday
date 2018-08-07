package com.chen.birthday.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.chen.birthday.Constants;
import com.chen.birthday.PupilInfo;
import com.chen.birthday.util.DateUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DBManager {
	private DBOpenHelper mdbhelper;

	private final int CHECK_TYPE_MMDD = 0;
	private final int CHECK_TYPE_YYYYMMDD = 1;

	private ArrayList<String> ExcludeIDList = new ArrayList<String>();
	
	private Date Date0101 = new Date(0, 0, 1);
	private Date Date1231 = new Date(0, 11, 31);
	
	public DBManager(Context context) {
		this.mdbhelper = new DBOpenHelper(context);
	}

	public void clearExcludeIDList() {
		ExcludeIDList.clear();
	}
	
	/*
	 * Add Multiple pupil
	 * parameter : ArrayList or one pupil info
	 */
	synchronized public boolean AddNewPupilInfo(ArrayList<PupilInfo> pupil_list) {
		boolean success = true;
		synchronized (DBOpenHelper.DB_LOCK) {
			SQLiteDatabase mdb = mdbhelper.getWritableDatabase();

			try {
				if (mdb != null) {
					mdb.beginTransaction();

					for (int i = 0; i < pupil_list.size(); i++) {
						ContentValues values = new ContentValues();
						values.put(DBConstant.PUPIL_NAME, pupil_list.get(i).name);
						values.put(DBConstant.PUPIL_FAMILY, pupil_list.get(i).family);
						values.put(DBConstant.PUPIL_ADDRESS, pupil_list.get(i).address);
						values.put(DBConstant.PUPIL_BIRTHDAY, pupil_list.get(i).birthday);
						values.put(DBConstant.PUPIL_NEXT_BIRTHDAY, pupil_list.get(i).next_birthday);

						mdb.insert(DBConstant.TABLE_PUPILS, null, values);
					}
				}

				mdb.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
				success = false;

			} finally {
				if (mdb != null) {
					mdb.endTransaction();
					mdb.close();
				}					
			}
		}

		return success;
	}

	/*
	 * Add One Pupil
	 */
	synchronized public boolean AddNewPupilInfo(PupilInfo pupil_info) {
		boolean success = true;
		synchronized (DBOpenHelper.DB_LOCK) {
			SQLiteDatabase mdb = mdbhelper.getWritableDatabase();

			try {
				if (mdb != null) {
					mdb.beginTransaction();

					ContentValues values = new ContentValues();
					values.put(DBConstant.PUPIL_NAME, pupil_info.name);
					values.put(DBConstant.PUPIL_FAMILY, pupil_info.family);
					values.put(DBConstant.PUPIL_ADDRESS, pupil_info.address);
					values.put(DBConstant.PUPIL_BIRTHDAY, pupil_info.birthday);
					values.put(DBConstant.PUPIL_NEXT_BIRTHDAY, pupil_info.next_birthday);

					mdb.insert(DBConstant.TABLE_PUPILS, null, values);
				}

				mdb.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
				success = false;
			} finally {
				if (mdb != null) {
					mdb.endTransaction();
					mdb.close();
				}					
			}
		}	

		return success;
	}
	
	/*
	 * ALL
	 */
	synchronized public ArrayList<PupilInfo> getAllPupilInfo() {
		synchronized (DBOpenHelper.DB_LOCK) {
			ArrayList<PupilInfo> all_item = new ArrayList<PupilInfo>();
			SQLiteDatabase mdb = mdbhelper.getReadableDatabase();
			PupilInfo item = null;
			Cursor cursor = null;
			
			if (mdb != null) {
				String sql_selelct_str = "SELECT * FROM " + DBConstant.TABLE_PUPILS + " WHERE id <> 0 ORDER BY id";
				cursor = mdb.rawQuery(sql_selelct_str, null);
				
				while (cursor.moveToNext()) {
					String id = cursor.getString(cursor.getColumnIndex(DBConstant.ID));
					String name = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_NAME));
					String family = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_FAMILY));
					String address = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_ADDRESS));
					String birthday = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_BIRTHDAY));
					String next_birthday = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_NEXT_BIRTHDAY));

					item = new PupilInfo(name, family, address, birthday, next_birthday, true);
					item.id = id;
					
					all_item.add(item);
				}
				cursor.close();
				mdb.close();
			}
			return all_item;
		}	
	}
	
	/*
	 * Today
	 */
	synchronized public ArrayList<PupilInfo> getTodayPupilInfo(Date today) {
		ArrayList<PupilInfo> all_item = getPupilsInfoFromOneDateToOtherDate(today, today, CHECK_TYPE_MMDD, 0, true);
		
		// if none found, get nearest birthday members
		if (all_item.size() == 0) {
			// today - 12/31
			ArrayList<PupilInfo> temp_list = getPupilsInfoFromOneDateToOtherDate(today, Date1231, CHECK_TYPE_MMDD, 1, false);
			if (temp_list.size() <= 0) {
				// 1/1 - today
				temp_list = getPupilsInfoFromOneDateToOtherDate(Date0101, today, CHECK_TYPE_MMDD, 1, false);
			}
			
			if (temp_list.size() > 0) {
				String strBirthday = temp_list.get(0).birthday;
				Date date = DateUtil.stringToDate(strBirthday, "yyyy-MM-dd");
				temp_list = getPupilsInfoFromOneDateToOtherDate(date, date, CHECK_TYPE_MMDD, 0, true);
				
				all_item.addAll(temp_list);
			}
		}

		return all_item;
	}

	/*
	 * This Week
	 */
	synchronized public ArrayList<PupilInfo> getWeekPupilInfo(Date today) {
		ArrayList<PupilInfo> all_item = new ArrayList<PupilInfo>();

		Calendar cal_startday = Calendar.getInstance();
		cal_startday.setTime(today);
		cal_startday.add(Calendar.DAY_OF_MONTH, 1);
		Date startday = cal_startday.getTime(); // 1 day later

		Calendar cal_endday = Calendar.getInstance();
		cal_endday.setTime(today);
		cal_endday.add(Calendar.DAY_OF_MONTH, 7);
		Date endday = cal_endday.getTime(); // 7 day later

		String mmddStartDay = DateUtil.dateToString(startday, "MMdd");
		String mmddEndDay = DateUtil.dateToString(endday, "MMdd");

		try {
			long number1 = Long.parseLong(mmddStartDay);
			long number2 = Long.parseLong(mmddEndDay);
			if(number1 < number2) {
				all_item = getPupilsInfoFromOneDateToOtherDate(startday, endday, CHECK_TYPE_MMDD, 0, true);
			
			} else {
				ArrayList<PupilInfo> item_list1 = getPupilsInfoFromOneDateToOtherDate(startday, Date1231, CHECK_TYPE_MMDD, 0, true);
				all_item.addAll(item_list1);
				
				ArrayList<PupilInfo> item_list2 = getPupilsInfoFromOneDateToOtherDate(Date0101, endday, CHECK_TYPE_MMDD, 0, true);
				all_item.addAll(item_list2);
			}
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		
		Calendar cal_endday1 = Calendar.getInstance();
		cal_endday1.setTime(today);
		cal_endday1.add(Calendar.DAY_OF_MONTH, 8);
		Date endday1 = cal_endday1.getTime(); // 8 day later
		
		// if none found, get nearest birthday members
		if (all_item.size() < Constants.WEEK_TICKET_COUNT) {
			int need_get_count = Constants.WEEK_TICKET_COUNT - all_item.size();
			ArrayList<PupilInfo> item_list1 = getPupilsInfoFromOneDateToOtherDate(endday1, Date1231, CHECK_TYPE_MMDD, need_get_count, true);
			all_item.addAll(item_list1);

			// if none found, get nearest birthday members
			if (all_item.size() < Constants.WEEK_TICKET_COUNT) {
				need_get_count = Constants.WEEK_TICKET_COUNT - all_item.size();
				ArrayList<PupilInfo> item_list2 = getPupilsInfoFromOneDateToOtherDate(Date0101, endday1, CHECK_TYPE_MMDD, need_get_count, true);
				all_item.addAll(item_list2);
			}
		}
		
		return all_item;
	}

	/*
	 * This Month
	 */
	synchronized public ArrayList<PupilInfo> getMonthPupilInfo(Date today) {
		ArrayList<PupilInfo> all_item = new ArrayList<PupilInfo>();

		Calendar cal_startday = Calendar.getInstance();
		cal_startday.setTime(today);
		cal_startday.add(Calendar.DAY_OF_MONTH, 1);
		Date startday = cal_startday.getTime(); // 1 day later

		Calendar cal_endday = Calendar.getInstance();
		cal_endday.setTime(today);
		cal_endday.add(Calendar.DAY_OF_MONTH, 30);
		Date endday = cal_endday.getTime(); // 30 day later

		String mmddStartDay = DateUtil.dateToString(startday, "MMdd");
		String mmddEndDay = DateUtil.dateToString(endday, "MMdd");

		try {
			long number1 = Long.parseLong(mmddStartDay);
			long number2 = Long.parseLong(mmddEndDay);
			if(number1 < number2) {
				all_item = getPupilsInfoFromOneDateToOtherDate(startday, endday, CHECK_TYPE_MMDD, 0, true);
			
			} else {
				ArrayList<PupilInfo> item_list1 = getPupilsInfoFromOneDateToOtherDate(startday, Date1231, CHECK_TYPE_MMDD, 0, true);
				all_item.addAll(item_list1);
				
				ArrayList<PupilInfo> item_list2 = getPupilsInfoFromOneDateToOtherDate(Date0101, endday, CHECK_TYPE_MMDD, 0, true);
				all_item.addAll(item_list2);
			}
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		
		Calendar cal_endday1 = Calendar.getInstance();
		cal_endday1.setTime(today);
		cal_endday1.add(Calendar.DAY_OF_MONTH, 31);
		Date endday1 = cal_endday1.getTime(); // 31 day later
		
		// if none found, get nearest birthday members
		if (all_item.size() < Constants.MONTH_TICKET_COUNT) {
			int need_get_count = Constants.MONTH_TICKET_COUNT - all_item.size();
			ArrayList<PupilInfo> item_list1 = getPupilsInfoFromOneDateToOtherDate(endday1, Date1231, CHECK_TYPE_MMDD, need_get_count, true);
			all_item.addAll(item_list1);

			// if none found, get nearest birthday members
			if (all_item.size() < Constants.MONTH_TICKET_COUNT) {
				need_get_count = Constants.MONTH_TICKET_COUNT - all_item.size();
				ArrayList<PupilInfo> item_list2 = getPupilsInfoFromOneDateToOtherDate(Date0101, endday1, CHECK_TYPE_MMDD, need_get_count, true);
				all_item.addAll(item_list2);
			}
		}
		
		return all_item;
	}
	
	/*
	 * New Born Baby
	 */
	synchronized public ArrayList<PupilInfo> getNewBornPupilInfo(Date today) {
		ArrayList<PupilInfo> all_item = new ArrayList<PupilInfo>();

		Calendar cal_startday = Calendar.getInstance();
		cal_startday.setTime(today);
		cal_startday.add(Calendar.YEAR, -1);
		Date startday = cal_startday.getTime(); // 1 year ago

		Calendar cal_endday = Calendar.getInstance();
		cal_endday.setTime(today);
		cal_endday.add(Calendar.YEAR, 5000);
		Date endday = cal_endday.getTime(); // 5000 year later
		
		all_item = getPupilsInfoFromOneDateToOtherDate(startday, endday, CHECK_TYPE_YYYYMMDD, 0, true);
		
		return all_item;
	}
	
	/*
	 * Rest Year
	 */
	synchronized public ArrayList<PupilInfo> getRestPupilInfo() {
		synchronized (DBOpenHelper.DB_LOCK) {
			ArrayList<PupilInfo> all_item = new ArrayList<PupilInfo>();
			SQLiteDatabase mdb = mdbhelper.getReadableDatabase();
			PupilInfo item = null;
			Cursor cursor = null;

			if (mdb != null) {
				String notInString = "";
				// get id list that need to exclude
				if (ExcludeIDList.size() > 0) {
					int size = ExcludeIDList.size();
					notInString = " WHERE id not in (";
					for (int i = 0; i < size - 1; i++) {
						notInString += ExcludeIDList.get(i) + ",";
					}
					notInString += ExcludeIDList.get(size-1) + ")";
				}

				String sql_selelct_str = "SELECT *, strftime('%m%d', " + DBConstant.PUPIL_BIRTHDAY + ") AS md, strftime('%Y', " + DBConstant.PUPIL_BIRTHDAY + ") AS y"
						+ " FROM " + DBConstant.TABLE_PUPILS + notInString 
						+ " ORDER BY md, y";

				cursor = mdb.rawQuery(sql_selelct_str, null);
				
				while (cursor.moveToNext()) {
					String id = cursor.getString(cursor.getColumnIndex(DBConstant.ID));
					String name = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_NAME));
					String family = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_FAMILY));
					String address = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_ADDRESS));
					String birthday = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_BIRTHDAY));
					String next_birthday = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_NEXT_BIRTHDAY));

					item = new PupilInfo(name, family, address, birthday, next_birthday, true);
					item.id = id;
					
					ExcludeIDList.add(id+"");

					all_item.add(item);
				}
				cursor.close();
				mdb.close();
			}
			return all_item;
		}		
	}
	
	/*
	 * type = 0 : mmdd
	 * type = 1 : yyyymmdd
	 */
	synchronized public ArrayList<PupilInfo> getPupilsInfoFromOneDateToOtherDate(Date startDate, Date endDate, int type, int limit, boolean bExclude) {
		synchronized (DBOpenHelper.DB_LOCK) {
			ArrayList<PupilInfo> all_item = new ArrayList<PupilInfo>();
			SQLiteDatabase mdb = mdbhelper.getReadableDatabase();
			PupilInfo item = null;
			Cursor cursor = null;
			
			String strStartDay, strEndDay;

			if (type == CHECK_TYPE_MMDD) {
				strStartDay = DateUtil.dateToString(startDate, "MMdd");
				strEndDay = DateUtil.dateToString(endDate, "MMdd");
			} else {
				strStartDay = DateUtil.dateToString(startDate, "yyyyMMdd");
				strEndDay = DateUtil.dateToString(endDate, "yyyyMMdd");
			}

			if (mdb != null) {
				String sql_selelct_str = "";
				String notInString = "";
				// get id list that need to exclude
				if (ExcludeIDList.size() > 0) {
					int size = ExcludeIDList.size();
					notInString = " And id not in (";
					for (int i = 0; i < size - 1; i++) {
						notInString += ExcludeIDList.get(i) + ",";
					}
					notInString += ExcludeIDList.get(size-1) + ")";
				}

				if (type == CHECK_TYPE_MMDD) {
					sql_selelct_str = "SELECT *, strftime('%m%d', " + DBConstant.PUPIL_BIRTHDAY + ") AS md, strftime('%Y', " + DBConstant.PUPIL_BIRTHDAY + ") AS y"
							+ " FROM " + DBConstant.TABLE_PUPILS
							+ " WHERE md >= '" + strStartDay + "' AND md <= '" + strEndDay + "'" + notInString 
							+ " ORDER BY md, y";

				} else {
					sql_selelct_str = "SELECT *, strftime('%m%d', " + DBConstant.PUPIL_BIRTHDAY + ") AS md, strftime('%Y%m%d', " + DBConstant.PUPIL_BIRTHDAY + ") AS ymd"
							+ " FROM " + DBConstant.TABLE_PUPILS
							+ " WHERE ymd >= '" + strStartDay + "' AND ymd <= '" + strEndDay + "'" + notInString
							+ " ORDER BY md, ymd";
				}

				if (limit > 0) {
					sql_selelct_str = sql_selelct_str + " limit " + limit;
				}
				
				
				cursor = mdb.rawQuery(sql_selelct_str, null);
				
				while (cursor.moveToNext()) {
					String id = cursor.getString(cursor.getColumnIndex(DBConstant.ID));
					String name = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_NAME));
					String family = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_FAMILY));
					String address = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_ADDRESS));
					String birthday = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_BIRTHDAY));
					String next_birthday = cursor.getString(cursor.getColumnIndex(DBConstant.PUPIL_NEXT_BIRTHDAY));

					item = new PupilInfo(name, family, address, birthday, next_birthday, true);
					item.id = id;
					
					if (bExclude) {
						ExcludeIDList.add(id+"");
					}

					all_item.add(item);
				}
				cursor.close();
				mdb.close();
			}
			return all_item;
		}		
	}
	
	public synchronized int deleteAllPupilInfo() {
		SQLiteDatabase db = mdbhelper.getWritableDatabase();
		int answer = db.delete(DBConstant.TABLE_PUPILS, null, null);
		db.close();
		
		return answer;
	}
}
