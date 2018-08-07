package com.chen.birthday.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {
	public static Object DB_LOCK = new Object();
	private static DBOpenHelper mdbopenhelper = null;
	private static SQLiteDatabase mwritabledb = null;
	private static SQLiteDatabase mreadabledb = null;

	public DBOpenHelper(Context context) {
		super(context, DBConstant.DATABASE_NAME, null, DBConstant.DATABASE_VERSION);
	}

	public static SQLiteDatabase writeDB() {
		if ((mwritabledb == null) && (mdbopenhelper != null))
			mwritabledb = mdbopenhelper.getWritableDatabase();
		return mwritabledb;
	}

	public static DBOpenHelper createDB(Context paramContext) {
		if (mdbopenhelper == null)
			mdbopenhelper = new DBOpenHelper(paramContext);
		return mdbopenhelper;
	}

	public static SQLiteDatabase readDB() {
		if ((mreadabledb == null) && (mdbopenhelper != null))
			mreadabledb = mdbopenhelper.getReadableDatabase();
		return mreadabledb;
	}

	public final void onCreate(SQLiteDatabase db) {
		
		db.execSQL(DBConstant.CREATE_DB_SQL_PREFIX + DBConstant.TABLE_PUPILS
				+ " (id integer primary key autoincrement, "
				+ DBConstant.PUPIL_NAME + " INTEGER, "
				+ DBConstant.PUPIL_FAMILY + " text, "
				+ DBConstant.PUPIL_ADDRESS + " text, "
				+ DBConstant.PUPIL_BIRTHDAY + " text, "
				+ DBConstant.PUPIL_NEXT_BIRTHDAY + " text)");
	}

	public final void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
//		Log.w("---[DEBUG]----", "Upgrading database from version " + oldVersion
//				+ " to " + newVersion + ", which will destroy all old data");
		
		db.execSQL(DBConstant.DELETE_DB_SQL_PREFIX + DBConstant.TABLE_PUPILS);
		
		onCreate(db);
	}
}