package com.zy.myapplication.newer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

import com.zy.myapplication.R;
import com.zy.myapplication.utils.Utils;

/**
 * @author ZhangYue
 * @time 2018/6/14  14:14
 * <p>
 * 考虑到之前的布局限制比较多，布局不够灵活，需要重新设计一下
 * <p>
 * 主要的需要进一步解决的优化项：
 * 编写xml方式简化——可以设置两部分控件内容区域，菜单区域，代码和xml两种方式，编写需要严格按照该子布局的前两个布局是菜单页和内容页去排布
 * 左右两边控件有要求，必须要是View Group的子类————自定义两个控件继承ViewGroup
 * 灵活设置提示小图标——类似侧滑箭头/提示————
 */

public class ZSlideLayout extends ViewGroup {
    private static final int MENU_RIGHT_MARGIN_NORMAL = 300;
    private static final int MENU_TOGGLE_DURATION = 200;
    //防蹭滑灵敏度默认值——滑动影响点击事件
    private static final int MENU_SLIDE_SENSITI = 5;
    public static final String TAG = "---zy---";

    private ZSlidingMenu mMenu;
    private ZSlidingContent mContent;
    private Scroller mScroller;

    private int screenWidth;
    private int screenHeight;

    /**
     * 距离右边的间距，主要影响的是menu出现的宽度
     */
    private int menuRightMargin;
    /**
     * 点击事件的灵敏度
     */
    private int sensety;

    /**
     * 默认情况下菜单是否是打开的
     */
    boolean isInitOpen;

    /**
     * 现在的状态是否为打开
     */
    private boolean isOpen;

    /**
     * 菜单部分的宽度
     */
    private int menuWidth;

    /**
     * 控件的高度
     * 当初是设计来给主页面侧边栏的，但后面有需求要在页面内
     * 做成侧拉菜单样式所以这块会涉及到该控件上方控件的高度
     * 问题，最后演变成高度默认但是可以根据实际情况设置
     */
    private int ZLayoutHeight;

    /**
     * 点击右边的内容页 true-菜单会自动收缩只展示内容页，先处理点击处的焦点问题进行综合管理焦点
     * false-菜单不会自动收缩，菜单和内容分开处理焦点
     */
    private boolean autoHide;

    // private int menuHeight;

    public ZSlideLayout(Context context) {
        this(context, null);
    }

    public ZSlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZSlideLayout(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager windowM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowM.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;

        ZLayoutHeight = screenHeight - Utils.getStatusBarHeight(context);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mScroller = new Scroller(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZSlideLayout);
        //菜单距离右边的距离
        menuRightMargin = typedArray.getDimensionPixelOffset(R.styleable.ZSlideLayout_menuRightMargin, MENU_RIGHT_MARGIN_NORMAL);
        //菜单默认是打开还是关闭
        isInitOpen = typedArray.getBoolean(R.styleable.ZSlideLayout_menuInitOpened, false);
        isOpen = isInitOpen;
        sensety = typedArray.getDimensionPixelOffset(R.styleable.ZSlideLayout_menuSensitivity, MENU_SLIDE_SENSITI);
        ZLayoutHeight = typedArray.getDimensionPixelOffset(R.styleable.ZSlideLayout_menuHeight, ZLayoutHeight);
        autoHide = typedArray.getBoolean(R.styleable.ZSlideLayout_menuAutoFocused, false);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMenu = (ZSlidingMenu) getChildAt(0);
        mContent = (ZSlidingContent) getChildAt(1);

        menuWidth = mMenu.getLayoutParams().width = screenWidth - menuRightMargin;
        mMenu.getLayoutParams().height = ZLayoutHeight;
        int contentWidth = mContent.getLayoutParams().width = screenWidth;
        mContent.getLayoutParams().height = ZLayoutHeight;

        measureChild(mContent, widthMeasureSpec, heightMeasureSpec);
        measureChild(mMenu, widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(menuWidth + contentWidth, ZLayoutHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (onStateChangedListener != null)
            onStateChangedListener.stateChange(isInitOpen);
        if (isInitOpen) {
            mMenu.layout(0, 0, menuWidth, ZLayoutHeight);
            mContent.layout(menuWidth, 0, menuWidth + screenWidth, ZLayoutHeight);
        } else {
            mMenu.layout(-menuWidth, 0, 0, ZLayoutHeight);
            mContent.layout(0, 0, screenWidth, ZLayoutHeight);
        }
    }

    int firstX = 0;
    int lastX = 0;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isInitOpen) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    firstX = (int) event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    lastX = (int) event.getX();
                    int dx = lastX - firstX;
                    if (dx > 0) {// 手势向右
                        if (-getScrollX() + dx > menuWidth) {// 达到边界//
                            scrollTo(-menuWidth, 0);
                        } else {
//                        mMenu.setTranslationX(2 * (menuWidth + getScrollX()) / 3);
                            scrollBy(-dx, 0);
                        }
                    } else {// 手势向左
                        if (getScrollX() + Math.abs(dx) >= 0) {// 达到边界
                            scrollTo(0, 0);
                        } else {
//                        mMenu.setTranslationX(2 * (menuWidth + getScrollX()) / 3);
                            scrollBy(-dx, 0);
                        }
                    }
                    firstX = lastX;
                    break;
                case MotionEvent.ACTION_UP:
                    if (getScrollX() < -menuWidth / 2) {// 打开
                        openMenu();
                    } else {
                        closeMenu();
                    }
                    break;
                default:
                    break;
            }

