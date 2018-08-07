package com.chen.birthday.util;

import android.os.Environment;
import android.os.StatFs;
import java.io.File;

public class SDCardUtil {
	public static boolean detectIsAvailable() {
		if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))&& 
				(Environment.getExternalStorageDirectory().canWrite()))
			return true;
		else
			return false;
	}
	
	public static  boolean isAvailableExternalMemorySize() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) 
        {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            if ((availableBlocks * blockSize / 1024L / 1024L) > 50L) //50Mbyte
            	return true;
            else
            	return false;
        }
        else 
        {
            return false;
        }
    }

	public static boolean detectStorage() 
	{
		if (Environment.getExternalStorageState().equals("mounted")) 
		{
			StatFs statfs = new StatFs(Environment.getExternalStorageDirectory().getPath());
			if (statfs.getBlockSize() * statfs.getAvailableBlocks() / 1024L / 1024L > 50L)
				return true;
			else
				return false;
		} 
		else 
			return false;
	}
}
