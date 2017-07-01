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
public class ProgressCircle extends ImageView {
    private static final Paint circlePaint = new Paint();
    private static final Paint barPaint = new Paint();

    /***
     * Initialize all the paint in the static block so multiple views can reuse the paint
     */
    static {
        barPaint.setColor(Color.RED);
        barPaint.setStrokeWidth(20);
        circlePaint.setColor(Color.BLACK);
    }

    private float degrees = 0f;
    public ProgressCircle(Context context) {
        super(context);
        init();
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /***
     * Set the max Height to the width
     */
    public void init() {
        setMaxHeight(getWidth());
    }

    /***
     * Set the degrees, this will mod by 360 because of equivalence
     * if the degrees are different then last time it will redraw teh view
     * @param degrees
     */
    public void setDegrees(float degrees) {
        this.degrees%=360;
        if(this.degrees != degrees) {
            this.degrees = degrees;
            invalidate();
        }
    }

    /***+
     * Gets the decimal of the number
     * it then multiplies it by 360 to get the degrees
     * lastly it subtracts 90 so 0 points up instead of right
     * @param percent
     */
    public void setPercent(float percent) {
        percent%=1;
        setDegrees(percent*360f-90f);
    }

    /***
     * Draw the circle with the arm
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // convert degrees to radians
        float radians = (float)(Math.PI/180f *this.degrees);
        // get the radius
        float mid = getWidth()/2;
        // use equation for a circle  to get x and y
        float x = (float)Math.cos(radians)*mid + mid;
        float y = (float)Math.sin(radians)*mid + mid;

        canvas.drawCircle(mid,mid,mid, circlePaint);
        canvas.drawLine(mid,mid, x,y,barPaint);

    }
}