            return true;
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    firstX = (int) event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    lastX = (int) event.getX();
                    int dx = lastX - firstX;
                    if (dx > 0) {// 手势向右
                        if (-getScrollX() + dx >= 0) {// 达到边界//
                            scrollTo(0, 0);
                        } else {
//                        mMenu.setTranslationX(-2 * (-menuWidth + getScrollX()) / 3);
                            scrollBy(-dx, 0);
                        }
                    } else {// 手势向左
                        if (-getScrollX() + dx <= -menuWidth) {// 达到边界
                            scrollTo(menuWidth, 0);
                        } else {
//                        mMenu.setTranslationX(2 * (menuWidth + getScrollX()) / 3);
                            scrollBy(-dx, 0);
                        }
                    }
                    firstX = lastX;
                    break;
                case MotionEvent.ACTION_UP:
                    if (getScrollX() > menuWidth / 2) {// 打开
                        closeMenu();
                    } else {
                        openMenu();
                    }
                    break;
                default:
                    break;
            }

            return false;
        }

    }

    public void toggleMenu() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    public boolean isOpened() {
        return isOpen;
    }

    public void openMenu() {
        if (onStateChangedListener != null)
            onStateChangedListener.stateChange(true);
        isOpen = true;
        if (isInitOpen) {
            mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, MENU_TOGGLE_DURATION);
        } else
            mScroller.startScroll(getScrollX(), 0, -menuWidth - getScrollX(), 0, MENU_TOGGLE_DURATION);
        invalidate();
    }

    private void closeMenu() {
        if (onStateChangedListener != null)
            onStateChangedListener.stateChange(false);
        isOpen = false;
        if (isInitOpen) {
            mScroller.startScroll(getScrollX(), 0, menuWidth - getScrollX(), 0, MENU_TOGGLE_DURATION);
        } else
            mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, MENU_TOGGLE_DURATION);
        invalidate();
    }

    int iFirstX, iFirstY;
    int iLastX, iLastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean needIntercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                iFirstX = (int) ev.getX();
                iFirstY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                iLastX = (int) ev.getX();
                iLastY = (int) ev.getY();
                int dx = iLastX - iFirstX;
                int dy = iLastY - iFirstY;
                if (Math.abs(dx) < sensety || Math.abs(dy) < sensety) {
                    break;
                }
                if (Math.abs(dx) > Math.abs(dy)) {
                    needIntercept = true;
                } else {
                    needIntercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (autoHide) {
                    if (ev.getX() > menuWidth && isOpen) {
                        closeMenu();
                        return true;
                    }
                }
                break;
        }
        iLastX = (int) ev.getX();
        // 这个重置步骤很重要！！！
        firstX = (int) ev.getX();
        return needIntercept;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
//            mMenu.setTranslationX(2 * (menuWidth + getScrollX()) / 3);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (tipHelper.menuTipHelper != null) {
            tipHelper.menuTipHelper.tipAttachedToMenu();
        }
        if (tipHelper.contentTipHelper != null) {
            tipHelper.contentTipHelper.tipAttachedToContent();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    private OnStateChangedListener onStateChangedListener;

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.onStateChangedListener = onStateChangedListener;
    }

    public static class TipHelper {

        protected MenuTipHelper menuTipHelper;

        protected ContentTipHelper contentTipHelper;

        interface MenuTipHelper {
            public void tipAttachedToMenu();
        }

        interface ContentTipHelper {
            public void tipAttachedToContent();
        }
    }

    TipHelper tipHelper;

    public void addTipViewOnMenu(final TipView tipView) {
        if (tipHelper == null) {
            tipHelper = new TipHelper();
        }
        tipHelper.menuTipHelper = new TipHelper.MenuTipHelper() {
            @Override
            public void tipAttachedToMenu() {
                if (tipView.getContentView() != null) {
                    mMenu.addTips(tipView);
                }
            }
        };

    }

    public void addTipViewOnContent(final TipView tipView) {
        if (tipHelper == null) {
            tipHelper = new TipHelper();
        }
        tipHelper.contentTipHelper = new TipHelper.ContentTipHelper() {
            @Override
            public void tipAttachedToContent() {
                if (tipView.getContentView() != null) {
                    mContent.addTips(tipView);
                }
            }
        };
    }

}
