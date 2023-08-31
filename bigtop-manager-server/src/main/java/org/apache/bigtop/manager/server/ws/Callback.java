package org.apache.bigtop.manager.server.ws;

import org.apache.bigtop.manager.common.message.type.ResultMessage;

public interface Callback {

   void call(ResultMessage resultMessage);
}
