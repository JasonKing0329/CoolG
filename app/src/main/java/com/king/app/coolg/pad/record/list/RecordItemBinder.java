package com.king.app.coolg.pad.record.list;

import android.view.View;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.databinding.AdapterRecordItemGridPadBinding;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.VideoModel;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.utils.FormatUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/12 0012.
 */

public class RecordItemBinder {

    private int mSortMode;

    private boolean selectionMode;
    private Map<Long, Boolean> mCheckMap;
    private OnPopupListener popupListener;

    public void setSortMode(int mSortMode) {
        this.mSortMode = mSortMode;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setCheckMap(Map<Long, Boolean> mCheckMap) {
        this.mCheckMap = mCheckMap;
    }

    public void setPopupListener(OnPopupListener popupListener) {
        this.popupListener = popupListener;
    }

    public void bind(AdapterRecordItemGridPadBinding binding, int position, RecordProxy item) {

        if (selectionMode) {
            binding.tvSeq.setVisibility(View.GONE);
            binding.cbCheck.setVisibility(View.VISIBLE);
            binding.cbCheck.setChecked(mCheckMap.get(item.getRecord().getId()) == null ? false:true);
        }
        else {
            binding.tvSeq.setVisibility(View.VISIBLE);
            binding.cbCheck.setVisibility(View.GONE);

            binding.tvSeq.setText(String.valueOf(position + 1));

            binding.ivEdit.setOnClickListener(v -> {
                if (popupListener != null) {
                    popupListener.onPopupRecord(v, position, item.getRecord());
                }
            });
        }

        List<Star> starList = item.getRecord().getStarList();
        StringBuffer starBuffer = new StringBuffer();
        if (!ListUtil.isEmpty(starList)) {
            for (Star star:starList) {
                starBuffer.append("&").append(star.getName());
            }
        }
        String starText = starBuffer.toString();
        if (starText.length() > 1) {
            starText = starText.substring(1);
        }
        binding.tvStars.setText(starText);

        // can be played in device
        if (VideoModel.getVideoPath(item.getRecord().getName()) == null) {
            binding.ivPlay.setVisibility(View.INVISIBLE);
        }
        else {
            binding.ivPlay.setVisibility(View.VISIBLE);
        }

        GlideApp.with(binding.ivImage.getContext())
                .load(item.getImagePath())
                .error(R.drawable.def_small)
                .into(binding.ivImage);

        binding.tvPics.setText("(" + ImageProvider.getRecordPicNumber(item.getRecord().getName()) + " pics)");

        showSortScore(binding, item.getRecord(), mSortMode);
    }

    private void showSortScore(AdapterRecordItemGridPadBinding binding, Record item, int mSortMode) {
        switch (mSortMode) {
            case PreferenceValue.GDB_SR_ORDERBY_DATE:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText(FormatUtil.formatDate(item.getLastModifyTime()));
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BAREBACK:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText("" + item.getScoreBareback());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BJOB:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreBjob());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                    binding.tvSort.setText("" + item.getRecordType3w().getScoreBjob());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_CSHOW:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreCshow());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                    binding.tvSort.setText("" + item.getRecordType3w().getScoreCshow());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_CUM:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText("" + item.getScoreCum());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_PASSION:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText("" + item.getScorePassion());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK1:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreFkType1());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK2:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreFkType2());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK3:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreFkType3());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK4:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreFkType4());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK5:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreFkType5());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FK6:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreFkType6());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_FOREPLAY:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreForePlay());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                    binding.tvSort.setText("" + item.getRecordType3w().getScoreForePlay());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_HD:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText("" + item.getHdLevel());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_RHYTHM:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreRhythm());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                    binding.tvSort.setText("" + item.getRecordType3w().getScoreRhythm());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_RIM:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreRim());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                    binding.tvSort.setText("" + item.getRecordType3w().getScoreRim());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCENE:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreScene());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                    binding.tvSort.setText("" + item.getRecordType3w().getScoreScene());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText("" + item.getScoreFeel());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SPECIAL:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText("" + item.getScoreSpecial());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR1:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR2:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARCC1:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARCC2:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STORY:
                binding.tvSort.setVisibility(View.VISIBLE);
                if (item.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
                    binding.tvSort.setText("" + item.getRecordType1v1().getScoreStory());
                }
                else if (item.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI
                        || item.getType() == DataConstants.VALUE_RECORD_TYPE_LONG) {
                    binding.tvSort.setText("" + item.getRecordType3w().getScoreStory());
                }
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE_BASIC:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_SCORE_EXTRA:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STAR:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText("" + item.getScoreStar());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_STARC:
                break;
            case PreferenceValue.GDB_SR_ORDERBY_BODY:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText("" + item.getScoreBody());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_COCK:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText("" + item.getScoreCock());
                break;
            case PreferenceValue.GDB_SR_ORDERBY_ASS:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText("" + item.getScoreAss());
                break;
            default:
                binding.tvSort.setText(String.valueOf(item.getScore()));
                break;
        }
    }

    public interface OnPopupListener {
        void onPopupRecord(View view, int position, Record record);
    }
}
