package ashalagari.karnabanu.magicwand;


import android.content.Context;
import android.os.Message;

import java.io.IOException;
import java.util.logging.Handler;

public class ConnectionCheck extends Thread implements Runnable{
    MainActivity mainActivity;
    int RECONNECT=1;
    boolean isKilled=false;
    static final int CLIENT_DISCONNECTED=1;
    ConnectionCheck(Context context){
        mainActivity=(MainActivity)context;
    }

    @Override
    public void run() {
        System.out.println("Check Started");
        try{
            int data=mainActivity.inputStream.read();
            if(data==-1){
                if(!isKilled)
                    mainActivity.disconnected(RECONNECT);
                return;
            }
        } catch (IOException e) {
            if(!isKilled)
                mainActivity.disconnected(RECONNECT);
            return;
        }
    }

}
