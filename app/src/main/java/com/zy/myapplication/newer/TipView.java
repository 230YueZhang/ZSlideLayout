package com.zy.myapplication.newer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.zy.myapplication.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * @author ZhangYue
 * @project ZSlideLayout
 * @createdTime 2018/6/15  10:21
 */
public class TipView {

    private View tipView;

    public View getContentView() {
        return tipView;
    }

    public void setContentView(View view, FrameLayout.LayoutParams layoutParams) {
        this.tipView = view;
        tipView.setLayoutParams(layoutParams);
    }

    public static class TipViewBuilder {
        public final static int LEFT = 0x10;
        public final static int RIGHT = 0x11;
        public final static int TOP = 0x12;
        public final static int BOTTOM = 0x13;

        private WeakReference<Context> contextWeakReference;

        //提示控件的位置
        private int location;

        private int resId;

        private View view;

        /**
         * 提示控件的位置
         *
         * @param location
         * @return
         */
        public TipViewBuilder setLocation(int location) {
            this.location = location;
            return this;
        }

        /**
         * @param context
         * @param resourceId 资源id ——可以是图片、布局文件
         */
        public TipViewBuilder(Context context, @DrawableRes @LayoutRes int resourceId) {
            contextWeakReference = new WeakReference<>(context);
            this.resId = resourceId;
            view = getRes(resourceId);
        }

        protected View getRes(int resourceId) {
            try {
                Drawable drawable = contextWeakReference.get().getResources().getDrawable(resourceId);
                if (drawable != null) {
                    ImageView imageView = new ImageView(contextWeakReference.get());
                    imageView.setImageResource(resourceId);

                    return imageView;
                }
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            try {
                View view = LayoutInflater.from(contextWeakReference.get()).inflate(resourceId, null);
                if (view != null) {
                    return view;
                }
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            Toast.makeText(contextWeakReference.get(), contextWeakReference.get().getString(R.string.res_notfound), Toast.LENGTH_SHORT).show();
            return null;
        }

        protected FrameLayout.LayoutParams getLayoutParams(int resourceId) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            try {
                XmlResourceParser xmlResourceParser = contextWeakReference.get().getResources().getLayout(resourceId);
                int type;
                while ((type = xmlResourceParser.next()) != XmlPullParser.START_TAG &&
                        type != XmlPullParser.END_DOCUMENT) {
                    // Empty
                }
                final AttributeSet attrs = Xml.asAttributeSet(xmlResourceParser);
                FrameLayout frameLayout = new FrameLayout(contextWeakReference.get());
                final ViewGroup.LayoutParams params = frameLayout.generateLayoutParams(attrs);
                int width = params.width;
                int height = params.height;
                layoutParams = new FrameLayout.LayoutParams(width, height);
                frameLayout = null;
                xmlResourceParser.close();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (location != 0) {
                layoutParams.gravity = getGravity(location);
            }
            return layoutParams;
        }

        protected int getGravity(int location) {
            int gravity = Gravity.CENTER;
            switch (location) {
                case LEFT:
                    gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                    break;
                case RIGHT:
                    gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                    break;
                case TOP:
                    gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    break;
                case BOTTOM:
                    gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                    break;
                default:
                    break;
            }
            return gravity;
        }

        public TipView build() {
            TipView tipView = new TipView();
            view.setLayoutParams(getLayoutParams(resId));
            tipView.tipView = view;

            return tipView;
        }
    }
}
