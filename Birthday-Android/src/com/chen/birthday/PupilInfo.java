package com.chen.birthday;

import com.chen.birthday.util.DateUtil;

public class PupilInfo {
	public String id;
	public String name;
	public String family;
	public String address;
	public String birthday;
	public String next_birthday;
	
	public PupilInfo(String name, String family, String address, String birthday, String next_birthday, boolean alreadyConvert) {
		this.name = name;
		this.family = family;
		this.address = address;
		if (alreadyConvert) {
			this.birthday = birthday;
			this.next_birthday = next_birthday;
		} else {
			this.birthday = DateUtil.dateStringToOtherDateString(birthday, "dd/MM/yyyy", "yyyy-MM-dd");
			this.next_birthday = DateUtil.dateStringToOtherDateString(next_birthday, "dd/MM/yyyy", "yyyy-MM-dd");
		}
	}
}
