package com.thinkgem.jeesite.common.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.UriUtils;

import com.thinkgem.jeesite.common.config.Global;

/**
 * 查看CK上传的图片
 * @author ThinkGem
 * @version 2014-06-25
 */
//用户文件下载小服务程序继承超文本传输协议小服务程序
public class UserfilesDownloadServlet extends HttpServlet {
	//序列化
	private static final long serialVersionUID = 1L;
	//调用记录器
	private Logger logger = LoggerFactory.getLogger(getClass());
	//构建文件输出流方法，参数为(请求，响应)，抛出servlet异常，io流异常
	public void fileOutputStream(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		//文件路径，通过请求方法获得uri路径
		String filepath = req.getRequestURI();
		//全局配置类.用户文件基本URL,返回指定子字符串在此字符串中第一次出现处的索引。
		int index = filepath.indexOf(Global.USERFILES_BASE_URL);
		//如果返回索引大于0，说明文件路径存在
		if(index >= 0) {
			//subString(int beginIndex): 返回一个新的字符串String，它是此字符串的一个子字符串,重新赋值于filepath。
			filepath = filepath.substring(index + Global.USERFILES_BASE_URL.length());
		}
		try {
			//decode:译码
			filepath = UriUtils.decode(filepath, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			//捕捉不支持的编码异常，记录到日志
			logger.error(String.format("解释文件路径失败，URL地址为%s", filepath), e1);
		}
		//getUserfilesBaseDir():获取用户上传文件的根目录+用户上传文件的基本url路径+文件路径
		//File(String pathname):通过将给定路径名字符串转换为抽象路径名来创建一个新 File实例。
		File file = new File(Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL + filepath);
		try {
			//文件复制工具
			FileCopyUtils.copy(new FileInputStream(file), resp.getOutputStream());
			resp.setHeader("Content-Type", "application/octet-stream");
			return;
			//捕捉未找到文件异常
		} catch (FileNotFoundException e) {
			//将异常存储到exception，显示到页面
			req.setAttribute("exception", new FileNotFoundException("请求的文件不存在"));
			//转发请求
			req.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(req, resp);
		}
	}
	//重写doGet方法
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		fileOutputStream(req, resp);
	}
	//重写doPost方法
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		fileOutputStream(req, resp);
	}
}
