package com.thinkgem.jeesite.common.utils;

import java.text.DecimalFormat;

/**
 * <p>
 * 文件大小工具类.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2013-01-02 12:50 PM
 * @since JDK 1.5
 */
//文件大小助理类
public class FileSizeHelper {
	public static long ONE_KB = 1024;
	public static long ONE_MB = ONE_KB * 1024;
	public static long ONE_GB = ONE_MB * 1024;
	public static long ONE_TB = ONE_GB * (long)1024;
	public static long ONE_PB = ONE_TB * (long)1024;
	//获取人的可读文件大小，返回一个String类型,入参Long类型文件大小(大写的Long)
	public static String getHumanReadableFileSize(Long fileSize) {
		if(fileSize == null) return null;
		return getHumanReadableFileSize(fileSize.longValue());
	}
	//获取人的可读文件大小，返回一个String类型,入参long类型文件大小
	public static String getHumanReadableFileSize(long fileSize) {
		if(fileSize < 0) {
			return String.valueOf(fileSize);
		}
		String result = getHumanReadableFileSize(fileSize, ONE_PB, "PB");
		if(result != null) {
			return result;
		}

		result = getHumanReadableFileSize(fileSize, ONE_TB, "TB");
		if(result != null) {
			return result;
		}
		result = getHumanReadableFileSize(fileSize, ONE_GB, "GB");
		if(result != null) {
			return result;
		}
		result = getHumanReadableFileSize(fileSize, ONE_MB, "MB");
		if(result != null) {
			return result;
		}
		result = getHumanReadableFileSize(fileSize, ONE_KB, "KB");
		if(result != null) {
			return result;
		}
		return String.valueOf(fileSize)+"B";
	}
	//获取人的可读文件大小，返回一个String类型,入参(long类型文件大小,单位，单位时间)
	private static String getHumanReadableFileSize(long fileSize, long unit, String unitName) {
		if(fileSize == 0) return "0";

		if(fileSize / unit >= 1) {
			double value = fileSize / (double)unit;
			//DecimalFormat:十进制格式类
			DecimalFormat df = new DecimalFormat("######.##"+unitName);
			return df.format(value);
		}
		return null;
	}
}
