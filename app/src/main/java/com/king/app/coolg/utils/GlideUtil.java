package com.king.app.coolg.utils;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;

import java.io.File;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/24 8:52
 */
public class GlideUtil {

    /**
     * download preview item
     * @return
     */
    public static RequestOptions getDownloadPreviewOptions() {
        RequestOptions options = new RequestOptions();
        options.error(R.drawable.def_small);
        options.placeholder(R.drawable.def_small);
        options.dontAnimate();
        return options;
    }

    /**
     * record item
     * @return
     */
    public static RequestOptions getRecordOptions() {
        RequestOptions options = new RequestOptions();
        options.error(R.drawable.def_large);
        options.placeholder(R.drawable.def_large);
        options.dontAnimate();
        return options;
    }

    /**
     * record item
     * @return
     */
    public static RequestOptions getRecordAnimOptions() {
        RequestOptions options = new RequestOptions();
        options.error(R.drawable.def_large);
        return options;
    }

    /**
     * star item
     * @return
     */
    public static RequestOptions getStarOptions() {
        RequestOptions options = new RequestOptions();
        options.error(R.drawable.def_person_square);
        options.placeholder(R.drawable.def_person_square);
        options.dontAnimate();
        return options;
    }

    /**
     * star item
     * @return
     */
    public static RequestOptions getStarWideOptions() {
        RequestOptions options = new RequestOptions();
        options.error(R.drawable.ic_def_person_wide);
        options.placeholder(R.drawable.ic_def_person_wide);
        options.dontAnimate();
        return options;
    }

    /**
     * 背景图
     * @return
     */
//    public static RequestOptions getBgOptions(String path) {
//        RequestOptions options = new RequestOptions();
//        options.error(R.drawable.def_bk);
//        options.placeholder(R.drawable.def_bk);
//        setFileSignature(options, path);
//        return options;
//    }

    /**
     * 以文件修改时间为signature
     * @param path
     * @return
     */
    public static RequestOptions getSignedRequestOptions(String path) {
        RequestOptions options = new RequestOptions();
        setFileSignature(options, path);
        return options;
    }

    /**
     * 以文件的修改时间为signature
     * @param path
     * @return
     */
    private static Key getFileSignature(String path) {
        if (path == null) {
            return null;
        }

        File file = new File(path);
        if (file.exists()) {
            return new GlideStringKey(String.valueOf(file.lastModified()));
        }
        else {
            return null;
        }
    }

    /**
     * 设置缓存signature
     * @param options
     * @param filePath
     */
    public static void setFileSignature(RequestOptions options, String filePath) {
        Key key = getFileSignature(filePath);
        if (key != null) {
            options.signature(key);
        }
    }
}
