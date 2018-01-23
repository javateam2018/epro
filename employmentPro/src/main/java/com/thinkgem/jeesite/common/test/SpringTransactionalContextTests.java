package com.thinkgem.jeesite.common.test;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * Spring 单元测试基类
 * @author ThinkGem
 * @version 2013-05-15
 */
@ActiveProfiles("production")//活动简介(产品)
@ContextConfiguration(locations = {"/spring-context.xml"})//上下文配置
//Spring事务上下文测试 继承 抽象的事务JUnit4 Spring上下文测试
public class SpringTransactionalContextTests extends AbstractTransactionalJUnit4SpringContextTests {
	//数据源
	protected DataSource dataSource;

	@Autowired//自动装配
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}
	
}
