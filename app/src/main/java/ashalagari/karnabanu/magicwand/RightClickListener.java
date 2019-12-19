package ashalagari.karnabanu.magicwand;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class RightClickListener implements View.OnTouchListener {
    MainActivity mainActivity;
    RightClickListener(Context context){
        mainActivity=(MainActivity)context;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        byte[] command = new byte[1];
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                command[0] = 4;
                mainActivity.sendbuffer(command);
                return true;
            case MotionEvent.ACTION_UP:
                command[0] = 5;
                mainActivity.sendbuffer(command);
                return true;
        }
        return false;
    }

}
