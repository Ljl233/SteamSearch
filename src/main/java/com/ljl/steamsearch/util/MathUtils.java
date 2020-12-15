package com.ljl.steamsearch.util;

public class MathUtils {
    public static int parseIntRemoveSplit(String s) {
        String[] split = s.split(",");
        int ans = 0;
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
            ans = ans * 1000 + Integer.parseInt(split[i]);
        }
        return ans;
    }
}
