package com.king.app.gdb.data.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 11:49
 */
@Entity(nameInDb = "record_type3")
public class RecordType3w {

    @Id(autoincrement = true)
    private Long id;

    private int sequence;
    private int scoreFkType1;
    private int scoreFkType2;
    private int scoreFkType3;
    private int scoreFkType4;
    private int scoreFkType5;
    private int scoreFkType6;
    private int scoreFkType7;
    private int scoreFkType8;
    private int scoreStory;
    private int scoreScene;
    private int scoreRim;
    private int scoreBjob;
    private int scoreForePlay;
    private int scoreRhythm;
    private int scoreCshow;
    @Generated(hash = 1744147642)
    public RecordType3w(Long id, int sequence, int scoreFkType1, int scoreFkType2,
            int scoreFkType3, int scoreFkType4, int scoreFkType5, int scoreFkType6,
            int scoreFkType7, int scoreFkType8, int scoreStory, int scoreScene,
            int scoreRim, int scoreBjob, int scoreForePlay, int scoreRhythm,
            int scoreCshow) {
        this.id = id;
        this.sequence = sequence;
        this.scoreFkType1 = scoreFkType1;
        this.scoreFkType2 = scoreFkType2;
        this.scoreFkType3 = scoreFkType3;
        this.scoreFkType4 = scoreFkType4;
        this.scoreFkType5 = scoreFkType5;
        this.scoreFkType6 = scoreFkType6;
        this.scoreFkType7 = scoreFkType7;
        this.scoreFkType8 = scoreFkType8;
        this.scoreStory = scoreStory;
        this.scoreScene = scoreScene;
        this.scoreRim = scoreRim;
        this.scoreBjob = scoreBjob;
        this.scoreForePlay = scoreForePlay;
        this.scoreRhythm = scoreRhythm;
        this.scoreCshow = scoreCshow;
    }
    @Generated(hash = 1033636538)
    public RecordType3w() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getSequence() {
        return this.sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
    public int getScoreFkType1() {
        return this.scoreFkType1;
    }
    public void setScoreFkType1(int scoreFkType1) {
        this.scoreFkType1 = scoreFkType1;
    }
    public int getScoreFkType2() {
        return this.scoreFkType2;
    }
    public void setScoreFkType2(int scoreFkType2) {
        this.scoreFkType2 = scoreFkType2;
    }
    public int getScoreFkType3() {
        return this.scoreFkType3;
    }
    public void setScoreFkType3(int scoreFkType3) {
        this.scoreFkType3 = scoreFkType3;
    }
    public int getScoreFkType4() {
        return this.scoreFkType4;
    }
    public void setScoreFkType4(int scoreFkType4) {
        this.scoreFkType4 = scoreFkType4;
    }
    public int getScoreFkType5() {
        return this.scoreFkType5;
    }
    public void setScoreFkType5(int scoreFkType5) {
        this.scoreFkType5 = scoreFkType5;
    }
    public int getScoreFkType6() {
        return this.scoreFkType6;
    }
    public void setScoreFkType6(int scoreFkType6) {
        this.scoreFkType6 = scoreFkType6;
    }
    public int getScoreFkType7() {
        return this.scoreFkType7;
    }
    public void setScoreFkType7(int scoreFkType7) {
        this.scoreFkType7 = scoreFkType7;
    }
    public int getScoreFkType8() {
        return this.scoreFkType8;
    }
    public void setScoreFkType8(int scoreFkType8) {
        this.scoreFkType8 = scoreFkType8;
    }
    public int getScoreStory() {
        return this.scoreStory;
    }
    public void setScoreStory(int scoreStory) {
        this.scoreStory = scoreStory;
    }
    public int getScoreScene() {
        return this.scoreScene;
    }
    public void setScoreScene(int scoreScene) {
        this.scoreScene = scoreScene;
    }
    public int getScoreRim() {
        return this.scoreRim;
    }
    public void setScoreRim(int scoreRim) {
        this.scoreRim = scoreRim;
    }
    public int getScoreBjob() {
        return this.scoreBjob;
    }
    public void setScoreBjob(int scoreBjob) {
        this.scoreBjob = scoreBjob;
    }
    public int getScoreForePlay() {
        return this.scoreForePlay;
    }
    public void setScoreForePlay(int scoreForePlay) {
        this.scoreForePlay = scoreForePlay;
    }
    public int getScoreRhythm() {
        return this.scoreRhythm;
    }
    public void setScoreRhythm(int scoreRhythm) {
        this.scoreRhythm = scoreRhythm;
    }
    public int getScoreCshow() {
        return this.scoreCshow;
    }
    public void setScoreCshow(int scoreCshow) {
        this.scoreCshow = scoreCshow;
    }
}
