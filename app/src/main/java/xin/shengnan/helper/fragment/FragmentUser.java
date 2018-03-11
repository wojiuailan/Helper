package xin.shengnan.helper.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import xin.shengnan.helper.R;
import xin.shengnan.helper.aty.ActivityLogin;
import xin.shengnan.helper.local.Config;
import xin.shengnan.helper.net.CreateRelation;
import xin.shengnan.helper.net.DeleteRelation;
import xin.shengnan.helper.service.ServiceGPS;

public class FragmentUser extends Fragment {

    private RadioGroup mRG;
    private EditText mETFamilyName, mETDeleteFamilyName;
    private RadioButton mRBOne, mRBFive, mRBTen;
    private Button mBtnOutLogin, mBtnCreateRelation, mBtnDeleteRelation;
    private Switch mSwitchAutoLogin, mSwitchUpdateGPS;
    private ServiceGPS.MyBinder myBinder;

    private Context mContext;

    private boolean booleanSwitchAutoLogin;
    private boolean booleanSwitchUpdateGPS;

    private static final int CREATE_SUCCESS = 1;
    private static final int CREATE_FAIL = 0;
    private static final int DELETE_SUCCESS = 3;
    private static final int DELETE_FAIL = 2;

    //传入Activity的参数
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    /**
     * 初始化视图
     * */
    private void initView(View view) {
        mRG = view.findViewById(R.id.rg_update_delay_time);
        mRBOne = view.findViewById(R.id.rb_one);
        mRBFive = view.findViewById(R.id.rb_five);
        mRBTen = view.findViewById(R.id.rb_ten);
        mBtnOutLogin = view.findViewById(R.id.btn_out_login);
        mSwitchAutoLogin = view.findViewById(R.id.switch_auto_login);
        mSwitchUpdateGPS = view.findViewById(R.id.switch_up_gps);
        mETFamilyName = view.findViewById(R.id.et_create_family_name);
        mBtnCreateRelation = view.findViewById(R.id.btn_create_relation);
        mETDeleteFamilyName = view.findViewById(R.id.et_delete_family_name);
        mBtnDeleteRelation = view.findViewById(R.id.btn_delete_relation);

        initViewSet();
    }

    //设置点击事件
    private void setOnClick() {
        mBtnOutLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor sPEditor = mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE).edit();
                sPEditor.putString(Config.USERNAME, "");
                sPEditor.putString(Config.TOKEN, "");
                sPEditor.apply();
                startActivity(new Intent(mContext, ActivityLogin.class));
                getActivity().finish();
            }
        });

        mBtnCreateRelation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String familyName = mETFamilyName.getText().toString();
                if (familyName.isEmpty()) {
                    Toast.makeText(mContext, R.string.can_not_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sP = mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE);
                new CreateRelation(sP.getString(Config.USERNAME, null), sP.getString(Config.TOKEN, null), familyName, new Config.SuccessCallback() {
                    public void successDo(String s) {
                        String status = Config.getParam(".*" + Config.STATUS + "=", s);
                        myHandler.sendEmptyMessage(Integer.valueOf(status));
                    }
                }, null);
            }
        });

        mBtnDeleteRelation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String familyName = mETDeleteFamilyName.getText().toString();
                if (familyName.isEmpty()) {
                    Toast.makeText(mContext, R.string.can_not_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sP = mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE);
                new DeleteRelation(sP.getString(Config.USERNAME, null), sP.getString(Config.TOKEN, null), familyName, new Config.SuccessCallback() {
                    public void successDo(String msg) {
                        String status = Config.getParam(".*" + Config.STATUS + "=", msg);
                        myHandler.sendEmptyMessage(Integer.valueOf(status) + 2);
                    }
                }, null);
            }
        });

        mRBOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setServiceDelayTime(60000);
            }
        });

        mRBFive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setServiceDelayTime(300000);
            }
        });

        mRBTen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setServiceDelayTime(600000);
            }
        });

        mSwitchAutoLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (booleanSwitchAutoLogin) {
                    booleanSwitchAutoLogin = false;
                    mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE).edit().putBoolean(Config.AUTO_LOGIN, false).apply();
                } else {
                    booleanSwitchAutoLogin = true;
                    mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE).edit().putBoolean(Config.AUTO_LOGIN, true).apply();
                }
            }
        });

        mSwitchUpdateGPS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (booleanSwitchUpdateGPS) {
                    booleanSwitchUpdateGPS = false;
                    mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE).edit().putBoolean(Config.AUTO_UPDATE_GPS, false).apply();
                    mRG.setVisibility(View.GONE);
                    if (myBinder != null) {
                        myBinder.cancelFlushLocation();
                    }
                } else {
                    booleanSwitchUpdateGPS = true;
                    mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE).edit().putBoolean(Config.AUTO_UPDATE_GPS, true).apply();
                    mRG.setVisibility(View.VISIBLE);
                    if (myBinder != null) {
                        myBinder.startThreadLocation();
                    }
                }
            }
        });
    }

    //设置上传延时时间
    private void setServiceDelayTime(long i) {
        if (myBinder != null) {
            myBinder.setDelayTime(i);
        }
        mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE).edit().putLong(Config.AUTO_UPDATE_GPS_DELAY_TIME, i).apply();
    }

    //初始化视图的设置
    private void initViewSet() {
        SharedPreferences sP = mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE);
        if (sP.getBoolean(Config.AUTO_LOGIN, true)) {
            mSwitchAutoLogin.setChecked(true);
            booleanSwitchAutoLogin = true;
        } else {
            mSwitchAutoLogin.setChecked(false);
            booleanSwitchAutoLogin = false;
        }

        if (sP.getBoolean(Config.AUTO_UPDATE_GPS, true)) {
            booleanSwitchUpdateGPS = true;
            mRG.setVisibility(View.VISIBLE);
            mSwitchUpdateGPS.setChecked(true);
            long delayTime = sP.getLong(Config.AUTO_UPDATE_GPS_DELAY_TIME, 300000);
            switch((int) (delayTime/1000)) {
                case 60:
                    mRBOne.setChecked(true);
                    break;
                case 300:
                    mRBFive.setChecked(true);
                    break;
                case 600:
                    mRBTen.setChecked(true);
                    break;
            }

            ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    myBinder = (ServiceGPS.MyBinder) service;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
            getActivity().bindService(new Intent(mContext, ServiceGPS.class), serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            booleanSwitchUpdateGPS = false;
            mRG.setVisibility(View.GONE);
            mSwitchUpdateGPS.setChecked(false);
        }

        setOnClick();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }


    private MyHandler myHandler = new MyHandler();
    //用于线程更新UI，弹出toast
    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case CREATE_SUCCESS:
                    Toast.makeText(mContext, R.string.create_relation_success, Toast.LENGTH_SHORT).show();
                    break;
                case CREATE_FAIL:
                    Toast.makeText(mContext, R.string.create_relation_fail, Toast.LENGTH_SHORT).show();
                    break;
                case DELETE_SUCCESS:
                    Toast.makeText(mContext, R.string.delete_relation_success, Toast.LENGTH_SHORT).show();
                    break;
                case DELETE_FAIL:
                    Toast.makeText(mContext, R.string.delete_relation_fail, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
