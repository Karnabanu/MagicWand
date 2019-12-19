package ashalagari.karnabanu.magicwand;

import android.app.Instrumentation;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

/**
 * Created by kalpana on 6/8/2016.
 */
public class UiManager {
    MainActivity mainActivity;
    Context context;
    Toolbar toolBar;
    ViewGroup toolBarParent;
    MenuItem refreshItem;
    InputMethodManager inputMethodManager;

    UiManager(Context pcontext){
        context=pcontext;
        mainActivity=(MainActivity)pcontext;
        toolBar=(Toolbar)(mainActivity.findViewById(R.id.toolbar));
        toolBarParent=(ViewGroup)toolBar.getParent();
        inputMethodManager=(InputMethodManager)(mainActivity.getSystemService(context.INPUT_METHOD_SERVICE));
    }

    /*public void makeStartScreenActive(){
        mainActivity.findViewById(R.id.devicesList).startAnimation(mainActivity.unblur);
        //mainActivity.findViewById(R.id.devicelist_item).startAnimation(mainActivity.unblur);
    }

    public void makeStartScreenBlur(){

        /*mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mainActivity.viewFlipper.getDisplayedChild()==2) {
                    ((EditText) mainActivity.findViewById(R.id.KeyBoardInput)).setText(null);
                    inputMethodManager.hideSoftInputFromWindow(((EditText) mainActivity.findViewById(R.id.KeyBoardInput)).getWindowToken(), 0);
                }
                // Next screen comes in from right
                mainActivity.viewFlipper.setInAnimation(mainActivity.getBaseContext(), R.anim.slide_in_from_left);

                // Current screen goes out from left.
                mainActivity.viewFlipper.setOutAnimation(mainActivity.getBaseContext(), R.anim.slide_out_to_right);

                // Display previous screen.
                mainActivity.viewFlipper.setDisplayedChild(0);
                if(mainActivity.findViewById(R.id.toolbar)==null)
                    toolBarParent.addView((View) toolBar);

                showRefreshItem(false);


            }
        });
        mainActivity.getSupportActionBar().setTitle("Magic Wand");
        mainActivity.Caller=0;
        mainActivity.findViewById(R.id.devicesList).startAnimation(mainActivity.blur);
        //mainActivity.findViewById(R.id.devicelist_item).startAnimation(mainActivity.blur);
    }*/



    public void showDevicesListScreen(boolean fromRight){
        if(fromRight){
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mainActivity.viewFlipper.getDisplayedChild()==1){
                        ((EditText)mainActivity.findViewById(R.id.KeyBoardInput)).setText(null);
                        inputMethodManager.hideSoftInputFromInputMethod(((EditText) mainActivity.findViewById(R.id.KeyBoardInput)).getWindowToken(), 0);
                    }
                    System.out.println("Enter");
                    // Next screen comes in from right.
                    mainActivity.viewFlipper.setInAnimation(mainActivity.getBaseContext(), R.anim.slide_in_from_right);

                    // Current screen goes out from left.
                    mainActivity.viewFlipper.setOutAnimation(mainActivity.getBaseContext(), R.anim.slide_out_to_left);

                    // Display previous screen.
                    mainActivity.viewFlipper.setDisplayedChild(0);
                    //refreshItem.setVisible(true);
                    showRefreshItem(true);
                }
            });
        }
        else{
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mainActivity.viewFlipper.getDisplayedChild()==1){
                        ((EditText)mainActivity.findViewById(R.id.KeyBoardInput)).setText(null);
                        inputMethodManager.hideSoftInputFromWindow(((EditText) mainActivity.findViewById(R.id.KeyBoardInput)).getWindowToken(), 0);
                    }
                    System.out.println("Enterd else");
                    // Nextscreen comes in from right.
                    mainActivity.viewFlipper.setInAnimation(mainActivity.getBaseContext(), R.anim.slide_in_from_left);

                    // Current screen goes out from left.
                    mainActivity.viewFlipper.setOutAnimation(mainActivity.getBaseContext(), R.anim.slide_out_to_right);

                    // Display previous screen.
                    mainActivity.viewFlipper.setDisplayedChild(0);

                    if(mainActivity.findViewById(R.id.toolbar)==null)
                        toolBarParent.addView((View) toolBar);
                    showRefreshItem(true);
                }
            });
        }
        mainActivity.getSupportActionBar().setTitle("Showing Paired Devices");
        mainActivity.Caller=0;
    }

    public void showControlScreen(){
        System.out.println("ShowControlScreenEntered");
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inputMethodManager.showSoftInput(mainActivity.findViewById(R.id.KeyBoardInput),0);
                // Next screen comes in from right.
                mainActivity.viewFlipper.setInAnimation(mainActivity.getBaseContext(), R.anim.slide_in_from_right);

                // Current screen goes out from left.
                mainActivity.viewFlipper.setOutAnimation(mainActivity.getBaseContext(), R.anim.slide_out_to_left);

                // Display previous screen.
                mainActivity.viewFlipper.setDisplayedChild(1);
                if(mainActivity.findViewById(R.id.toolbar)!=null)
                    (toolBarParent).removeView((View) toolBar);
                mainActivity.uiManager.showToast("Connected",Toast.LENGTH_SHORT);
            }
        });

        System.out.println("ShowControlScreenExiting");
    }

    public void showToast(final String message, final int duriation){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, duriation).show();
            }
        });
    }

    public void attempSleep(int duriation){
        try{
            Thread.sleep(duriation);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setRefreshViewId(MenuItem item){
        refreshItem= (MenuItem) item;
    }

    public void showRefreshItem(boolean visiblity){
        refreshItem.setVisible(visiblity);
    }

    public void showProgressDailog(final String message){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.connectionStatusDialog.setIndeterminate(true);
                mainActivity.connectionStatusDialog.setMessage(message);
                mainActivity.connectionStatusDialog.setCancelable(false);

                mainActivity.connectionStatusDialog.show();
            }
        });
    }

    public void dismissProgressDailog(){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.connectionStatusDialog.dismiss();
            }
        });
    }

}
