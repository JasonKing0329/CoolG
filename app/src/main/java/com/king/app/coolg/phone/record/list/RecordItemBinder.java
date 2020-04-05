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

public class RecordItemBinder extends BaseItemBinder {

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

        if (PreferenceValue.GDB_SR_ORDERBY_DATE == mSortMode) {
            binding.tvDate.setVisibility(View.INVISIBLE);
        }
        try {
            showSortScore(binding.tvSort, bean, mSortMode);
        } catch (Exception e) {}

        bindImage(binding.ivImage, item);

        if (selectionMode) {
            binding.cbCheck.setVisibility(View.VISIBLE);
            binding.cbCheck.setChecked(mCheckMap.get(item.getRecord().getId()) == null ? false:true);
        }
        else {
            binding.cbCheck.setVisibility(View.GONE);
        }
    }

}
