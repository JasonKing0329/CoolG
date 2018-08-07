package com.king.app.coolg.utils;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.transformer.TransitionEffect;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/21 11:44
 */
public class LMBannerViewUtil {

    public static final String[] ANIM_TYPES = new String[]{
            "defaultEffect", "alpha", "rotate", "cube", "flip", "accordion", "zoomFade",
            "fade", "zoomCenter", "zoomStack", "stack", "depth", "zoom", "zoomOut"/*, "parallax"*/
    };

    /**
     * 切换时的动画模式
     * @param position
     */
    public static void setScrollAnim(LMBanners lmBanners, int position){
        switch (position) {
            case 0:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.Default);//Default
                break;
            case 1:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.Alpha);//Alpha
                break;
            case 2:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.Rotate);//Rotate
                break;
            case 3:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.Cube);//Cube
                break;
            case 4:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.Flip);//Flip
                break;
            case 5:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.Accordion);//Accordion
                break;
            case 6:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.ZoomFade);//ZoomFade
                break;
            case 7:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.Fade);//Fade
                break;
            case 8:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.ZoomCenter);//ZoomCenter
                break;
            case 9:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.ZoomStack);//ZoomStack
                break;
            case 10:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.Stack);//Stack
                break;
            case 11:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.Depth);//Depth
                break;
            case 12:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.Zoom);//Zoom
                break;
            case 13:
                lmBanners.setHoriZontalTransitionEffect(TransitionEffect.ZoomOut);//ZoomOut
                break;
//            case 14:
//                lmBanners.setHoriZontalCustomTransformer(new ParallaxTransformer(R.id.id_image));//Parallax
//                break;

        }
    }

}
