package com.king.app.coolg.phone.star;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.support.v7.graphics.Palette;
import android.view.View;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.BindingDialogFragment;
import com.king.app.coolg.databinding.FragmentDialogStarRatingBinding;
import com.king.app.coolg.model.palette.CoolPalette;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.coolg.view.widget.StarRatingView;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 16:25
 */
public class StarRatingDialog extends BindingDialogFragment<FragmentDialogStarRatingBinding>
    implements StarRatingView.OnStarChangeListener {

    private DialogInterface.OnDismissListener onDismissListener;

    private StarRatingViewModel mModel;

    private long starId;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_dialog_star_rating;
    }

    @Override
    protected void initView(View view) {

        mModel = ViewModelProviders.of(this).get(StarRatingViewModel.class);

        mBinding.starFace.setOnStarChangeListener(this);
        mBinding.starBody.setOnStarChangeListener(this);
        mBinding.starDk.setOnStarChangeListener(this);
        mBinding.starPassion.setOnStarChangeListener(this);
        mBinding.starVideo.setOnStarChangeListener(this);
        mBinding.starSex.setOnStarChangeListener(this);

        mBinding.ivClose.setOnClickListener(v -> dismissAllowingStateLoss());

        mModel.starObserver.observe(this, star -> showStar(star));
        mModel.ratingObserver.observe(this, rating -> showRatings(rating));

        mBinding.tvSave.setOnClickListener(v -> {
            mModel.saveRating();
            dismissAllowingStateLoss();
        });

        mModel.loadStarRating(starId);
    }

    public void setStarId(long starId) {
        this.starId = starId;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    public void showStar(Star star) {
        mBinding.tvStar.setText(star.getName());

        GlideApp.with(getContext())
                .asBitmap()
                .load(mModel.getStarImage())
                .listener(new CoolPalette(getLifecycle()) {
                    @Override
                    protected void onPaletteCreated(Palette palette) {
                        setStarColor(mModel.generateStarColor(getResources(), palette));
                    }
                })
                .error(R.drawable.ic_def_person_wide)
                .into(mBinding.ivStar);
    }

    public void showRatings(StarRating rating) {

        mBinding.tvRating.setText(mModel.getComplex());

        mBinding.starFace.setCheckNumber(rating.getFace());
        mBinding.tvFace.setText(StarRatingUtil.getRatingValue(rating.getFace()));
        mBinding.starBody.setCheckNumber(rating.getBody());
        mBinding.tvBody.setText(StarRatingUtil.getRatingValue(rating.getBody()));
        mBinding.starDk.setCheckNumber(rating.getDk());
        mBinding.tvDk.setText(StarRatingUtil.getRatingValue(rating.getDk()));
        mBinding.starSex.setCheckNumber(rating.getSexuality());
        mBinding.tvSex.setText(StarRatingUtil.getRatingValue(rating.getSexuality()));
        mBinding.starPassion.setCheckNumber(rating.getPassion());
        mBinding.tvPassion.setText(StarRatingUtil.getRatingValue(rating.getPassion()));
        mBinding.starVideo.setCheckNumber(rating.getVideo());
        mBinding.tvVideo.setText(StarRatingUtil.getRatingValue(rating.getVideo()));
    }

    public void setStarColor(int color) {
        mBinding.starVideo.setStarColor(color);
        mBinding.starSex.setStarColor(color);
        mBinding.starPassion.setStarColor(color);
        mBinding.starDk.setStarColor(color);
        mBinding.starFace.setStarColor(color);
        mBinding.starBody.setStarColor(color);
    }

    @Override
    public void onStarChanged(StarRatingView view, float checkedStar) {
        String rateValue = StarRatingUtil.getRatingValue(checkedStar);
        switch (view.getId()) {
            case R.id.star_face:
                mModel.getRating().setFace(checkedStar);
                mBinding.tvFace.setText(rateValue);
                break;
            case R.id.star_body:
                mModel.getRating().setBody(checkedStar);
                mBinding.tvBody.setText(rateValue);
                break;
            case R.id.star_dk:
                mModel.getRating().setDk(checkedStar);
                mBinding.tvDk.setText(rateValue);
                break;
            case R.id.star_passion:
                mModel.getRating().setPassion(checkedStar);
                mBinding.tvPassion.setText(rateValue);
                break;
            case R.id.star_video:
                mModel.getRating().setVideo(checkedStar);
                mBinding.tvVideo.setText(rateValue);
                break;
            case R.id.star_sex:
                mModel.getRating().setSexuality(checkedStar);
                mBinding.tvSex.setText(rateValue);
                break;
        }
        mBinding.tvRating.setText(mModel.getComplex());
    }

}
