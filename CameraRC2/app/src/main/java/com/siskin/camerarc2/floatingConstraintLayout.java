package com.siskin.camerarc2;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by header on 2017/11/29.
 */

public class floatingConstraintLayout extends ConstraintLayout {
    public floatingConstraintLayout(Context context) {
        super(context);
    }

    public floatingConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public floatingConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
