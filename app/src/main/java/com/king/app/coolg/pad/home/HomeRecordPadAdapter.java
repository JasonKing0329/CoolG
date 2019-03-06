package com.king.app.coolg.pad.home;

import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.base.adapter.HeadChildBindingAdapter;
import com.king.app.coolg.databinding.AdapterHomeRecordHeadPadBinding;
import com.king.app.coolg.databinding.AdapterHomeRecordListBinding;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 9:46
 */
public class HomeRecordPadAdapter extends HeadChildBindingAdapter<AdapterHomeRecordHeadPadBinding, AdapterHomeRecordListBinding
        , String, Record> {

    private RequestOptions recordOptions;
    private OnListListener onListListener;

    public HomeRecordPadAdapter() {
        recordOptions = GlideUtil.getRecordOptions();
    }

    public void setOnListListener(OnListListener onListListener) {
        this.onListListener = onListListener;
    }

    @Override
    protected Class getItemClass() {
        return Record.class;
    }

    @Override
    protected int getHeaderRes() {
        return R.layout.adapter_home_record_head_pad;
    }

    @Override
    protected int getItemRes() {
        return R.layout.adapter_home_record_list;
    }

    @Override
    protected void onBindHead(AdapterHomeRecordHeadPadBinding binding, int position, String head) {
        binding.tvHead.setText(head);
    }

    @Override
    protected void onBindItem(AdapterHomeRecordListBinding binding, int position, Record record) {

        binding.groupItem.setTag(position);
        binding.groupItem.setOnClickListener(v -> {
            if (onListListener != null) {
                onListListener.onClickItem(position, record);
            }
        });

        Glide.with(CoolApplication.getInstance())
                .load(ImageProvider.getRecordRandomPath(record.getName(), null))
                .apply(recordOptions)
                .into(binding.ivRecordImage);

        List<Star> starList = record.getStarList();
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
        binding.tvRecordStar.setText(starText);

        binding.tvRecordDate.setVisibility(View.GONE);

        // deprecated item
        if (record.getDeprecated() == 1) {
            binding.tvDeprecated.setVisibility(View.VISIBLE);
        }
        else {
            binding.tvDeprecated.setVisibility(View.GONE);
        }

        binding.ivPlay.setVisibility(View.GONE);
    }

    public interface OnListListener {
        void onClickItem(int position, Record record);
    }

    public int getSpanSize(int position) {
        if (list.get(position) instanceof Record) {
            return 1;
        }
        else {
            return 2;
        }
    }

}
