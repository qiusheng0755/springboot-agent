package com.yml;

import com.utils.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.Map;

/**
 * @Author qiusheng
 * @Date 2021-1-31 12:38
 * @Version 1.0
 */
@Slf4j
public class YmlTest {

    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        Reader applicationReader = null;
        try {
            applicationReader = ResourceUtils.read("test.yml");
            Map<String, Map<String, Object>> moduleConfig = yaml.loadAs(applicationReader, Map.class);
            if (!CollectionUtils.isEmpty(moduleConfig)) {
                System.out.println(moduleConfig);
                moduleConfig.forEach((moduleName, providerConfig) -> {
                    if (providerConfig.size() > 0) {
                        log.info("Get a module define from test.yml, module name: {}", moduleName);
                        providerConfig.forEach((providerName, config) -> {
                            log.info(
                                    "Get a provider define belong to {} module, provider name: {}", moduleName,
                                    providerName
                            );
                            final Map<String, ?> propertyValue = (Map<String, ?>) config;
                            if (propertyValue != null) {
                                propertyValue.forEach((key, value) -> {
                                    log.info("key={}, value={}", key, value);
                                });
                            }
                        });
                    } else {
                        log.warn(
                                "Get a module define from test.yml, but no provider define, use default, module name: {}",
                                moduleName
                        );
                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
