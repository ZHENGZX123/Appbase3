package com.zwhd.appbase.http;

import android.os.Handler;
import android.os.Message;

/***
 * 网络数据请求回调函数
 */
public abstract class BaseHttpHandler extends Handler {
    public BaseHttpHandler(HttpHandler httpHandler) {
        super();
        this.httpHandler = httpHandler;
    }

    private HttpHandler httpHandler;

    public HttpHandler getHttpHandler() {
        return httpHandler;
    }

    public void setHttpHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg == null)
            return;
        if (httpHandler == null)
            return;
        int what = msg.what;

        switch (what) {
            case HttpResponseMsgType.RESPONSE_ERR:
                try {
                    httpHandler.httpErr();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case HttpResponseMsgType.RESPONSE_SUCCESS:
                try {
                    httpHandler.httpSuccess((HttpResponseModel) msg.obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }

    }

}
