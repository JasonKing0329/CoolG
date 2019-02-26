package com.king.app.coolg.phone.video.home;

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
        }
        mBinding.setBean(mBean);

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
            mBean.setFkType1(mBinding.cbFtype1.isChecked());
            mBean.setFkType2(mBinding.cbFtype2.isChecked());
            mBean.setFkType3(mBinding.cbFtype3.isChecked());
            mBean.setFkType4(mBinding.cbFtype4.isChecked());
            mBean.setFkType5(mBinding.cbFtype5.isChecked());
            mBean.setFkType6(mBinding.cbFtype6.isChecked());
            onRecommendListener.onSetSql(mBean);
            dismissAllowingStateLoss();
        });

        mBinding.cbFtype.setOnCheckedChangeListener((compoundButton, isChecked) -> mBean.setWithFkType(isChecked));
    }

    private void appendSql(String condition) {
        String sql = mBinding.etSql.getText().toString();
        if (sql.length() == 0) {
            sql = condition;
        }
        else {
            sql = sql + " AND " + condition;
        }
        mBinding.etSql.setText(sql);
    }

    public interface OnRecommendListener {
        void onSetSql(RecommendBean bean);
    }
}
