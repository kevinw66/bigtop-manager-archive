package org.apache.bigtop.manager.common.message.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.Pool;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.enums.MessageType;
import org.apache.bigtop.manager.common.message.type.*;
import org.apache.bigtop.manager.common.message.type.pojo.*;
import org.apache.bigtop.manager.common.message.type.pojo.OSSpecificInfo;
import org.apache.bigtop.manager.common.message.type.pojo.RepoInfo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class KryoPoolHolder {

    private static final Pool<Kryo> KRYO_POOL = new Pool<Kryo>(true, false, 16) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setCopyReferences(true);

            // message types
            kryo.register(BaseMessage.class);
            kryo.register(BaseCommandMessage.class);
            kryo.register(HeartbeatMessage.class);
            kryo.register(CommandMessage.class);
            kryo.register(ResultMessage.class);
            kryo.register(HostCacheMessage.class);
            kryo.register(HostCheckMessage.class);

            // message pojo
            kryo.register(HostInfo.class);
            kryo.register(OSSpecificInfo.class);
            kryo.register(BasicInfo.class);
            kryo.register(ClusterInfo.class);
            kryo.register(RepoInfo.class);
            kryo.register(HostCheckType.class);
            kryo.register(HostCheckType[].class);
            kryo.register(MessageType.class);
            kryo.register(Command.class);

            // java classes
            kryo.register(BigDecimal.class);
            kryo.register(Timestamp.class);
            kryo.register(ArrayList.class);
            kryo.register(Integer.class);
            kryo.register(String.class);
            kryo.register(HashMap.class);
            kryo.register(HashSet.class);

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
