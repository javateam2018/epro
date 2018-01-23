package com.thinkgem.jeesite.common.mapper.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "MapConvertor")
// 默认情况下，Jaxb编组出来的xml中的字段顺序是随机的，可使用@XmlType的propOrder属性来指定序列化的顺序。
@XmlAccessorType(XmlAccessType.FIELD)
// 表示使用这个类中的 private非静态字段作为 XML的序列化的属性或者元素
/**
 * 映射转化器类
 */
public class MapConvertor {

	private List<MapEntry> entries = new ArrayList<MapEntry>();

	// 新增入口方法(映射入口)
	public void addEntry(MapEntry entry) {
		entries.add(entry);
	}

	// 获取映射入口的方法，返回一个list集合
	public List<MapEntry> getEntries() {
		return entries;
	}

	// 构建一个静态类映射入口
	public static class MapEntry {
		// 声明key,String类型
		private String key;
		// 声明值，Object类型
		private Object value;

		// 无参构造器
		public MapEntry() {
			super();
		}

		// 带参数Map.Entry<String, Object> entry集合的构造器
		public MapEntry(Map.Entry<String, Object> entry) {
			super();
			this.key = entry.getKey();
			this.value = entry.getValue();
		}

		// 带参数String key, Object value集合的构造器
		public MapEntry(String key, Object value) {
			super();
			this.key = key;
			this.value = value;
		}

		// 封装
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	}
}