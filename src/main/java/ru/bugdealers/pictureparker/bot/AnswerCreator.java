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
import java.util.Random;

@Component
public class AnswerCreator {
    private SessionRepository sessionRepository;
    private PictureRepository pictureRepository;
    private AnswerRepository answerRepository;
    private ScriptRunner scriptRunner;
    private LocalHostRequester localHostRequester;

    @Autowired
    public AnswerCreator(SessionRepository sessionRepository, PictureRepository pictureRepository, AnswerRepository answerRepository, ScriptRunner scriptRunner, LocalHostRequester localHostRequester) {
        this.sessionRepository = sessionRepository;
        this.pictureRepository = pictureRepository;
        this.answerRepository = answerRepository;
        this.scriptRunner = scriptRunner;
        this.localHostRequester = localHostRequester;
    }

    public Picture forPictureDescription(long userId, String messageText) {
        return byPictureDescription(userId, messageText);
    }

    public String forTextQuestion(long userId, String messageText) {
        return byTextQuestion(userId, messageText);
    }

    public Picture forPhotoMessage(long userId, String pathToImage) {
        return byPictureImage(userId, pathToImage);
    }

    private Picture byPictureDescription(long userId, String messageText) {
        long pictureId = scriptRunner.getPictureIdByDescription(messageText);
        if (pictureId == -1) {
            return null;
        }

        Picture picture = pictureRepository.findOne(pictureId);
        Session session = new Session(userId, picture);
        sessionRepository.save(session);
        return picture;
    }

    private Picture byPictureImage(long userId, String pathToImage) {
        long pictureId = scriptRunner.getPictureIdByImage(pathToImage);
        if (pictureId == -1) {
            return null;
        }

        Picture picture = pictureRepository.findOne(pictureId);
        Session session = new Session(userId, picture);
        sessionRepository.save(session);
        return picture;
    }

    private String byTextQuestion(long userId, String messageText) {
        Session session = sessionRepository.findOne(userId);
        if (session == null) {
            return "Введите \"поищи картину\" и опишите картину";
        }

        long standardQuestioId = scriptRunner.getStandardQuestionIdByQuestion(messageText);
        List<Answer> answers = answerRepository.findAnswerByStandardQuestionIdAndPictureId(standardQuestioId, session.getPicture().getId());
        Random rn = new Random();
        int i = rn.nextInt(answers.size());
        return answers.get(i).getText();
    }

}
