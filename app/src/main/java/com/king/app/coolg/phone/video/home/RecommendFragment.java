package com.king.app.coolg.phone.video.home;

import android.widget.CompoundButton;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.FragmentVideoRecommendBinding;
import com.king.app.coolg.model.setting.SettingProperty;
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

    @Override
    protected void initView() {
        mBean = SettingProperty.getVideoRecBean();
        if (mBean == null) {
            mBean = new RecommendBean();
            setAllTypeChecked();
        }
        mBinding.setBean(mBean);
        onFTypeChanged(mBean.isWithFkType());

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
        mBinding.tvOk.setOnClickListener(v -> {
            mBean.setSql(mBinding.etSql.getText().toString().trim());
            if (mBinding.cbFtype.isChecked()) {
                if (!isFTypeChecked()) {
                    showMessageShort("You must check at least one type");
                    return;
                }
            }
            mBean.setFkType1(mBinding.cbFtype1.isChecked());
            mBean.setFkType2(mBinding.cbFtype2.isChecked());
            mBean.setFkType3(mBinding.cbFtype3.isChecked());
            mBean.setFkType4(mBinding.cbFtype4.isChecked());
            mBean.setFkType5(mBinding.cbFtype5.isChecked());
            mBean.setFkType6(mBinding.cbFtype6.isChecked());
            mBean.setTypeAll(mBinding.cbTypeAll.isChecked());
            mBean.setType1v1(mBinding.cbType1v1.isChecked());
            mBean.setType3w(mBinding.cbType3w.isChecked());
            mBean.setTypeMulti(mBinding.cbTypeMulti.isChecked());
            mBean.setTypeTogether(mBinding.cbTypeTogether.isChecked());
            if (!isTypeChecked()) {
                setAllTypeChecked();
            }
            onRecommendListener.onSetSql(mBean);
            dismissAllowingStateLoss();
        });

        mBinding.cbFtype.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            onFTypeChanged(isChecked);
            mBean.setWithFkType(isChecked);
        });
    }

    private void setAllTypeChecked() {
        mBean.setTypeAll(true);
        mBean.setType1v1(true);
        mBean.setType3w(true);
        mBean.setTypeMulti(true);
        mBean.setTypeTogether(true);
    }

    private void onFTypeChanged(boolean isChecked) {
        if (isChecked) {
            mBinding.cbTypeAll.setEnabled(false);
            mBinding.cbType3w.setEnabled(false);
            mBinding.cbTypeMulti.setEnabled(false);
            mBinding.cbTypeTogether.setEnabled(false);
            mBinding.cbTypeAll.setChecked(false);
            mBinding.cbType3w.setChecked(false);
            mBinding.cbTypeMulti.setChecked(false);
            mBinding.cbTypeTogether.setChecked(false);
            mBinding.cbType1v1.setChecked(true);
        }
        else {
            mBinding.cbTypeAll.setEnabled(true);
            mBinding.cbType3w.setEnabled(true);
            mBinding.cbTypeMulti.setEnabled(true);
            mBinding.cbTypeTogether.setEnabled(true);
        }
    }

    private CompoundButton.OnCheckedChangeListener typeListener = (compoundButton, isChecked) -> {
        if (compoundButton == mBinding.cbTypeAll) {
            mBinding.cbTypeAll.setChecked(isChecked);
            mBinding.cbType3w.setChecked(isChecked);
            mBinding.cbTypeMulti.setChecked(isChecked);
            mBinding.cbTypeTogether.setChecked(isChecked);
            mBinding.cbType1v1.setChecked(isChecked);
        }
        else {
            if (!isChecked) {
                if (compoundButton == mBinding.cbType1v1 && mBinding.cbFtype.isChecked()) {
                    mBinding.cbType1v1.setChecked(true);
                }
                else {
                    onSubTypeChangeAllType(false);
                }
            }
            else {
                if (mBinding.cbType1v1.isChecked() && mBinding.cbType3w.isChecked() && mBinding.cbTypeMulti.isChecked() && mBinding.cbTypeTogether.isChecked()) {
                    onSubTypeChangeAllType(true);
                }
            }
        }
    };

    private void onSubTypeChangeAllType(boolean check) {
        mBinding.cbTypeAll.setOnCheckedChangeListener(null);
        mBinding.cbTypeAll.setChecked(check);
        mBinding.cbTypeAll.setOnCheckedChangeListener(typeListener);
    }

    private boolean isFTypeChecked() {
        return mBinding.cbFtype1.isChecked() || mBinding.cbFtype2.isChecked() || mBinding.cbFtype3.isChecked()
                || mBinding.cbFtype4.isChecked() || mBinding.cbFtype5.isChecked() || mBinding.cbFtype6.isChecked();
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

    public interface OnRecommendListener {
        void onSetSql(RecommendBean bean);
    }
}
