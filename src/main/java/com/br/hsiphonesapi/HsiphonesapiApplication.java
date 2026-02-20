package com.br.hsiphonesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class HsiphonesapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HsiphonesapiApplication.class, args);
	}

}
