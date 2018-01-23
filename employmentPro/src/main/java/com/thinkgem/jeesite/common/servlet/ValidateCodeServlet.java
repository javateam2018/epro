/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 生成随机验证码
 * 
 * @author ThinkGem
 * @version 2014-7-27
 */
@SuppressWarnings("serial") // 抑制警告(连续的)
// 验证码servlet
public class ValidateCodeServlet extends HttpServlet {
	// 声明验证码字符串
	public static final String VALIDATE_CODE = "validateCode";
	// 宽
	private int w = 70;
	// 高
	private int h = 26;

	// 无参构造器
	public ValidateCodeServlet() {
		super();
	}

	/**
	 * 调用销毁方法
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * 构建验证方法，参数（请求，验证码），返回一个boolean类型
	 * 
	 * @param request
	 *            请求
	 * @param validateCode
	 *            验证码
	 * @return
	 */
	public static boolean validate(HttpServletRequest request, String validateCode) {
		// 获得页面传来的验证码赋值到code字符串
		String code = (String) request.getSession().getAttribute(VALIDATE_CODE);
		// 验证是否和显示的验证码一致，返回验证结果
		return validateCode.toUpperCase().equals(code);
	}

	// doGet方法
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String validateCode = request.getParameter(VALIDATE_CODE); // AJAX验证，成功返回true
		if (StringUtils.isNotBlank(validateCode)) {
			// 验证码不为空，则判断
			response.getOutputStream().print(validate(request, validateCode) ? "true" : "false");
		} else {
			// 验证码为空，调用doPost方法
			this.doPost(request, response);
		}
	}

	// doPost方法
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		createImage(request, response);
	}

	/**
	 * 创建验证码图片方法
	 * 
	 * @param request
	 *            请求
	 * @param response
	 *            响应
	 * @throws IOException
	 *             抛出io异常
	 */
	private void createImage(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		/*
		 * 得到参数高，宽，都为数字时，则使用设置高宽，否则使用默认值
		 */
		String width = request.getParameter("width");
		String height = request.getParameter("height");
		if (StringUtils.isNumeric(width) && StringUtils.isNumeric(height)) {
			w = NumberUtils.toInt(width);
			h = NumberUtils.toInt(height);
		}
		// 缓冲图片
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		/*
		 * Graphics:图形Graphics 类是所有图形上下文的抽象基类，允许应用程序在组件（已经在各种设备上实现）以及闭屏图像上进行绘制。
		 */
		Graphics g = image.getGraphics();

		/*
		 * 生成背景
		 */
		createBackground(g);

		/*
		 * 生成字符
		 */
		String s = createCharacter(g);
		// 显示验证码
		request.getSession().setAttribute(VALIDATE_CODE, s);
		// dispse():释放此图形的上下文以及它使用的所有系统资源。
		g.dispose();
		// 输出流
		OutputStream out = response.getOutputStream();
		// 调用输入流写入
		ImageIO.write(image, "JPEG", out);
		// 关闭输出流
		out.close();

	}

	/**
	 * 创建获取边沿颜色的方法
	 * 
	 * @param fc
	 * @param bc
	 * @return 颜色类Color
	 */
	private Color getRandColor(int fc, int bc) {
		int f = fc;
		int b = bc;
		// 随机数
		Random random = new Random();
		if (f > 255) {
			f = 255;
		}
		if (b > 255) {
			b = 255;
		}
		return new Color(f + random.nextInt(b - f), f + random.nextInt(b - f), f + random.nextInt(b - f));
	}

	/**
	 * 创建背景
	 * 
	 * @param g
	 *            图形类
	 */
	private void createBackground(Graphics g) {
		// 填充背景
		g.setColor(getRandColor(220, 250));
	
		// fillRect(int x, int y, int width, int height) 填充指定的矩形。
		g.fillRect(0, 0, w, h);
		// 加入干扰线条
		for (int i = 0; i < 8; i++) {
			//setColor(Color c):将此图形上下文的当前颜色设置为指定颜色。
			g.setColor(getRandColor(40, 150));
			//随机数
			Random random = new Random();
			//nextInt(int n):返回一个伪随机数，在 0（包括）和指定值（不包括）之间均匀分布的 int值。
			int x = random.nextInt(w);
			int y = random.nextInt(h);
			int x1 = random.nextInt(w);
			int y1 = random.nextInt(h);
			/*
			 * drawLine(int x1, int y1, int x2, int y2):
			 * 在此图形上下文的坐标系中，使用当前颜色在点 (x1, y1)
			 * 和 (x2,y2)之间画一条线。
			 */
			g.drawLine(x, y, x1, y1);
		}
	}

	/**
	 * 创造验证码
	 * @param g 图形g
	 * @return 验证码字符串
	 */
	private String createCharacter(Graphics g) {
		//验证码序列（char类型数组）
		char[] codeSeq = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U',
				'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9' };
		//字体类型（String类型数组）
		String[] fontTypes = { "Arial", "Arial Black", "AvantGarde Bk BT", "Calibri" };
		//随机数
		Random random = new Random();
		//可变的字符序列
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 4; i++) {//生成4个字符
			String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);// random.nextInt(10));
			g.setColor(new Color(50 + random.nextInt(100), 50 + random.nextInt(100), 50 + random.nextInt(100)));
			g.setFont(new Font(fontTypes[random.nextInt(fontTypes.length)], Font.BOLD, 26));
			g.drawString(r, 15 * i + 5, 19 + random.nextInt(8));
			// g.drawString(r, i*w/4, h-5);
			//append(String str):将指定的字符串追加到此字符序列。
			s.append(r);
		}
		return s.toString();
	}

}
