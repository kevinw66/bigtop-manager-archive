package org.apache.bigtop.manager.common.message.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.Pool;
import org.apache.bigtop.manager.common.message.type.BaseMessage;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class KryoPoolHolder {

    private static final Pool<Kryo> KRYO_POOL = new Pool<Kryo>(true, false, 16) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setCopyReferences(true);

            kryo.setReferences(false);
            kryo.setRegistrationRequired(false);
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            // message types
            kryo.register(BaseMessage.class);
            kryo.register(HeartbeatMessage.class);

            // message pojo
            kryo.register(HostInfo.class);

            // java classes
            kryo.register(BigDecimal.class);
            kryo.register(Timestamp.class);

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
