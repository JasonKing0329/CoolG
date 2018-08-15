package com.king.app.coolg.phone.order;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.view.View;

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

    public static final String EXTRA_SELECT_MODE = "select_mode";
    public static final String EXTRA_SELECT_STAR = "select_star";
    public static final String EXTRA_SELECT_RECORD = "select_record";
    public static final String RESP_ORDER_ID = "order_id";

    private OrderFragment ftCurrent;
    private StarOrderFragment ftStar;
    private RecordOrderFragment ftRecord;

    @Override
    protected int getContentView() {
        return R.layout.activity_order_phone;
    }

    @Override
    public boolean isSelectMode() {
        return getIntent().getBooleanExtra(EXTRA_SELECT_MODE, false);
    }

    @Override
    protected void initView() {
        mBinding.actionbar.setOnBackListener(() -> finish());
        if (getIntent().getBooleanExtra(EXTRA_SELECT_STAR, false)
                || getIntent().getBooleanExtra(EXTRA_SELECT_RECORD, false)) {
            mBinding.tabLayout.setVisibility(View.GONE);
        }
        else {
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
        }

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
        if (getIntent().getBooleanExtra(EXTRA_SELECT_RECORD, false)){
            ftRecord = new RecordOrderFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ft, ftRecord, "RecordOrderFragment")
                    .commit();
            ftCurrent = ftRecord;
        }
        else {
            ftStar = new StarOrderFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_ft, ftStar, "StarOrderFragment")
                    .commit();
            ftCurrent = ftStar;
        }
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

    @Override
    public void onSelectOrder(long id) {
        Intent intent = new Intent();
        intent.putExtra(OrderPhoneActivity.RESP_ORDER_ID, id);
        setResult(RESULT_OK, intent);
        finish();
    }
}
