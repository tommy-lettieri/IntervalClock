package lettieri.intervalclock.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Luigi on 7/1/2017.
 */
public class ProgressBar extends ImageView {
    private static final Paint paint = new Paint();
    private float percent = 0;

    /***
     * Make the paint static for reuse
     */
    static {
        paint.setColor(Color.RED);
    }

    public ProgressBar(Context context)
    {
        super(context);
        init();
    }

    public ProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /***
     * Sets the view background to black
     */
    private void init() {
        setBackgroundColor(Color.BLACK);
    }

    /***
     * Since this cannot go over 100 we keep the percent circlular meaning 1.1 = 2.1
     * To do this we just grab the decimal off the given input
     * if that percent has changed then redraw the view
     * and set the percent
     * @param percent
     */
    public void setPercent(float percent) {
        percent%=1;
        if(this.percent != percent) {
            this.percent = percent;
            invalidate();
        }
    }

    /***
     * Draw a rectangle from 0,0 ==> progress, height to fill in a rectangle representing time
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float pos = percent * getWidth();
        canvas.drawRect(0,0,pos,getHeight(),paint);
    }
}
