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
        if(editTexts.length<6){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        if (!TextUtils.isEmpty(editTexts[0].getText())) {
            sb.append(editTexts[0].getText().toString()).append(":");
        }
        if (!TextUtils.isEmpty(editTexts[1].getText())) {
            sb.append(editTexts[1].getText().toString()).append(":");
        }
        if (!TextUtils.isEmpty(editTexts[2].getText())) {
            sb.append(editTexts[2].getText().toString()).append(":");
        }
        if (!TextUtils.isEmpty(editTexts[3].getText())) {
            sb.append(editTexts[3].getText().toString()).append(":");
        }
        if (!TextUtils.isEmpty(editTexts[4].getText())) {
            sb.append(editTexts[4].getText().toString()).append(":");
        }
        if (!TextUtils.isEmpty(editTexts[5].getText())) {
            sb.append(editTexts[5].getText().toString());
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

        editTexts[0].setKeyListener(digitsKeyListener);
        editTexts[1].setKeyListener(digitsKeyListener);
        editTexts[2].setKeyListener(digitsKeyListener);
        editTexts[3].setKeyListener(digitsKeyListener);
        editTexts[4].setKeyListener(digitsKeyListener);
        editTexts[5].setKeyListener(digitsKeyListener);
        editTexts[6].setKeyListener(digitsKeyListener);

        editTexts[0].setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editTexts[0].setNextFocusForwardId(editTexts[1].getId());

        editTexts[1].setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editTexts[1].setNextFocusForwardId(editTexts[2].getId());

        editTexts[2].setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editTexts[2].setNextFocusForwardId(editTexts[3].getId());

        editTexts[3].setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editTexts[3].setNextFocusForwardId(editTexts[4].getId());

        editTexts[4].setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editTexts[4].setNextFocusForwardId(editTexts[5].getId());

        editTexts[5].setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editTexts[5].setNextFocusForwardId(editTexts[6].getId());

        editTexts[1].setTransformationMethod(new AllCapTransformationMethod(true));
        editTexts[2].setTransformationMethod(new AllCapTransformationMethod(true));
        editTexts[3].setTransformationMethod(new AllCapTransformationMethod(true));
        editTexts[4].setTransformationMethod(new AllCapTransformationMethod(true));
        editTexts[5].setTransformationMethod(new AllCapTransformationMethod(true));
        editTexts[6].setTransformationMethod(new AllCapTransformationMethod(true));
    }

    public static String getStringData() {
        return "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM1234567890";
    }
}
