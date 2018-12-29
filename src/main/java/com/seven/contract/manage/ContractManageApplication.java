package com.seven.contract.manage;

import com.seven.contract.manage.uploader.EnableUploader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties
@EnableScheduling
@EnableUploader
public class ContractManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractManageApplication.class, args);
	}
}
