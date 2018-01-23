/**
 * Copyright (c) 2005-2012 springside.org.cn
 */
package com.thinkgem.jeesite.common.beanvalidator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * JSR303(Java Specification Requests：java规范请求（Java 规范提案）303)
 * Validator（验证器）(Hibernate Validator)工具类.
 * 
 * ConstraintViolation(约束违反)中包含propertyPath（性质路径）, message（信息） 和invalidValue（无效值）等信息.
 * 提供了各种convert（转变）方法，适合不同的i18n（国际化）需求:
 * 1. List<String>, String内容为message
 * 2. List<String>, String内容为propertyPath（性质路径） + separator（分隔符） + message（信息）
 * 3. Map<propertyPath, message>
 * 
 * 详情见wiki: https://github.com/springside/springside4/wiki/HibernateValidator
 * @author calvin
 * @version 2013-01-15
 */
//bean验证方法
public class BeanValidators {

	/**
	 * 调用JSR303的validate（验证）方法, 验证失败时抛出ConstraintViolationException（约束违反异常）.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })//抑制警告({"未经核对的"，"行类型"})
	//验证和异常方法
	public static void validateWithException(Validator validator, Object object, Class<?>... groups)
			throws ConstraintViolationException {
		//set集合，无序唯一：约束违反=验证类
		Set constraintViolations = validator.validate(object, groups);
		//isEmpty()为set自带接口方法，返回一个boolean值
		if (!constraintViolations.isEmpty()) {
			/**
			 * 如果set集合不为空，则抛出新的约束违反异常
			 */
			throw new ConstraintViolationException(constraintViolations);
		}
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException（约束违反异常）中的Set<ConstraintViolations>中为List<message>.
	 */
	//提取信息方法（约束违反异常类入参），因为调用别的方法，因此不用@suppresswarnings
	public static List<String> extractMessage(ConstraintViolationException e) {
		//返回一个约束违反异常set集合
		return extractMessage(e.getConstraintViolations());
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolation>为List<message>
	 */
	@SuppressWarnings("rawtypes")//抑制警告("行类型")
	//重载extractMessage方法
	public static List<String> extractMessage(Set<? extends ConstraintViolation> constraintViolations) {
		List<String> errorMessages = Lists.newArrayList();
		for (ConstraintViolation violation : constraintViolations) {
			errorMessages.add(violation.getMessage());
		}
		return errorMessages;
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为Map<property, message>.
	 */
	//set转为map，提取性质和信息的方法,因为调用别的方法，因此不用@suppresswarnings
	public static Map<String, String> extractPropertyAndMessage(ConstraintViolationException e) {
		return extractPropertyAndMessage(e.getConstraintViolations());
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolation>为Map<property, message>.
	 */
	//提取性质和信息的方法重载，抑制警告
	@SuppressWarnings("rawtypes")
	public static Map<String, String> extractPropertyAndMessage(Set<? extends ConstraintViolation> constraintViolations) {
		Map<String, String> errorMessages = Maps.newHashMap();
		for (ConstraintViolation violation : constraintViolations) {
			errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
		}
		return errorMessages;
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为List<propertyPath message>.
	 */
	//提取性质和信息作为list集合，方法重载
	public static List<String> extractPropertyAndMessageAsList(ConstraintViolationException e) {
		return extractPropertyAndMessageAsList(e.getConstraintViolations(), " ");
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolations>为List<propertyPath message>.
	 */
	//提取性质和信息作为list集合，方法重载，抑制警告
	@SuppressWarnings("rawtypes")
	public static List<String> extractPropertyAndMessageAsList(Set<? extends ConstraintViolation> constraintViolations) {
		return extractPropertyAndMessageAsList(constraintViolations, " ");
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为List<propertyPath +separator+ message>.
	 */
	//提取性质和信息作为list集合,入参为带分隔符
	public static List<String> extractPropertyAndMessageAsList(ConstraintViolationException e, String separator) {
		return extractPropertyAndMessageAsList(e.getConstraintViolations(), separator);
	}

	/**
	 * 辅助方法, 转换Set<ConstraintViolation>为List<propertyPath +separator+ message>.
	 */
	//提取性质和信息作为list集合,入参为带分隔符
	@SuppressWarnings("rawtypes")
	public static List<String> extractPropertyAndMessageAsList(Set<? extends ConstraintViolation> constraintViolations,
			String separator) {
		List<String> errorMessages = Lists.newArrayList();
		for (ConstraintViolation violation : constraintViolations) {
			errorMessages.add(violation.getPropertyPath() + separator + violation.getMessage());
		}
		return errorMessages;
	}
}