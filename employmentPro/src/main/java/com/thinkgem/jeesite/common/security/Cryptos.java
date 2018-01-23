/**
 * Copyright (c) 2005-2012 springside.org.cn
 */
package com.thinkgem.jeesite.common.security;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.thinkgem.jeesite.common.utils.Encodes;
import com.thinkgem.jeesite.common.utils.Exceptions;

/**
 * 支持HMAC-SHA1消息签名 及 DES/AES对称加密的工具类.
 * 
 * 支持Hex与Base64两种编码方式.
 * 
 * @author calvin
 */
//加密类
public class Cryptos {
	//AES:高级加密标准
	private static final String AES = "AES";
	//PKCS:公钥加密标准
	private static final String AES_CBC = "AES/CBC/PKCS5Padding";
	private static final String HMACSHA1 = "HmacSHA1";
	//默认AUR编码
	private static final String DEFAULT_URL_ENCODING = "UTF-8";
	private static final int DEFAULT_HMACSHA1_KEYSIZE = 160; //RFC2401
	private static final int DEFAULT_AES_KEYSIZE = 128;
	
	private static final int DEFAULT_IVSIZE = 16;
	//默认键
	private static final byte[] DEFAULT_KEY = new byte[]{-97,88,-94,9,70,-76,126,25,0,3,-20,113,108,28,69,125}; 
	//安全随机数
	private static SecureRandom random = new SecureRandom();

