package ru.nozdratenko.sdpo;

import jssc.SerialPortException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import ru.nozdratenko.sdpo.helper.ThermometerHelper;
import ru.nozdratenko.sdpo.lib.Bluetooth;
import ru.nozdratenko.sdpo.util.SdpoLog;

import java.awt.image.BufferedImage;
import java.io.IOException;

@SpringBootApplication
public class SdpoApplication {

	public static void main(String[] args) throws IOException, SerialPortException {
		Sdpo.init();
		SpringApplication.run(SdpoApplication.class, args);
	}

	@Bean
	public HttpMessageConverter<BufferedImage> bufferedImageHttpMessageConverter() {
		return new BufferedImageHttpMessageConverter();
	}
}
