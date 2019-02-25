package com.king.app.coolg.model.image;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.view.widget.CoverView;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 16:42
 */
public class CoolImageAdapter {

    @BindingAdapter("recordUrl")
    public static void setRecordUrl(ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .error(R.drawable.def_small)
                .into(view);
    }

    @BindingAdapter("recordLargeUrl")
    public static void setRecordLargeUrl(ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .error(R.drawable.def_large)
                .into(view);
    }

    @BindingAdapter("starUrl")
    public static void setStarUrl(ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .error(R.drawable.def_person)
                .into(view);
    }

    @BindingAdapter("coverRecordUrl")
    public static void setCoverRecordUrl(CoverView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .error(R.drawable.def_small)
                .into(view.getImageView());
    }

    @BindingAdapter("coverStarUrl")
    public static void setCoverStarUrl(CoverView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .error(R.drawable.def_person)
                .into(view.getImageView());
    }

}
