package ru.bugdealers.pictureparker.utilits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by Magomed Gamzatov on 21.10.2017.
 */
@Component
@Order(1)
public class FolderInitializer implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(FolderInitializer.class);

    @Value("${script.folder}")
    private String scriptFolder;
    @Value("${voice.folder}")
    private String voiceFolder;
    @Value("${image.folder}")
    private String imageFolder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("createFolderIfNotExists for scriptFolder {}", createFolderIfNotExists(scriptFolder));
        logger.info("createFolderIfNotExists for voiceFolder {}", createFolderIfNotExists(voiceFolder));
        logger.info("createFolderIfNotExists for imageFolder {}", createFolderIfNotExists(imageFolder));
    }

    private boolean createFolderIfNotExists(String pathToFolder) {
        boolean flag = false;
        File directory = new File(pathToFolder);
        if(!directory.exists()) {
            flag = directory.mkdirs();
        }
        return flag;
    }
}
