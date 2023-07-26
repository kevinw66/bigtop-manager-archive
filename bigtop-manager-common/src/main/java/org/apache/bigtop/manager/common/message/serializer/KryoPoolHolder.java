package org.apache.bigtop.manager.common.message.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.Pool;
import org.apache.bigtop.manager.common.message.type.BaseMessage;
import org.apache.bigtop.manager.common.message.type.CommandMessage;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecific;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

public class KryoPoolHolder {

    private static final Pool<Kryo> KRYO_POOL = new Pool<Kryo>(true, false, 16) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setCopyReferences(true);

            // message types
            kryo.register(BaseMessage.class);
            kryo.register(HeartbeatMessage.class);
            kryo.register(CommandMessage.class);
            kryo.register(ResultMessage.class);

            // message pojo
            kryo.register(HostInfo.class);
            kryo.register(OSSpecific.class);

            // java classes
            kryo.register(BigDecimal.class);
            kryo.register(Timestamp.class);
            kryo.register(ArrayList.class);
            kryo.register(Integer.class);
            kryo.register(String.class);

            return kryo;
        }
    };

    public static Kryo obtainKryo() {
        return KRYO_POOL.obtain();
    }

    public static void freeKryo(Kryo kryo) {
        KRYO_POOL.free(kryo);
    }
}
