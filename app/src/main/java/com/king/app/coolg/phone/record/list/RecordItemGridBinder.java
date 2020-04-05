package com.king.app.coolg.phone.record.list;

import android.view.View;

import com.king.app.coolg.databinding.AdapterRecordItemGridBinding;
import com.king.app.coolg.model.VideoModel;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;

import java.util.List;

/**
 * Created by Administrator on 2018/8/12 0012.
 */

public class RecordItemGridBinder extends BaseItemBinder {

    private OnPopupListener popupListener;

    public void setPopupListener(OnPopupListener popupListener) {
        this.popupListener = popupListener;
    }

    public void bind(AdapterRecordItemGridBinding binding, int position, RecordProxy item) {

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
                starBuffer.append(", ").append(star.getName());
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

        binding.tvPics.setText("(" + ImageProvider.getRecordPicNumber(item.getRecord().getName()) + " pics)");

        bindImage(binding.ivRecord, item);

        showSortScore(binding.tvSort, item.getRecord(), mSortMode);
    }

    public interface OnPopupListener {
        void onPopupRecord(View view, int position, Record record);
    }
}
