package ru.bugdealers.pictureparker.model.entity;

import javax.persistence.*;

/**
 * Created by Magomed Gamzatov on 21.10.2017.
 */
@Entity
public class Answer {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "picture_id")
    private Picture picture;

    @ManyToOne
    @JoinColumn(name = "standard_question_id")
    private StandardQuestion standardQuestion;

    @Column(length = 5000)
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public StandardQuestion getStandardQuestion() {
        return standardQuestion;
    }

    public void setStandardQuestion(StandardQuestion standardQuestion) {
        this.standardQuestion = standardQuestion;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
