package xin.shengnan.helper.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import xin.shengnan.helper.MainActivity;
import xin.shengnan.helper.R;
import xin.shengnan.helper.aty.ActivityLogin;
import xin.shengnan.helper.aty.ActivityUser;
import xin.shengnan.helper.local.Config;

public class FragmentWelcome extends Fragment {

    private Context context;
    private boolean buttonClick = false;
    private int delayTime = 4;

    public FragmentWelcome() {
    }

    @SuppressLint("ValidFragment")
    public FragmentWelcome(Context c) {
        this();
        this.context = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button skip = view.findViewById(R.id.btn_skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick = true;
                startNextActivity();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (delayTime > 0) {
                    try {
                        Thread.sleep(500);
                        delayTime--;
                    } catch (InterruptedException e) {
                        delayTime = 0;
                    }
                }
                if (!buttonClick) {
                    startNextActivity();
                }
            }
        }).start();
    }

    private void startNextActivity() {
        SharedPreferences mPreferences = getActivity().getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE);
        Intent intent;
        if (!mPreferences.getBoolean(Config.AUTO_LOGIN, true)) {
            intent = new Intent(context, ActivityLogin.class);
        } else {
            intent = new Intent(context, ActivityUser.class);
        }
        startActivity(intent);
        getActivity().finish();
    }
}
