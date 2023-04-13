package org.apache.bigtop.manager.common.message.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.apache.bigtop.manager.common.message.type.BaseMessage;

import java.io.ByteArrayOutputStream;

public class KryoMessageSerializer implements MessageSerializer {

    @Override
    public byte[] serialize(BaseMessage message) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        Kryo kryo = KryoManager.obtainKryo();
        kryo.writeClassAndObject(output, message);
        output.flush();
        output.close();
        KryoManager.freeKryo(kryo);

        return output.getBuffer();
    }
}
