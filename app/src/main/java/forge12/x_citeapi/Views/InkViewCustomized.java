package forge12.x_citeapi.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.simplify.ink.InkView;

/**
 * Used to receive the signature from the customer
 */
public class InkViewCustomized extends InkView {
    static final int DEFAULT_FLAGS = FLAG_INTERPOLATION | FLAG_RESPONSIVE_WIDTH;

    public InkViewCustomized(Context context) {
        this(context, DEFAULT_FLAGS);
    }

    public InkViewCustomized(Context context, int flags) {
        super(context);
    }

    public InkViewCustomized(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public InkViewCustomized(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        getParent().requestDisallowInterceptTouchEvent(true);
        super.onDraw(canvas);
    }
}
