package ru.bugdealers.pictureparker.bot;

import com.petersamokhin.bots.sdk.clients.Client;
import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.bugdealers.pictureparker.model.entity.Session;
import ru.bugdealers.pictureparker.net.UrlFileLoader;
import ru.bugdealers.pictureparker.net.YandexSpeechKitConnector;
import ru.bugdealers.pictureparker.repository.PictureRepository;
import ru.bugdealers.pictureparker.repository.SessionRepository;

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

    private Logger logger = LoggerFactory.getLogger(VkBotRunner.class);

    @Value("${accessToken}")
    private String accessToken;

    private UrlFileLoader urlFileLoader;
    private YandexSpeechKitConnector yandexSpeechKitConnector;
    private SessionRepository sessionRepository;
    private PictureRepository pictureRepository;

    @Autowired
    public VkBotRunner(UrlFileLoader urlFileLoader, YandexSpeechKitConnector yandexSpeechKitConnector, SessionRepository sessionRepository, PictureRepository pictureRepository) {
        this.urlFileLoader = urlFileLoader;
        this.yandexSpeechKitConnector = yandexSpeechKitConnector;
        this.sessionRepository = sessionRepository;
        this.pictureRepository = pictureRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Client client = new Group(accessToken);
        client.onMessage(message -> {
            client.enableTyping(true);
            logger.info(message.getText());
            if (message.isPhotoMessage()) {
                onPhotoMessage(client, message);
            } else if (message.isVoiceMessage()) {
                onVoiceMessage(client, message);
            } else {


                Session session = sessionRepository.findOne((long) message.authorId());
                if (session == null) {
                    session = new Session((long) message.authorId(), pictureRepository.findOne((long) (message.authorId() % 23)));
                    sessionRepository.save(session);
                }


                new Message()
                        .from(client)
                        .to(message.authorId())
                        .text(session.getPicture().getId())
                        .send();
            }
        });
    }

    private void onPhotoMessage(Client client, Message message) {
        if (message.getPhotos().length() > 0) {
            try {
                File outputFile = File.createTempFile("image", ".jpg");
                urlFileLoader.downloadFileFromUrl(message.getBiggestPhotoUrl(message.getPhotos()), outputFile.getAbsolutePath());
                logger.info(outputFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new Message()
                .from(client)
                .to(message.authorId())
                .text("крутое изображение, чувак!")
                .send();
    }

    private void onVoiceMessage(Client client, Message message) {
        String text = "";
        try {
            File outputFile = File.createTempFile("voice", ".ogg");
            urlFileLoader.downloadFileFromUrl(message.getVoiceMessage().get("url").toString(), outputFile.getAbsolutePath());
            logger.info(outputFile.getAbsolutePath());
            text = yandexSpeechKitConnector.recognizeTextFromAudio(outputFile);
            logger.info("text from audio {}", text);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Message()
                .from(client)
                .to(message.authorId())
                .text("мне послышалось или ты сказал \"" + text + "\"")
                .send();
    }
}
