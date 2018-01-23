/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.persistence.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 标识MyBatis的DAO,方便{@link org.mybatis.spring.mapper.MapperScannerConfigurer}的扫描。
 * 
 * @author thinkgem
 * @version 2013-8-28
 */
@Retention(RetentionPolicy.RUNTIME)
/**
 * Retention(保留)注解说明,这种类型的注解会被保留到那个阶段. 有三个值:
 * 1.RetentionPolicy.SOURCE——这种类型的Annotations只在源代码级别保留, 编译时就会被忽略
 * 2.RetentionPolicy.CLASS——这种类型的Annotations编译时被保留, 在class文件中存在,但JVM将会忽略
 * 3.RetentionPolicy.RUNTIME——这种类型的Annotations将被JVM保留,
 * 所以他们能在运行时被JVM或其他使用反射机制的代码所读取和使用.
 */
@Target(ElementType.TYPE)
/**
 * Target：指示注释类型所适用的程序元素的种类
 */
@Documented
/**
 * Documented 注解表明这个注解应该被 javadoc工具记录. 
 * 默认情况下,javadoc是不包括注解的.
 * 但如果声明注解时指定了 @Documented,
 * 则它会被 javadoc 之类的工具处理, 
 * 所以注解类型信息也会被包括在生成的文档中.
 */
@Component//组件
public @interface MyBatisDao {

	/**
	 * The value may indicate a suggestion for a logical component name, to be
	 * turned into a Spring bean in case of an autodetected component.
	 * 可能表明一个建议值为一个逻辑组件名称,变成一个Spring bean的一个组件。
	 * @return the suggested component name, if any
	 */
	String value() default "";

}