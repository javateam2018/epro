/**
 * Copyright (c) 2005-2012 springside.org.cn
 */
package com.thinkgem.jeesite.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 线程相关工具类.
 * @author calvin
 * @version 2013-01-15
 */
public class Threads {

	/**
	 * sleep等待,单位为毫秒,忽略InterruptedException.
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// Ignore.：忽略
			return;
		}
	}

	/**
	 * sleep等待,忽略InterruptedException.
	 */
	public static void sleep(long duration, TimeUnit unit) {
		try {
			Thread.sleep(unit.toMillis(duration));
		} catch (InterruptedException e) {
			// Ignore.：忽略
			return;
		}
	}

	/**
	 * 按照ExecutorService JavaDoc示例代码编写的Graceful Shutdown方法.
	 * 先使用shutdown, 停止接收新任务并尝试完成所有已存在任务.
	 * 如果超时, 则调用shutdownNow, 取消在workQueue中Pending的任务,并中断所有阻塞函数.
	 * 如果仍人超時，則強制退出.
	 * 另对在shutdown时线程本身被调用中断做了处理.
	 */
	//正常关闭方法
	public static void gracefulShutdown(ExecutorService pool, int shutdownTimeout, int shutdownNowTimeout,
			TimeUnit timeUnit) {
		pool.shutdown(); // Disable new tasks from being submitted：禁用新的任务被提交
		try {
			// Wait a while for existing tasks to terminate：等待一段时间终止现有的任务
			if (!pool.awaitTermination(shutdownTimeout, timeUnit)) {
				pool.shutdownNow(); // Cancel currently executing tasks：取消当前执行的任务
				// Wait a while for tasks to respond to being cancelled
				// 一段等待任务应对被取消
				if (!pool.awaitTermination(shutdownNowTimeout, timeUnit)) {
					System.err.println("Pool did not terminated");
				}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			//(重新)取消如果当前线程也中断了
			pool.shutdownNow();
			// Preserve interrupt status:保存中断状态
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 直接调用shutdownNow的方法, 有timeout控制.取消在workQueue中Pending的任务,并中断所有阻塞函数.
	 */
	public static void normalShutdown(ExecutorService pool, int timeout, TimeUnit timeUnit) {
		try {
			pool.shutdownNow();
			if (!pool.awaitTermination(timeout, timeUnit)) {
				System.err.println("Pool did not terminated");
			}
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

}
