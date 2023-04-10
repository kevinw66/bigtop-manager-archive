package org.apache.bigtop.manager.common.utils;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

@Component
public class YamlUtils<T> {

    @Resource
    private Yaml yaml;

    public T loadYaml(String path, Class<T> clazz) throws FileNotFoundException {
        FileReader reader = new FileReader(path);
        BufferedReader buffer = new BufferedReader(reader);
        return yaml.loadAs(buffer, clazz);
    }
}
