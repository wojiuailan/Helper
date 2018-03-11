package xin.shengnan.helper.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import xin.shengnan.helper.R;
import xin.shengnan.helper.local.Config;
import xin.shengnan.helper.net.GetFamilyGPS;
import xin.shengnan.helper.service.ServiceGPS;


public class FragmentGPS extends Fragment {

    private LocationManager mLocationManager;
    private SharedPreferences mSharedPreferences;

    private TextView mTVLongitude, mTVLatitude, mTVSpeed, mTVHeight;
    private TextView mTVFamilyLongitude, mTVFamilyLatitude;
    private EditText mETFamilyName;

    private Context mContext;

    private boolean firstRLU = true;

    public FragmentGPS() {
    }

    @SuppressLint("ValidFragment")
    public FragmentGPS(Context context) {
        this();
        mSharedPreferences = context.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE);
        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTVFamilyLongitude = view.findViewById(R.id.tv_family_longitude);
        mTVFamilyLatitude = view.findViewById(R.id.tv_family_latitude);
        mETFamilyName = view.findViewById(R.id.et_gps_family_name);
        mTVHeight = view.findViewById(R.id.tv_height);
        mTVSpeed = view.findViewById(R.id.tv_speed);
        mTVLatitude = view.findViewById(R.id.tv_latitude);
        mTVLongitude = view.findViewById(R.id.tv_longitude);
        Button mBtnFlushEveryTime = view.findViewById(R.id.btn_every_time_flush_gps);
        Button mBtnFamilySelect = view.findViewById(R.id.btn_get_family_gps);

        mBtnFamilySelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String familyName = mETFamilyName.getText().toString();
                if (familyName.isEmpty()) {
                    return;
                }
                new GetFamilyGPS(mSharedPreferences.getString(Config.USERNAME, null), mSharedPreferences.getString(Config.TOKEN, null), familyName, new Config.SuccessCallback() {
                    public void successDo(String msg) {
                        String status = Config.getParam(".*" + Config.STATUS + "=", msg);
                        String location;
                        int s = Integer.valueOf(status);
                        if (2 == s) {
                            mHandler.sendEmptyMessage(MyHandler.SHOW_RELATION_ERROR);
                        } else if (1 == s) {
                            location = Config.getParam(".*" + Config.LOCATION + "=", msg);
                            Message m = new Message();
                            Bundle b = new Bundle();
                            b.putString(Config.LOCATION, location);
                            m.setData(b);
                            m.what = MyHandler.FLUSH_UI;
                            mHandler.sendMessage(m);
                        }
                    }
                }, null);
            }
        });


        mBtnFlushEveryTime.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            public void onClick(View v) {
                Location mLocation;
                String providerLocation = LocationManager.GPS_PROVIDER;
                if (null == (mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER))) {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    providerLocation = LocationManager.NETWORK_PROVIDER;
                }
                if (null != mLocation) {
                    setTextView(mLocation);
                    if (firstRLU) {
                        firstRLU = false;
                        mLocationManager.requestLocationUpdates(providerLocation, 1000, 10, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location mLocation) {
                                setTextView(mLocation);
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
                    }

                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gps, container, false);
    }

    private MyHandler mHandler = new MyHandler();

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        static final int SHOW_RELATION_ERROR = 1;
        static final int FLUSH_UI = 2;

        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RELATION_ERROR:
                    Toast.makeText(mContext, R.string.not_is_family, Toast.LENGTH_SHORT).show();
                    break;
                case FLUSH_UI:
                    String location = msg.getData().getString(Config.LOCATION);
                    try {
                        if (location != null) {
                            JSONObject j = new JSONObject(location);
                            mTVFamilyLongitude.setText(getString(R.string.location_longitude) + j.getString("longitude"));
                            mTVFamilyLatitude.setText(getString(R.string.location_latitude) + j.getString("latitude"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void setTextView(Location mLocation) {
        mTVLongitude.setText(getString(R.string.location_longitude) + mLocation.getLongitude());
        mTVLatitude.setText(getString(R.string.location_latitude) + mLocation.getLatitude());
        mTVSpeed.setText(getString(R.string.location_speed) + mLocation.getSpeed());
        mTVHeight.setText(getString(R.string.location_height) + mLocation.getAltitude());
    }
}
