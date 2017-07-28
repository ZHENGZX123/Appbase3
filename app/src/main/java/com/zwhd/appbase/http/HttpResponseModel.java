package com.zwhd.appbase.http;

import java.io.Serializable;
import java.util.Map;

/**
 * 网络返回的对象
 * 
 * @author Yi
 * */
public class HttpResponseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 请求的地址
	 * */
	private String url;
	/**
	 * 返回的二进制数据
	 * */
	private byte[] response;
	/**
	 * 第几次请求
	 * */
	private int which;
	/**
	 * 请求附带的参数（供回调函数使用）
	 * */
	private Map<String, Object> map;

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public HttpResponseModel(String url, byte[] response) {
		super();
		this.url = url;
		this.response = response;
	}

	public HttpResponseModel(String url, byte[] response, int which) {
		super();
		this.url = url;
		this.response = response;
		this.which = which;
	}

	public HttpResponseModel(String url, byte[] response, int which, Map<String, Object> map) {
		super();
		this.url = url;
		this.response = response;
		this.which = which;
		this.map = map;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte[] getResponse() {
		return response;
	}

	public void setResponse(byte[] response) {
		this.response = response;
	}

	public int getWhich() {
		return which;
	}

	public void setWhich(int which) {
		this.which = which;
	}

}
