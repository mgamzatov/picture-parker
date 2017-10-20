package ru.bugdealers.pictureparker.model.entity;

import javax.persistence.*;

/**
 * Created by Rashid.Iaraliev on 20.10.17.
 */
@Entity
public class Session {

    @Id
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "picture_id")
    private Picture picture;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}
