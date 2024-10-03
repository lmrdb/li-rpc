package com.li.lirpc.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;

import java.util.Map;

/**
 * 配置工具类
 */
public class ConfigUtils {

    private static Props props;
    private static Map<String, Object> yamlMap;

    /**
     * 加载配置对象
     *
     * @param tClass
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     *
     * @param tClass
     * @param prefix
     * @param environment
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }

        String propertiesFile = configFileBuilder + ".properties";
        String ymlFile = configFileBuilder + ".yml";
        String yamlFile = configFileBuilder + ".yaml";

        // 优先加载 properties 文件
        if (loadProperties(propertiesFile)) {
            return props.toBean(tClass, prefix);
        }
        // 如果 properties 文件不存在，则尝试加载 yaml/yml 文件
        if (loadYaml(ymlFile)) {
            return mapToBean(tClass, prefix);
        }
        if (loadYaml(yamlFile)) {
            return mapToBean(tClass, prefix);
        }

        throw new RuntimeException("配置文件未找到: " + propertiesFile + ", " + ymlFile + ", 或 " + yamlFile);
    }

    /**
     * 加载 properties 文件
     *
     * @param filePath
     * @return
     */
    private static boolean loadProperties(String filePath) {
        try {
            props = new Props(filePath);
            props.autoLoad(true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 加载 yaml/yml 文件
     *
     * @param filePath
     * @return
     */
    private static boolean loadYaml(String filePath) {
        try {
            yamlMap = YamlUtil.loadByPath(filePath);
            return yamlMap != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将 yaml map 转换为指定的 bean
     *
     * @param tClass
     * @param prefix
     * @param <T>
     * @return
     */
    private static <T> T mapToBean(Class<T> tClass, String prefix) {
        if (StrUtil.isNotBlank(prefix)) {
            Object prefixMap = yamlMap.get(prefix);
            if (prefixMap instanceof Map) {
                return BeanUtil.toBean((Map<?, ?>) prefixMap, tClass);
            }
        }
        return BeanUtil.toBean(yamlMap, tClass);
    }
}
