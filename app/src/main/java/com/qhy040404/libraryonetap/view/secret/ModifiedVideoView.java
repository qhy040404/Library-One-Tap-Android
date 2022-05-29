package com.qhy040404.libraryonetap.view.secret;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class ModifiedVideoView extends VideoView {
    public ModifiedVideoView(Context context) {
        super(context);
    }

    public ModifiedVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModifiedVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
}
