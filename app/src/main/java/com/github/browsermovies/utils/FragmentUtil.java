package com.github.browsermovies.utils;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;


public class FragmentUtil {
    public static Context getContext(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= 23) {
            return fragment.getContext();
        }
        return fragment.getActivity();
    }

    private FragmentUtil() {
    }
}
