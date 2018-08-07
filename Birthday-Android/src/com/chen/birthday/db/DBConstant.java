package com.chen.birthday.db;

public class DBConstant {
	/* Related DB */
	public static final String DATABASE_NAME = "birthday.db";
	public static final int DATABASE_VERSION = 1;

	public static final String CREATE_DB_SQL_PREFIX = "CREATE TABLE IF NOT EXISTS ";
	public static final String DELETE_DB_SQL_PREFIX = "DROP TABLE IF EXISTS ";

	/* DB Tables Name */
	public static final String TABLE_PUPILS = "pupils";
	
	public static final String ID = "id";

	/* APP_User Table Column Name */
	public static final String PUPIL_NAME = "name";
	public static final String PUPIL_FAMILY = "family";
	public static final String PUPIL_ADDRESS = "address";
	public static final String PUPIL_BIRTHDAY = "birthday";
	public static final String PUPIL_NEXT_BIRTHDAY = "next_birthday";
}
