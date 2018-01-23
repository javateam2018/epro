/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.filter;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;

/**
 * 页面高速缓存过滤器
 * 
 * @author ThinkGem
 * @version 2013-8-5
 */
// PageCachingFilter:页面高速缓存过滤器 继承 SimplePageCachingFilter简单的页面缓存过滤器
public class PageCachingFilter extends SimplePageCachingFilter {
	// 私有的隐藏信息
	private CacheManager cacheManager = SpringContextHolder.getBean(CacheManager.class);

	@Override//表示重写，有注释和保证一定重写的作用
	protected CacheManager getCacheManager() {
		this.cacheName = "pageCachingFilter";
		return cacheManager;
	}

}
