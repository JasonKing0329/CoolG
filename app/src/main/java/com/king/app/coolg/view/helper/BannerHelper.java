package com.king.app.coolg.view.helper;

import com.king.app.coolg.model.bean.BannerParams;
import com.king.lib.banner.BannerFlipStyleProvider;
import com.king.lib.banner.CoolBanner;

import java.util.Random;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/3/18 9:37
 */
public class BannerHelper {

    public static void setBannerParams(CoolBanner banner, BannerParams params) {
        // 轮播切换时间
        banner.setDuration(params.getDuration());

        if (params.isRandom()) {
            Random random = new Random();
            int type = Math.abs(random.nextInt()) % BannerFlipStyleProvider.ANIM_TYPES.length;
            BannerFlipStyleProvider.setPagerAnim(banner, type);
        }
        else {
            BannerFlipStyleProvider.setPagerAnim(banner, params.getType());
        }
    }

}
