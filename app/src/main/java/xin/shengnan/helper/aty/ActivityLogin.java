package xin.shengnan.helper.aty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import xin.shengnan.helper.R;
import xin.shengnan.helper.local.Config;
import xin.shengnan.helper.net.Login;

public class ActivityLogin extends AppCompatActivity {

    private EditText mETUsername, mETPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        Button mBtnLogin = findViewById(R.id.btn_login);
        mETUsername = findViewById(R.id.et_username);
        mETPassword = findViewById(R.id.et_password);
        TextView mTVToRegister = findViewById(R.id.tv_to_register);

        mTVToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ActivityLogin.this, ActivityRegister.class));
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = mETUsername.getText().toString();
                String password = mETPassword.getText().toString();
                String s = username.replaceAll(getString(R.string.alnum), "");
                if (s.length() > 0) {
                    new AlertDialog.Builder(ActivityLogin.this).setTitle(R.string.hint).setMessage(R.string.hint_illega_username).show();
                } else {
                    getSharedPreferences(Config.CONFIG, MODE_PRIVATE).edit().putString(Config.USERNAME, username).commit();
                    new Login(username, password, Login.MODE_PASSWORD, new Config.SuccessCallback() {
                        public void successDo(String message) {
                            String status = Config.getParam(".*" + Config.STATUS + "=", message);//得到status

                            if (1 == Integer.valueOf(status)) { //登录成功，保存Token到本地
                                SharedPreferences.Editor mEditor = getSharedPreferences(Config.CONFIG, MODE_PRIVATE).edit();
                                String token = Config.getParam(".*" + Config.TOKEN + "=", message);
                                mEditor.putString(Config.TOKEN, token);
                                mEditor.commit();

                                startActivity(new Intent(ActivityLogin.this, ActivityUser.class));
                                finish();
                            }
                        }
                    }, new Config.FailCallback() {
                        public void failDo() {
                            getSharedPreferences(Config.CONFIG, MODE_PRIVATE).edit().putString(Config.USERNAME, "").commit();
                        }
                    });
                }
            }
        });
    }
}
