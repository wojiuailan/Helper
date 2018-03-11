package xin.shengnan.helper.tool;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2018/1/31.
 */

public class MyDL extends DrawerLayout {
    public MyDL(Context context) {
        this(context,null);
    }

    public MyDL(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyDL(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     *
     * 判断左右滑动
     *
     * */
    private void checkHorizontalMove(MotionEvent ev) {

        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                pointStart.x = (int)ev.getX();
                pointStart.y = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int xMove = (int)ev.getX() - pointStart.x;
                int yMove = (int)ev.getY() - pointStart.y;
                if (Math.abs(xMove) > MOVE && Math.abs(xMove) > Math.abs(yMove)){
                    touchIsHorizontal = true;
                    touchIs = true;
                }
                pointStart.x = (int)ev.getX();
                pointStart.y = (int)ev.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        super.dispatchTouchEvent(ev);
    }


    private boolean touchIsHorizontal = false;
    private boolean touchIs = false;

    private Point pointStart = new Point();

    private final int MOVE = 10;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (!touchIsHorizontal){
            checkHorizontalMove(ev);
            return true;
        }

        if (touchIs){
            switch(ev.getAction()){
                case MotionEvent.ACTION_MOVE:
                    int scrollX = getScrollX();
                    int x = (int) (ev.getX() - pointStart.x);

                    if (x > 0){
                        openDrawer(GravityCompat.START);
                    } else {
                        closeDrawer(GravityCompat.START);
                    }

                    pointStart.x = (int) ev.getX();
                    pointStart.y = (int)ev.getY();
                default:
                    touchIsHorizontal = false;
                    touchIs = false;
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
