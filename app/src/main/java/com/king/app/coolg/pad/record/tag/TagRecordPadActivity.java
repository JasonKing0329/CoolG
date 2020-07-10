package com.king.app.coolg.pad.record.tag;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseTagAdapter;
import com.king.app.coolg.databinding.ActivityRecordTagPadBinding;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.record.tag.AbsTagRecordActivity;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/7/10 11:24
 */
@Route("TagRecordPad")
public class TagRecordPadActivity extends AbsTagRecordActivity<ActivityRecordTagPadBinding> {

    private BaseTagAdapter<Tag> tagAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_tag_pad;
    }

    @Override
    protected void initView() {
        super.initActionBar(mBinding.actionbar);

        mBinding.rvRecords.setLayoutManager(new GridLayoutManager(this, 4));
        mBinding.rvRecords.setEnableLoadMore(true);
        mBinding.rvRecords.setOnLoadMoreListener(() -> mModel.loadMoreRecords(null));

        tagAdapter = new BaseTagAdapter<Tag>() {
            @Override
            protected String getText(Tag data) {
                return data.getName();
            }

            @Override
            protected long getId(Tag data) {
                return data.getId() == null ? 0:data.getId();
            }

            @Override
            protected boolean isDisabled(Tag item) {
                return false;
            }
        };
        tagAdapter.setOnItemSelectListener(new BaseTagAdapter.OnItemSelectListener<Tag>() {
            @Override
            public void onSelectItem(Tag data) {
                mModel.loadTagRecords(data.getId());
            }

            @Override
            public void onUnSelectItem(Tag tag) {

            }
        });
        tagAdapter.setLayoutResource(R.layout.adapter_star_tag_item_pad);

        mBinding.fabTop.setOnClickListener(v -> mBinding.rvRecords.scrollToPosition(0));
    }

    @Override
    protected RecyclerView getRecordRecyclerView() {
        return mBinding.rvRecords;
    }

    @Override
    protected void showTags(List<Tag> tags) {
        tagAdapter.setData(tags);
        tagAdapter.bindFlowLayout(mBinding.flowTags);
    }

    @Override
    protected void focusOnTag(Integer position) {
        tagAdapter.setSelection(position);
    }

    @Override
    protected void goToClassicPage() {
        Router.build("RecordListPad")
                .go(this);
        finish();
    }

    @Override
    protected void goToRecordPage(Record data) {
        Router.build("RecordPad")
                .with(RecordActivity.EXTRA_RECORD_ID, data.getId())
                .go(this);
    }

    @Override
    protected void addToPlayOrder(Record record) {
        mModel.saveRecordToPlayOrder(record);
        Router.build("PlayOrder")
                .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                .requestCode(REQUEST_VIDEO_ORDER)
                .go(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_ORDER) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(PlayOrderActivity.RESP_SELECT_RESULT);
                mModel.addToPlay(list);
            }
        }
    }
}
