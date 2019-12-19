package ashalagari.karnabanu.magicwand;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;



public class LeftClickListener implements View.OnTouchListener{
    MainActivity mainActivity;
    LeftClickListener(Context context){
        mainActivity=(MainActivity)context;
    }

    @Override
    public boolean onTouch(View v,MotionEvent event) {
        byte[] command = new byte[1];
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                command[0] = 2;
                mainActivity.sendbuffer(command);
                return true;
            case MotionEvent.ACTION_UP:
                command[0] = 3;
                mainActivity.sendbuffer(command);
                return true;
        }
        return false;
    }

}