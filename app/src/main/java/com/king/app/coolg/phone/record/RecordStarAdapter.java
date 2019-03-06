package com.king.app.coolg.phone.record;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordStarPhoneBinding;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.param.DataConstants;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/8 14:52
 */
public class RecordStarAdapter extends BaseBindingAdapter<AdapterRecordStarPhoneBinding, RecordStar> {

    private RequestOptions starOptions;

    public RecordStarAdapter() {
        starOptions = GlideUtil.getStarOptions();
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_star_phone;
    }

    @Override
    protected void onBindItem(AdapterRecordStarPhoneBinding binding, int position, RecordStar star) {

        binding.tvName.setText(star.getStar().getName());
        StringBuffer buffer = new StringBuffer();
        buffer.append(DataConstants.getTextForType(star.getType()));
        if (star.getScore() != 0 || star.getScoreC() != 0) {
            buffer.append("(").append(star.getScore()).append("/").append(star.getScoreC()).append(")");
        }
        binding.tvFlag.setText(buffer.toString());
        Glide.with(binding.ivStar.getContext())
                .load(ImageProvider.getStarRandomPath(star.getStar().getName(), null))
                .apply(starOptions)
                .into(binding.ivStar);
    }
}
