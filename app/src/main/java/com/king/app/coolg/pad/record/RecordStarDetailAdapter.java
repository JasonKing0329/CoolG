package com.king.app.coolg.pad.record;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordStarDetailPadBinding;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.param.DataConstants;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/22 13:34
 */
public class RecordStarDetailAdapter extends BaseBindingAdapter<AdapterRecordStarDetailPadBinding, RecordStar> {
    
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_star_detail_pad;
    }

    @Override
    protected void onBindItem(AdapterRecordStarDetailPadBinding binding, int position, RecordStar star) {

        binding.tvName.setText(star.getStar().getName());
        StringBuffer buffer = new StringBuffer();
        buffer.append(DataConstants.getTextForType(star.getType()));
        if (star.getScore() != 0 || star.getScoreC() != 0) {
            buffer.append("(").append(star.getScore()).append("/").append(star.getScoreC()).append(")");
        }
        binding.tvFlag.setText(buffer.toString());
        GlideApp.with(binding.ivStar.getContext())
                .load(ImageProvider.getStarRandomPath(star.getStar().getName(), null))
                .error(R.drawable.def_person)
                .into(binding.ivStar);
    }
}
