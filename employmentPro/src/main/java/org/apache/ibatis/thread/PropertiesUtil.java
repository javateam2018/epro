package org.apache.ibatis.thread;

import java.util.Properties;
/**
 * 属性工具类
 */
public class PropertiesUtil {

	private static String filename = "/mybatis-refresh.properties";
	private static Properties pro = new Properties();
	static {
		try {
			pro.load(PropertiesUtil.class.getResourceAsStream(filename));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Load mybatis-refresh “"+filename+"” file error.");
		}
	}

	public static int getInt(String key) {
		int i = 0;
		try {
			i = Integer.parseInt(getString(key));
		} catch (Exception e) {
		}
		return i;
	}

	public static String getString(String key) {
		return pro == null ? null : pro.getProperty(key);
	}

}
