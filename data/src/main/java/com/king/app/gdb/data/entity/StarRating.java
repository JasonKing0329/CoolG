package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Desc: rating of star
 *
 * @author：Jing Yang
 * @date: 2018/5/8 18:55
 */
@Entity(nameInDb = "star_rating")
public class StarRating {

    @Id(autoincrement = true)
    private Long id;

    private long starId;

    private float face;

    private float body;

    private float sexuality;

    private float dk;

    private float passion;

    private float video;

    private float complex;

    @Generated(hash = 365035643)
    public StarRating(Long id, long starId, float face, float body, float sexuality,
            float dk, float passion, float video, float complex) {
        this.id = id;
        this.starId = starId;
        this.face = face;
        this.body = body;
        this.sexuality = sexuality;
        this.dk = dk;
        this.passion = passion;
        this.video = video;
        this.complex = complex;
    }

    @Generated(hash = 2089122788)
    public StarRating() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getStarId() {
        return this.starId;
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    public float getFace() {
        return this.face;
    }

    public void setFace(float face) {
        this.face = face;
    }

    public float getBody() {
        return this.body;
    }

    public void setBody(float body) {
        this.body = body;
    }

    public float getSexuality() {
        return this.sexuality;
    }

    public void setSexuality(float sexuality) {
        this.sexuality = sexuality;
    }

    public float getDk() {
        return this.dk;
    }

    public void setDk(float dk) {
        this.dk = dk;
    }

    public float getPassion() {
        return this.passion;
    }

    public void setPassion(float passion) {
        this.passion = passion;
    }

    public float getVideo() {
        return this.video;
    }

    public void setVideo(float video) {
        this.video = video;
    }

    public float getComplex() {
        return this.complex;
    }

    public void setComplex(float complex) {
        this.complex = complex;
    }

}
