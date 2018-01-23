package com.thinkgem.jeesite.common.mapper.adapters;

//adapter:转换器
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;
/**
 * 创建一个映射转换器类继承xml转换器
 */
public class MapAdapter extends XmlAdapter<MapConvertor, Map<String, Object>> {

	@Override//重写方法marshal(排序)Map集合key值唯一
	public MapConvertor marshal(Map<String, Object> map) throws Exception {
		//new一个映射转化器(MapConvertor)，MapConvertor构造器不同
		MapConvertor convertor = new MapConvertor();
		//强遍历map集合的进入设置方法
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			MapConvertor.MapEntry e = new MapConvertor.MapEntry(entry);
			convertor.addEntry(e);
		}
		//返回MapConvertor转化器
		return convertor;
	}

	@Override//重写方法unmarshal(不排序)MapConvertor构造器不同
	public Map<String, Object> unmarshal(MapConvertor map) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		//hashMapkey值唯一
		for (MapConvertor.MapEntry e : map.getEntries()) {
			result.put(e.getKey(), e.getValue());
		}
		return result;
	}

}
