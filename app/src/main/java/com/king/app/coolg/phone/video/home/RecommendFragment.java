package com.king.app.coolg.phone.video.home;

import android.view.View;
import android.widget.CompoundButton;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.FragmentVideoRecommendBinding;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.DraggableContentFragment;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/26 17:47
 */
public class RecommendFragment extends DraggableContentFragment<FragmentVideoRecommendBinding> {

    private OnRecommendListener onRecommendListener;

    private RecommendBean mBean;

    private boolean isHideOnline;

    public void setOnRecommendListener(OnRecommendListener onRecommendListener) {
        this.onRecommendListener = onRecommendListener;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_video_recommend;
    }

    public void setBean(RecommendBean mBean) {
        this.mBean = mBean;
    }

    @Override
    protected void initView() {
        if (mBean == null) {
            mBean = new RecommendBean();
            setAllTypeChecked();
        }
        mBinding.setBean(mBean);

        mBinding.cbTypeAll.setOnCheckedChangeListener(typeListener);
        mBinding.cbType1v1.setOnCheckedChangeListener(typeListener);
        mBinding.cbType3w.setOnCheckedChangeListener(typeListener);
        mBinding.cbTypeMulti.setOnCheckedChangeListener(typeListener);
        mBinding.cbTypeTogether.setOnCheckedChangeListener(typeListener);

        mBinding.btnOften.setOnClickListener(v -> {
            new AlertDialogFragment()
                    .setItems(AppConstants.RECORD_SQL_EXPRESSIONS
                            , (dialog, which) -> {
                                appendSql(AppConstants.RECORD_SQL_EXPRESSIONS[which]);
                            })
                    .show(getChildFragmentManager(), "AlertDialogFragment");
        });
        mBinding.btnOften1v1.setOnClickListener(v -> {
            new AlertDialogFragment()
                    .setItems(AppConstants.RECORD_1v1_SQL_EXPRESSIONS
                            , (dialog, which) -> {
                                appendSql1v1(AppConstants.RECORD_1v1_SQL_EXPRESSIONS[which]);
                            })
                    .show(getChildFragmentManager(), "AlertDialogFragment");
        });
        mBinding.btnOften3w.setOnClickListener(v -> {
            new AlertDialogFragment()
                    .setItems(AppConstants.RECORD_3w_SQL_EXPRESSIONS
                            , (dialog, which) -> {
                                appendSql3w(AppConstants.RECORD_3w_SQL_EXPRESSIONS[which]);
                            })
                    .show(getChildFragmentManager(), "AlertDialogFragment");
        });
        mBinding.tvOk.setOnClickListener(v -> {
            mBean.setSql(mBinding.etSql.getText().toString().trim());
            if (mBinding.etSql1v1.getVisibility() == View.VISIBLE) {
                mBean.setSql1v1(mBinding.etSql1v1.getText().toString().trim());
            }
            if (mBinding.etSql3w.getVisibility() == View.VISIBLE) {
                mBean.setSql3w(mBinding.etSql3w.getText().toString().trim());
            }
            mBean.setTypeAll(mBinding.cbTypeAll.isChecked());
            mBean.setType1v1(mBinding.cbType1v1.isChecked());
            mBean.setType3w(mBinding.cbType3w.isChecked());
            mBean.setTypeMulti(mBinding.cbTypeMulti.isChecked());
            mBean.setTypeTogether(mBinding.cbTypeTogether.isChecked());
            mBean.setOnline(mBinding.cbOnline.isChecked());
            if (!isTypeChecked()) {
                setAllTypeChecked();
            }
            onRecommendListener.onSetSql(mBean);
            dismissAllowingStateLoss();
        });

        if (isHideOnline) {
            mBinding.cbOnline.setVisibility(View.GONE);
        }
    }

    private void setAllTypeChecked() {
        mBean.setTypeAll(true);
        mBean.setType1v1(true);
        mBean.setType3w(true);
        mBean.setTypeMulti(true);
        mBean.setTypeTogether(true);
    }

