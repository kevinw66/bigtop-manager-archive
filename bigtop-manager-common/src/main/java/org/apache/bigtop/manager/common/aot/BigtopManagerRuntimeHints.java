package org.apache.bigtop.manager.common.aot;


import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import org.apache.bigtop.manager.common.message.serializer.KryoMessageDeserializer;
import org.apache.bigtop.manager.common.message.serializer.KryoMessageSerializer;
import org.apache.bigtop.manager.common.message.type.BaseMessage;
import org.apache.bigtop.manager.common.message.type.HeartbeatMessage;
import org.apache.bigtop.manager.common.message.type.pojo.HostInfo;
import org.springframework.aot.hint.*;

import java.util.List;

public class BigtopManagerRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register method for reflection
        ReflectionHints reflection = hints.reflection();

        List<TypeReference> typeReferences = TypeReference.listOf(
                KryoMessageSerializer.class,
                KryoMessageDeserializer.class,
                DefaultSerializers.BigDecimalSerializer.class,
                DefaultSerializers.DateSerializer.class,
                DefaultSerializers.ClassSerializer.class,
                BaseMessage.class,
                HeartbeatMessage.class,
                HostInfo.class
        );

        reflection.registerTypes(typeReferences, TypeHint.builtWith(MemberCategory.values()));
    }

}


