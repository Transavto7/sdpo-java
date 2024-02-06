package ru.nozdratenko.sdpo;

import jssc.SerialPortException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import ru.nozdratenko.sdpo.commands.Command;
import ru.nozdratenko.sdpo.helper.BrowserHelper;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class SdpoApplication {

	public static void main(String[] args) throws IOException, SerialPortException {
		Sdpo.init();

		if (args.length > 0) {
			Command.execute(args[0], Arrays.copyOfRange(args, 1, args.length));
			return;
		}

		Sdpo.loadData();
//		Sdpo.openBrowser();
		SpringApplication.run(SdpoApplication.class, args);
	}

	@Bean
	public HttpMessageConverter<BufferedImage> bufferedImageHttpMessageConverter() {
		return new BufferedImageHttpMessageConverter();
	}
}
