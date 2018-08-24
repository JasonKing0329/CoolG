package com.king.app.coolg.phone.record.list;

import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.databinding.AdapterRecordItemListBinding;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.utils.FormatUtil;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.param.DataConstants;

import java.util.Map;

/**
 * Created by Administrator on 2018/8/12 0012.
 */

public class RecordItemBinder {

    private RequestOptions options;

    private int mSortMode;

    private boolean selectionMode;
    private Map<Long, Boolean> mCheckMap;

    public RecordItemBinder() {
        options = GlideUtil.getRecordSmallOptions();
    }

    public void setSortMode(int mSortMode) {
        this.mSortMode = mSortMode;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setCheckMap(Map<Long, Boolean> mCheckMap) {
        this.mCheckMap = mCheckMap;
    }

    public void bind(AdapterRecordItemListBinding binding, int position, RecordProxy item) {
        Record bean = item.getRecord();
        binding.tvName.setText(bean.getName());
        if (bean.getScoreBareback() > 0) {
            binding.tvName.setTextColor(binding.tvName.getResources().getColor(R.color.gdb_record_text_bareback_light));
        }
        else {
            binding.tvName.setTextColor(binding.tvName.getResources().getColor(R.color.gdb_record_text_normal_light));
        }
        binding.tvPath.setText(bean.getDirectory());
        binding.tvDate.setText(FormatUtil.formatDate(bean.getLastModifyTime()));
        binding.tvDeprecated.setVisibility(bean.getDeprecated() == 1 ? View.VISIBLE:View.GONE);
        binding.tvSeq.setText(String.valueOf(position + 1));
        if (TextUtils.isEmpty(bean.getSpecialDesc())) {
            binding.tvSpecial.setVisibility(View.GONE);
        }
        else {
            binding.tvSpecial.setVisibility(View.VISIBLE);
            binding.tvSpecial.setText(bean.getSpecialDesc());
        }
        binding.tvScene.setText(bean.getScene());

        try {
            showSortScore(binding, bean, mSortMode);
        } catch (Exception e) {}

        Glide.with(binding.ivImage.getContext())
                .load(item.getImagePath())
                .apply(options)
                .into(binding.ivImage);

        if (selectionMode) {
            binding.cbCheck.setVisibility(View.VISIBLE);
            binding.cbCheck.setChecked(mCheckMap.get(item.getRecord().getId()) == null ? false:true);
        }
        else {
            binding.cbCheck.setVisibility(View.GONE);
        }
    }

    private void showSortScore(AdapterRecordItemListBinding binding, Record item, int mSortMode) {
        switch (mSortMode) {
            case PreferenceValue.GDB_SR_ORDERBY_DATE:
                binding.tvSort.setVisibility(View.VISIBLE);
                binding.tvSort.setText(FormatUtil.formatDate(item.getLastModifyTime()));
                binding.tvDate.setVisibility(View.INVISIBLE);
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
            default:
                binding.tvSort.setText(String.valueOf(item.getScore()));
                break;
        }
    }

}