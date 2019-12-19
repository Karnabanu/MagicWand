package ashalagari.karnabanu.magicwand;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import java.nio.ByteBuffer;

public class TouchPad extends View {

    public int width;
    public int height;
    private Canvas mCanvas;
    Context context;
    private float mX, mY;
    private boolean doubleTouch=false;

    private static int ClickFlag=0;
    private MainActivity mainActivity= (MainActivity) getContext();
    private MotionEvent LeftPreesEvent;

    public TouchPad(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
    }

    byte[] toBytes(int i)
    {
        byte[] result = new byte[4];

        result[3] = (byte) (i >> 24);
        result[2] = (byte) (i >> 16);
        result[1] = (byte) (i >> 8);
        result[0] = (byte) (i );

        return result;
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }



    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y,MotionEvent event) {
        ClickFlag=1;
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        ClickFlag=0;
        float dx=x-mX;
        float dy=y-mY;
        //System.out.println(dx+","+dy);
        byte[] command=new byte[9];

        if(!doubleTouch)
            command[0]=0;
        else
            command[0]=6;
        System.arraycopy(toBytes(/*dx*/(int)Math.round(dx)), 0, command, 1, 4);
        System.arraycopy(toBytes(/*dy*/(int) Math.round(dy)), 0, command, 5, 4);
        mainActivity.sendbuffer(command);
        mX=x;
        mY=y;
    }




    // when ACTION_UP stop touch
    private void upTouch(MotionEvent event) {
        if(ClickFlag==1){
            event.setAction(MotionEvent.ACTION_DOWN);
            mainActivity.leftClickListener.onTouch(this,event);
            event.setAction(MotionEvent.ACTION_UP);
            mainActivity.leftClickListener.onTouch(this,event);
        }
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        //System.out.println("toi "+event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y, event);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch(event);
                invalidate();
                System.out.println("up");
                break;
            case MotionEvent.ACTION_POINTER_2_DOWN:
                System.out.println("detected");
                doubleTouch=true;
                break;
            case MotionEvent.ACTION_POINTER_2_UP:
                System.out.println("relesedected");
                doubleTouch=false;
                break;
            case MotionEvent.ACTION_POINTER_1_UP:
                System.out.println("1up");
                doubleTouch=false;
                break;
        }
        return true;
    }


}