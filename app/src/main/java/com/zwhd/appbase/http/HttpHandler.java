package com.zwhd.appbase.http;


public interface HttpHandler {
	/**
	 * 网络请求逻辑处理成功
	 * 
	 * @param message
	 *            返回的Message序列化对象
	 * */
	public void success(HttpResponseModel message) throws Exception;

	/**
	 * 网络请求逻辑处理失败
	 * 
	 * @param message
	 *            返回的Rsp序列化对象
	 * */
	public void error(HttpResponseModel message) throws Exception;

	/**
	 * 网络请求数据返回错误
	 * */
	public void httpErr() throws Exception;

	/**
	 * 网络请求数据返回成功
	 * 
	 * @param message
	 *            返回的Message序列化对象
	 * */
	public void httpSuccess(HttpResponseModel message) throws Exception;

}
