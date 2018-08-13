package com.king.app.coolg.phone.record.scene;

import android.text.TextUtils;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentDialogHsvColorBinding;
import com.king.app.coolg.model.bean.HsvColorBean;
import com.king.app.coolg.view.dialog.DraggableContentFragment;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/13 15:21
 */
public class HsvColorDialogContent extends DraggableContentFragment<FragmentDialogHsvColorBinding> {

    private HsvColorBean hsvColorBean;
    private OnHsvColorListener onHsvColorListener;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_dialog_hsv_color;
    }

    @Override
    protected void initView() {
        if (hsvColorBean == null) {
            hsvColorBean = new HsvColorBean();
        }
        mBinding.etStart.setText(String.valueOf(hsvColorBean.gethStart()));
        mBinding.etAngle.setText(String.valueOf(hsvColorBean.gethArg()));
        mBinding.etS.setText(String.valueOf(hsvColorBean.getS()));
        mBinding.etV.setText(String.valueOf(hsvColorBean.getV()));
        if (hsvColorBean.getS() >=0 || hsvColorBean.getV() >= 0) {
            mBinding.llSv.setVisibility(View.VISIBLE);
            mBinding.cbStable.setChecked(true);
        }
        else {
            mBinding.llSv.setVisibility(View.INVISIBLE);
        }
        mBinding.cbStable.setOnCheckedChangeListener((compoundButton, check) -> mBinding.llSv.setVisibility(check ? View.VISIBLE:View.INVISIBLE));

        mBinding.tvPreview.setOnClickListener(v -> onPreview());
        mBinding.tvOk.setOnClickListener(v -> onSave());
    }

    private void onSave() {
        String start = mBinding.etStart.getText().toString().trim();
        if (TextUtils.isEmpty(start)) {
            return;
        }
        String angle = mBinding.etAngle.getText().toString().trim();
        if (TextUtils.isEmpty(angle)) {
            return;
        }
        hsvColorBean.sethStart(Integer.parseInt(start));
        hsvColorBean.sethArg(Integer.parseInt(angle));

        if (mBinding.llSv.getVisibility() == View.VISIBLE) {
            String s = mBinding.etS.getText().toString().trim();
            if (TextUtils.isEmpty(s)) {
                return;
            }
            String v = mBinding.etV.getText().toString().trim();
            if (TextUtils.isEmpty(v)) {
                return;
            }
            hsvColorBean.setS(Float.parseFloat(s));
            hsvColorBean.setV(Float.parseFloat(v));
        }
        try {
            if (onHsvColorListener != null) {
                onHsvColorListener.onSaveColor(hsvColorBean);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        dismissAllowingStateLoss();
    }

    private void onPreview() {
        if (onHsvColorListener != null) {
            String start = mBinding.etStart.getText().toString().trim();
            if (TextUtils.isEmpty(start)) {
                return;
            }
            String angle = mBinding.etAngle.getText().toString().trim();
            if (TextUtils.isEmpty(angle)) {
                return;
            }
            hsvColorBean.sethStart(Integer.parseInt(start));
            hsvColorBean.sethArg(Integer.parseInt(angle));

            if (mBinding.llSv.getVisibility() == View.VISIBLE) {
                String s = mBinding.etS.getText().toString().trim();
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                String v = mBinding.etV.getText().toString().trim();
                if (TextUtils.isEmpty(v)) {
                    return;
                }
                hsvColorBean.setS(Float.parseFloat(s));
                hsvColorBean.setV(Float.parseFloat(v));
            }

            try {
                onHsvColorListener.onPreviewHsvColor(hsvColorBean);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void setHsvColorBean(HsvColorBean hsvColorBean) {
        this.hsvColorBean = hsvColorBean;
    }

    public void setOnHsvColorListener(OnHsvColorListener onHsvColorListener) {
        this.onHsvColorListener = onHsvColorListener;
    }

    public interface OnHsvColorListener {
        void onPreviewHsvColor(HsvColorBean hsvColorBean);
        void onSaveColor(HsvColorBean hsvColorBean);
    }
}
