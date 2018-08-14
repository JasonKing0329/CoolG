package com.king.app.coolg.phone.order;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityOrderPhoneBinding;
import com.king.app.coolg.phone.order.record.RecordOrderFragment;
import com.king.app.coolg.phone.order.star.StarOrderFragment;
import com.king.app.jactionbar.OnConfirmListener;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/14 13:36
 */
@Route("OrderPhone")
public class OrderPhoneActivity extends MvvmActivity<ActivityOrderPhoneBinding, BaseViewModel> implements IOrderHolder {

    private OrderFragment ftCurrent;
    private StarOrderFragment ftStar;
    private RecordOrderFragment ftRecord;

    @Override
    protected int getContentView() {
        return R.layout.activity_order_phone;
    }

    @Override
    protected void initView() {
        mBinding.actionbar.setOnBackListener(() -> finish());
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText("Star"));
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText("Record"));
        mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showStarPage();
                }
                else {
                    showRecordPage();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_add:
                    ftCurrent.addOrder();
                    break;
                case R.id.menu_delete:
                    mBinding.actionbar.showConfirmStatus(menuId);
                    ftCurrent.setSelectionMode(true);
                    break;
            }
        });
        mBinding.actionbar.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public boolean disableInstantDismissConfirm() {
                return true;
            }

            @Override
            public boolean disableInstantDismissCancel() {
                return false;
            }

            @Override
            public boolean onConfirm(int actionId) {
                switch (actionId) {
                    case R.id.menu_delete:
                        ftCurrent.delete();
                        break;
                }
                return false;
            }

            @Override
            public boolean onCancel(int actionId) {
                ftCurrent.setSelectionMode(false);
                return true;
            }
        });
    }

    @Override
    public void cancelConfirmStatus() {
        mBinding.actionbar.cancelConfirmStatus();
        ftCurrent.setSelectionMode(false);
    }

    @Override
    protected BaseViewModel createViewModel() {
        return null;
    }

    @Override
    protected void initData() {
        ftStar = new StarOrderFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_ft, ftStar, "StarOrderFragment")
                .commit();
        ftCurrent = ftStar;
    }

    private void showStarPage() {
        if (ftCurrent != ftStar) {
            getSupportFragmentManager().beginTransaction()
                    .show(ftStar)
                    .hide(ftRecord)
                    .commit();
            ftCurrent = ftStar;
        }
    }

    private void showRecordPage() {
        if (ftCurrent != ftRecord) {
            if (ftRecord == null) {
                ftRecord = new RecordOrderFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fl_ft, ftRecord, "RecordOrderFragment")
                        .hide(ftStar)
                        .commit();
            }
            else {
                getSupportFragmentManager().beginTransaction()
                        .show(ftRecord)
                        .hide(ftStar)
                        .commit();
            }
            ftCurrent = ftRecord;
        }
    }

    @Override
    public void onBackPressed() {
        if (ftCurrent != null) {
            if (ftCurrent.onBackPressed()) {
                return;
            }
        }
        super.onBackPressed();
    }
}
