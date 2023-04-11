package org.apache.bigtop.manager.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class PropertiesUtils {

    public void createProperties(String fileName, Map<String, Object> map) {
        Properties properties = new Properties();
        properties.putAll(map);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            properties.store(fileWriter, "");
        } catch (IOException e) {
            log.error("Failed to createProperties: ", e);
            throw new RuntimeException(e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ignore) {

                }
            }
        }
    }
}
