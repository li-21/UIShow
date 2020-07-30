package com.libinbin.uishow.behavior;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.libinbin.uishow.R;


/**
 * @author LiBinBin
 * @create 2020/7/29
 * @Describe
 */
public class HeaderLogoBehavior extends CoordinatorLayout.Behavior<View> {


    public HeaderLogoBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if (dependency != null && dependency.getId() == R.id.image) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (dependency != null && dependency.getId() == R.id.image) {
            Resources resources = dependency.getResources();

            final float progress = 1.f -
                    Math.abs(dependency.getTranslationY() / (dependency.getHeight() - resources.getDimension(R.dimen.collapsed_header_height)));
            TextView tv = child.findViewById(R.id.toolbar_tv);
            tv.setAlpha(progress);
            return true;
        }
        return false;

    }

}
