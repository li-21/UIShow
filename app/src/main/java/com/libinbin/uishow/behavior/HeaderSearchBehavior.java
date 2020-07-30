package com.libinbin.uishow.behavior;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.libinbin.uishow.R;


/**
 * @author LiBinBin
 * @create 2020/7/29
 * @Describe
 */
public class HeaderSearchBehavior extends CoordinatorLayout.Behavior<View> {

//    private ArgbEvaluator argbEvaluator;

    public HeaderSearchBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

//        argbEvaluator = new ArgbEvaluator();
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
            final float header_height = resources.getDimension(R.dimen.collapsed_header_height);
            final float offset_y = resources.getDimension(R.dimen.collapsed_float_offset_y);
            final float child_height = child.getHeight();
            final float child_height_end = (header_height - child_height) / 2 - (header_height + offset_y);
            child.setTranslationY(header_height + resources.getDimension(R.dimen.collapsed_float_offset_y));
//        }
            // Background
//        child.setBackgroundColor((int) argbEvaluator.evaluate(
//                progress,
//                resources.getColor(R.color.colorCollapsedFloatBackground),
//                resources.getColor(R.color.colorInitFloatBackground)));

//        dependentViewRL.get().setBackgroundColor((int) argbEvaluator.evaluate(
//                progress,
//                resources.getColor(R.color.colorCollapsedFloatBackground),
//                resources.getColor(R.color.colorInitFloatBackground)));

            // Margins
            final float collapsedMargin = resources.getDimension(R.dimen.collapsed_search_margin_right);
            final float zero = resources.getDimension(R.dimen.collapsed_float_zero);
            final float marginLeftRight = resources.getDimension(R.dimen.collapsed_search_margin_left2right);
            final int marginRight = (int) (collapsedMargin + (marginLeftRight - collapsedMargin) * progress);
            final int marginTop = (int) (child_height_end + (zero - child_height_end) * progress);
            final int marginLeft = (int) marginLeftRight;
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            lp.setMargins(marginLeft, marginTop, marginRight, 0);
            child.setLayoutParams(lp);
            return true;
        }
        return false;

    }

}
