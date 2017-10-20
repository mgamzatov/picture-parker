package ru.bugdealers.pictureparker;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.bugdealers.pictureparker.bot.VkBotRunner;

@SpringBootApplication
public class PictureParkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PictureParkerApplication.class, args);
	}
}
