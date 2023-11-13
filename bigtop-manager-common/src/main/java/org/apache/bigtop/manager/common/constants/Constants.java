package org.apache.bigtop.manager.common.constants;

public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Construct Constants");
    }

    /**
     * stack cache dir
     */
    public static final String STACK_CACHE_DIR = "/opt/bigtop-manager-agent/cache";

    /**
     * host key for all hosts
     */
    public static final String ALL_HOST_KEY = "all";

    /**
     * registry session timeout
     */
    public static final long REGISTRY_SESSION_TIMEOUT = 5 * 1000L;

    /**
     * kryo buffer size
     */
    public static final int KRYO_BUFFER_SIZE = 65536;

    /**
     * websocket binary message size limit
     */
    public static final int WS_BINARY_MESSAGE_SIZE_LIMIT = 65536;

    /**
     * timeout for command message to wait for response
     */
    public static final long COMMAND_MESSAGE_RESPONSE_TIMEOUT = 15 * 60 * 1000L;
}
