package org.apache.bigtop.manager.agent.utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;

@Slf4j
public class YamlUtils {

    private static final Yaml YAML;

    static {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        YAML = new Yaml(representer);
    }

    /**
     * Read yaml file
     *
     * @param path  source yaml file path
     * @param clazz class
     * @return object
     */
    public static <T> T readYaml(String path, Class<T> clazz) {
        FileReader reader = null;
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            log.error(path + " File Not Found, ", e);
            return null;
        }

        BufferedReader buffer = new BufferedReader(reader);
        return YAML.loadAs(buffer, clazz);
    }

    /**
     * Write data to yaml file
     *
     * @param path out yaml file path
     * @param data yaml content
     */
    public void writeYaml(String path, String data) {
        try (FileWriter fileWriter = new FileWriter(path, false)) {
            YAML.dump(data, fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
