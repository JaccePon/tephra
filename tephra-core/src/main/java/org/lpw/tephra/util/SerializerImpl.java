package org.lpw.tephra.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author lpw
 */
@Component("tephra.util.serializer")
public class SerializerImpl implements Serializer {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;

    @Override
    public byte[] serialize(Object object) {
        if (object == null)
            return null;

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);
            objectOutput.writeObject(object);
            objectOutput.close();
            output.close();

            return output.toByteArray();
        } catch (IOException e) {
            logger.warn(e, "序列化对象[{}]时发生异常！", object);

            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unserialize(byte[] bytes) {
        if (validator.isEmpty(bytes))
            return null;

        T object = null;
        try {
            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes));
            object = (T) input.readObject();
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            logger.warn(e, "反序列化对象时发生异常！");
        }

        return object;
    }
}
