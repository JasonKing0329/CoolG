package com.king.app.coolg.pad.record;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordStarPadBinding;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.gdb.data.entity.RecordStar;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/22 13:32
 */
public class RecordStarAdapter extends BaseBindingAdapter<AdapterRecordStarPadBinding, RecordStar> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_star_pad;
    }

    @Override
    protected void onBindItem(AdapterRecordStarPadBinding binding, int position, RecordStar star) {

        GlideApp.with(binding.ivStar.getContext())
                .load(ImageProvider.getStarRandomPath(star.getStar().getName(), null))
                .error(R.drawable.def_person)
                .into(binding.ivStar);
    }
}
