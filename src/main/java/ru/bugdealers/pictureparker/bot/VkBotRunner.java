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
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ru.bugdealers.pictureparker.model.entity.Picture;
import ru.bugdealers.pictureparker.model.entity.Session;
import ru.bugdealers.pictureparker.net.UrlFileLoader;
import ru.bugdealers.pictureparker.net.YandexSpeechKitConnector;
import ru.bugdealers.pictureparker.repository.PictureRepository;
import ru.bugdealers.pictureparker.repository.SessionRepository;
import ru.bugdealers.pictureparker.utilits.FolderInitializer;
import sun.misc.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;

@Component
@Order(2)
@PropertySource("classpath:privacy.properties")
public class VkBotRunner implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(VkBotRunner.class);

    @Value("${accessToken}")
    private String accessToken;
    @Value("${voice.folder}")
    private String voiceFolder;
    @Value("${image.folder}")
    private String imageFolder;

    private static final List<String> DESCRIPTION_TAGS = Arrays.asList("найти", "найди", "ищи ", "покажи", "искать");
    private static final int TAG_LENGTH = 7;

    private String pictureFolder;
    private UrlFileLoader urlFileLoader;
    private YandexSpeechKitConnector yandexSpeechKitConnector;
    private AnswerCreator answerCreator;

    @Autowired
    public VkBotRunner(UrlFileLoader urlFileLoader, YandexSpeechKitConnector yandexSpeechKitConnector, AnswerCreator answerCreator) throws IOException {
        this.urlFileLoader = urlFileLoader;
        this.yandexSpeechKitConnector = yandexSpeechKitConnector;
        this.answerCreator = answerCreator;
        this.pictureFolder = new ClassPathResource("pictures").getURL().getPath();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Client client = new Group(accessToken);
        client.onMessage(message -> {
            client.enableTyping(true);

            logger.info("message: ", message.getText());
            if (message.isPhotoMessage()) {
                onPhotoMessage(client, message);
            } else if (message.isVoiceMessage()) {
                onVoiceMessage(client, message);
            } else {
                String messageText = message.getText().trim().toLowerCase();
                if(isNewPictureRequest(messageText)) {
                    Picture picture = answerCreator.forPictureDescription(message.authorId(), message.getText());
                    pictureResponse(client, message, picture);
                } else {
                    new Message()
                            .from(client)
                            .to(message.authorId())
                            .text(answerCreator.forTextQuestion(message.authorId(), message.getText()))
                            .send();
                }
            }
        });
    }

    private void pictureResponse(Client client, Message message, Picture picture) {
        if(picture!=null) {
            String pathToImage = pictureFolder + File.separator + picture.getId() + ".jpg";
            new Message()
                    .from(client)
                    .to(message.authorId())
                    .text(picture.getName() + "\n" + picture.getReference())
                    .photo(pathToImage)
                    .send();

            new Message()
                    .from(client)
                    .to(message.authorId())
                    .text("Можем поговорить об этой картине. Что ты хочешь узнать?")
                    .send();
        } else {
            new Message()
                    .from(client)
                    .to(message.authorId())
                    .text("Картина не найдена")
                    .send();
        }
    }

    private void onPhotoMessage(Client client, Message message) {
        try {
            File outputFile = new File(imageFolder + "/image-" + System.currentTimeMillis() + ".jpg");
            urlFileLoader.downloadFileFromUrl(message.getBiggestPhotoUrl(message.getPhotos()), outputFile.getAbsolutePath());
            logger.info("path to image: {}", outputFile.getAbsolutePath());

            Picture picture = answerCreator.forPhotoMessage(message.authorId(), outputFile.getAbsolutePath());
            pictureResponse(client, message, picture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onVoiceMessage(Client client, Message message) {
        String text = "";
        try {
            File outputFile = new File(voiceFolder + "/voice-" + System.currentTimeMillis() + ".oog");
            urlFileLoader.downloadFileFromUrl(message.getVoiceMessage().get("url").toString(), outputFile.getAbsolutePath());
            logger.info("path to audio: {}", outputFile.getAbsolutePath());
            text = yandexSpeechKitConnector.recognizeTextFromAudio(outputFile);
            logger.info("text from audio {}", text);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Message()
                .from(client)
                .to(message.authorId())
                .text("- \""+ text + "\"")
                .send();
        client.enableTyping(true);
        if(isNewPictureRequest(text)) {
            Picture picture = answerCreator.forPictureDescription(message.authorId(), message.getText());
            pictureResponse(client, message, picture);
        } else {
            new Message()
                    .from(client)
                    .to(message.authorId())
                    .text(answerCreator.forTextQuestion(message.authorId(), text))
                    .send();
        }
    }

    private boolean isNewPictureRequest(String messageText) {
        return messageText.length() > TAG_LENGTH && DESCRIPTION_TAGS.stream().parallel().anyMatch(messageText.substring(0, TAG_LENGTH)::contains);
    }
}
