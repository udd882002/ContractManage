package com.seven.contract.manage.uploader;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@ComponentScan("com.seven.contract.manage.uploader")
@EnableConfigurationProperties({UploaderProperties.class})
public class UploaderConfiguration {

}
