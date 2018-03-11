package xin.shengnan.helper.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

import xin.shengnan.helper.local.Config;
import xin.shengnan.helper.net.SaveGPS;

public class ServiceGPS extends Service {

    private MyBinder myBinder;
    private LocationManager mLocationManager;
    private Location mLocation;

    private boolean flushLocationRun = true;

    private long sleepTime = 300000;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        myBinder = new MyBinder();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        startLocationThread();
    }

    //开启一个上传GPS的线程
    private void startLocationThread() {
        sleepTime = getSharedPreferences(Config.CONFIG, MODE_PRIVATE).getLong(Config.AUTO_UPDATE_GPS_DELAY_TIME, 300000);
        new Thread(new Runnable() {
            public void run() {
                while (flushLocationRun) {
                    try {
                        updateLocation();
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //上传GPS
    @SuppressLint("MissingPermission")
    private void updateLocation() {
        SharedPreferences mSP = getSharedPreferences(Config.CONFIG, MODE_PRIVATE);
        Date date = new Date();
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        if (null == (mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))) {
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (null != mLocation) {
            String location = "{\"longitude\": \"" + mLocation.getLongitude() + "\", \"latitude\": \"" + mLocation.getLatitude() + "\", \"date\": \"" + sDate.format(date) + "\"}";
            new SaveGPS(mSP.getString(Config.USERNAME, null), mSP.getString(Config.TOKEN, null), location, null, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flushLocationRun = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    public class MyBinder extends Binder {

        public void cancelFlushLocation() {
            flushLocationRun = false;
        }

        public void startThreadLocation() {
            if (!flushLocationRun) {
                flushLocationRun = true;
                startLocationThread();
            }
        }

        public void setDelayTime(long time) {
            sleepTime = time;
        }
    }


}
