package ru.bugdealers.pictureparker.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bugdealers.pictureparker.model.entity.Picture;
import ru.bugdealers.pictureparker.model.entity.StandardQuestion;

/**
 * Created by Rashid.Iaraliev on 20.10.17.
 */
@Repository
public interface StandardQuestionRepository extends CrudRepository<StandardQuestion, Long> {
}