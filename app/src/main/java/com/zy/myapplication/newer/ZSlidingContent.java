package com.zy.myapplication.newer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

/**
 * Created by ZhangYue on 2017/4/14.
 */

public class ZSlidingContent extends FrameLayout {

    private WeakReference<Context> contextWeakReference;

    public ZSlidingContent(Context context) {
        this(context, null);
    }

    public ZSlidingContent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZSlidingContent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        contextWeakReference = new WeakReference<>(context);
    }

    public void addTips(TipView tipView) {
        View childView = tipView.getContentView();
        if (childView.getParent() != null) {
            ((ViewGroup) childView.getParent()).removeView(childView);
        }
        addView(childView);
    }

}
