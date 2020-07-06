package com.king.app.coolg.phone.star.tag;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.databinding.ActivityStarTagBinding;
import com.king.app.coolg.phone.record.TagAdapter;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.gdb.data.entity.Tag;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/6/29 14:04
 */
@Route("TagStar")
public class TagStarActivity extends AbsTagStarActivity<ActivityStarTagBinding> {

    private TagAdapter tagAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_star_tag;
    }

    @Override
    protected TagStarViewModel createViewModel() {
        return generateViewModel(TagStarViewModel.class);
    }

    @Override
    protected void initView() {

        super.initActionBar(mBinding.actionbar);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        mBinding.rvTags.setLayoutManager(manager);
        mBinding.rvTags.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(10);
                outRect.top = ScreenUtils.dp2px(5);
                outRect.bottom = ScreenUtils.dp2px(5);
            }
        });

        mBinding.rvStars.setLayoutManager(new GridLayoutManager(this, 2));

        mBinding.fabTop.setOnClickListener(v -> mBinding.rvStars.scrollToPosition(0));
    }

    @Override
    protected RecyclerView getStarRecyclerView() {
        return mBinding.rvStars;
    }

    @Override
    protected void focusOnTag(Integer position) {
        tagAdapter.setSelection(position);
        tagAdapter.notifyDataSetChanged();
    }

    @Override
    protected void showTags(List<Tag> tags) {
        if (tagAdapter == null) {
            tagAdapter = new TagAdapter();
            tagAdapter.setList(tags);
            tagAdapter.setSelection(0);
            tagAdapter.setOnItemClickListener((view, position, data) -> mModel.loadTagStars(data.getId()));
            mBinding.rvTags.setAdapter(tagAdapter);
        }
        else {
            tagAdapter.setList(tags);
            tagAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void goToClassicPage() {
        Router.build("StarListPhone")
                .go(this);
    }

    @Override
    protected void goToStarPage(long starId) {
        Router.build("StarPhone")
                .with(StarActivity.EXTRA_STAR_ID, starId)
                .go(this);
    }

}
