package com.king.app.coolg.pad.star.tag;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseTagAdapter;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityStarTagPadBinding;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.star.tag.AbsTagStarActivity;
import com.king.app.gdb.data.entity.Tag;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/7/6 16:48
 */
@Route("TagStarPad")
public class TagStarPadActivity extends AbsTagStarActivity<ActivityStarTagPadBinding> {

    private BaseTagAdapter<Tag> tagAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_star_tag_pad;
    }

    @Override
    protected void initView() {
        super.initActionBar(mBinding.actionbar);

//        defineStarList(mBinding.rvStars, AppConstants.TAG_STAR_GRID, 4);// grid type
        defineStarList(mBinding.rvStars, AppConstants.TAG_STAR_STAGGER, 3);// stagger type

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
                mModel.loadTagStars(data.getId());
            }

            @Override
            public void onUnSelectItem(Tag tag) {

            }
        });
        tagAdapter.setLayoutResource(R.layout.adapter_star_tag_item_pad);

        mBinding.fabTop.setOnClickListener(v -> mBinding.rvStars.scrollToPosition(0));
    }

    @Override
    protected RecyclerView getStarRecyclerView() {
        return mBinding.rvStars;
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
        Router.build("StarListPad")
                .go(this);
        finish();
    }

    @Override
    protected void goToStarPage(long starId) {
        Router.build("StarPad")
                .with(StarActivity.EXTRA_STAR_ID, starId)
                .go(this);
    }

}
