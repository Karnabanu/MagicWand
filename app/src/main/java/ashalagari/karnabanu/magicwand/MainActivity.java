package ashalagari.karnabanu.magicwand;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {
    MainActivity mainActivity;
    public BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    public final String bluetoothOffMessage="Turn Off Bluetooth";
    public final String bluetoohOnMessage="Trun On Bluetooth";
    int devicePosition=-1;
    BluetoothDevice device;
    BluetoothSocket socket = null;
    InputStream inputStream;
    OutputStream sendingStream;
    LeftClickListener leftClickListener;
    RightClickListener rightClickListener;
    KeyBoardListener keyBoardListener;
    ViewFlipper viewFlipper;
    ConnectionCheck connectionCheck;
    ProgressDialog connectionStatusDialog;
    final WatchDog watchDog=new WatchDog(this);
    FloatingSettingsListener floatingSettingsListener;
    UiManager uiManager;
    public final ReentrantLock sendLock=new ReentrantLock();
    int Caller=0;
    int DISCONNECTED=1;
    int CONNECT_BUTTON=0;

    Drawable icBluetoothEnable;
    Drawable icBluetoothDisable;
    Menu menu;
    //AlphaAnimation blur = new AlphaAnimation(0.1F, 0.1F);
    //AlphaAnimation unblur=new AlphaAnimation(1F,1F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainActivity=this;

        this.registerReceiver(watchDog, new IntentFilter(BA.ACTION_STATE_CHANGED));
        connectionStatusDialog=new ProgressDialog((Context)mainActivity,R.style.AppTheme_Dark_Dialog);
        new Thread(){
            @Override
            public void run(){
                viewFlipper=(ViewFlipper)findViewById(R.id.viewFlipper);
                uiManager=new UiManager(mainActivity);
                /*blur.setDuration(0); // Make animation instant
                blur.setFillAfter(true); // Tell it to persist after the animation ends
                unblur.setDuration(0);
                unblur.setFillAfter(true);*/

                BA = BluetoothAdapter.getDefaultAdapter();

                /*if(BA.isEnabled())
                    uiManager.makeStartScreenActive();
                else
                    uiManager.makeStartScreenBlur();*/

                ((ListView)findViewById(R.id.devicesList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        devicePosition = position;
                    }
                });
                setPairedDeviceslist();

                keyBoardListener=new KeyBoardListener((Context)mainActivity);
                ((EditText)findViewById(R.id.KeyBoardInput)).addTextChangedListener(keyBoardListener);

                leftClickListener=new LeftClickListener((Context)mainActivity);
                ((Button) findViewById(R.id.LeftClick)).setOnTouchListener(leftClickListener);

                rightClickListener=new RightClickListener((Context)mainActivity);
                ((Button) findViewById(R.id.RightClick)).setOnTouchListener(rightClickListener);

                floatingSettingsListener=new FloatingSettingsListener((Context)mainActivity);
                ((FloatingActionButton)findViewById(R.id.floatingSettings)).setOnClickListener(floatingSettingsListener);


            }
        }.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu pmenu) {
        this.menu=pmenu;
        getMenuInflater().inflate(R.menu.menu_main, pmenu);
        icBluetoothEnable= ResourcesCompat.getDrawable(getResources(), R.drawable.ic_bluetooth_enable, null);
        icBluetoothDisable=ResourcesCompat.getDrawable(getResources(),R.drawable.ic_bluetooth_disable,null);
        uiManager.setRefreshViewId(pmenu.findItem(R.id.action_refresh));
        setBluetoothStatus(0, pmenu.findItem(R.id.bluetoothSwitch));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.bluetoothSwitch){
            setBluetoothStatus(1,null);
            return true;
        }
        if (id == R.id.action_exit) {
            finish();
            return true;
        }
        if(id==R.id.action_refresh){
            if(!BA.isEnabled())
                uiManager.showToast("Please turn on bluetooth",1000);
            setPairedDeviceslist();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickBluetoothSwitch(View v){
        if (!(BA.isEnabled()) )
            BA.enable();
        else
            BA.disable();
    }

    public void onClickStartMagicWand(View v){

        if(!(BA.isEnabled())){
            uiManager.showToast("Please Turn on Bluetooth",Toast.LENGTH_LONG);
            return;
        }
        uiManager.showDevicesListScreen(true);
    }

    public void setPairedDeviceslist(){

        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) {
            list.add(bt.getName());
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,R.layout.devicelist_item, list);
        ((ListView)findViewById(R.id.devicesList)).setAdapter(adapter);
        devicePosition=-1;
    }


    public void connect(View v) {
        final Context context=this;
        new Thread() {
            @Override
            public void run() {
                if(!BA.isEnabled()){
                    uiManager.showToast("Please switch On the bluetooth",Toast.LENGTH_SHORT);
                    return;
                }
                if(devicePosition==-1){
                    uiManager.showToast("Plese Select Device",Toast.LENGTH_SHORT);
                    return;
                }
                try {
                    if(Caller==DISCONNECTED)
                        uiManager.showProgressDailog("");
                    if(Caller==CONNECT_BUTTON)
                        uiManager.showProgressDailog("Connecting..");

                    ArrayList temp=new ArrayList();
                    temp.addAll(pairedDevices);
                    device= (BluetoothDevice) (temp.get(devicePosition));

                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                    socket.connect();

                    inputStream = socket.getInputStream();
                    sendingStream = socket.getOutputStream();
                    try {
                        connectionCheck = new ConnectionCheck(context);
                        connectionCheck.start();
                    } catch (Exception e) {
                        System.out.println("Bug " + e);
                    }
                    if(Caller==CONNECT_BUTTON)
                        uiManager.attempSleep(250);
                    if(Caller==DISCONNECTED)
                        uiManager.attempSleep(100);
                    uiManager.dismissProgressDailog();
                    if(Caller==CONNECT_BUTTON)
                        uiManager.showControlScreen();
                }
                catch (IOException e) {
                    if(Caller==CONNECT_BUTTON) {
                        uiManager.attempSleep(1000);
                        uiManager.dismissProgressDailog();
                        uiManager.showToast("Client Unreachable", Toast.LENGTH_LONG);
                    }
                    if(Caller==DISCONNECTED) {
                        uiManager.attempSleep(250);
                        uiManager.dismissProgressDailog();
                        uiManager.showDevicesListScreen(false);
                        uiManager.showToast("Client Closed", Toast.LENGTH_LONG);
                    }
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        System.out.println("From magic wand"+"Unableto close the socket");
                    }
                }
                return;
            }
        }.start();
    }

    public void disconnected(int MODE){
        int ABORT=0;

        if(MODE==ABORT){
            if (socket != null) {
                try{
                    System.out.println("From MainActivity" + "Client Closing");
                    connectionCheck.isKilled=true;
                    socket.close();
                    sendingStream.close();
                    inputStream.close();

                    if(!(BA.isEnabled()))
                        return;
                    uiManager.showDevicesListScreen(false);
                    uiManager.showToast("Disconnected",Toast.LENGTH_SHORT);
                    return ;
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("From MainActivity"+ "Exception in closing socket"+e);
                    return;
                }
                catch (Exception e){
                    System.out.println(e);
                    return;
                }
            }
        }
        else{
            Caller=1;
            try{
                if(socket!=null)
                    socket.close();
                if(sendingStream!=null)
                    sendingStream.close();
                if(inputStream!=null)
                    inputStream.close();
            }
            catch(IOException e){
                e.printStackTrace();
                System.out.println("From  MainActivity Disconnected: Error while closing the sockets in else");
            }
            connect(findViewById(R.id.connect));
        }
    }

    public void sendbuffer(byte[] buffer)
    {
        try {
            sendLock.lock();
            sendingStream.write(buffer);
            sendingStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            sendLock.unlock();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        System.out.println("Keypressed " + String.valueOf(event.getKeyCode()));

        if(event.getKeyCode()==67){
            if(event.getAction()==KeyEvent.ACTION_DOWN) {
                byte[] command = new byte[2];
                command[0] = 1;
                command[1] = 8;
                sendbuffer(command);
            }
        }
        return super.dispatchKeyEvent(event);
    };
    public void setBluetoothIcon(boolean status){
        boolean ENABLE=true;
        if(status==ENABLE){
            menu.findItem(R.id.bluetoothSwitch).setIcon(icBluetoothEnable);
        }
        else{
           menu.findItem(R.id.bluetoothSwitch).setIcon(icBluetoothDisable);
        }
    }
    public void setBluetoothStatus(int callFor,MenuItem menuItem){
        int Icon_Change=0,Action_Perform=1;

        if(callFor==Icon_Change){
            if(BA.isEnabled()){
                //menuItem.setIcon(icBluetoothEnable);
                setBluetoothIcon(true);
            }
            else {
                setBluetoothIcon(false);
                //menuItem.setIcon(icBluetoothDisable);
            }
        }
        if(callFor==Action_Perform){
            if(BA.isEnabled()){
                BA.disable();
            }
            else{
                BA.enable();
            }
        }
    }

    @Override
    public void finish(){
        disconnected(0);
        super.finish();
    }
}




