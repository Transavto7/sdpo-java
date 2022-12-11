package ru.nozdratenko.sdpo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SdpoApplication {

	public static void main(String[] args) {
		Sdpo.init();
		SpringApplication.run(SdpoApplication.class, args);
	}

}
