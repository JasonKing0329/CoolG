package com.king.app.coolg.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * @author JingYang
 * @version create time：2016-1-15 下午1:31:42
 *
 */
public class ColorUtil {

	public static String formatColor(int color) {
		String hexColor = "" + Integer.toHexString(color);
		//全不透明的可以去掉ff
		if (hexColor.length() == 8 && hexColor.startsWith("ff")) {
			hexColor = hexColor.substring(2);
		}
		//如果是类似00002233，前面的0全部会被截断
		if (color > 0 && hexColor.length() < 8) {
			StringBuffer buffer = new StringBuffer(hexColor);
			for (int i = 0; i < 8 - hexColor.length(); i ++) {
				buffer.insert(0, "0");
			}
			hexColor = buffer.toString();
		}
		return hexColor;
	}

	/**
	 * 深色背景白色文字，浅色背景#333333文字
	 * @param color
	 * @return
	 */
	public static int generateForgroundColorForBg(int color) {
		if (isDeepColor(color)) {
			return Color.WHITE;
		}
		else {
			return Color.parseColor("#666666");
		}
	}

	/**
	 * 判断是不是深色
	 *
	 * @return
	 */
	public static boolean isDeepColor(int color) {
//		double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
//		return darkness >= 0.5;
		return Color.red(color) * 0.299 + Color.green(color) * 0.578 + Color.blue(color) * 0.114 < 192;
	}

	/**
	 * 随机暗色，作为背景色，配合白色文字
	 * 利用hsv模型，s大于0.7, v大于0.5
	 * H是色彩,S是深浅,V是明暗
	 * 关于HSV模型可以参考http://blog.csdn.net/viewcode/article/details/8203728
	 *
	 * @return
	 */
	public static int randomWhiteTextBgColor() {
		Random random = new Random();
		float[] hsv = new float[3];
		hsv[0] = random.nextFloat() * 360;
		hsv[1] = random.nextFloat();
		if (hsv[1] < 0.7f) {
			hsv[1] = 0.7f + hsv[1] / 0.7f * 0.3f;
		}
		hsv[2] = random.nextFloat();
		if (hsv[2] < 0.5f) {
			hsv[2] += 0.5f;
		}

//        Log.d(TAG, "hsv[" + hsv[0] + "," + hsv[1] + "," + hsv[2] + "]");
		return Color.HSVToColor(hsv);
	}

}
