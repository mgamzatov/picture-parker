package ru.bugdealers.pictureparker.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bugdealers.pictureparker.model.entity.Answer;
import ru.bugdealers.pictureparker.model.entity.Picture;
import ru.bugdealers.pictureparker.model.entity.Session;
import ru.bugdealers.pictureparker.net.LocalHostRequester;
import ru.bugdealers.pictureparker.repository.AnswerRepository;
import ru.bugdealers.pictureparker.repository.PictureRepository;
import ru.bugdealers.pictureparker.repository.SessionRepository;
import ru.bugdealers.pictureparker.repository.StandardQuestionRepository;
import ru.bugdealers.pictureparker.utilits.ScriptRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class AnswerCreator {
    private SessionRepository sessionRepository;
    private PictureRepository pictureRepository;
    private AnswerRepository answerRepository;
    private StandardQuestionRepository standardQuestionRepository;
    private ScriptRunner scriptRunner;
    private LocalHostRequester localHostRequester;
    private Logger logger = LoggerFactory.getLogger(AnswerCreator.class);

    @Autowired
    public AnswerCreator(SessionRepository sessionRepository, PictureRepository pictureRepository, AnswerRepository answerRepository, StandardQuestionRepository standardQuestionRepository, ScriptRunner scriptRunner, LocalHostRequester localHostRequester) {
        this.sessionRepository = sessionRepository;
        this.pictureRepository = pictureRepository;
        this.answerRepository = answerRepository;
        this.standardQuestionRepository = standardQuestionRepository;
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

        logger.info("pictureId: ", pictureId);
        Picture picture = pictureRepository.findOne(pictureId);
        logger.info("pictureName: ", picture.getName());
        Session session = new Session(userId, picture);
        logger.info("session: ", session.getUserId());
        sessionRepository.save(session);
        return picture;
    }

    private Picture byPictureImage(long userId, String pathToImage) {
        long pictureId = localHostRequester.getPictureIdByImage(pathToImage);
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
            return "Здравствуйте, введите или скажите \"поищи картину\" и опишите картину";
        }

        long standardQuestionId = scriptRunner.getStandardQuestionIdByQuestion(messageText);
        if (standardQuestionId == -1) {
            return "Переформулируйте вопрос, пожалуйста";
        }

        String result;
        try {
            List<Answer> answers = answerRepository.findAnswerByStandardQuestionIdAndPictureId(standardQuestionId, session.getPicture().getId());
            Random rn = new Random();
            int i = rn.nextInt(answers.size());
            result = answers.get(i).getText();
        } catch (Exception e) {
            logger.error(e.toString());
            result = standardQuestionRepository.findOne(standardQuestionId).getText() + " Эту информацию мне еще не предоставили";
        }
        return result;
    }

}
