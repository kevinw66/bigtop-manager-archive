package org.apache.bigtop.manager.common.message.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.Pool;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.common.message.entity.command.CommandMessageType;
import org.apache.bigtop.manager.common.message.entity.*;
import org.apache.bigtop.manager.common.message.entity.command.CommandRequestMessage;
import org.apache.bigtop.manager.common.message.entity.command.CommandResponseMessage;
import org.apache.bigtop.manager.common.message.entity.pojo.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class KryoPoolHolder {

    private static final Pool<Kryo> KRYO_POOL = new Pool<Kryo>(true, false, 16) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setCopyReferences(true);

            // message types
            kryo.register(BaseMessage.class);
            kryo.register(BaseRequestMessage.class);
            kryo.register(BaseResponseMessage.class);
            kryo.register(HeartbeatMessage.class);
            kryo.register(CommandResponseMessage.class);
            kryo.register(CommandRequestMessage.class);

            // message pojo
            kryo.register(HostInfo.class);
            kryo.register(OSSpecificInfo.class);
            kryo.register(ClusterInfo.class);
            kryo.register(RepoInfo.class);
            kryo.register(HostCheckType.class);
            kryo.register(HostCheckType[].class);
            kryo.register(CommandMessageType.class);
            kryo.register(Command.class);
            kryo.register(ScriptInfo.class);
            kryo.register(ComponentInfo.class);
            kryo.register(CustomCommandInfo.class);

            // java classes
            kryo.register(BigDecimal.class);
            kryo.register(Timestamp.class);
            kryo.register(ArrayList.class);
            kryo.register(Integer.class);
            kryo.register(String.class);
            kryo.register(HashMap.class);
            kryo.register(LinkedHashMap.class);
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
