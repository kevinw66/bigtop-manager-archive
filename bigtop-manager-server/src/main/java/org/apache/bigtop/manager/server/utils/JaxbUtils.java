package org.apache.bigtop.manager.server.utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class JaxbUtils {

    @SuppressWarnings("unchecked")
    public static <T> T readFromFile(File file, Class<T> clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readFromPath(String path, Class<T> clazz) {
        return readFromFile(new File(path), clazz);
    }
}
