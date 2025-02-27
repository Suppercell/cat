package com.dianping.cat.alarm.spi.dingtalk;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * Created by dustin on 2017/3/17.
 */
public class SendResult {
    private boolean isSuccess;
    private Integer errorCode;
    private String  errorMsg;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String toString() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("errorCode", errorCode);
        items.put("errorMsg", errorMsg);
        items.put("isSuccess", isSuccess);
        return JSON.toJSONString(items);
    }
}
