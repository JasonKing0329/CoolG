package com.king.app.coolg.phone.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.gdb.data.entity.Record;
import com.king.lib.banner.CoolBannerAdapter;

/**
 *
 */
public class RecommendAdapter extends CoolBannerAdapter<Record> {

    public OnItemListener onItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.adapter_recommend_item;
    }

    @Override
    protected void onBindView(View view, int position, Record data) {
        ImageView imageView = view.findViewById(R.id.iv_image);
        TextView starView = view.findViewById(R.id.tv_name);

        // 采用随机生成模式
        data = onItemListener.getNewItem();
        // 没有匹配的记录
        if (data == null) {
            return;
        }

        GlideApp.with(imageView.getContext())
                .load(ImageProvider.getRecordRandomPath(data.getName(), null))
                .error(R.drawable.def_small)
                .into(imageView);

        starView.setText(data.getName());

        Record record = data;
        imageView.setOnClickListener(v -> onItemListener.onClickItem(record));
    }

    public interface OnItemListener {
        Record getNewItem();
        void onClickItem(Record record);
    }
}
