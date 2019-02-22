package com.king.app.coolg.phone.video.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.view.widget.video.EmbedVideoView;
import com.king.app.gdb.data.entity.Record;

/**
 * LMBanner并没有完全像BaseAdapter那样设计结构
 * 也无法采用ViewHolder缓存机制，本处只用了3个item，直接这样也就行了
 */
public class RecommendAdapter implements LBaseAdapter<Record> {

    private EmbedVideoView videoView;
    private TextView tvName;

    public OnItemListener onItemListener;

    public RecommendAdapter() {

    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    @Override
    public View getView(LMBanners lBanners, Context context, int position, Record data) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_video_recommend_item, null);
        videoView = view.findViewById(R.id.video_view);
        tvName = view.findViewById(R.id.tv_name);
        // 采用随机生成模式
        data = onItemListener.getNewItem();
        // 没有匹配的记录
        if (data == null) {
            return view;
        }
        // TODO set video url and cover image

        Record record = data;
        tvName.setOnClickListener(v -> onItemListener.onClickItem(record));
        return view;
    }

    public interface OnItemListener {
        Record getNewItem();
        void onClickItem(Record record);
    }
}
