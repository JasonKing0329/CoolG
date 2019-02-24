package com.king.app.coolg.phone.home;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.base.adapter.HeaderFooterBindingAdapter;
import com.king.app.coolg.databinding.AdapterFooterMoreBinding;
import com.king.app.coolg.databinding.AdapterHomeHeadBinding;
import com.king.app.coolg.databinding.AdapterHomeRecordListBinding;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.RippleUtil;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 16:34
 */
public class HomeAdapter extends HeaderFooterBindingAdapter<AdapterHomeHeadBinding, AdapterFooterMoreBinding, AdapterHomeRecordListBinding, Record> {

    private RequestOptions recordOptions;
    private SimpleDateFormat dateFormat;

    private OnListListener onListListener;
    private OnHeadActionListener onHeadActionListener;
    private List<PlayItem> mPlayList;

    private HomePlayAdapter playAdapter;

    public HomeAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        recordOptions = GlideUtil.getRecordOptions();
    }

    public void setOnListListener(OnListListener onListListener) {
        this.onListListener = onListListener;
    }

    public void setOnHeadActionListener(OnHeadActionListener onHeadActionListener) {
        this.onHeadActionListener = onHeadActionListener;
    }

    @Override
    protected int getHeaderRes() {
        return R.layout.adapter_home_head;
    }

    @Override
    protected int getFooterRes() {
        return R.layout.adapter_footer_more;
    }

    @Override
    protected int getItemRes() {
        return R.layout.adapter_home_record_list;
    }

    @Override
    protected void onBindHead(AdapterHomeHeadBinding binding) {
        Drawable drawable = RippleUtil.getRippleBackground(binding.llStar.getResources().getColor(R.color.home_section_phone_star)
                , binding.llStar.getResources().getColor(R.color.ripple_color));
        binding.llStar.setBackground(drawable);
        binding.llStar.setOnClickListener(v -> {
            if (onHeadActionListener != null) {
                onHeadActionListener.onClickStars();
            }
        });
        drawable = RippleUtil.getRippleBackground(binding.llStar.getResources().getColor(R.color.home_section_phone_record)
                , binding.llStar.getResources().getColor(R.color.ripple_color));
        binding.llRecords.setBackground(drawable);
        binding.llRecords.setOnClickListener(v -> {
            if (onHeadActionListener != null) {
                onHeadActionListener.onClickRecords();
            }
        });
        drawable = RippleUtil.getRippleBackground(binding.llStar.getResources().getColor(R.color.home_section_phone_order)
                , binding.llStar.getResources().getColor(R.color.ripple_color));
        binding.llOrder.setBackground(drawable);
        binding.llOrder.setOnClickListener(v -> {
            if (onHeadActionListener != null) {
                onHeadActionListener.onClickOrders();
            }
        });
        drawable = RippleUtil.getRippleBackground(binding.llStudio.getResources().getColor(R.color.home_section_phone_studio)
                , binding.llStudio.getResources().getColor(R.color.ripple_color));
        binding.llStudio.setBackground(drawable);
        binding.llStudio.setOnClickListener(v -> {
            if (onHeadActionListener != null) {
                onHeadActionListener.onClickStudios();
            }
        });

        binding.tvPlayList.setOnClickListener(v -> onHeadActionListener.goToPlayList());

//        if (ListUtil.isEmpty(mPlayList)) {
//            binding.tvPlayList.setVisibility(View.GONE);
//            binding.rvPlayList.setVisibility(View.GONE);
//        }
//        else {
//            binding.tvPlayList.setVisibility(View.VISIBLE);
//            binding.rvPlayList.setVisibility(View.VISIBLE);
//            binding.tvPlayList.setText("Play List (" + mPlayList.size() + ")");
//            binding.tvPlayList.setOnClickListener(v -> onHeadActionListener.goToPlayList());
//            if (playAdapter == null) {
//                binding.rvPlayList.setLayoutManager(new LinearLayoutManager(binding.rvPlayList.getContext(), LinearLayoutManager.HORIZONTAL, false));
//                playAdapter = new HomePlayAdapter();
//                playAdapter.setList(mPlayList);
//                playAdapter.setOnItemClickListener((view, position, data) -> onHeadActionListener.onClickPlayItem(view, data.getRecord()));
//                binding.rvPlayList.setAdapter(playAdapter);
//            }
//            else {
//                playAdapter.setList(mPlayList);
//                playAdapter.notifyDataSetChanged();
//            }
//        }
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
    protected void onBindItem(AdapterHomeRecordListBinding binding, int position, Record record) {

        binding.groupItem.setTag(position);
        binding.groupItem.setOnClickListener(itemListener);

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

        // 第一个位置以及与上一个位置日期不同的，显示日期
        if (position == 0 || isNotSameDay(record, list.get(position - 1))) {
            binding.tvRecordDate.setVisibility(View.VISIBLE);
            binding.tvRecordDate.setText(dateFormat.format(new Date(record.getLastModifyTime())));
        }
        else {
            binding.tvRecordDate.setVisibility(View.GONE);
        }

        // deprecated item
        if (record.getDeprecated() == 1) {
            binding.tvDeprecated.setVisibility(View.VISIBLE);
        }
        else {
            binding.tvDeprecated.setVisibility(View.GONE);
        }

        if (record.getDeprecated() == DataConstants.DEPRECATED) {
            binding.ivPlay.setVisibility(View.GONE);
        }
        else {
            binding.ivPlay.setVisibility(View.VISIBLE);
            binding.ivPlay.setOnClickListener(v -> onListListener.onAddPlay(record));
        }
    }

    private boolean isNotSameDay(Record curRecord, Record lastRecord) {
        String curDay = dateFormat.format(new Date(curRecord.getLastModifyTime()));
        String lastDay = dateFormat.format(new Date(lastRecord.getLastModifyTime()));
        return !curDay.equals(lastDay);
    }

    public void updatePlayList(List<PlayItem> list) {
        this.mPlayList = list;
        // refresh head
        notifyItemChanged(0);
    }

    public interface OnListListener {
        void onLoadMore();
        void onClickItem(View view, Record record);
        void onAddPlay(Record record);
    }

    public interface OnHeadActionListener {
        void onClickStars();
        void onClickRecords();
        void onClickOrders();
        void onClickStudios();
        void onClickPlayItem(View view, Record record);
        void goToPlayList();
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
