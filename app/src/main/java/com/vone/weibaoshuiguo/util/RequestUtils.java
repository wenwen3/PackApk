package com.vone.weibaoshuiguo.util;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.Map;

public class RequestUtils  {

    public static RequestUtils instance;

    public static RequestUtils getInstance(){
        if(instance == null){
            instance = new RequestUtils();
        }

        return instance;
    }

    public void request(Map<String,String> requestParamsMap, String requestUrl, RequestCallBack<String> requestCallBack){
        HttpUtils httpUtils = new HttpUtils();
        RequestParams requestParams = new RequestParams();
        if(requestParamsMap != null ){
            for (Map.Entry<String,String> mEntries: requestParamsMap.entrySet()) {
                requestParams.addBodyParameter(mEntries.getKey(),mEntries.getValue());
            }
        }
        httpUtils.send(HttpRequest.HttpMethod.POST,requestUrl,requestParams,requestCallBack);
    }
}
