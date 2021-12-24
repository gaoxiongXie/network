package com.wen.network.pojo;

import java.io.Serializable;

/**
 * 服务器返回的数据，格式固定
 * @param <T>
 */
public class BaseResponse<T> implements Serializable {
    public int errorCode;
    public String errorMessage;
    public T data;
}
