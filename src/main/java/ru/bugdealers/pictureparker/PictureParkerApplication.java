package ru.bugdealers.pictureparker;

import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.bugdealers.pictureparker.utilits.FolderInitializer;

@SpringBootApplication
public class PictureParkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PictureParkerApplication.class, args);
	}

	@Bean
    public OkHttpClient okHttpClient() {
	    return new OkHttpClient();
    }

//	@Bean(name = "folderInitializer")
//	public FolderInitializer folderInitializer() {
//		return new FolderInitializer();
//	}
}
