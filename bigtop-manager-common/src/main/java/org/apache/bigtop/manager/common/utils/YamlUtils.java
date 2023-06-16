package org.apache.bigtop.manager.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;

@Slf4j
public class YamlUtils {

    private static final Yaml YAML;

    static {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);

        Representer representer = new Representer(dumperOptions);
        representer.getPropertyUtils().setSkipMissingProperties(true);

        YAML = new Yaml(representer, dumperOptions);
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
     * @param data yaml content, maybe Map, json or java bean
     */
    public static void writeYaml(String path, Object data) {
        try (FileWriter fileWriter = new FileWriter(path, false)) {
            YAML.dump(data, fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
