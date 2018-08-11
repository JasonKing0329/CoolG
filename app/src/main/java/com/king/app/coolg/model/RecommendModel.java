package com.king.app.coolg.model;

import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.bean.RecordFilterBean;
import com.king.app.coolg.model.bean.RecordFilterModel;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.param.DataConstants;

/**
 * @desc
 * @auth 景阳
 * @time 2018/2/19 0019 14:25
 */

public class RecommendModel {

    /**
     * 检查是否通过过滤项
     * @param record
     * @param filterModel
     * @return
     */
    public boolean checkItem(Record record, RecordFilterModel filterModel) {
        boolean result = true;
        for (int i = 0; i < filterModel.getList().size(); i ++) {
            result = checkPassFilterItem(record, filterModel.getList().get(i));
            result = result && result;
            if (!result) {
                break;
            }
        }
        return result;
    }

    /**
     * 检查是否通过过滤项
     * @param record
     * @param filterBean
     * @return
     */
    private boolean checkPassFilterItem(Record record, RecordFilterBean filterBean) {
        int min = filterBean.getMin();
        int max = filterBean.getMax();
        // 只设置了min，没有设置max
        if (filterBean.getMax() == 0) {
            max = Integer.MAX_VALUE;
        }

        // 如果没勾上，直接返回符合条件
        if (!filterBean.isEnable()
                // 也没有设置条件
                || min == 0 && max == Integer.MAX_VALUE
                // min 大于了 max是不合理情况，也视为符合条件
                || min > max) {
            return true;
        }

        int targetScore = getTargetScore(record, filterBean.getKeyword());
        DebugLog.e("targetScore = " + targetScore);
        return targetScore >= min && targetScore <= max;
    }

    /**
     * 根据过滤器的keyword判断score对应的record参数
     *
     * @param record
     * @param keyword
     * @return
     */
    private int getTargetScore(Record record, String keyword) {
        if (keyword.equals(AppConstants.FILTER_KEY_SCORE)) {
            return record.getScore();
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_STORY)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreStory();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreStory();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                return record.getRecordType3w().getScoreStory();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                return record.getRecordType3w().getScoreStory();
            }
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_DEPRECATED)) {
            return record.getDeprecated();
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_CUM)) {
            return record.getScoreCum();
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_PASSION)) {
            return record.getScorePassion();
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_STAR)) {
            return record.getScoreStar();
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_BAREBACK)) {
            return record.getScoreBareback();
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_BJOB)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreBjob();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreBjob();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                return record.getRecordType3w().getScoreBjob();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                return record.getRecordType3w().getScoreBjob();
            }
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_RHYTHM)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreRhythm();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreRhythm();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                return record.getRecordType3w().getScoreRhythm();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                return record.getRecordType3w().getScoreRhythm();
            }
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_RIM)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreRim();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreRim();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                return record.getRecordType3w().getScoreRim();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                return record.getRecordType3w().getScoreRim();
            }
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_SCECE)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreScene();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreScene();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                return record.getRecordType3w().getScoreScene();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                return record.getRecordType3w().getScoreScene();
            }
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_CSHOW)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreCshow();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreCshow();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                return record.getRecordType3w().getScoreCshow();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                return record.getRecordType3w().getScoreCshow();
            }
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_SPECIAL)) {
            return record.getScoreSpecial();
        }
        else if (keyword.equals(AppConstants.FILTER_KEY_SCORE_FOREPLAY)) {
            if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                return record.getRecordType1v1().getScoreForePlay();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
                return record.getRecordType3w().getScoreForePlay();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
                return record.getRecordType3w().getScoreForePlay();
            }
            else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                return record.getRecordType3w().getScoreForePlay();
            }
        }
        return 0;
    }
}
