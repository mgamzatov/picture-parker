package ru.bugdealers.pictureparker.bot;

import com.petersamokhin.bots.sdk.clients.Client;
import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.bugdealers.pictureparker.net.UrlFileLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Component
@PropertySource("classpath:privacy.properties")
public class VkBotRunner implements ApplicationRunner {

    @Value("${accessToken}")
    private String accessToken;

    private UrlFileLoader urlFileLoader;

    @Autowired
    public VkBotRunner(UrlFileLoader urlFileLoader) {
        this.urlFileLoader = urlFileLoader;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Client client = new Group(accessToken);
        client.onMessage(message -> {
            System.out.println(message.getText());
            if(message.getPhotos().length() > 0) {
                try {
                    File outputFile = File.createTempFile("image", ".jpg");
                    urlFileLoader.downloadFileFromUrl(message.getBiggestPhotoUrl(message.getPhotos()), outputFile.getAbsolutePath());
                    System.out.println(outputFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            new Message()
                    .from(client)
                    .to(message.authorId())
                    .text("Хэллоу, Ворлд!")
                    .send();
        });
    }
}
