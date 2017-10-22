package ru.bugdealers.pictureparker.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bugdealers.pictureparker.model.entity.Answer;
import ru.bugdealers.pictureparker.model.entity.Picture;
import ru.bugdealers.pictureparker.model.entity.Session;
import ru.bugdealers.pictureparker.net.LocalHostRequester;
import ru.bugdealers.pictureparker.repository.AnswerRepository;
import ru.bugdealers.pictureparker.repository.PictureRepository;
import ru.bugdealers.pictureparker.repository.SessionRepository;
import ru.bugdealers.pictureparker.utilits.ScriptRunner;

import java.util.Arrays;
import java.util.List;

@Component
public class AnswerCreator {
    private SessionRepository sessionRepository;
    private PictureRepository pictureRepository;
    private AnswerRepository answerRepository;
    private ScriptRunner scriptRunner;
    private LocalHostRequester localHostRequester;
    private static final List<String> DESCRIPTION_TAGS = Arrays.asList("найти", "найди", "ищи ", "покажи");
    private static final int TAG_LENGTH = 7;

    @Autowired
    public AnswerCreator(SessionRepository sessionRepository, PictureRepository pictureRepository, AnswerRepository answerRepository, ScriptRunner scriptRunner, LocalHostRequester localHostRequester) {
        this.sessionRepository = sessionRepository;
        this.pictureRepository = pictureRepository;
        this.answerRepository = answerRepository;
        this.scriptRunner = scriptRunner;
        this.localHostRequester = localHostRequester;
    }

    public String forSimpleMessage(long userId, String messageText) {
        messageText = messageText.trim().toLowerCase();


        if (messageText.length() > TAG_LENGTH && DESCRIPTION_TAGS.stream().parallel().anyMatch(messageText.substring(0, TAG_LENGTH)::contains)) {
            return byPictureDescription(userId, messageText);
        }

        return byTextQuestion(userId, messageText);

    }

    public String forPhotoMessage(long userId, String pathToImage) {
        return byPictureImage(userId, pathToImage);
    }

    private String byPictureDescription(long userId, String messageText) {
        long pictureId = scriptRunner.getPictureIdByDescription(messageText);
        if (pictureId == -1) {
            return "Картина не найдена";
        }

        Picture picture = pictureRepository.findOne(pictureId);
        Session session = new Session(userId, picture);
        sessionRepository.save(session);
        return picture.getName();
    }

    private String byPictureImage(long userId, String pathToImage) {
        long pictureId = scriptRunner.getPictureIdByImage(pathToImage);
        if (pictureId == -1) {
            return "Картина не найдена";
        }

        Picture picture = pictureRepository.findOne(pictureId);
        Session session = new Session(userId, picture);
        sessionRepository.save(session);
        return picture.getName();
    }

    private String byTextQuestion(long userId, String messageText) {
        Session session = sessionRepository.findOne(userId);
        if (session == null) {
            return "Введите \"поищи картину\" и опишите картину";
        }

        long standardQuestioId = scriptRunner.getStandardQuestionIdByQuestion(messageText);
        List<Answer> answers = answerRepository.findAnswerByStandardQuestionIdAndPictureId(standardQuestioId, session.getPicture().getId());

        return answers.get(0).getText();
    }

}
