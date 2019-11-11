package com.king.app.coolg.phone.video.home;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.HeaderFooterBindingAdapter;
import com.king.app.coolg.databinding.AdapterFooterMoreBinding;
import com.king.app.coolg.databinding.AdapterVideoHeadBinding;
import com.king.app.coolg.databinding.AdapterVideoHomeItemBinding;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.coolg.view.widget.video.OnPlayEmptyUrlListener;
import com.king.app.gdb.data.entity.Record;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 16:20
 */
public class HomeAdapter extends HeaderFooterBindingAdapter<AdapterVideoHeadBinding, AdapterFooterMoreBinding, AdapterVideoHomeItemBinding, PlayItemViewBean> {

    private OnListListener onListListener;

    private OnHeadActionListener onHeadActionListener;

    private VideoHeadData headData;

    private OnPlayEmptyUrlListener onPlayEmptyUrlListener;

    private SimpleDateFormat dateFormat;

    public HomeAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void setOnListListener(OnListListener onListListener) {
        this.onListListener = onListListener;
    }

    public void setOnHeadActionListener(OnHeadActionListener onHeadActionListener) {
        this.onHeadActionListener = onHeadActionListener;
    }

    public void setOnPlayEmptyUrlListener(OnPlayEmptyUrlListener onPlayEmptyUrlListener) {
        this.onPlayEmptyUrlListener = onPlayEmptyUrlListener;
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
        binding.tvServer.setOnClickListener(v -> onHeadActionListener.onServer());
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
    protected void onBindItem(AdapterVideoHomeItemBinding binding, int position, PlayItemViewBean bean) {
        binding.setBean(bean);
        binding.videoView.setFingerprint(position);
        binding.videoView.getCoverView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideApp.with(binding.videoView.getContext())
                .load(bean.getCover())
                .error(R.drawable.def_small)
                .into(binding.videoView.getCoverView());
        if (!TextUtils.isEmpty(bean.getPlayUrl())) {
            binding.videoView.setVideoPath(bean.getPlayUrl());
            binding.videoView.prepare();
        }
        binding.videoView.setOnPlayEmptyUrlListener(onPlayEmptyUrlListener);
        binding.ivAdd.setOnClickListener(v -> onListListener.onAddToVideoOrder(bean));
        binding.tvName.setOnClickListener(v -> onListListener.onClickItem(position, bean));

        // 第一个位置以及与上一个位置日期不同的，显示日期
        if (position == 0 || isNotSameDay(bean.getRecord(), list.get(position - 1).getRecord())) {
            binding.tvDate.setVisibility(View.VISIBLE);
            binding.tvDate.setText(dateFormat.format(new Date(bean.getRecord().getLastModifyTime())));
        }
        else {
            binding.tvDate.setVisibility(View.GONE);
        }

    }

    private boolean isNotSameDay(Record curRecord, Record lastRecord) {
        String curDay = dateFormat.format(new Date(curRecord.getLastModifyTime()));
        String lastDay = dateFormat.format(new Date(lastRecord.getLastModifyTime()));
        return !curDay.equals(lastDay);
    }

    public interface OnListListener {
        void onLoadMore();
        void onClickItem(int position, PlayItemViewBean bean);
        void onAddToVideoOrder(PlayItemViewBean bean);
    }

    public interface OnHeadActionListener {
        void onServer();
        void onSetPlayList();
        void onPlayList();
        void onClickPlayList(VideoPlayList order);
        void onRefreshGuy();
        void onGuy();
        void onClickGuy(VideoGuy guy);
    }

}
