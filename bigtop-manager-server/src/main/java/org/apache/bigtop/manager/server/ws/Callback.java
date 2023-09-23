package org.apache.bigtop.manager.server.ws;

import org.apache.bigtop.manager.common.message.type.ResultMessage;

public interface Callback {

    /**
     * 根据回调消息
     * </p>
     * 1、处理状态更改并持久化
     * </p>
     * 2、计算执行进度返回前端进行展示
     * @param resultMessage agent返回的消息
     */
    void call(ResultMessage resultMessage);
}
