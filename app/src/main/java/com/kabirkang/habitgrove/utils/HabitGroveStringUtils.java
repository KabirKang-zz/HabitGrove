package com.kabirkang.habitgrove.utils;

import android.support.annotation.NonNull;

/**
 * Created by kabirkang on 9/25/17.
 */

public class HabitGroveStringUtils {
    public static String capitalized(@NonNull final String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private HabitGroveStringUtils() {
    }
}
