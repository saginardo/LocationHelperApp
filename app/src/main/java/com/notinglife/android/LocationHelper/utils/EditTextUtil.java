package com.notinglife.android.LocationHelper.utils;

import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-12 9:40
 */

public class EditTextUtil {
    public static String generateMacAddress(EditText... editTexts) {
        if (editTexts.length < 6) {
            return null;
        }
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < editTexts.length - 1; i++) {
            if (!TextUtils.isEmpty(editTexts[i].getText())) {
                sb.append(editTexts[i].getText().toString()).append(":");
            }
        }
        if (!TextUtils.isEmpty(editTexts[editTexts.length - 1].getText())) {
            sb.append(editTexts[editTexts.length - 1].getText().toString());
        }
        return sb.toString().toUpperCase();
    }

    public static void editTextToUpperCase(EditText... editTexts) {

        DigitsKeyListener digitsKeyListener = new DigitsKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                return getStringData().toCharArray();
            }

            @Override
            public int getInputType() {
                return EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
            }
        };

        //设置设备id和，mac地址仅能输入数字和字母
        //设置大写转小写字母
        for (EditText editText : editTexts) {
            editText.setKeyListener(digitsKeyListener);
            editText.setTransformationMethod(new AllCapTransformationUtil(true));
        }
        //设置 edittext的下一个输入框和ime键盘的确定键改写
        for (int i = 0; i < editTexts.length - 1; i++) {
            editTexts[i].setImeOptions(EditorInfo.IME_ACTION_NEXT);
            editTexts[i].setNextFocusForwardId(editTexts[i + 1].getId());
        }
    }

    public static String getStringData() {
        return "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM1234567890";
    }


    public static void ISTOEDIT(Boolean isEditable, EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setEnabled(isEditable);
        }
    }
}
