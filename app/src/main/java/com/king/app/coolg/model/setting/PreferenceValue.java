package com.king.app.coolg.model.setting;

/**
 * Created by JingYang on 2016/7/19 0019.
 * Description:
 */
public class PreferenceValue {

    public static final int HTTP_MAX_DOWNLOAD = 3;
    public static final int GDB_LATEST_NUM = 30;
    /**
     * 最高同时下载7个
     */
    public static final int HTTP_MAX_DOWNLOAD_UPLIMIT = 7;

    public static final int GDB_SR_ORDERBY_NONE = 0;
    public static final int GDB_SR_ORDERBY_NAME = 1;
    public static final int GDB_SR_ORDERBY_DATE = 2;
    public static final int GDB_SR_ORDERBY_SCORE =3;
    public static final int GDB_SR_ORDERBY_PASSION =4;
    public static final int GDB_SR_ORDERBY_CUM =5;
    public static final int GDB_SR_ORDERBY_STAR1 =6;
    public static final int GDB_SR_ORDERBY_STARCC1 =7;
    public static final int GDB_SR_ORDERBY_BJOB =8;
    public static final int GDB_SR_ORDERBY_STAR2 =9;
    public static final int GDB_SR_ORDERBY_STARCC2 =10;
    public static final int GDB_SR_ORDERBY_BAREBACK =11;
    public static final int GDB_SR_ORDERBY_SCOREFEEL =12;
    public static final int GDB_SR_ORDERBY_STORY =13;
    public static final int GDB_SR_ORDERBY_FOREPLAY =14;
    public static final int GDB_SR_ORDERBY_RIM =15;
    public static final int GDB_SR_ORDERBY_RHYTHM =16;
    public static final int GDB_SR_ORDERBY_SCENE =17;
    public static final int GDB_SR_ORDERBY_CSHOW =18;
    public static final int GDB_SR_ORDERBY_SPECIAL =19;
    public static final int GDB_SR_ORDERBY_HD =20;
    public static final int GDB_SR_ORDERBY_FK1 =21;
    public static final int GDB_SR_ORDERBY_FK2 =22;
    public static final int GDB_SR_ORDERBY_FK3 =23;
    public static final int GDB_SR_ORDERBY_FK4 =24;
    public static final int GDB_SR_ORDERBY_FK5 =25;
    public static final int GDB_SR_ORDERBY_FK6 =26;
    public static final int GDB_SR_ORDERBY_SCORE_BASIC =27;
    public static final int GDB_SR_ORDERBY_SCORE_EXTRA =28;
    public static final int GDB_SR_ORDERBY_STAR =29;
    public static final int GDB_SR_ORDERBY_STARC =30;
    public static final int GDB_SR_ORDERBY_TIME =31;
    public static final int GDB_SR_ORDERBY_SIZE =32;

    public static final int STAR_LIST_VIEW_LIST =0;
    public static final int STAR_LIST_VIEW_GRID =1;
    public static final int STAR_LIST_VIEW_CIRCLE =2;
    public static final int STAR_LIST_VIEW_RICH =3;

    public static final String SORT_COLUMN_KEY_NAME = "Name";
    public static final String SORT_COLUMN_KEY_DATE = "Date";
    public static final String SORT_COLUMN_KEY_SCORE = "Score";
    public static final String SORT_COLUMN_KEY_PASSION = "Passion";
    public static final String SORT_COLUMN_KEY_CUM = "Cum";
    public static final String SORT_COLUMN_KEY_FEEL = "ScoreFeel";
    public static final String SORT_COLUMN_KEY_SPECIAL = "Special";
    public static final String SORT_COLUMN_KEY_STAR = "Star";
    public static final String[] RECORD_SORT_ARRAY = new String[] {
            "None", SORT_COLUMN_KEY_NAME, SORT_COLUMN_KEY_DATE, SORT_COLUMN_KEY_SCORE, SORT_COLUMN_KEY_PASSION,
            SORT_COLUMN_KEY_CUM, SORT_COLUMN_KEY_FEEL, SORT_COLUMN_KEY_SPECIAL, SORT_COLUMN_KEY_STAR
    };

    /**
     * pad
     */
    public static final int PAD_STAR_RECORDS_GRID1 = 0;
    public static final int PAD_STAR_RECORDS_GRID2 = 1;

    public static final int PHONE_ORDER_SORT_BY_NAME = 0;
    public static final int PHONE_ORDER_SORT_BY_ITEMS = 1;
    public static final int PHONE_ORDER_SORT_BY_CREATE_TIME = 2;
    public static final int PHONE_ORDER_SORT_BY_UPDATE_TIME =3;
}
