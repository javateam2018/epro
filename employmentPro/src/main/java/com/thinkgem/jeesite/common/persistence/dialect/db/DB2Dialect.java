/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.persistence.dialect.db;

import com.thinkgem.jeesite.common.persistence.dialect.Dialect;

/**
 * DB2的分页数据库方言实现
 *
 * @author poplar.yfyang
 * @version 1.0 2010-10-10 下午12:31
 * @since JDK 1.5
 */
public class DB2Dialect implements Dialect {
    @Override//重写
    //支持限制的方法supportsLimit()
    public boolean supportsLimit() {
        return true;
    }
    //获取行数的方法，导入sql语句
    private static String getRowNumber(String sql) {
    	// 构造一个不带任何字符的字符串生成器，其初始容量为50。（不写默认值为16），append附加
        StringBuilder rownumber = new StringBuilder(50)
                .append("rownumber() over(");
        /*
         * order by：以什么排序，toLowerCase()：使用默认语言环境的规则将此 String 中的所有字符都转换为小写。
         * indexOf(String str) :返回指定子字符串在此字符串中第一次出现处的索引。
         */
        int orderByIndex = sql.toLowerCase().indexOf("order by");
        //sql语句还存在并且hasDistinct(sql)不为false，进入if
        if (orderByIndex > 0 && !hasDistinct(sql)) {
            rownumber.append(sql.substring(orderByIndex));
        }

        rownumber.append(") as rownumber_,");
        //测试
        //System.out.println(rownumber.toString());
        return rownumber.toString();
    }

    private static boolean hasDistinct(String sql) {
    	//返回小写的sql包含select distinct（明显的） 的语句
        return sql.toLowerCase().contains("select distinct");
    }

    @Override//重写
    //获取限制字符串的方法（将入参改成Integer类）
    //offset: 分页开始纪录条数   limit:显示页数
    public String getLimitString(String sql, int offset, int limit) {
        return getLimitString(sql, offset, Integer.toString(offset), Integer.toString(limit));
    }

    /**
     * 将sql变成分页sql语句,提供将offset及limit使用占位符号(placeholder)替换.
     * <pre>
     * 如mysql
     * dialect.getLimitString("select * from user", 12, ":offset",0,":limit") 将返回
     * select * from user limit :offset,:limit
     * </pre>
     *
     * @param sql               实际SQL语句
     * @param offset            分页开始纪录条数
     * @param offsetPlaceholder 分页开始纪录条数－占位符号
     * @param limitPlaceholder  分页纪录条数占位符号
     * @return 包含占位符的分页sql
     */
    public String getLimitString(String sql, int offset, String offsetPlaceholder, String limitPlaceholder) {
        /*
         * select：查询，toLowerCase()：使用默认语言环境的规则将此 String 中的所有字符都转换为小写。
         * indexOf(String str) :返回指定子字符串在此字符串中第一次出现处的索引。
         */
    	int startOfSelect = sql.toLowerCase().indexOf("select");
    	// 分页查询
        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100)
                .append(sql.substring(0, startOfSelect)) //add the comment：添加评论
                .append("select * from ( select ") //nest the main query in an outer select：在外部选择主查询的嵌套
                .append(getRowNumber(sql)); //add the rownumber bit into the outer query select list
        		//添加行号一点到外查询选择列表
        if (hasDistinct(sql)) {
            pagingSelect.append(" row_.* from ( ") //add another (inner) nested select
            		//添加另一个(内部)嵌套的查询
                    .append(sql.substring(startOfSelect)) //add the main query
                    //添加主查询
                    .append(" ) as row_"); //close off the inner nested select
                    //关闭内嵌套的查询
        } else {
            pagingSelect.append(sql.substring(startOfSelect + 6)); //add the main query
            //添加主查询
        }

        pagingSelect.append(" ) as temp_ where rownumber_ ");

        //add the restriction to the outer select
        //添加限制外查询,offset:分页开始纪录条数
        if (offset > 0) {
//			int end = offset + limit;
            String endString = offsetPlaceholder + "+" + limitPlaceholder;
            pagingSelect.append("between ").append(offsetPlaceholder)
                    .append("+1 and ").append(endString);
        } else {
            pagingSelect.append("<= ").append(limitPlaceholder);
        }
        //从Stringbuilder转回String
        return pagingSelect.toString();
    }
}
