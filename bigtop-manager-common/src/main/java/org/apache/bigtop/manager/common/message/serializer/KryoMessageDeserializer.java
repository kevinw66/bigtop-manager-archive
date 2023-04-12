package org.apache.bigtop.manager.common.message.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.apache.bigtop.manager.common.message.type.BaseMessage;

public class KryoMessageDeserializer implements MessageDeserializer {

    @Override
    public BaseMessage deserialize(byte[] bytes) {
        Input input = new Input(bytes);
        Kryo kryo = KryoManager.obtainKryo();
        BaseMessage baseMessage = (BaseMessage) kryo.readClassAndObject(input);
        input.close();
        KryoManager.freeKryo(kryo);

        return baseMessage;
    }
}
