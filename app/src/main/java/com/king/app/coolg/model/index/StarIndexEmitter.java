package com.king.app.coolg.model.index;

import android.text.TextUtils;

import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.StarRatingUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableEmitter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/21 10:15
 */
public class StarIndexEmitter {

    private Map<String, IndexRange> playerIndexMap;

    public StarIndexEmitter() {
        playerIndexMap = new HashMap<>();
    }

    public void clear() {
        playerIndexMap.clear();
    }

    private IndexRange mLastRange;

    public Map<String, IndexRange> getPlayerIndexMap() {
        return playerIndexMap;
    }

    public String getIndex(int position) {
        Iterator<String> iterator = playerIndexMap.keySet().iterator();
        String index = "Unknown";
        while (iterator.hasNext()) {
            String key = iterator.next();
            IndexRange range = playerIndexMap.get(key);
            if (position >= range.start && position <= range.end) {
                index = key;
                break;
            }
        }
        return index;
    }

    private void endCreate(int lastIndex) {
        if (mLastRange != null) {
            mLastRange.end = lastIndex;
        }
    }

    private void addIndex(ObservableEmitter<String> e, String value, int i) {
        IndexRange range = playerIndexMap.get(value);
        // 新index出现
        if (range == null) {
            // 结束上一个range
            if (mLastRange != null) {
                mLastRange.end = i - 1;
            }

            range = new IndexRange();
            range.start = i;
            playerIndexMap.put(value, range);

            mLastRange = range;
            e.onNext(String.valueOf(value));
        }
    }

    public void createRecordsIndex(ObservableEmitter<String> e, List<StarProxy> mList) {
        // list查询出来已经是有序的
        for (int i = 0; i < mList.size(); i ++) {
            int number = mList.get(i).getStar().getRecordList().size();
            //1 2 3 4 5 6 7 8 9 10 12 15 20 25 30 35 40 40+
            String key;
            if (number > 40) {
                key = "40+";
            } else if (number > 35 && number <= 40) {
                key = "40";
            } else if (number > 30 && number <= 35) {
                key = "35";
            } else if (number > 25 && number <= 30) {
                key = "30";
            } else if (number > 20 && number <= 25) {
                key = "25";
            } else if (number > 15 && number <= 20) {
                key = "20";
            } else if (number > 12 && number <= 15) {
                key = "15";
            } else if (number > 10 && number <= 12) {
                key = "12";
            } else {
                key = String.valueOf(number);
            }
            addIndex(e, key, i);
        }
        endCreate(mList.size() - 1);
    }

    public void createRatingIndex(ObservableEmitter<String> e, List<StarProxy> mList, int type) {
        // list查询出来已经是有序的
        for (int i = 0; i < mList.size(); i ++) {
            String rating = StarRatingUtil.getRatingValue(getRatingValue(mList.get(i), type));
            addIndex(e, rating, i);
        }
        endCreate(mList.size() - 1);
    }

    private float getRatingValue(StarProxy proxy, int type) {
        float value = 0;
        try {
            switch (type) {
                case AppConstants.STAR_SORT_RATING:
                    value = proxy.getStar().getRatings().get(0).getComplex();
                    break;
                case AppConstants.STAR_SORT_RATING_FACE:
                    value = proxy.getStar().getRatings().get(0).getFace();
                    break;
                case AppConstants.STAR_SORT_RATING_BODY:
                    value = proxy.getStar().getRatings().get(0).getBody();
                    break;
                case AppConstants.STAR_SORT_RATING_DK:
                    value = proxy.getStar().getRatings().get(0).getDk();
                    break;
                case AppConstants.STAR_SORT_RATING_SEXUALITY:
                    value = proxy.getStar().getRatings().get(0).getSexuality();
                    break;
                case AppConstants.STAR_SORT_RATING_PASSION:
                    value = proxy.getStar().getRatings().get(0).getPassion();
                    break;
                case AppConstants.STAR_SORT_RATING_VIDEO:
                    value = proxy.getStar().getRatings().get(0).getVideo();
                    break;
            }
        } catch (Exception e) {}
        return value;
    }

    public void createNameIndex(ObservableEmitter<String> e, List<StarProxy> mList) {
        // player list查询出来已经是有序的
        for (int i = 0; i < mList.size(); i ++) {
            String targetText = mList.get(i).getStar().getName();
            String first;
            if (TextUtils.isEmpty(targetText)) {
                first = "#";
            }
            else {
                first = String.valueOf(targetText.charAt(0));
            }
            addIndex(e, first, i);
        }
        endCreate(mList.size() - 1);
    }

}
