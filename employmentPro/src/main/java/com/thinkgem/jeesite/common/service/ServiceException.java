/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.service;

/**
 * Service层公用的Exception, 从由Spring管理事务的函数中抛出时会触发事务回滚.
 * 
 * @author ThinkGem
 */
// 服务层异常继承运行时间异常
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	// 无参构造器
	public ServiceException() {
		super();
	}

	// 带参数(字符串信息)构造器
	public ServiceException(String message) {
		super(message);
	}

	/**
	 * Throwable 类是 Java 语言中所有错误或异常的超类。只有当对象是此类（或其子类之一）的实例时，
	 * 才能通过 Java 虚拟机或者 Javathrow 语句抛出。
	 * 类似地，只有此类或其子类之一才可以是 catch 子句中的参数类型。
	 * @param cause
	 */
	// 带参数(异常原因)构造器
	public ServiceException(Throwable cause) {
		super(cause);
	}
	//带参数(字符串信息和异常原因)构造器
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
