package gr.fragment.projectwalnut.Core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import gr.fragment.projectwalnut.Events.Event;

public class System {

    public static Context mContext = null;
    public static WifiManager mWifiManager = null;
    private static WifiManager.WifiLock mWifiLock = null; //Allows an application to keep the Wi-Fi radio awake.
    private static PowerManager.WakeLock mWakeLock = null; //A wake lock is a mechanism to indicate that your application needs  to have the device stay on
    public static ConnectivityManager mConnectivityManager = null;

    public static ArrayList<Event> events;


    public static void init(Context context){
        mContext = context;
        events = new ArrayList<>();
        Logger.debug("Initializing System...");

        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(mWifiLock == null)
            mWifiLock = mWifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "wifiLock");
        //if it not already done, lock wifi
        if(!mWifiLock.isHeld())
            mWifiLock.acquire();

        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if(mWakeLock == null)
            mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "jw:wakeLock");
        //if it is not already done, lock screen and cpu
        if(!mWakeLock.isHeld())
            mWakeLock.acquire();


        Logger.debug("Check permissions");
        if(!System.checkPermission("android.permission.ACCESS_NETWORK_STATE"))
            System.askPermission("android.permission.ACCESS_NETWORK_STATE");

        if(!System.checkPermission("android.permission.CHANGE_WIFI_STATE"))
            System.askPermission("android.permission.CHANGE_WIFI_STATE");

        if(!System.checkPermission("android.permission.CHANGE_WIFI_MULTICAST_STATE"))
            System.askPermission("android.permission.CHANGE_WIFI_MULTICAST_STATE");

        if(!System.checkPermission("android.permission.ACCESS_WIFI_STATE"))
            System.askPermission("android.permission.ACCESS_WIFI_STATE");

        if(!System.checkPermission("android.permission.ACCESS_COARSE_LOCATION"))
            System.askPermission("android.permission.ACCESS_COARSE_LOCATION");

        if(!System.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE"))
            System.askPermission("android.permission.WRITE_EXTERNAL_STORAGE");

        if(!System.checkPermission("android.permission.READ_EXTERNAL_STORAGE"))
            System.askPermission("android.permission.READ_EXTERNAL_STORAGE");

    }

    public static void quit(){
        //if wifi is locked, release
        if(mWifiLock.isHeld())
            mWifiLock.release();

        //if cpu and screen are locked, release
        if(mWakeLock.isHeld())
            mWakeLock.release();
    }

    public static boolean is_device_rooted() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("ls /data\n");
            os.writeBytes("exit\n");
            os.flush();
            try {
                p.waitFor();
                return p.exitValue() != 1;
            }
            catch (InterruptedException e) { Logger.debug(e.getMessage()); }
        }
        catch (IOException e) { Logger.debug(e.getMessage()); }
        return false;
    }

    private static boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private static void askPermission(String permission){
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{permission}, 1);
    }

    public static String formatTime(long delta){
        if(delta < 1000)
            return delta + " ms";
        else if(delta < 60000)
            return (delta / 1000.0) + " s";
        else
            return (delta / 60000.0) + " m";
    }

    public static void shareText(String text){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(sendIntent);
    }

}
