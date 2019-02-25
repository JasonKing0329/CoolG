package com.king.app.coolg.phone.video.home;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.HeaderFooterBindingAdapter;
import com.king.app.coolg.databinding.AdapterFooterMoreBinding;
import com.king.app.coolg.databinding.AdapterVideoHeadBinding;
import com.king.app.coolg.databinding.AdapterVideoHomeItemBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 16:20
 */
public class HomeAdapter extends HeaderFooterBindingAdapter<AdapterVideoHeadBinding, AdapterFooterMoreBinding, AdapterVideoHomeItemBinding, VideoItem> {

    private OnListListener onListListener;

    private OnHeadActionListener onHeadActionListener;

    private VideoHeadData headData;

    public void setOnListListener(OnListListener onListListener) {
        this.onListListener = onListListener;
    }

    public void setOnHeadActionListener(OnHeadActionListener onHeadActionListener) {
        this.onHeadActionListener = onHeadActionListener;
    }

    public void setHeadData(VideoHeadData headData) {
        this.headData = headData;
    }

    @Override
    protected int getHeaderRes() {
        return R.layout.adapter_video_head;
    }

    @Override
    protected int getFooterRes() {
        return R.layout.adapter_footer_more;
    }

    @Override
    protected int getItemRes() {
        return R.layout.adapter_video_home_item;
    }

    @Override
    protected void onBindHead(AdapterVideoHeadBinding binding) {
        binding.setData(headData);
        binding.ivRefreshGuys.setOnClickListener(v -> onHeadActionListener.onRefreshGuy());
        binding.tvGuys.setOnClickListener(v -> onHeadActionListener.onGuy());
        binding.ivStar0.setOnClickListener(v -> onHeadActionListener.onClickGuy(headData.getGuy(0)));
        binding.ivStar1.setOnClickListener(v -> onHeadActionListener.onClickGuy(headData.getGuy(1)));
        binding.ivStar2.setOnClickListener(v -> onHeadActionListener.onClickGuy(headData.getGuy(2)));
        binding.ivStar3.setOnClickListener(v -> onHeadActionListener.onClickGuy(headData.getGuy(3)));
        binding.ivSetPlayList.setOnClickListener(v -> onHeadActionListener.onSetPlayList());
        binding.tvPlayList.setOnClickListener(v -> onHeadActionListener.onPlayList());
        binding.ivList0.setOnClickListener(v -> onHeadActionListener.onClickPlayList(headData.getPlayList(0)));
        binding.ivList1.setOnClickListener(v -> onHeadActionListener.onClickPlayList(headData.getPlayList(1)));
        binding.ivList2.setOnClickListener(v -> onHeadActionListener.onClickPlayList(headData.getPlayList(2)));
        binding.ivList3.setOnClickListener(v -> onHeadActionListener.onClickPlayList(headData.getPlayList(3)));
    }

    @Override
    protected void onBindFooter(AdapterFooterMoreBinding binding) {
        binding.groupMore.setOnClickListener(v -> {
            if (onListListener != null) {
                onListListener.onLoadMore();
            }
        });
    }

    @Override
    protected void onBindItem(AdapterVideoHomeItemBinding binding, int position, VideoItem item) {
        binding.setBean(item);
    }

    public interface OnListListener {
        void onLoadMore();
        void onClickItem(View view, VideoItem record);
    }

    public interface OnHeadActionListener {
        void onSetPlayList();
        void onPlayList();
        void onClickPlayList(VideoPlayList order);
        void onRefreshGuy();
        void onGuy();
        void onClickGuy(VideoGuy guy);
    }

}
