package com.tech.givvruk.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.elgroup.givvruk.R;

@SuppressLint("ClickableViewAccessibility")
public class HorizontalListView extends AdapterView<ListAdapter> {
    private static final int INSERT_AT_END_OF_LIST = -1;
    private static final int INSERT_AT_START_OF_LIST = 0;
    private static final float FLING_DEFAULT_ABSORB_VELOCITY = 30f;
    private static final float FLING_FRICTION = 0.009f;
    private static final String BUNDLE_ID_CURRENT_X = "BUNDLE_ID_CURRENT_X";
    private static final String BUNDLE_ID_PARENT_STATE = "BUNDLE_ID_PARENT_STATE";
    protected Scroller mFlingTracker = new Scroller(getContext());
    private final GestureListener mGestureListener = new GestureListener();
    private GestureDetector mGestureDetector;
    private int mDisplayOffset;
    protected ListAdapter mAdapter;
    private List<Queue<View>> mRemovedViewsCache = new ArrayList<Queue<View>>();
    private boolean mDataChanged = false;
    private Rect mRect = new Rect();
    private View mViewBeingTouched = null;
    private int mDividerWidth = 0;
    private Drawable mDivider = null;
    protected int mCurrentX;
    protected int mNextX;
    private Integer mRestoreX = null;
    private int mMaxX = Integer.MAX_VALUE;
    private int mLeftViewAdapterIndex;
    private int mRightViewAdapterIndex;
    private int mCurrentlySelectedAdapterIndex;

    private RunningOutOfDataListener mRunningOutOfDataListener = null;

    private int mRunningOutOfDataThreshold = 0;

    private boolean mHasNotifiedRunningLowOnData = false;

    private OnScrollStateChangedListener mOnScrollStateChangedListener = null;

    private OnScrollStateChangedListener.ScrollState mCurrentScrollState = OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE;

    private EdgeEffectCompat mEdgeGlowLeft;

    private EdgeEffectCompat mEdgeGlowRight;

    private int mHeightMeasureSpec;

    private boolean mBlockTouchAction = false;

    private boolean mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = false;

