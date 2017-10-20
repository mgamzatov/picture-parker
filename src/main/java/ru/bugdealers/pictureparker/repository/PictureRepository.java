package ru.bugdealers.pictureparker.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.bugdealers.pictureparker.model.entity.Picture;

/**
 * Created by Rashid.Iaraliev on 20.10.17.
 */
@Repository
public interface PictureRepository extends CrudRepository<Picture, Long> {
}