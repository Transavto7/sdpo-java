package ru.nozdratenko.sdpo;

import jssc.SerialPortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.nozdratenko.sdpo.commands.Command;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
@EnableAsync
public class SdpoApplication implements CommandLineRunner {
	private final Sdpo sdpo;

	@Autowired
	public SdpoApplication(Sdpo sdpo) {
		this.sdpo = sdpo;
	}

	public static void main(String[] args) throws IOException, SerialPortException {

		SpringApplication.run(SdpoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		sdpo.init();
		if (args.length > 0) {
			Command.execute(args[0], Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		sdpo.loadData();
		sdpo.openBrowser();
	}

	@Bean
	public HttpMessageConverter<BufferedImage> bufferedImageHttpMessageConverter() {
		return new BufferedImageHttpMessageConverter();
	}
}
