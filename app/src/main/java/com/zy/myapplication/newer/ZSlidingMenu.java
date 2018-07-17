package com.zy.myapplication.newer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zy.myapplication.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by ZhangYue on 2017/4/14.
 */

public class ZSlidingMenu extends FrameLayout {

    public ZSlidingMenu(Context context) {
        this(context, null);
    }

    public ZSlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZSlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void addTips(TipView tipView) {
        View childView = tipView.getContentView();
        if (childView.getParent() != null) {
            ((ViewGroup) childView.getParent()).removeView(childView);
        }
        addView(childView);
    }

}
