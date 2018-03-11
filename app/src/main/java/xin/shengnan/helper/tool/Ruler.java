package xin.shengnan.helper.tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Administrator on 2018/2/10.
 * */

public class Ruler extends View {

    private final int WIDTH_RATE = 5;
    private final int SCALE_LONG = 5;
    private final int SCALE_SHORT = 3;

    public Ruler(Context context) {
        this(context, null);
    }

    public Ruler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Ruler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth(), height = getHeight();
        float interval = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, getResources().getDisplayMetrics());
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setColor(0x99AA00);
        p.setAlpha(0xFF);
        p.setTextSize(interval * 3);

        canvas.drawRect(0, 0, interval, height, p);
        float intervalNumber = height/interval;
        float widthDraw = interval / WIDTH_RATE;
        int j = 0;
        for (int i = 0; i < intervalNumber; i++) {
            if (0 == (i % 10)) {
                canvas.drawRect(0,i * interval, SCALE_LONG * interval, i * interval + widthDraw, p);
                String unit = "";
                if (0 == i) {
                    unit = "CM";
                }
                canvas.drawText(j + unit, SCALE_LONG * interval, (i + 3) * interval, p);
                j++;
                continue;
            }
            canvas.drawRect(0, i * interval, SCALE_SHORT * interval, i * interval + widthDraw, p);
        }
    }
}
