package com.zwhd.appbase.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.Message;

import com.zwhd.appbase.util.Logger;

/**
 * 网络请求
 *
 * @author YI
 */
public class BaseHttpRequest extends Thread {
    /**
     * HTTP请求地址
     */
    String requestUrl;
    /**
     * HTTP请求客户端
     */
    HttpClient httpClient = new DefaultHttpClient();
    /**
     * HTTP POST请求
     */
    HttpPost httpPost;
    /**
     * HTTP 请求回调
     */
    BaseHttpHandler handler;
    /**
     * 是否继续执行回调操作
     */
    boolean requesting;
    /**
     * HTTP 请求参数
     */
    Object params;
    /**
     * 同时请求同一个地址的第几次请求
     */
    int which;
    /**
     * HTTP请求的附带参数
     */
    Map<String, Object> map;
    /**
     * 是否展现等待进度
     */
    boolean isShowLoad;
    /**
     * HTTP 请求标记
     */
    String requestTag;

    public boolean isRequesting() {
        return requesting;
    }

    public void setRequesting(boolean requesting) {
        this.requesting = requesting;
    }

    public boolean isShowLoad() {
        return isShowLoad;
    }

    private BaseHttpRequest() {
    }

    /**
     * @param requestUrl 请求地址
     * @param params     请求参数   支持(protobuf,Map<String,String>,JSONObject)
     * @param handler    请求回调函数
     * @param which      第几次请求
     * @param map        请求的附带参数
     * @param isShowLoad 是否展现等待进度
     */
    public BaseHttpRequest(String requestUrl, Object params, BaseHttpHandler handler, int which, Map<String, Object> map, String requestTag, boolean isShowLoad) throws Exception {
        this.requestUrl = requestUrl;
        this.params = params;
        this.handler = handler;
        this.which = which;
        this.map = map;
        this.requestTag = requestTag;
        this.isShowLoad = isShowLoad;
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30 * 1000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30 * 1000);
        httpPost = new HttpPost(requestUrl);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        if (params instanceof com.google.protobuf.Message) {//protbuf
            httpPost.setEntity(new ByteArrayEntity(((com.google.protobuf.Message) params).toByteArray()));
        } else if (params instanceof Map) {//Map<String,String>
            List<BasicNameValuePair> list = new ArrayList<>();
            Set<Map.Entry<String, String>> set = ((Map) params).entrySet();
            for (Map.Entry<String, String> entry : set) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        } else if (params instanceof JSONObject) {//JSONObject
            httpPost.setEntity(new StringEntity(((JSONObject) params).toString()));
        }
        setRequesting(true);
    }

    private void excute() throws Exception {
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
            if (handler != null && isRequesting())
                handler.sendEmptyMessage(HttpResponseMsgType.RESPONSE_ERR);
        }
        Logger.log("request url :" + requestUrl);
        Logger.log("request params :" + params);
        if (httpResponse != null && httpResponse.getStatusLine() != null)
            Logger.log("response code :" + httpResponse.getStatusLine().getStatusCode());
        if (httpResponse != null && httpResponse.getStatusLine() != null
                && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                byte[] b = EntityUtils.toByteArray(httpEntity);
                Logger.log(new String(b));
                if (handler != null && isRequesting()) {
                    try {
                        Message message = new Message();
                        message.what = HttpResponseMsgType.RESPONSE_SUCCESS;
                        HttpResponseModel model = new HttpResponseModel(requestUrl, b, which, map);
                        message.obj = model;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(HttpResponseMsgType.RESPONSE_ERR);
                    }
                }
            } else {
                if (handler != null && isRequesting())
                    handler.sendEmptyMessage(HttpResponseMsgType.RESPONSE_ERR);
            }
        } else {
            if (handler != null && isRequesting())
                handler.sendEmptyMessage(HttpResponseMsgType.RESPONSE_ERR);
        }
        httpPost.abort();
    }

    @Override
    public void run() {
        super.run();
        BaseHttpConnectPool.handler.sendEmptyMessage(BaseHttpConnectPool.OPEN_SUG);
        try {
            excute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BaseHttpConnectPool.httpRequests.remove(requestTag);
        BaseHttpConnectPool.handler.sendEmptyMessage(BaseHttpConnectPool.CLOSE_SUG);
    }
}
