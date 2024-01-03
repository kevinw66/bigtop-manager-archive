package org.apache.bigtop.manager.server.utils;

import org.slf4j.MDC;

import static org.apache.bigtop.manager.common.constants.Constants.TRACE_ID_KEY;

public class LogUtils {

    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }
}
