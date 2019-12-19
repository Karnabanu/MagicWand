package ashalagari.karnabanu.magicwand;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.widget.Button;

/**
 * Created by kalpana on 6/7/2016.
 */
public class WatchDog extends BroadcastReceiver {
    private MainActivity mainActivity;
    WatchDog(MainActivity mA){
        mainActivity=mA;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final MainActivity mainActivity=(MainActivity)context;
        String action=intent.getAction();
        if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
            if(mainActivity.BA.getState()==BluetoothAdapter.STATE_TURNING_OFF){
                //((Button)mainActivity.findViewById(R.id.BuletoothSwitch)).setText(mainActivity.bluetoohOnMessage);
                mainActivity.uiManager.showToast("Bluetooth Turning Off", Toast.LENGTH_SHORT);
                mainActivity.setBluetoothIcon(false);
                //mainActivity.menu.findItem(R.id.bluetoothSwitch).setIcon(mainActivity.icBluetoothDisable);
                if(mainActivity.viewFlipper.getDisplayedChild()!=0) {
                    if(mainActivity.connectionCheck!=null)
                        mainActivity.connectionCheck.isKilled=true;
                    mainActivity.uiManager.showDevicesListScreen(true);
                    mainActivity.setPairedDeviceslist();
                    mainActivity.disconnected(0);
                }
            }
            if(mainActivity.BA.getState()==BluetoothAdapter.STATE_TURNING_ON){
                //((Button)mainActivity.findViewById(R.id.BuletoothSwitch)).setText(mainActivity.bluetoothOffMessage);
                //mainActivity.setBluetoothStatus(0, mainActivity.menu.findItem(R.id.bluetoothSwitch));
                mainActivity.setBluetoothIcon(true);
                //mainActivity.uiManager.makeStartScreenActive();
                //mainActivity.menu.findItem(R.id.bluetoothSwitch).setIcon(mainActivity.icBluetoothDisable);
                mainActivity.uiManager.showToast("Bluetooth Turning On", Toast.LENGTH_SHORT);
            }

        }
    }
}
