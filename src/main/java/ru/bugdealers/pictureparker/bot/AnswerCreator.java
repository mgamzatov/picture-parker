package ru.bugdealers.pictureparker.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bugdealers.pictureparker.model.entity.Picture;
import ru.bugdealers.pictureparker.model.entity.Session;
import ru.bugdealers.pictureparker.repository.PictureRepository;
import ru.bugdealers.pictureparker.repository.SessionRepository;
import ru.bugdealers.pictureparker.utilits.ScriptRunner;

@Component
public class AnswerCreator {
    private SessionRepository sessionRepository;
    private PictureRepository pictureRepository;
    private ScriptRunner scriptRunner;
    private static final String DESCRIPTION_TAG = "описание";

    @Autowired
    public AnswerCreator(SessionRepository sessionRepository, PictureRepository pictureRepository, ScriptRunner scriptRunner) {
        this.sessionRepository = sessionRepository;
        this.pictureRepository = pictureRepository;
        this.scriptRunner = scriptRunner;
    }

    public String forSimpleMessage(long userId, String messageText) {
        messageText = messageText.trim().toLowerCase();

        if (messageText.length() > DESCRIPTION_TAG.length() && messageText.substring(0, DESCRIPTION_TAG.length() + 1).contains(DESCRIPTION_TAG)) {
            return byPictureDescription(userId, messageText);
        }

        return byTextQuestion(userId, messageText);

    }

    private String byPictureDescription(long userId, String messageText) {
        long pictureId = scriptRunner.getPictureIdByDescription(messageText);
        Picture picture = pictureRepository.findOne(pictureId);
        Session session = new Session(userId, picture);
        sessionRepository.save(session);
        return picture.getName();
    }

    private String byTextQuestion(long userId, String messageText) {
        Session session = sessionRepository.findOne(userId);
        if (session == null) {
            return "Введите #описание и опишите картину";
        }

        return "123";

    }

}
