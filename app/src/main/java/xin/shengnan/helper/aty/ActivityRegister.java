package xin.shengnan.helper.aty;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import xin.shengnan.helper.R;
import xin.shengnan.helper.local.Config;
import xin.shengnan.helper.net.Register;

public class ActivityRegister extends AppCompatActivity {

    private EditText mUsername, mPassword;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = findViewById(R.id.et_register_username);
        mPassword = findViewById(R.id.et_register_password);
        mBtnRegister = findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                if (null != username && null != password) {
                    String s = username.replaceAll(getString(R.string.alnum), "");
                    if (s.length() > 0) {
                        new AlertDialog.Builder(ActivityRegister.this).setTitle(R.string.hint).setMessage(R.string.hint_illega_username).show();
                    } else {
                        new Register(username, password, new Config.SuccessCallback() {
                            public void successDo(String msg) {
                                String status = Config.getParam(".*" + Config.STATUS + "=", msg);
                                if (1 == Integer.valueOf(status)) {
//                                    new AlertDialog.Builder(ActivityRegister.this).setMessage(R.string.register_success).show();
//                                    showSuccess();
                                    finish();
                                }
                            }
                        }, null);
                    }
                }
            }
        });
    }

    public void showSuccess() {
        new AlertDialog.Builder(ActivityRegister.this).setMessage(R.string.register_success).show();
    }
}
