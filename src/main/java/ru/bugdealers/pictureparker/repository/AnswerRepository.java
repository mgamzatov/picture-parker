package ru.bugdealers.pictureparker.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bugdealers.pictureparker.model.entity.Answer;
import ru.bugdealers.pictureparker.model.entity.Picture;
import ru.bugdealers.pictureparker.model.entity.Session;
import ru.bugdealers.pictureparker.model.entity.StandardQuestion;

import java.util.List;

/**
 * Created by Rashid.Iaraliev on 20.10.17.
 */
@Repository
public interface AnswerRepository extends CrudRepository<Answer, Long> {
    List<Answer> findAnswerByStandardQuestionAndPicture(StandardQuestion standardQuestion, Picture picture);
    List<Answer> findAnswerByStandardQuestionIdAndPictureId(long standardQuestionId, long pictureId);
}