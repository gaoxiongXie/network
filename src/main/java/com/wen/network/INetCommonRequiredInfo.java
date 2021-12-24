package com.wen.network;

import java.util.Map;

public interface INetCommonRequiredInfo {
    //是否是 debug模式
    boolean isDebug();

    //设置公共header参数，如果有的话
    Map<String, Object> getCommonHeader();


    //设置公共post参数
    Map<String, String> getCommonParams();

}
