package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //标识为配置类
@Slf4j
public class OssConfiguration {
    // 通过配置类注入第三方工具的好处在于
    // 1. 便于管理, 通过配置类统一管理第三方工具的配置 设置条件 启用禁用 便于维护
    // 2. 便于扩展, 通过配置类可以方便的扩展第三方工具的配置, 例如配置多个第三方工具
    // 3. 便于测试, 通过配置类可以方便的进行单元测试
    // 4. 便于解耦, 通过配置类可以方便的解耦第三方工具的配置,不用修改第三方工具的源码

    // 配置阿里云OSS
     @Bean // 第三方配置类 注册到Spring容器 名称为方法名
     @ConditionalOnMissingBean // 当Spring容器中不存在该Bean时才注册
     public AliOssUtil ossUtil(AliOssProperties aliOssProperties){
         log.info("初始化阿里云OSS配置, {}", aliOssProperties);
         return new AliOssUtil(aliOssProperties.getEndpoint(),
                 aliOssProperties.getAccessKeyId(),
                 aliOssProperties.getAccessKeySecret(),
                 aliOssProperties.getBucketName());
     }
}
