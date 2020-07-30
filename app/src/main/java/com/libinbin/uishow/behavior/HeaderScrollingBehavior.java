package com.libinbin.uishow.behavior;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

import com.libinbin.uishow.R;

import java.lang.ref.WeakReference;

/**
 * @author LiBinBin 
 * @create 2020/7/29
 * @Describe  
 */
public class HeaderScrollingBehavior extends CoordinatorLayout.Behavior<RecyclerView> {

    private WeakReference<View> dependentView;

    public HeaderScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RecyclerView child, View dependency) {
        if (dependency != null && dependency.getId() == R.id.image) {
            if(dependentView==null){
                dependentView = new WeakReference<>(dependency);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, RecyclerView child, int layoutDirection) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (lp.height == CoordinatorLayout.LayoutParams.MATCH_PARENT) {
            child.layout(0, 0, parent.getWidth(), (int) (parent.getHeight() - getDependentViewCollapsedHeight()));
            return true;
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RecyclerView child, View dependency) {
        child.setTranslationY(dependency.getHeight() + dependency.getTranslationY());
        return true;
    }

    /**
     * 用户按下手指时触发，询问 NestedScrollParent 是否要处理这次滑动操作，
     * 如果返回 true 则表示“我要处理这次滑动”，如果返回 false 则表示“不处理”，
     * 后面的一系列回调函数就不会被调用了。它有一个关键的参数，就是滑动方向，
     * 表明了用户是垂直滑动还是水平滑动
     * @param coordinatorLayout
     * @param child
     * @param directTargetChild
     * @param target
     * @param axes
     * @param type
     * @return
     */
    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;

    }

    /**
     * 当 NestedScrollParent 接受要处理本次滑动后，这个回调被调用，
     * 我们可以做一些准备工作，比如让之前的滑动动画结束。
     * @param coordinatorLayout
     * @param child
     * @param directTargetChild
     * @param target
     * @param axes
     * @param type
     */
    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    /**
     * 当 NestedScrollChild 即将被滑动时调用，在这里你可以做一些处理。
     * 值得注意的是，这个方法有一个参数 int[] consumed，你可以修改这个数组来表示你到底处理掉了多少像素
     * 。假设用户滑动了 100px，你做了 90px 的位移，那么就需要把 consumed[1] 改成 90（下标 0、1 分别对应 x、y 轴）
     * ，这样 NestedScrollChild 就能知道，然后继续处理剩下的 10px。
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dx
     * @param dy
     * @param consumed
     * @param type
     */
    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy < 0) {
            return;
        }
        View dependentView = getDependentView();
        float newTranslateY = dependentView.getTranslationY() - dy;
        float minHeaderTranslate = -(dependentView.getHeight() - getDependentViewCollapsedHeight());


        if (newTranslateY > minHeaderTranslate) {
            dependentView.setTranslationY(newTranslateY);
            consumed[1] = dy;
        }else{
            dependentView.setTranslationY(minHeaderTranslate);
        }
    }

    /**
     * 上一个方法结束后，NestedScrollChild 处理剩下的距离。
     * 比如上面还剩 10px，这里 NSC 滚动 2px 后发现已经到头了，于是 NestedScrollChild 结束其滚动，调用该方法，
     * 并将 NestedScrollChild 处理剩下的像素数作为参数（dxUnconsumed、dyUnconsumed）传过来，这里传过来的就是 8px。
     * 参数中还会有 NestedScrollChild 处理过的像素数（dxConsumed、dyConsumed）。这个方法主要处理一些越界后的滚动。
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dxConsumed
     * @param dyConsumed
     * @param dxUnconsumed
     * @param dyUnconsumed
     * @param type
     */
    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (dyUnconsumed > 0) {
            return;
        }
        View dependentView = getDependentView();
        float newTranslateY = dependentView.getTranslationY() - dyUnconsumed;
        final float maxHeaderTranslate = 0;

        if (newTranslateY < maxHeaderTranslate) {
            dependentView.setTranslationY(newTranslateY);
        }else{
            dependentView.setTranslationY(maxHeaderTranslate);
        }
    }

    /**
     * 用户松开手指并且会发生惯性滚动之前调用。参数提供了速度信息，我们这里可以根据速度，
     * 决定最终的状态是展开还是折叠，并且启动滑动动画。通过返回值我们可以通知 NestedScrollChild 是否自己还要进行滑动滚动，
     * 一般情况如果面板处于中间态，我们就不让 NestedScrollChild 接着滚了，因为我们还要用动画把面板完全展开或者完全折叠。
     *  此处返回false，不拦截RecyclerView的惯性滚动
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, float velocityX, float velocityY) {
        return false;
    }

    /**
     * 一切滚动停止后调用，如果不会发生惯性滚动，fling 相关方法不会调用，直接执行到这里。
     * 这里我们做一些清理工作，当然有时也要处理中间态问题。
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param type
     */
    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull RecyclerView child, @NonNull View target, int type) {
    }


    private float getDependentViewCollapsedHeight() {
        return getDependentView().getResources().getDimension(R.dimen.collapsed_header_height);
    }

    private View getDependentView() {
        return dependentView.get();
    }

}
