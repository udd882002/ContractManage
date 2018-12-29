package com.seven.contract.manage.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException; 
 
/**
 * 提供一些Hash算法的工具类 
 */
public final class Hash { 
 
 private Hash() {} 
 
 /**
  * 计算字符串的MD5摘要 
  * @param str 要计算的字符串 
  * @return MD5摘要 
  */ 
 public final static byte[] md5(String str) { 
  return digest(str, "MD5"); 
 } 
 
 /**
  * 计算字符串的SHA-1摘要 
  * @param str 要计算的字符串 
  * @return SHA-1摘要 
  */ 
 public final static byte[] sha1(String str) { 
  return digest(str, "SHA-1"); 
 } 
 
 /**
  * 计算字符串的SHA-256摘要 
  * @param str 要计算的字符串 
  * @return SHA-256摘要 
  */ 
 public final static byte[] sha256(String str) { 
  return digest(str, "SHA-256"); 
 } 
 
 /**
  * 计算字符串的SHA-512摘要 
  * @param str 要计算的字符串 
  * @return SHA-512摘要 
  */ 
 public final static byte[] sha512(String str) { 
  return digest(str, "SHA-512"); 
 } 
 
 /**
  * 计算字节数组的MD5摘要 
  * @param bytes 要计算的字节数组 
  * @return MD5摘要 
  */ 
 public final static byte[] md5(byte[] bytes) { 
  return digest(bytes, "MD5"); 
 } 
  
 /**
  * 计算字节数组的SHA-1摘要 
  * @param bytes 要计算的字节数组 
  * @return SHA-1摘要 
  */ 
 public final static byte[] sha1(byte[] bytes) { 
  return digest(bytes, "SHA-1"); 
 } 
  
 /**
  * 计算字节数组的SHA-256摘要 
  * @param bytes 要计算的字节数组 
  * @return SHA-256摘要 
  */ 
 public final static byte[] sha256(byte[] bytes) { 
  return digest(bytes, "SHA-256"); 
 } 
 
 /**
  * 计算字节数组的SHA-512摘要 
  * @param bytes 要计算的字节数组 
  * @return SHA-512摘要 
  */ 
 public final static byte[] sha512(byte[] bytes) { 
  return digest(bytes, "SHA-512"); 
 } 
 
 /**
  * 计算文件的MD5摘要 
  * @param file 要计算的文件 
  * @return 文件的MD5摘要 
  * @throws IOException IO异常 如文件不存在 
  */ 
 public final static byte[] md5(File file) throws IOException { 
  return digest(new FileInputStream(file), file.length(), "MD5"); 
 } 
  
 /**
  * 计算文件的SHA-1摘要 
  * @param file 要计算的文件 
  * @return 文件的SHA-1摘要 
  * @throws IOException IO异常 如文件不存在 
  */ 
 public final static byte[] sha1(File file) throws IOException { 
  return digest(new FileInputStream(file), file.length(), "SHA-1"); 
 } 
  
 /**
  * 计算文件的SHA-256摘要 
  * @param file 要计算的文件 
  * @return 文件的SHA-256摘要 
  * @throws IOException IO异常 如文件不存在 
  */ 
 public final static byte[] sha256(File file) throws IOException { 
  return digest(new FileInputStream(file), file.length(), "SHA-256"); 
 } 
 
 /**
  * 计算文件的SHA-512摘要 
  * @param file 要计算的文件 
  * @return 文件的SHA-512摘要 
  * @throws IOException IO异常 如文件不存在 
  */ 
 public final static byte[] sha512(File file) throws IOException { 
  return digest(new FileInputStream(file), file.length(), "SHA-512"); 
 }

 public final static String toString(byte[] byteBuffer) {
  // 将 byte 转化为 string
  StringBuffer strHexString = new StringBuffer();
  for (int i = 0; i < byteBuffer.length; i++)
  {
   String hex = Integer.toHexString(0xff & byteBuffer[i]);
   if (hex.length() == 1)
   {
    strHexString.append('0');
   }
   strHexString.append(hex);
  }
  return strHexString.toString();
 }
 
 private final static byte[] digest(String str, String algorithm) { 
  return digest(str.getBytes(), algorithm); 
 } 
 
 private final static byte[] digest(byte[] bytes, String algorithm) { 
  try { 
   MessageDigest md = MessageDigest.getInstance(algorithm); 
   md.update(bytes); 
   return md.digest(); 
  } catch (NoSuchAlgorithmException e) { 
   return null; 
  } 
 } 
 
 private final static byte[] digest(InputStream in, long length, String algorithm) throws IOException { 
  try { 
   MessageDigest md = MessageDigest.getInstance(algorithm); 
   byte[] buffer = new byte[1024]; 
   int l; 
   while ((l = in.read(buffer)) > 0) { 
    md.update(buffer, 0, l); 
   } 
   return md.digest(); 
  } catch (NoSuchAlgorithmException e) { 
   return null; 
  } finally { 
   in.close(); 
  } 
 }

}

