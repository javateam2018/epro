/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.mapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.activiti.engine.impl.util.json.JSONString;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 简单封装Jackson，实现JSON String<->Java Object的Mapper. 封装不同的输出风格,
 * 使用不同的builder函数创建实例.
 * 
 * @author ThinkGem
 * @version 2013-11-15
 */
// json映射继承Object映射
public class JsonMapper extends ObjectMapper {
	// ObjectMapper继承序列化
	private static final long serialVersionUID = 1L;
	// 记录器logger
	private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

	private static JsonMapper mapper;

	public JsonMapper() {
		this(Include.NON_EMPTY);
	}

	public JsonMapper(Include include) {
		// 设置输出时包含属性的风格
		if (include != null) {
			// 设置序列化包含物
			this.setSerializationInclusion(include);
		}
		// 允许单引号、允许不带引号的字段名称
		this.enableSimple();
		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		// disable:禁用 Deserialization：反序列化 Feature：特征
		this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 空值处理为空串
		// Serializer：串行转化器(把并行数据变成串行数据的寄存器) Provider：供应者
		this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
			@Override // 覆盖/重写注解
			public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
					throws IOException, JsonProcessingException {
				jgen.writeString("");
			}
		});
		// 进行HTML解码。 registerModule：登记模块
		// new一个简单的模块增加序列化（入参String类,json序列化<String>）
		this.registerModule(new SimpleModule().addSerializer(String.class, new JsonSerializer<String>() {
			@Override
			public void serialize(String value, JsonGenerator jgen, SerializerProvider provider)
					throws IOException, JsonProcessingException {
				jgen.writeString(StringEscapeUtils.unescapeHtml4(value));
			}
		}));
		// 设置时区(获取默认时区)
		this.setTimeZone(TimeZone.getDefault());// getTimeZone("GMT+8:00")
	}

	/**
	 * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
	 */
	// json映射获取实例（instance）
	public static JsonMapper getInstance() {
		if (mapper == null) {
			// 当mapper为null时，new一个简单有效的jsonMapper附值到mapper
			mapper = new JsonMapper().enableSimple();
		}
		// 返回一个jsonmapper类
		return mapper;
	}

	/**
	 * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
	 */
	// 非默认映射静态方法，返回一个jsonMapper类
	public static JsonMapper nonDefaultMapper() {
		if (mapper == null) {
			// 当mapper为null时，new一个jsonMapper包含非默认值，附值于mapper
			mapper = new JsonMapper(Include.NON_DEFAULT);
		}
		// 返回mapper的值
		return mapper;
	}

	/**
	 * Object可以是POJO(简单的java对象:plain old java object)，也可以是Collection或数组。
	 * 如果对象为Null, 返回"null". 如果集合为空集合, 返回"[]".
	 */
	// 将object转化为String类型的toJson方法
	public String toJson(Object object) {
		try {
			// 返回一个作为Stirng类型的object输入值
			return this.writeValueAsString(object);
		} catch (IOException e) {
			// 记录器提示错误：书写json字符串错误：object，io流异常堆栈
			logger.warn("write to json string error:" + object, e);
			// 报异常则返回一个空值
			return null;
		}
	}

	/**
	 * 反序列化POJO(简单的java对象:plain old java object)或简单Collection如List<String>.
	 * 
	 * 如果JSON字符串为Null或"null"字符串, 返回Null. 如果JSON字符串为"[]", 返回空集合.
	 * 
	 * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String,JavaType)
	 * 
	 * @see #fromJson(String, JavaType)
	 */
	public <T> T fromJson(String jsonString, Class<T> clazz) {
		// 调用StringUtils工具类的是否为空的方法，如果jsonString为空，则返回一个null
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}
		// 如果jsonString不为空，则读取jsonString,class类的值
		try {
			return this.readValue(jsonString, clazz);
		} catch (IOException e) {
			// 解析jsonString错误：jsonString，io流异常堆栈
			logger.warn("parse json string error:" + jsonString, e);
			// 返回一个空值
			return null;
		}
	}

	/**
	 * 反序列化复杂Collection如List<Bean>, 先使用函数createCollectionType构造类型,然后调用本函数.
	 * 
	 * @see #createCollectionType(Class, Class...)
	 */
	@SuppressWarnings("unchecked") // 抑制警告("未经抑制的")
	public <T> T fromJson(String jsonString, JavaType javaType) {
		// javaType类继承可序列化
		// 调用StringUtils工具类的是否为空的方法，如果jsonString为空，则返回一个null
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}
		// 如果jsonString不为空，则读取jsonString,javaType类的值
		try {
			return (T) this.readValue(jsonString, javaType);
		} catch (IOException e) {
			// 解析jsonString错误：jsonString，io流异常堆栈
			logger.warn("parse json string error:" + jsonString, e);
			// 返回一个空值
			return null;
		}
	}

	/**
	 * 构造泛型的Collection Type如: ArrayList<MyBean>,
	 * 则调用constructCollectionType(ArrayList.class,MyBean.class) HashMap
	 * <String,MyBean>, 则调用(HashMap.class,String.class, MyBean.class)
	 */
	// 构造一个创造collection类型的类，返回一个javaType elementClasses：元素类
	public JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		// 获取类型工厂方法，调用构造参数化类型的方法，将collectionClass，elementClasses入参，返回一个javaType类
		return this.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}

	/**
	 * 當JSON里只含有Bean的部分属性时，更新一個已存在Bean，只覆盖該部分的属性.
	 */
	@SuppressWarnings("unchecked") // 抑制警告("未经抑制的")
	// 更新bean方法
	public <T> T update(String jsonString, T object) {
		try {
			// 调用读取为更新的方法点出读取值的方法读取jsonString，返回一个T类型
			return (T) this.readerForUpdating(object).readValue(jsonString);
		} catch (JsonProcessingException e) {
			// 捕捉json加工异常
			// 记录器记录警告：更新jsonString类到object错误，堆栈
			logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
		} catch (IOException e) {
			// 捕获io异常异常
			// 记录器记录警告：更新jsonString类到object错误，io堆栈
			logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
		}
		return null;
	}

	/**
	 * 输出JSONP格式数据((JSON with Padding)是JSON的一种“使用模式”，可用于解决主流浏览器的跨域数据访问的问题).
	 */
	// functionName：运行名称，返回一个String类型
	public String toJsonP(String functionName, Object object) {
		return toJson(new JSONPObject(functionName, object));
	}

	/**
	 * 设定是否使用Enum(枚举)的toString函数来读写Enum(枚举), 为False时时使用Enum的name()函数来读写Enum,
	 * 默认为False. 注意本函数一定要在Mapper(映射)创建后, 所有的读写动作之前调用.
	 */
	// 创建一个enableEnumUseToString(使能够使用枚举的toString方法)，返回一个jsonMapper类
	public JsonMapper enableEnumUseToString() {
		// 调用enable方法，序列化枚举使用toString
		this.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		// 调用enable方法，非序列化枚举使用toString
		this.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		// 返回这个jsonMapper类
		return this;
	}

	/**
	 * 支持使用Jaxb(允许java开发人员将java类映射为xml表示方式(java architecture for XML Binding))
	 * 的Annotation(注释，注解)，使得POJO（简单的java对象）上的annotation不用与Jackson耦合。
	 * 默认会先查找jaxb的annotation，如果找不到再找jackson的。
	 */
	// 构造使能够jaxb注解的方法，返回json映射，降低JOPO的annotation与jackson的耦合度，使用jaxb
	public JsonMapper enableJaxbAnnotation() {
		// new一个jaxb注解模块
		JaxbAnnotationModule module = new JaxbAnnotationModule();
		// 调用登记模块的方法将jaxb注解模块登记
		this.registerModule(module);
		// 返回jaxb注解模块
		return this;
	}

	/**
	 * 允许单引号 允许不带引号的字段名称
	 */
	// 创造一个使能够简单的方法，返回一个jsonMapper类
	public JsonMapper enableSimple() {
		// 调用configure方法(特征：允许单个引用)
		this.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		// 调用configure方法(特征：允许不带引号字段名字)
		this.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		return this;
	}

	/**
	 * 取出Mapper做进一步的设置或使用其他序列化API. API（Application Programming
	 * Interface，应用程序编程接口）
	 */
	// 获取mapper，返回一个ObjectMapper类型
	public ObjectMapper getMapper() {
		return this;
	}

	/**
	 * 对象转换为JSON字符串
	 * 
	 * @param object
	 * @return JSON字符串
	 */
	// 构造一个静态的返回String类型的方法，将object类转化为json字符串
	public static String toJsonString(Object object) {
		// JsonMapper类获取实例化方法调用toJson方法将object转化为json字符串
		return JsonMapper.getInstance().toJson(object);
	}

	/**
	 * JSON字符串转换为对象
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return object
	 */
	// 构造一个静态的返回Object类型的方法，将JSON字符串转换为object类
	public static Object fromJsonString(String jsonString, Class<?> clazz) {
		// JsonMapper类获取实例化方法调用gfromJson方法将json字符串转化为object
		return JsonMapper.getInstance().fromJson(jsonString, clazz);
	}

	/**
	 * 测试
	 */
	// 定义一个main方法，测试
	public static void main(String[] args) {
		List<Map<String, Object>> list = Lists.newArrayList();
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", 1);
		map.put("pId", -1);
		map.put("name", "根节点");
		list.add(map);
		// 重新new一个map赋值于map，内存地址不一样
		map = Maps.newHashMap();
		map.put("id", 2);
		map.put("pId", 1);
		map.put("name", "你好");
		map.put("open", true);
		list.add(map);
		// 将object转化为String类型的toJson方法
		String json = JsonMapper.getInstance().toJson(list);
		System.out.println(json);
		// 将JSON字符串转换为object类
		Object obj = JsonMapper.getInstance().fromJson(json, Object.class);
		System.out.println(obj.toString());
		// 构造一个静态的返回String类型的方法，将object类转化为json字符串
		String jsonStr = JsonMapper.toJsonString(obj);
		System.out.println(jsonStr);
		// 构造一个静态的返回Object类型的方法，将JSON字符串转换为object类
		Object jsonObj = JsonMapper.fromJsonString(jsonStr, Object.class);
		System.out.println(jsonObj.toString());
	}

}
