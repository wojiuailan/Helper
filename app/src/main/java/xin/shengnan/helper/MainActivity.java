package xin.shengnan.helper;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.PermissionRequest;

import java.security.Permission;

import xin.shengnan.helper.aty.ActivityEditNote;
import xin.shengnan.helper.aty.ActivityLogin;
import xin.shengnan.helper.aty.ActivityUser;
import xin.shengnan.helper.fragment.FragmentGuide;
import xin.shengnan.helper.fragment.FragmentWelcome;
import xin.shengnan.helper.local.Config;
import xin.shengnan.helper.net.Login;
import xin.shengnan.helper.net.SaveGPS;
import xin.shengnan.helper.service.ServiceGPS;
import xin.shengnan.helper.tool.Ruler;


public class MainActivity extends AppCompatActivity {

    FragmentWelcome mFragmentWelcome;
    FragmentGuide mFragmentGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkFirstOpen();

    }

    private void checkFirstOpen() {

        SharedPreferences mSF = getSharedPreferences(Config.CONFIG, MODE_PRIVATE);

        if (mSF.getBoolean(Config.FIRST_OPEN, true)) {
            SharedPreferences.Editor mEditor = mSF.edit();
            mEditor.putBoolean(Config.FIRST_OPEN, false);
            mEditor.commit();
            startGuide();
        } else {
            startWelcome();
        }
    }

    private void startGuide() {
        if (mFragmentGuide == null){
            mFragmentGuide = new FragmentGuide(MainActivity.this);
        }
        FragmentManager mFragmentManager = getFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.fl_fragment, mFragmentGuide).commit();
    }


    private void startWelcome() {
        if (mFragmentWelcome == null){
            mFragmentWelcome = new FragmentWelcome(MainActivity.this);
        }
        FragmentManager mFragmentManager = getFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.fl_fragment, mFragmentWelcome).commit();
    }
}
