package org.apache.bigtop.manager.agent.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.io.*;

@Slf4j
@Component
public class YamlUtils<T> {

    @Resource
    private Yaml yaml;

    /**
     * Read yaml file
     *
     * @param path  source yaml file path
     * @param clazz class
     * @return object
     */
    public T readYaml(String path, Class<T> clazz) {
        FileReader reader = null;
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            log.error(path + " File Not Found, ", e);
            return null;
        }
        BufferedReader buffer = new BufferedReader(reader);
        return yaml.loadAs(buffer, clazz);
    }

    /**
     * Write data to yaml file
     *
     * @param path out yaml file path
     * @param data yaml content
     */
    public void writeYaml(String path, String data) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(path, false);
            yaml.dump(data, fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}
