package com.king.app.coolg.view;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.model.image.ImageProvider;

public class GlideBindAdapter {

    @BindingAdapter("starImage")
    public static void setStarImage(ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .error(R.drawable.def_person)
                .into(view);
    }

    @BindingAdapter("imageByStarName")
    public static void setImageByStarName(ImageView view, String name) {
        String url = ImageProvider.getStarRandomPath(name, null);
        GlideApp.with(view.getContext())
                .load(url)
                .error(R.drawable.def_person)
                .into(view);
    }

    @BindingAdapter("recordSmallImage")
    public static void setRecordImage(ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .error(R.drawable.def_small)
                .into(view);
    }

    @BindingAdapter("recordLargeImage")
    public static void setRecordLargeImage(ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .error(R.drawable.def_large)
                .into(view);
    }

    @BindingAdapter("imageByRecordName")
    public static void setImageByRecordName(ImageView view, String name) {
        String url = ImageProvider.getRecordRandomPath(name, null);
        GlideApp.with(view.getContext())
                .load(url)
                .error(R.drawable.def_person)
                .into(view);
    }

}
