package com.king.app.coolg.model;

import com.google.gson.Gson;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.bean.RecordFilterBean;
import com.king.app.coolg.model.bean.RecordFilterModel;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.gdb.data.ISortRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/3 0003.
 * the filter handle process
 */

public class FilterHelper {

    public RecordFilterModel getFilters() {
        String json = SettingProperty.getRecordFilterModel();
        DebugLog.e(json);
        Gson gson = new Gson();
        try {
            RecordFilterModel modle = gson.fromJson(json, RecordFilterModel.class);
            if (modle == null) {
                return createFilters();
            }
            return modle;
        } catch (Exception e) {
            e.printStackTrace();
            return createFilters();
        }
    }
    public void saveFilters(RecordFilterModel model) {
        Gson gson = new Gson();
        String data = gson.toJson(model);
        DebugLog.e(data);
        SettingProperty.setRecordFilterModel(data);
    }

    /**
     * 重新创建默认filter
     * @return
     */
    public RecordFilterModel createFilters() {
        String[] keys = new String[] {
                AppConstants.FILTER_KEY_SCORE,
                AppConstants.FILTER_KEY_SCORE_CUM, AppConstants.FILTER_KEY_SCORE_PASSION,
                AppConstants.FILTER_KEY_SCORE_STAR,
                AppConstants.FILTER_KEY_SCORE_BJOB, AppConstants.FILTER_KEY_SCORE_BAREBACK,
                AppConstants.FILTER_KEY_SCORE_STORY, AppConstants.FILTER_KEY_SCORE_RHYTHM,
                AppConstants.FILTER_KEY_SCORE_SCECE, AppConstants.FILTER_KEY_SCORE_RIM,
                AppConstants.FILTER_KEY_SCORE_CSHOW, AppConstants.FILTER_KEY_SCORE_SPECIAL,
                AppConstants.FILTER_KEY_SCORE_FOREPLAY, AppConstants.FILTER_KEY_SCORE_DEPRECATED
        };
        String[] keyFileds = new String[] {
                ISortRecord.COLUMN_SCORE,
                ISortRecord.COLUMN_CUM, ISortRecord.COLUMN_FK,
                ISortRecord.COLUMN_STAR,
                ISortRecord.COLUMN_BJOB, ISortRecord.COLUMN_BAREBACK,
                ISortRecord.COLUMN_STORY, ISortRecord.COLUMN_RHYTHM,
                ISortRecord.COLUMN_SCENE, ISortRecord.COLUMN_RIM,
                ISortRecord.COLUMN_CSHOW, ISortRecord.COLUMN_SPECIAL,
                ISortRecord.COLUMN_FOREPLAY, ISortRecord.COLUMN_DEPRECATED
        };
        List<RecordFilterBean> list = new ArrayList<>();
        RecordFilterModel model = new RecordFilterModel();
        model.setSupportNR(false);
        model.setList(list);
        for (int i = 0; i < keys.length; i ++) {
            RecordFilterBean bean = new RecordFilterBean();
            bean.setKeyword(keys[i]);
            bean.setKeywordFiled(keyFileds[i]);
            list.add(bean);
        }
        saveFilters(model);
        return model;
    }
}
