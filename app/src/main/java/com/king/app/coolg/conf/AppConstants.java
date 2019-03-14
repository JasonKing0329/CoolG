package com.king.app.coolg.conf;

/**
 * Created by Administrator on 2016/11/27 0027.
 */

public class AppConstants {

    public static final String KEY_SCENE_ALL = "All";

    public static final String KEY_REQEUST_CODE = "request_code";
    public static final int REQUEST_SELECT_IMAGE = 101;
    public static final String DATA_SELECT_IMAGE = "data_select_image";

    public static final int STAR_SORT_NAME = 0;
    public static final int STAR_SORT_RECORDS = 1;
    public static final int STAR_SORT_RATING = 2;
    public static final int STAR_SORT_RATING_FACE = 3;
    public static final int STAR_SORT_RATING_BODY = 4;
    public static final int STAR_SORT_RATING_DK = 5;
    public static final int STAR_SORT_RATING_SEXUALITY = 6;
    public static final int STAR_SORT_RATING_PASSION = 7;
    public static final int STAR_SORT_RATING_VIDEO = 8;
    public static final int STAR_SORT_RANDOM = 9;

    public static final int SCENE_SORT_NAME = 0;
    public static final int SCENE_SORT_NUMBER = 1;
    public static final int SCENE_SORT_AVG = 2;
    public static final int SCENE_SORT_MAX = 3;

    public static final String FILTER_KEY_NR = "NR";
    public static final String FILTER_KEY_SCORE = "score";
    public static final String FILTER_KEY_SCORE_CUM = "cum";
    public static final String FILTER_KEY_SCORE_PASSION = "passion";
    public static final String FILTER_KEY_SCORE_STAR = "star";
    public static final String FILTER_KEY_SCORE_STARC = "starC";
    public static final String FILTER_KEY_SCORE_BJOB = "bjob";
    public static final String FILTER_KEY_SCORE_BAREBACK = "bareback";
    public static final String FILTER_KEY_SCORE_STORY = "story";
    public static final String FILTER_KEY_SCORE_RHYTHM = "rhythm";
    public static final String FILTER_KEY_SCORE_SCECE = "scene";
    public static final String FILTER_KEY_SCORE_RIM = "rim";
    public static final String FILTER_KEY_SCORE_CSHOW = "cshow";
    public static final String FILTER_KEY_SCORE_SPECIAL = "special";
    public static final String FILTER_KEY_SCORE_FOREPLAY = "foreplay";
    public static final String FILTER_KEY_SCORE_DEPRECATED = "deprecated";

    public static final String[] STAR_LIST_TITLES = new String[]{
            "All", "1", "0", "0.5"
    };

    /**
     * 与VALUE_RECORD_TYPE_XX严格对应
     */
    public static final String[] RECORD_LIST_TITLES = new String[]{
            "All", "1V1", "3W", "Multi", "Long"
    };

    public static final String RESP_ORDER_ID = "order_id";

    public static final String ORDER_STUDIO_NAME = "Studio";

    public static final Long PLAY_ORDER_TEMP_ID = 1l;
    public static final String PLAY_ORDER_TEMP_NAME = "temp";

    public static final String[] RECORD_SQL_EXPRESSIONS = new String[]{
            "score > ",
            "score_bareback > 0",
            "score_passion > ",
            "score_cum > ",
            "score_feel > ",
            "score_star > ",
            "hd_level = ",
            "scene = ''",
            "special_desc LIKE '%%'",
    };

}