    private OnClickListener mOnClickListener;

    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEdgeGlowLeft = new EdgeEffectCompat(context);
        mEdgeGlowRight = new EdgeEffectCompat(context);
        mGestureDetector = new GestureDetector(context, mGestureListener);
        bindGestureDetector();
        initView();
        retrieveXmlConfiguration(context, attrs);
        setWillNotDraw(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            HoneycombPlus.setFriction(mFlingTracker, FLING_FRICTION);
        }
    }

    private void bindGestureDetector() {
        final View.OnTouchListener gestureListenerHandler = new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
			@Override
            public boolean onTouch(final View v, final MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        };
        setOnTouchListener(gestureListenerHandler);
    }

    private void requestParentListViewToNotInterceptTouchEvents(Boolean disallowIntercept) {
        if (mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent != disallowIntercept) {
            View view = this;

            while (view.getParent() instanceof View) {
                if (view.getParent() instanceof ListView || view.getParent() instanceof ScrollView) {
                    view.getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
                    mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = disallowIntercept;
                    return;
                }

                view = (View) view.getParent();
            }
        }
    }

    private void retrieveXmlConfiguration(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalListView);
            final Drawable d = a.getDrawable(R.styleable.HorizontalListView_android_divider);
            if (d != null) {
            	setDivider(d);
            }
            final int dividerWidth = a.getDimensionPixelSize(R.styleable.HorizontalListView_dividerWidth, 0);
            if (dividerWidth != 0) {
                setDividerWidth(dividerWidth);
            }

            a.recycle();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable(BUNDLE_ID_PARENT_STATE, super.onSaveInstanceState());

        bundle.putInt(BUNDLE_ID_CURRENT_X, mCurrentX);

        return bundle;
    }

    @SuppressLint("ClickableViewAccessibility")
	@Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            mRestoreX = Integer.valueOf((bundle.getInt(BUNDLE_ID_CURRENT_X)));

            super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_ID_PARENT_STATE));
        }
    }

    public void setDivider(Drawable divider) {
        mDivider = divider;

        if (divider != null) {
            setDividerWidth(divider.getIntrinsicWidth());
        } else {
            setDividerWidth(0);
        }
    }

    public void setDividerWidth(int width) {
        mDividerWidth = width;
        requestLayout();
        invalidate();
    }

    private void initView() {
        mLeftViewAdapterIndex = -1;
        mRightViewAdapterIndex = -1;
        mDisplayOffset = 0;
        mCurrentX = 0;
        mNextX = 0;
        mMaxX = Integer.MAX_VALUE;
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
    }
    private void reset() {
        initView();
        removeAllViewsInLayout();
        requestLayout();
    }

    private DataSetObserver mAdapterDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            mDataChanged = true;
            mHasNotifiedRunningLowOnData = false;
            unpressTouchedChild();
            invalidate();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            mHasNotifiedRunningLowOnData = false;
            unpressTouchedChild();
            reset();
            invalidate();
            requestLayout();
        }
    };

    @Override
    public void setSelection(int position) {
        mCurrentlySelectedAdapterIndex = position;
    }

    @Override
    public View getSelectedView() {
        return getChild(mCurrentlySelectedAdapterIndex);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mAdapterDataObserver);
        }

        if (adapter != null) {
            mHasNotifiedRunningLowOnData = false;
            mAdapter = adapter;
            mAdapter.registerDataSetObserver(mAdapterDataObserver);
        }

        initializeRecycledViewCache(mAdapter.getViewTypeCount());
        reset();
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }
    private void initializeRecycledViewCache(int viewTypeCount) {
        mRemovedViewsCache.clear();
        for (int i = 0; i < viewTypeCount; i++) {
            mRemovedViewsCache.add(new LinkedList<View>());
        }
    }

    private View getRecycledView(int adapterIndex) {
        int itemViewType = mAdapter.getItemViewType(adapterIndex);

        if (isItemViewTypeValid(itemViewType)) {
            return mRemovedViewsCache.get(itemViewType).poll();
        }

        return null;
    }

    private void recycleView(int adapterIndex, View view) {
        int itemViewType = mAdapter.getItemViewType(adapterIndex);
        if (isItemViewTypeValid(itemViewType)) {
            mRemovedViewsCache.get(itemViewType).offer(view);
        }
    }

    private boolean isItemViewTypeValid(int itemViewType) {
        return itemViewType < mRemovedViewsCache.size();
    }

    private void addAndMeasureChild(final View child, int viewPos) {
        LayoutParams params = getLayoutParams(child);
        addViewInLayout(child, viewPos, params, true);
        measureChild(child);
    }

    private void measureChild(View child) {
        ViewGroup.LayoutParams childLayoutParams = getLayoutParams(child);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, getPaddingTop() + getPaddingBottom(), childLayoutParams.height);

        int childWidthSpec;
        if (childLayoutParams.width > 0) {
            childWidthSpec = MeasureSpec.makeMeasureSpec(childLayoutParams.width, MeasureSpec.EXACTLY);
        } else {
            childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        child.measure(childWidthSpec, childHeightSpec);
    }

    private ViewGroup.LayoutParams getLayoutParams(View child) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        return layoutParams;
    }

    @SuppressLint("WrongCall")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mAdapter == null) {
            return;
        }
        invalidate();
        if (mDataChanged) {
            int oldCurrentX = mCurrentX;
            initView();
            removeAllViewsInLayout();
            mNextX = oldCurrentX;
            mDataChanged = false;
        }

        if (mRestoreX != null) {
            mNextX = mRestoreX;
            mRestoreX = null;
        }

        if (mFlingTracker.computeScrollOffset()) {
            mNextX = mFlingTracker.getCurrX();
        }

        if (mNextX < 0) {
            mNextX = 0;

            if (mEdgeGlowLeft.isFinished()) {
                mEdgeGlowLeft.onAbsorb((int) determineFlingAbsorbVelocity());
            }

            mFlingTracker.forceFinished(true);
            setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
        } else if (mNextX > mMaxX) {
            mNextX = mMaxX;

            if (mEdgeGlowRight.isFinished()) {
                mEdgeGlowRight.onAbsorb((int) determineFlingAbsorbVelocity());
            }

            mFlingTracker.forceFinished(true);
            setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
        }

        int dx = mCurrentX - mNextX;
        removeNonVisibleChildren(dx);
        fillList(dx);
        positionChildren(dx);
        mCurrentX = mNextX;
        if (determineMaxX()) {
            onLayout(changed, left, top, right, bottom);
            return;
        }

        if (mFlingTracker.isFinished()) {
            if (mCurrentScrollState == OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING) {
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            }
        } else {
            ViewCompat.postOnAnimation(this, mDelayedLayout);
        }
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();

        if (mCurrentX == 0) {
            return 0;
        } else if (mCurrentX < horizontalFadingEdgeLength) {
            return (float) mCurrentX / horizontalFadingEdgeLength;
        } else {
            return 1;
        }
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();

        if (mCurrentX == mMaxX) {
            return 0;
        } else if ((mMaxX - mCurrentX) < horizontalFadingEdgeLength) {
            return (float) (mMaxX - mCurrentX) / horizontalFadingEdgeLength;
        } else {
           return 1;
        }
    }

    private float determineFlingAbsorbVelocity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return IceCreamSandwichPlus.getCurrVelocity(mFlingTracker);
        } else {
            return FLING_DEFAULT_ABSORB_VELOCITY;
        }
    }

    private Runnable mDelayedLayout = new Runnable() {
        @Override
        public void run() {
            requestLayout();
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeightMeasureSpec = heightMeasureSpec;
    };

    private boolean determineMaxX() {
        if (isLastItemInAdapter(mRightViewAdapterIndex)) {
            View rightView = getRightmostChild();

            if (rightView != null) {
                int oldMaxX = mMaxX;
                mMaxX = mCurrentX + (rightView.getRight() - getPaddingLeft()) - getRenderWidth();
                if (mMaxX < 0) {
                    mMaxX = 0;
                }

                if (mMaxX != oldMaxX) {
                    return true;
                }
            }
        }

        return false;
    }

    private void fillList(final int dx) {
        int edge = 0;
        View child = getRightmostChild();
        if (child != null) {
            edge = child.getRight();
        }
        fillListRight(edge, dx);
        edge = 0;
        child = getLeftmostChild();
        if (child != null) {
            edge = child.getLeft();
        }
        fillListLeft(edge, dx);
    }

    private void removeNonVisibleChildren(final int dx) {
        View child = getLeftmostChild();

        while (child != null && child.getRight() + dx <= 0) {
             mDisplayOffset += isLastItemInAdapter(mLeftViewAdapterIndex) ? child.getMeasuredWidth() : mDividerWidth + child.getMeasuredWidth();
            recycleView(mLeftViewAdapterIndex, child);
            removeViewInLayout(child);
            mLeftViewAdapterIndex++;
            child = getLeftmostChild();
        }
        child = getRightmostChild();
        while (child != null && child.getLeft() + dx >= getWidth()) {
            recycleView(mRightViewAdapterIndex, child);
            removeViewInLayout(child);
            mRightViewAdapterIndex--;
            child = getRightmostChild();
        }
    }

    private void fillListRight(int rightEdge, final int dx) {
        while (rightEdge + dx + mDividerWidth < getWidth() && mRightViewAdapterIndex + 1 < mAdapter.getCount()) {
            mRightViewAdapterIndex++;
            if (mLeftViewAdapterIndex < 0) {
                mLeftViewAdapterIndex = mRightViewAdapterIndex;
            }

            View child = mAdapter.getView(mRightViewAdapterIndex, getRecycledView(mRightViewAdapterIndex), this);
            addAndMeasureChild(child, INSERT_AT_END_OF_LIST);
            rightEdge += (mRightViewAdapterIndex == 0 ? 0 : mDividerWidth) + child.getMeasuredWidth();
            determineIfLowOnData();
        }
    }

    private void fillListLeft(int leftEdge, final int dx) {
        while (leftEdge + dx - mDividerWidth > 0 && mLeftViewAdapterIndex >= 1) {
            mLeftViewAdapterIndex--;
            View child = mAdapter.getView(mLeftViewAdapterIndex, getRecycledView(mLeftViewAdapterIndex), this);
            addAndMeasureChild(child, INSERT_AT_START_OF_LIST);
            leftEdge -= mLeftViewAdapterIndex == 0 ? child.getMeasuredWidth() : mDividerWidth + child.getMeasuredWidth();
            mDisplayOffset -= leftEdge + dx == 0 ? child.getMeasuredWidth() : mDividerWidth + child.getMeasuredWidth();
        }
    }
    private void positionChildren(final int dx) {
        int childCount = getChildCount();

        if (childCount > 0) {
            mDisplayOffset += dx;
            int leftOffset = mDisplayOffset;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                int left = leftOffset + getPaddingLeft();
                int top = getPaddingTop();
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);
                leftOffset += child.getMeasuredWidth() + mDividerWidth;
            }
        }
    }
    private View getLeftmostChild() {
        return getChildAt(0);
    }
    private View getRightmostChild() {
        return getChildAt(getChildCount() - 1);
    }

    private View getChild(int adapterIndex) {
        if (adapterIndex >= mLeftViewAdapterIndex && adapterIndex <= mRightViewAdapterIndex) {
            return getChildAt(adapterIndex - mLeftViewAdapterIndex);
        }

        return null;
    }
    private int getChildIndex(final int x, final int y) {
        int childCount = getChildCount();

        for (int index = 0; index < childCount; index++) {
            getChildAt(index).getHitRect(mRect);
            if (mRect.contains(x, y)) {
                return index;
            }
        }

        return -1;
    }
    private boolean isLastItemInAdapter(int index) {
        return index == mAdapter.getCount() - 1;
    }

    private int getRenderHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private int getRenderWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    public void scrollTo(int x) {
        mFlingTracker.startScroll(mNextX, 0, x - mNextX, 0);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING);
        requestLayout();
    }

    @Override
    public int getFirstVisiblePosition() {
        return mLeftViewAdapterIndex;
    }

    @Override
    public int getLastVisiblePosition() {
        return mRightViewAdapterIndex;
    }

    private void drawEdgeGlow(Canvas canvas) {
        if (mEdgeGlowLeft != null && !mEdgeGlowLeft.isFinished() && isEdgeGlowEnabled()) {
            final int restoreCount = canvas.save();
            final int height = getHeight();

            canvas.rotate(-90, 0, 0);
            canvas.translate(-height + getPaddingBottom(), 0);

            mEdgeGlowLeft.setSize(getRenderHeight(), getRenderWidth());
            if (mEdgeGlowLeft.draw(canvas)) {
                invalidate();
            }

            canvas.restoreToCount(restoreCount);
        } else if (mEdgeGlowRight != null && !mEdgeGlowRight.isFinished() && isEdgeGlowEnabled()) {
            final int restoreCount = canvas.save();
            final int width = getWidth();
            canvas.rotate(90, 0, 0);
            canvas.translate(getPaddingTop(), -width);
            mEdgeGlowRight.setSize(getRenderHeight(), getRenderWidth());
            if (mEdgeGlowRight.draw(canvas)) {
                invalidate();
            }

            canvas.restoreToCount(restoreCount);
        }
    }

    private void drawDividers(Canvas canvas) {
        final int count = getChildCount();
        final Rect bounds = mRect;
        mRect.top = getPaddingTop();
        mRect.bottom = mRect.top + getRenderHeight();
        for (int i = 0; i < count; i++) {
            if (!(i == count - 1 && isLastItemInAdapter(mRightViewAdapterIndex))) {
                View child = getChildAt(i);
                bounds.left = child.getRight();
                bounds.right = child.getRight() + mDividerWidth;
                if (bounds.left < getPaddingLeft()) {
                    bounds.left = getPaddingLeft();
                }
                if (bounds.right > getWidth() - getPaddingRight()) {
                    bounds.right = getWidth() - getPaddingRight();
                }
                drawDivider(canvas, bounds);
                if (i == 0 && child.getLeft() > getPaddingLeft()) {
                    bounds.left = getPaddingLeft();
                    bounds.right = child.getLeft();
                    drawDivider(canvas, bounds);
                }
            }
        }
    }
    private void drawDivider(Canvas canvas, Rect bounds) {
        if (mDivider != null) {
            mDivider.setBounds(bounds);
            mDivider.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDividers(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawEdgeGlow(canvas);
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
    }

    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mFlingTracker.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_FLING);
        requestLayout();
        return true;
    }

    protected boolean onDown(MotionEvent e) {
        mBlockTouchAction = !mFlingTracker.isFinished();
        mFlingTracker.forceFinished(true);
        setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);

        unpressTouchedChild();

        if (!mBlockTouchAction) {
            final int index = getChildIndex((int) e.getX(), (int) e.getY());
            if (index >= 0) {
                mViewBeingTouched = getChildAt(index);
                if (mViewBeingTouched != null) {
                    mViewBeingTouched.setPressed(true);
                    refreshDrawableState();
                }
            }
        }

        return true;
    }
    private void unpressTouchedChild() {
        if (mViewBeingTouched != null) {
            mViewBeingTouched.setPressed(false);
            refreshDrawableState();
            mViewBeingTouched = null;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return HorizontalListView.this.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            requestParentListViewToNotInterceptTouchEvents(true);
            setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_TOUCH_SCROLL);
            unpressTouchedChild();
            mNextX += (int) distanceX;
            updateOverscrollAnimation(Math.round(distanceX));
            requestLayout();

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            unpressTouchedChild();
            OnItemClickListener onItemClickListener = getOnItemClickListener();

            final int index = getChildIndex((int) e.getX(), (int) e.getY());
            if (index >= 0 && !mBlockTouchAction) {
                View child = getChildAt(index);
                int adapterIndex = mLeftViewAdapterIndex + index;

                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(HorizontalListView.this, child, adapterIndex, mAdapter.getItemId(adapterIndex));
                    return true;
                }
            }

            if (mOnClickListener != null && !mBlockTouchAction) {
                mOnClickListener.onClick(HorizontalListView.this);
            }

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            unpressTouchedChild();

            final int index = getChildIndex((int) e.getX(), (int) e.getY());
            if (index >= 0 && !mBlockTouchAction) {
                View child = getChildAt(index);
                OnItemLongClickListener onItemLongClickListener = getOnItemLongClickListener();
                if (onItemLongClickListener != null) {
                    int adapterIndex = mLeftViewAdapterIndex + index;
                    boolean handled = onItemLongClickListener.onItemLongClick(HorizontalListView.this, child, adapterIndex, mAdapter
                            .getItemId(adapterIndex));

                    if (handled) {
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    }
                }
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mFlingTracker == null || mFlingTracker.isFinished()) {
                setCurrentScrollState(OnScrollStateChangedListener.ScrollState.SCROLL_STATE_IDLE);
            }
            requestParentListViewToNotInterceptTouchEvents(false);

            releaseEdgeGlow();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            unpressTouchedChild();
            releaseEdgeGlow();
            requestParentListViewToNotInterceptTouchEvents(false);
        }

        return super.onTouchEvent(event);
    }

    private void releaseEdgeGlow() {
        if (mEdgeGlowLeft != null) {
            mEdgeGlowLeft.onRelease();
        }

        if (mEdgeGlowRight != null) {
            mEdgeGlowRight.onRelease();
        }
    }
    public void setRunningOutOfDataListener(RunningOutOfDataListener listener, int numberOfItemsLeftConsideredLow) {
        mRunningOutOfDataListener = listener;
        mRunningOutOfDataThreshold = numberOfItemsLeftConsideredLow;
    }
    public static interface RunningOutOfDataListener {
        void onRunningOutOfData();
    }
    private void determineIfLowOnData() {
        if (mRunningOutOfDataListener != null && mAdapter != null &&
                mAdapter.getCount() - (mRightViewAdapterIndex + 1) < mRunningOutOfDataThreshold) {
            if (!mHasNotifiedRunningLowOnData) {
                mHasNotifiedRunningLowOnData = true;
                mRunningOutOfDataListener.onRunningOutOfData();
            }
        }
    }
    @Override
    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }
    public interface OnScrollStateChangedListener {
        public enum ScrollState {
            SCROLL_STATE_IDLE,
            SCROLL_STATE_TOUCH_SCROLL,
            SCROLL_STATE_FLING
        }
        public void onScrollStateChanged(ScrollState scrollState);
    }
    public void setOnScrollStateChangedListener(OnScrollStateChangedListener listener) {
        mOnScrollStateChangedListener = listener;
    }

    private void setCurrentScrollState(OnScrollStateChangedListener.ScrollState newScrollState) {
        if (mCurrentScrollState != newScrollState && mOnScrollStateChangedListener != null) {
            mOnScrollStateChangedListener.onScrollStateChanged(newScrollState);
        }

        mCurrentScrollState = newScrollState;
    }

    private void updateOverscrollAnimation(final int scrolledOffset) {
        if (mEdgeGlowLeft == null || mEdgeGlowRight == null) return;
        int nextScrollPosition = mCurrentX + scrolledOffset;
        if (mFlingTracker == null || mFlingTracker.isFinished()) {
            if (nextScrollPosition < 0) {
                int overscroll = Math.abs(scrolledOffset);
                mEdgeGlowLeft.onPull((float) overscroll / getRenderWidth());
                if (!mEdgeGlowRight.isFinished()) {
                    mEdgeGlowRight.onRelease();
                }
            } else if (nextScrollPosition > mMaxX) {
                int overscroll = Math.abs(scrolledOffset);
                mEdgeGlowRight.onPull((float) overscroll / getRenderWidth());
                if (!mEdgeGlowLeft.isFinished()) {
                    mEdgeGlowLeft.onRelease();
                }
            }
        }
    }
    private boolean isEdgeGlowEnabled() {
        if (mAdapter == null || mAdapter.isEmpty()) return false;
        return mMaxX > 0;
    }

    @TargetApi(11)
    private static final class HoneycombPlus {
        static {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                throw new RuntimeException("Should not get to HoneycombPlus class unless sdk is >= 11!");
            }
        }
        public static void setFriction(Scroller scroller, float friction) {
            if (scroller != null) {
                scroller.setFriction(friction);
            }
        }
    }

    @TargetApi(14)
    private static final class IceCreamSandwichPlus {
        static {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                throw new RuntimeException("Should not get to IceCreamSandwichPlus class unless sdk is >= 14!");
            }
        }
        public static float getCurrVelocity(Scroller scroller) {
            return scroller.getCurrVelocity();
        }
    }
}
