package com.king.app.coolg.phone.star;

import android.view.View;

import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.HeaderFooterBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordItemListBinding;
import com.king.app.coolg.databinding.AdapterStarPhoneFooterBinding;
import com.king.app.coolg.databinding.AdapterStarPhoneHeaderBinding;
import com.king.app.coolg.phone.record.list.RecordItemBinder;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.phone.star.list.StarHeader;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.gdb.data.entity.Star;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 16:34
 */
public class StarAdapter extends HeaderFooterBindingAdapter<AdapterStarPhoneHeaderBinding, AdapterStarPhoneFooterBinding, AdapterRecordItemListBinding, RecordProxy> {

    private RequestOptions recordOptions;

    private OnListListener onListListener;
    private OnHeadActionListener onHeadActionListener;

    private List<String> starImageList;

    private StarHeader header;

    private RecordItemBinder recordBinder;

    private int mSortMode;

    private Star star;

    public StarAdapter() {
        recordOptions = GlideUtil.getRecordSmallOptions();
        header = new StarHeader();
        recordBinder = new RecordItemBinder();
    }

    public void setSortMode(int mSortMode) {
        this.mSortMode = mSortMode;
    }

    public void setOnListListener(OnListListener onListListener) {
        this.onListListener = onListListener;
    }

    public void setOnHeadActionListener(OnHeadActionListener onHeadActionListener) {
        this.onHeadActionListener = onHeadActionListener;
    }

    public void setStarImageList(List<String> starImageList) {
        this.starImageList = starImageList;
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
        header.bind(binding, star, starImageList, list == null ? 0:list.size());
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

    public interface OnListListener {
        void onClickItem(View view, RecordProxy record);
    }

    public interface OnHeadActionListener {
        void onClickStars();
        void onClickRecords();
        void onClickOrders();
    }

    private View.OnClickListener itemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (onListListener != null) {
                int position = (int) view.getTag();
                onListListener.onClickItem(view, list.get(position));
            }
        }
    };

}
