package org.apache.bigtop.manager.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Slf4j
@Component
public class YamlUtils<T> {

    @Resource
    private Yaml yaml;

    public T loadYaml(String path, Class<T> clazz) {
        FileReader reader = null;
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            log.error(path + " File Not Found", e);
            return null;
        }
        BufferedReader buffer = new BufferedReader(reader);
        return yaml.loadAs(buffer, clazz);
    }
}
