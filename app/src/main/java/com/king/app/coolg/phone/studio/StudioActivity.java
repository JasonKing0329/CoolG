package com.king.app.coolg.phone.studio;

import android.arch.lifecycle.ViewModelProviders;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityRecordStudioBinding;
import com.king.app.coolg.phone.studio.page.StudioPageFragment;
import com.king.app.jactionbar.JActionbar;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/18 11:16
 */
@Route("StudioPhone")
public class StudioActivity extends MvvmActivity<ActivityRecordStudioBinding, StudioViewModel> implements StudioHolder {

    private StudioListFragment ftList;

    private StudioPageFragment ftPage;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_studio;
    }

    @Override
    protected void initView() {
        ftList = new StudioListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_ft, ftList, "StudioListFragment")
                .commit();
    }

    @Override
    public JActionbar getJActionBar() {
        return mBinding.actionbar;
    }

    @Override
    protected StudioViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StudioViewModel.class);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void showStudioPage(long studioId) {
        ftPage = StudioPageFragment.newInstance(studioId);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_ft, ftPage, "StudioPageFragment")
                .hide(ftList)
                .commit();
    }

    @Override
    public void backToList() {
        getSupportFragmentManager().beginTransaction()
                .show(ftList)
                .remove(ftPage)
                .commit();
        mBinding.actionbar.setTitle("Studio");
        ftList.resetMenu();
    }

    @Override
    public void onBackPressed() {
        if (ftPage != null && ftPage.isVisible()) {
            backToList();
        }
        else {
            super.onBackPressed();
        }
    }
}