    private CompoundButton.OnCheckedChangeListener typeListener = (compoundButton, isChecked) -> {

        if (compoundButton == mBinding.cbTypeAll) {
            mBinding.cbTypeAll.setChecked(isChecked);
            mBinding.cbType3w.setChecked(isChecked);
            mBinding.cbTypeMulti.setChecked(isChecked);
            mBinding.cbTypeTogether.setChecked(isChecked);
            mBinding.cbType1v1.setChecked(isChecked);
            mBean.setTypeAll(isChecked);
            mBean.setType1v1(isChecked);
            mBean.setType3w(isChecked);
            mBean.setTypeMulti(isChecked);
            mBean.setTypeTogether(isChecked);
            mBinding.btnOften1v1.setVisibility(View.GONE);
            mBinding.btnOften3w.setVisibility(View.GONE);
            mBinding.etSql1v1.setVisibility(View.GONE);
            mBinding.etSql3w.setVisibility(View.GONE);
        }
        else {
            if (!isChecked) {
                onSubTypeChangeAllType(false);
            }
            else {
                if (mBinding.cbType1v1.isChecked() && mBinding.cbType3w.isChecked() && mBinding.cbTypeMulti.isChecked() && mBinding.cbTypeTogether.isChecked()) {
                    onSubTypeChangeAllType(true);
                }
            }

            if (compoundButton == mBinding.cbType1v1) {
                mBean.setType1v1(isChecked);
            }
            else if (compoundButton == mBinding.cbType3w) {
                mBean.setType3w(isChecked);
            }
            else if (compoundButton == mBinding.cbTypeMulti) {
                mBean.setTypeMulti(isChecked);
            }
            else if (compoundButton == mBinding.cbTypeTogether) {
                mBean.setTypeTogether(isChecked);
            }

            if (mBean.isOnlyType1v1()) {
                mBinding.btnOften1v1.setVisibility(View.VISIBLE);
                mBinding.etSql1v1.setVisibility(View.VISIBLE);
            }
            else {
                mBinding.btnOften1v1.setVisibility(View.GONE);
                mBinding.etSql1v1.setVisibility(View.GONE);
            }

            if (mBean.isOnlyType3w()) {
                mBinding.btnOften3w.setVisibility(View.VISIBLE);
                mBinding.etSql3w.setVisibility(View.VISIBLE);
            }
            else {
                mBinding.btnOften3w.setVisibility(View.GONE);
                mBinding.etSql3w.setVisibility(View.GONE);
            }
        }
    };

    private void onSubTypeChangeAllType(boolean check) {
        mBinding.cbTypeAll.setOnCheckedChangeListener(null);
        mBinding.cbTypeAll.setChecked(check);
        mBean.setTypeAll(check);
        mBinding.cbTypeAll.setOnCheckedChangeListener(typeListener);
    }

    private boolean isTypeChecked() {
        return mBinding.cbType1v1.isChecked() || mBinding.cbType3w.isChecked() || mBinding.cbTypeMulti.isChecked()
                || mBinding.cbTypeTogether.isChecked();
    }

    private void appendSql(String condition) {
        String sql = mBinding.etSql.getText().toString();
        if (sql.length() == 0) {
            sql = "T." + condition;
        }
        else {
            sql = sql + " AND T." + condition;
        }
        mBinding.etSql.setText(sql);
        mBinding.etSql.setSelection(sql.length());
    }

    private void appendSql1v1(String condition) {
        String sql = mBinding.etSql1v1.getText().toString();
        if (sql.length() == 0) {
            sql = condition;
        }
        else {
            sql = sql + " AND " + condition;
        }
        mBinding.etSql1v1.setText(sql);
        mBinding.etSql1v1.setSelection(sql.length());
    }

    private void appendSql3w(String condition) {
        String sql = mBinding.etSql3w.getText().toString();
        if (sql.length() == 0) {
            sql = condition;
        }
        else {
            sql = sql + " AND " + condition;
        }
        mBinding.etSql3w.setText(sql);
        mBinding.etSql3w.setSelection(sql.length());
    }

    public void setHideOnline(boolean hideOnline) {
        isHideOnline = hideOnline;
    }

    public interface OnRecommendListener {
        void onSetSql(RecommendBean bean);
    }
}
