package com.seven.contract.manage.utils.http;

import org.apache.http.Header;

import java.io.Serializable;

/**
 * Description: 封装httpClient响应结果
 * 
 * @author yankaiming
 * @date Created on 2018年4月19日
 */
public class HttpClientResult implements Serializable {

	private static final long serialVersionUID = 2168152194164783950L;

	/**
	 * 响应状态码
	 */
	private int code;

	/**
	 * 响应数据
	 */
	private String content;

	/**
	 * 响应header
	 */
	private Header[] header;

	public HttpClientResult() {
	}

	public HttpClientResult(int code) {
		this.code = code;
	}

	public HttpClientResult(String content) {
		this.content = content;
	}

	public HttpClientResult(int code, String content, Header[] header) {
		this.code = code;
		this.content = content;
		this.header = header;
	}

	public Header[] getHeader() { return header; }

	public void setHeader(Header[] header) { this.header = header; }

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}



	@Override
	public String toString() {
		return "HttpClientResult [code=" + code + ", content=" + content + "]";
	}

}
