package org.apache.bigtop.manager.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtils {

    private static InetAddress LOCAL_ADDRESS = null;
    private static volatile String HOST_ADDRESS;
    private static volatile String HOST_NAME;

    public static String getHost() {
        if (HOST_ADDRESS != null) {
            return HOST_ADDRESS;
        }
        try {
            InetAddress address = getLocalAddress();
            HOST_ADDRESS = address.getHostAddress();
            return HOST_ADDRESS;
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    public static String getHostname() {
        if (HOST_NAME != null) {
            return HOST_NAME;
        }
        try {
            InetAddress address = getLocalAddress();
            HOST_NAME = address.getHostName();
            return HOST_NAME;
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

    private static InetAddress getLocalAddress() throws UnknownHostException {
        if (null != LOCAL_ADDRESS) {
            return LOCAL_ADDRESS;
        }
        LOCAL_ADDRESS = InetAddress.getLocalHost();
        return LOCAL_ADDRESS;
    }

}