	//-- HMAC-SHA1 funciton --//
	/**
	 * 使用HMAC-SHA1进行消息签名, 返回字节数组,长度为20字节.
	 * 
	 * @param input 原始输入字符数组
	 * @param key HMAC-SHA1密钥
	 */
	public static byte[] hmacSha1(byte[] input, byte[] key) {
		try {
			//秘钥类
			SecretKey secretKey = new SecretKeySpec(key, HMACSHA1);
			//返回实现指定 MAC算法的 Mac对象。此类提供“消息验证码”（Message Authentication Code，MAC）算法的功能。
			Mac mac = Mac.getInstance(HMACSHA1);
            //用给定的密钥初始化此 Mac 对象。
			mac.init(secretKey);
			return mac.doFinal(input);
		} catch (GeneralSecurityException e) {
			//捕捉一般安全性异常
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 校验HMAC-SHA1签名是否正确.
	 * 
	 * @param expected 已存在的签名
	 * @param input 原始输入字符串
	 * @param key 密钥
	 */
	public static boolean isMacValid(byte[] expected, byte[] input, byte[] key) {
		byte[] actual = hmacSha1(input, key);
		return Arrays.equals(expected, actual);
	}

	/**
	 * 生成HMAC-SHA1密钥,返回字节数组,长度为160位(20字节).
	 * HMAC-SHA1算法对密钥无特殊要求, RFC2401建议最少长度为160位(20字节).
	 */
	public static byte[] generateHmacSha1Key() {
		try {
			//KeyGenerator此类提供（对称）密钥生成器的功能，密钥生成器是使用此类的某个 getInstance 类方法构造的。
			KeyGenerator keyGenerator = KeyGenerator.getInstance(HMACSHA1);
			keyGenerator.init(DEFAULT_HMACSHA1_KEYSIZE);
			//SecretKey:秘密（对称）密钥接口
			SecretKey secretKey = keyGenerator.generateKey();
			//获得编码
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			//捕捉一般安全性异常，抛出未经核对的异常
			throw Exceptions.unchecked(e);
		}
	}

	//-- AES funciton：高级加密标准函数 --//

	/**
	 * 使用AES加密原始字符串.
	 * 
	 * @param input 原始输入字符数组
	 */
	//aesEncrypt:高级加密标准加密，返回一个字符串
	public static String aesEncrypt(String input) {
		try {
			//Encodes：把什么译成编码
			return Encodes.encodeHex(aesEncrypt(input.getBytes(DEFAULT_URL_ENCODING), DEFAULT_KEY));
		} catch (UnsupportedEncodingException e) {
			//捕捉不支持的编码异常，返回一个空字符串
			return "";
		}
	}
	
	/**
	 * 使用AES加密原始字符串.
	 * 
	 * @param input 原始输入字符数组
	 * @param key 符合AES要求的密钥
	 */
	//重载高级加密标准加密,返回一个字符串
	public static String aesEncrypt(String input, String key) {
		try {
			//Encodes：把什么译成编码
			return Encodes.encodeHex(aesEncrypt(input.getBytes(DEFAULT_URL_ENCODING), Encodes.decodeHex(key)));
		} catch (UnsupportedEncodingException e) {
			//捕捉不支持的编码异常，返回一个空字符串
			return "";
		}
	}
	
	/**
	 * 使用AES加密原始字符串.
	 * 
	 * @param input 原始输入字符数组
	 * @param key 符合AES要求的密钥
	 */
	public static byte[] aesEncrypt(byte[] input, byte[] key) {
		return aes(input, key, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 使用AES加密原始字符串.
	 * 
	 * @param input 原始输入字符数组
	 * @param key 符合AES要求的密钥
	 * @param iv 初始向量
	 */
	public static byte[] aesEncrypt(byte[] input, byte[] key, byte[] iv) {
		return aes(input, key, iv, Cipher.ENCRYPT_MODE);
	}

	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * 
	 * @param input Hex编码的加密字符串
	 */
	public static String aesDecrypt(String input) {
		try {
			return new String(aesDecrypt(Encodes.decodeHex(input), DEFAULT_KEY), DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * 
	 * @param input Hex编码的加密字符串
	 * @param key 符合AES要求的密钥
	 */
	public static String aesDecrypt(String input, String key) {
		try {
			return new String(aesDecrypt(Encodes.decodeHex(input), Encodes.decodeHex(key)), DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * 
	 * @param input Hex编码的加密字符串
	 * @param key 符合AES要求的密钥
	 */
	//AES解密类，返回字节型数组(原始字符串)
	public static byte[] aesDecrypt(byte[] input, byte[] key) {
		return aes(input, key, Cipher.DECRYPT_MODE);
	}

	/**
	 * 使用AES解密字符串, 返回原始字符串.
	 * 
	 * @param input Hex编码的加密字符串
	 * @param key 符合AES要求的密钥
	 * @param iv 初始向量
	 */
	public static byte[] aesDecrypt(byte[] input, byte[] key, byte[] iv) {
		//Cipher.DECRYPT_MODE:密码.解密模式
		return aes(input, key, iv, Cipher.DECRYPT_MODE);
	}

	/**
	 * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * 
	 * @param input 原始字节数组
	 * @param key 符合AES要求的密钥
	 * @param mode Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 */
	private static byte[] aes(byte[] input, byte[] key, int mode) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, AES);
			Cipher cipher = Cipher.getInstance(AES);
			cipher.init(mode, secretKey);
			return cipher.doFinal(input);
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * 
	 * @param input 原始字节数组
	 * @param key 符合AES要求的密钥
	 * @param iv 初始向量
	 * @param mode Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 */
	private static byte[] aes(byte[] input, byte[] key, byte[] iv, int mode) {
		try {
			SecretKey secretKey = new SecretKeySpec(key, AES);
			//四参数规格
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			//cipher:密码
			Cipher cipher = Cipher.getInstance(AES_CBC);
			cipher.init(mode, secretKey, ivSpec);
			return cipher.doFinal(input);
		} catch (GeneralSecurityException e) {
			//捕捉一般安全性异常抛出
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 生成AES密钥,返回字符串.
	 */
	//生成AES秘钥字符串方法
	public static String generateAesKeyString() {
		return Encodes.encodeHex(generateAesKey(DEFAULT_AES_KEYSIZE));
	}
	
	/**
	 * 生成AES密钥,返回字节数组, 默认长度为128位(16字节).
	 */
	public static byte[] generateAesKey() {
		return generateAesKey(DEFAULT_AES_KEYSIZE);
	}

	/**
	 * 生成AES密钥,可选长度为128,192,256位.
	 */
	public static byte[] generateAesKey(int keysize) {
		try {
			//秘钥生成器类
			KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
			//初始化
			keyGenerator.init(keysize);
			//秘钥
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			//捕捉一般安全性异常抛出
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 生成随机向量,默认大小为cipher.getBlockSize(), 16字节.
	 */
	public static byte[] generateIV() {
		byte[] bytes = new byte[DEFAULT_IVSIZE];
		random.nextBytes(bytes);
		return bytes;
	}
}