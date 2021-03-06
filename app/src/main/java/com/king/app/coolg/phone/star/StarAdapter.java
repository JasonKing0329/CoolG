package com.king.app.coolg.phone.star;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.HeaderFooterBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordItemListBinding;
import com.king.app.coolg.databinding.AdapterStarPhoneFooterBinding;
import com.king.app.coolg.databinding.AdapterStarPhoneHeaderBinding;
import com.king.app.coolg.phone.record.list.RecordItemBinder;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.Tag;
import com.king.lib.banner.CoolBanner;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 16:34
 */
public class StarAdapter extends HeaderFooterBindingAdapter<AdapterStarPhoneHeaderBinding, AdapterStarPhoneFooterBinding, AdapterRecordItemListBinding, RecordProxy> {

    private OnListListener onListListener;
    private StarHeader.OnHeadActionListener onHeadActionListener;

    private StarHeader header;

    private RecordItemBinder recordBinder;

    private int mSortMode;

    private Star star;
    private List<StarRelationship> mRelationships;
    private List<StarStudioTag> mStudioList;
    private List<Tag> mTagList;

    public StarAdapter() {
        header = new StarHeader();
        recordBinder = new RecordItemBinder();
    }

    public void setSortMode(int mSortMode) {
        this.mSortMode = mSortMode;
    }

    public void setOnListListener(OnListListener onListListener) {
        this.onListListener = onListListener;
    }

    public void setOnHeadActionListener(StarHeader.OnHeadActionListener onHeadActionListener) {
        this.onHeadActionListener = onHeadActionListener;
    }

    public void setStar(Star star) {
        this.star = star;
    }

    @Override
    protected int getHeaderRes() {
        return R.layout.adapter_star_phone_header;
    }

    @Override
    protected int getFooterRes() {
        return R.layout.adapter_star_phone_footer;
    }

    @Override
    protected int getItemRes() {
        return R.layout.adapter_record_item_list;
    }

    @Override
    protected void onBindHead(AdapterStarPhoneHeaderBinding binding) {
        header.setOnHeadActionListener(onHeadActionListener);
        header.bind(binding, star, mRelationships, mStudioList, mTagList);
    }

    @Override
    protected void onBindFooter(AdapterStarPhoneFooterBinding binding) {

    }

    @Override
    protected void onBindItem(AdapterRecordItemListBinding binding, int position, RecordProxy record) {
        recordBinder.setSortMode(mSortMode);
        recordBinder.bind(binding, position, record);
        binding.getRoot().setOnClickListener(view -> {
            if (onListListener != null) {
                onListListener.onClickItem(view, record);
            }
        });
    }

    public void setRelationships(List<StarRelationship> relationships) {
        this.mRelationships = relationships;
    }

    public void setStudioList(List<StarStudioTag> studioList) {
        this.mStudioList = studioList;
    }

    public void setTagList(List<Tag> mTagList) {
        this.mTagList = mTagList;
    }

    public void onResume() {
    }

    public void onStop() {
    }

    public interface OnListListener {
        void onClickItem(View view, RecordProxy record);
    }

}
