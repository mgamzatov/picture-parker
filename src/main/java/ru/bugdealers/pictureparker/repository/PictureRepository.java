package ru.bugdealers.pictureparker.repository;

import org.springframework.data.repository.CrudRepository;
import ru.bugdealers.pictureparker.model.entity.Picture;

/**
 * Created by Rashid.Iaraliev on 20.10.17.
 */
public interface PictureRepository extends CrudRepository<Picture, Long> {
}