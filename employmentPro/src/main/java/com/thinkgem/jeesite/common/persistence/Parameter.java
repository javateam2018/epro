/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.persistence;

import java.util.HashMap;

/**
 * 查询参数类
 * @author ThinkGem
 * @version 2013-8-23
 */
//参数继承HashMap
public class Parameter extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 构造类，例：new Parameter(id, parentIds)
	 * 
	 * @param values
	 * 参数值 public void test(Object... objs){}
	 * 如果你的方法参数像上面那样定义的话，调用就非常的灵活，下面的调用均可：
	 * test(); test("1"); test("1", "2"); test("1", "2", "3");
     * 也就是说，你的参数可以是任意个。 而test方法里面获取参数时，则可以将objs当成一个数组： 
     * if(objs != null && objs.length > 0)
     * { System.out.println(objs[i]); }
	 */
	public Parameter(Object... values) {
		if (values != null){
			for (int i=0; i<values.length; i++){
				put("p"+(i+1), values[i]);
			}
		}
	}
	
	/**
	 * 构造类，例：new Parameter(new Object[][]{{"id", id}, {"parentIds", parentIds}})
	 * @param parameters 参数二维数组
	 */
	public Parameter(Object[][] parameters) {
		if (parameters != null){
			for (Object[] os : parameters){
				if (os.length == 2){
					put((String)os[0], os[1]);
				}
			}
		}
	}
	
}
