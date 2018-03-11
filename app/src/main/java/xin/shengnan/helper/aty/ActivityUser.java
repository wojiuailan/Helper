package xin.shengnan.helper.aty;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import xin.shengnan.helper.R;
import xin.shengnan.helper.fragment.FragmentGPS;
import xin.shengnan.helper.fragment.FragmentLight;
import xin.shengnan.helper.fragment.FragmentMeasure;
import xin.shengnan.helper.fragment.FragmentNote;
import xin.shengnan.helper.fragment.FragmentUser;
import xin.shengnan.helper.local.Config;
import xin.shengnan.helper.net.Login;
import xin.shengnan.helper.service.ServiceGPS;

public class ActivityUser extends AppCompatActivity {

    private FragmentGPS mGPS;
    private FragmentUser mUser;
    private FragmentNote mNote;
    private FragmentLight mLight;
    private FragmentMeasure mMeasure;

    private ImageView mIVMoreTool, mIVNote, mIVLight, mIVGPS, mIVMeasure;

    private RelativeLayout mTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        startGPSService();
        initView();
        requestLocationPermission();
        startService(new Intent(ActivityUser.this, ServiceGPS.class));
        checkLogin();
    }

    //开启GPS的上传Service
    private void startGPSService() {
        final SharedPreferences sP = getSharedPreferences(Config.CONFIG, MODE_PRIVATE);
        if (sP.getBoolean(Config.AUTO_UPDATE_GPS, true)) {
            Intent serviceGPS = new Intent(this, ServiceGPS.class);
            startService(serviceGPS);
        }
    }

    //请求GPS权限
    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) || PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            } else {
                Toast.makeText(ActivityUser.this, R.string.hint_no_gps, Toast.LENGTH_SHORT).show();
            }



        }
    }

    /**
     * 打开登录界面
     */
    private void startLogin() {
        Intent mIntent = new Intent(this, ActivityLogin.class);
        startActivity(mIntent);
        finish();
    }

    /**
     * 检查是否是自动登录和能否登录
     */
    private void checkLogin() {
        SharedPreferences mPreferences = getSharedPreferences(Config.CONFIG, MODE_PRIVATE);
        String token = mPreferences.getString(Config.TOKEN, null);
        if (null == token) {
            startLogin();
        } else {
            String username = mPreferences.getString(Config.USERNAME, null);
            if (null == username) {
                startLogin();
            } else {
                new Login(username, token, Login.MODE_TOKEN, new Config.SuccessCallback() {
                    public void successDo(String message) {
                        String status = Config.getParam(".*" + Config.STATUS + "=", message);
                        if (1 != Integer.valueOf(status)) {
                            startLogin();
                        }
                    }
                }, new Config.FailCallback() {
                    public void failDo() {
                        startLogin();
                    }
                });
            }
        }
    }


    /**
     * 设置所有的点击事件
     */
    private void setMyOnClick() {
        mIVMoreTool.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (View.GONE == mTools.getVisibility()) {
                    RotateAnimation mRotaReverse = new RotateAnimation(90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    mRotaReverse.setDuration(500);
                    AlphaAnimation mRlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                    mRlphaAnimation.setDuration(500);
                    mIVMoreTool.setImageResource(R.drawable.tools);
                    v.setAnimation(mRotaReverse);
                    mTools.setVisibility(View.VISIBLE);
                    mTools.setAnimation(mRlphaAnimation);
                } else {
                    closeMoreTools();
                }
            }
        });

        mIVMeasure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                FragmentTransaction mTransaction = getFragmentManager().beginTransaction();

                if (null == mMeasure) {
                    mMeasure = new FragmentMeasure();
                    addFragment(mTransaction, mMeasure);
                    hideAllFragment(mTransaction);
                    showOnFragment(mTransaction, mMeasure);
                } else {
                    hideAllFragment(mTransaction);
                    showOnFragment(mTransaction, mMeasure);
                }

                mTransaction.commit();

                closeMoreTools();
            }
        });

        mIVLight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                FragmentTransaction mTransaction = getFragmentManager().beginTransaction();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.CAMERA)) {
                        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                            Toast.makeText(ActivityUser.this, R.string.hint_no_flushlight, Toast.LENGTH_SHORT).show();
                            return ;
                        } else {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                        }
                    }
                }

                if (null == mLight) {
                    mLight = new FragmentLight();
                    addFragment(mTransaction, mLight);
                    hideAllFragment(mTransaction);
                    showOnFragment(mTransaction, mLight);
                } else {
                    hideAllFragment(mTransaction);
                    showOnFragment(mTransaction, mLight);
                }

                mTransaction.commit();

                closeMoreTools();
            }
        });

        mIVNote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                FragmentTransaction mTransaction = getFragmentManager().beginTransaction();

                if (null == mNote) {
                    mNote = new FragmentNote();
                    addFragment(mTransaction, mNote);
                    hideAllFragment(mTransaction);
                    showOnFragment(mTransaction, mNote);
                } else {
                    hideAllFragment(mTransaction);
                    showOnFragment(mTransaction, mNote);
                }

                mTransaction.commit();

                closeMoreTools();
            }
        });

        mIVGPS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                requestLocationPermission();

                FragmentTransaction mTransaction = getFragmentManager().beginTransaction();

                if (null == mGPS) {
                    mGPS = new FragmentGPS(ActivityUser.this);
                    addFragment(mTransaction, mGPS);
                    hideAllFragment(mTransaction);
                    showOnFragment(mTransaction, mGPS);
                } else {
                    hideAllFragment(mTransaction);
                    showOnFragment(mTransaction, mGPS);
                }

                mTransaction.commit();

                closeMoreTools();
            }
        });
    }

    private void closeMoreTools() {
        mIVMoreTool.setImageResource(R.drawable.more);
        mTools.setVisibility(View.GONE);
    }

    /**
     * 添加一个Fragment到FragmentTransaction中
     */
    private void addFragment(FragmentTransaction mTransaction, Fragment fragment) {
        mTransaction.add(R.id.fl_user_fragment, fragment);
    }

    /**
     * 显示一个Fragment
     */
    private void showOnFragment(FragmentTransaction mTransaction, Fragment fragment) {
        hideAllFragment(mTransaction);

        mTransaction.show(fragment);
    }

    /**
     * 初始化视图
     */
    private void initView() {


        mIVMoreTool = findViewById(R.id.iv_more_tool);
        mIVMeasure = findViewById(R.id.iv_measure);
        mIVLight = findViewById(R.id.iv_light);
        mIVNote = findViewById(R.id.iv_note);
        mIVGPS = findViewById(R.id.iv_gps);

        mTools = findViewById(R.id.rl_tools);

        FragmentTransaction mTransaction = getFragmentManager().beginTransaction();

        if (null == mNote) {
            mNote = new FragmentNote();
            mNote.setContext(ActivityUser.this);
            addFragment(mTransaction, mNote);
            showOnFragment(mTransaction, mNote);
        } else {
            showOnFragment(mTransaction, mNote);
        }

        if (null == mUser) {
            mUser = new FragmentUser();
            mUser.setContext(ActivityUser.this);
            mTransaction.replace(R.id.fl_user_menu, mUser);
        }

        mTransaction.commit();

        setMyOnClick();

    }

    /**
     * 隐藏所有Fragment
     */
    private void hideAllFragment(FragmentTransaction mTransaction) {
        if (null != mNote) {
            mTransaction.hide(mNote);
        }
        if (null != mGPS) {
            mTransaction.hide(mGPS);
        }
        if (null != mMeasure) {
            mTransaction.hide(mMeasure);
        }
        if (null != mLight) {
            mTransaction.hide(mLight);
        }
    }
}
