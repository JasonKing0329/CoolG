package com.king.app.gdb.data.param;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 14:58
 */
public class DataConstants {

    public static final int VALUE_RECORD_TYPE_1V1 = 1;
    public static final int VALUE_RECORD_TYPE_3W = 2;
    public static final int VALUE_RECORD_TYPE_MULTI = 3;

    public static final int VALUE_RELATION_TOP = 1;
    public static final int VALUE_RELATION_BOTTOM = 2;
    public static final int VALUE_RELATION_MIX = 3;

    /**
     * related to scoreNoCond parameter in RecordSingleScene
     */
    public static final int BAREBACK = 20;

    public static final String STAR_UNKNOWN = "Unknown";
    public static final String RECORD_UNKNOWN = "Unknown";

    public static final String STAR_MODE_ALL = "star_all";
    public static final String STAR_MODE_TOP = "star_top";
    public static final String STAR_MODE_BOTTOM = "star_bottom";
    public static final String STAR_MODE_HALF = "star_half";

    public static final String RECORD_NR = "NR";
    public static final int RECORD_HD_NR = 0;

    public static final String STAR_3W_FLAG_TOP = "top";
    public static final String STAR_3W_FLAG_BOTTOM = "bottom";
    public static final String STAR_3W_FLAG_MIX = "mix";

    public static final int DEPRECATED = 1;

    public static String getTextForType(int type) {
        switch (type) {
            case VALUE_RELATION_TOP:
                return STAR_3W_FLAG_TOP;
            case VALUE_RELATION_BOTTOM:
                return STAR_3W_FLAG_BOTTOM;
            case VALUE_RELATION_MIX:
                return STAR_3W_FLAG_MIX;
        }
        return null;
    }
}
