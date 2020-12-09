package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 11:46
 */
@Entity(tableName = "record")
public class Record {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String scene;
    private String directory;
    private String name;
    private int hdLevel;
    private int score;
    private int scoreFeel;
    private int scoreStar;
    private int scorePassion;
    private int scoreBody;
    private int scoreCock;
    private int scoreAss;
    private int scoreCum;
    private int scoreSpecial;
    private int scoreBareback;
    private int deprecated;
    private String specialDesc;
    private long lastModifyTime;
    private int type;
    private long recordDetailId;

    @Ignore
    private RecordType1v1 recordType1v1;// RecordDao.getRecordType1v1(id)

    @Ignore
    private RecordType3w recordType3w;// RecordDao.getRecordType3w(id)

    @Ignore
    private List<RecordStar> relationList;// RecordDao.getRecordStarRelations(id)

    @Ignore
    private List<Star> starList;// StarDao.getRecordStars(id)

    @Ignore
    private CountRecord countRecord;// RecordDao.getCountRecord(id)

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScene() {
        return this.scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHdLevel() {
        return this.hdLevel;
    }

    public void setHdLevel(int hdLevel) {
        this.hdLevel = hdLevel;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScoreFeel() {
        return this.scoreFeel;
    }

    public void setScoreFeel(int scoreFeel) {
        this.scoreFeel = scoreFeel;
    }

    public int getScoreStar() {
        return this.scoreStar;
    }

    public void setScoreStar(int scoreStar) {
        this.scoreStar = scoreStar;
    }

    public int getScorePassion() {
        return this.scorePassion;
    }

    public void setScorePassion(int scorePassion) {
        this.scorePassion = scorePassion;
    }

    public int getScoreCum() {
        return this.scoreCum;
    }

    public void setScoreCum(int scoreCum) {
        this.scoreCum = scoreCum;
    }

    public int getScoreSpecial() {
        return this.scoreSpecial;
    }

    public void setScoreSpecial(int scoreSpecial) {
        this.scoreSpecial = scoreSpecial;
    }

    public int getScoreBareback() {
        return this.scoreBareback;
    }

    public void setScoreBareback(int scoreBareback) {
        this.scoreBareback = scoreBareback;
    }

    public int getDeprecated() {
        return this.deprecated;
    }

    public void setDeprecated(int deprecated) {
        this.deprecated = deprecated;
    }

    public String getSpecialDesc() {
        return this.specialDesc;
    }

    public void setSpecialDesc(String specialDesc) {
        this.specialDesc = specialDesc;
    }

    public long getLastModifyTime() {
        return this.lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getRecordDetailId() {
        return this.recordDetailId;
    }

    public void setRecordDetailId(long recordDetailId) {
        this.recordDetailId = recordDetailId;
    }

    public int getScoreBody() {
        return this.scoreBody;
    }

    public void setScoreBody(int scoreBody) {
        this.scoreBody = scoreBody;
    }

    public int getScoreCock() {
        return this.scoreCock;
    }

    public void setScoreCock(int scoreCock) {
        this.scoreCock = scoreCock;
    }

    public int getScoreAss() {
        return this.scoreAss;
    }

    public void setScoreAss(int scoreAss) {
        this.scoreAss = scoreAss;
    }

    public RecordType1v1 getRecordType1v1() {
        return recordType1v1;
    }

    public void setRecordType1v1(RecordType1v1 recordType1v1) {
        this.recordType1v1 = recordType1v1;
    }

    public RecordType3w getRecordType3w() {
        return recordType3w;
    }

    public void setRecordType3w(RecordType3w recordType3w) {
        this.recordType3w = recordType3w;
    }

    public List<RecordStar> getRelationList() {
        return relationList;
    }

    public void setRelationList(List<RecordStar> relationList) {
        this.relationList = relationList;
    }

    public List<Star> getStarList() {
        return starList;
    }

    public void setStarList(List<Star> starList) {
        this.starList = starList;
    }

    public CountRecord getCountRecord() {
        return countRecord;
    }

    public void setCountRecord(CountRecord countRecord) {
        this.countRecord = countRecord;
    }
}
