package com.example.lenovo.murphysl.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

import com.example.lenovo.murphysl.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	/**
	 * 检验邮箱格式是否正确
	 * @param target
	 * @return
	 */
	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	/**
	 * 关键字高亮显示
	 *
	 * @param text
	 * @param targets
     * @return
     */
	public static SpannableStringBuilder highlight(String text, String[] targets) {

		SpannableStringBuilder spannable = new SpannableStringBuilder(text);
		CharacterStyle span = null;

		for(int i = 0 ; i < targets.length ; i ++){
			String target = targets[i];
			Pattern p = Pattern.compile(target);
			Matcher m = p.matcher(text);
			while (m.find()) {
				span = new ForegroundColorSpan(Color.RED);// 需要重复！
				spannable.setSpan(span, m.start(), m.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}

		return spannable;
	}

	/**
	 * 关键字高亮显示
	 *
	 * @param text
	 * @param targets
	 * @return
	 */
	public static String keyWord(String text, String[] targets) {
		String keyword = null;

		for(int i = 0 ; i < targets.length ; i ++){
			String target = targets[i];
			Pattern p = Pattern.compile(target);
			Matcher m = p.matcher(text);
			while (m.find()) {
				keyword = target;
			}
		}

		return keyword;
	}
}
