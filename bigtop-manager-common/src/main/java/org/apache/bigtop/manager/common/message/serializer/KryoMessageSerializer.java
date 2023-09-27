package org.apache.bigtop.manager.common.message.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.apache.bigtop.manager.common.message.type.BaseMessage;

import java.io.ByteArrayOutputStream;

import static org.apache.bigtop.manager.common.constants.Constants.KRYO_BUFFER_SIZE;

public class KryoMessageSerializer implements MessageSerializer {

    @Override
    public byte[] serialize(BaseMessage message) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream, KRYO_BUFFER_SIZE);
        Kryo kryo = KryoPoolHolder.obtainKryo();
        kryo.writeClassAndObject(output, message);
        output.flush();
        output.close();
        KryoPoolHolder.freeKryo(kryo);

        return output.getBuffer();
    }
}
