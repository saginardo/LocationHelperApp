package com.notinglife.android.LocationHelper.utils;

import android.text.method.ReplacementTransformationMethod;

/**
 * ${DESCRIPTION}
 *
 * @author saginardo
 * @version ${VERSION}
 *          date 2017-06-09 8:58
 */

public class AllCapTransformationUtil extends ReplacementTransformationMethod {

    private char[] lower = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private char[] upper = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private boolean allUpper = false;

    public AllCapTransformationUtil(boolean needUpper) {
        this.allUpper = needUpper;
    }

    @Override
    protected char[] getOriginal() {
        if (allUpper) {
            return lower;
        } else {
            return upper;
        }
    }

    @Override
    protected char[] getReplacement() {
        if (allUpper) {
            return upper;
        } else {
            return lower;
        }
    }
}
