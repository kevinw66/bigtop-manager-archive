package org.apache.bigtop.manager.mpack;

import org.apache.bigtop.manager.common.BigtopManagerCommonApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = BigtopManagerCommonApplication.class)
@ComponentScan(value = "org.apache.bigtop.manager")
class StackManagerTest {
    @Resource
    StackManager stackManager;

    @Test
    void initConfig() throws FileNotFoundException {
        stackManager.initConfig("E:\\opt\\code\\rawcode\\bigtop-manager\\bigtop-manager-mpack\\src\\main\\resources\\stack\\BIGTOP\\3.2.0\\metainfo.yaml");
        System.out.println(stackManager.stackInfo);
    }
}