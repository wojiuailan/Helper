package xin.shengnan.helper.aty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import xin.shengnan.helper.R;
import xin.shengnan.helper.local.Config;
import xin.shengnan.helper.net.SaveNote;

public class ActivityEditNote extends AppCompatActivity {

    private TextView mTVShowTitle, mTVDate;
    private EditText mETEditTitle, mETMessage;

    private boolean newNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        initView();
        String s = getIntent().getStringExtra("Message");
        if (null == s) {//代表是新添加Note
            newNote = true;
        } else {
            newNote = false;
            try {
                initMessage(new JSONObject(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //对编辑已有Note编辑初始化
    private void initMessage(JSONObject jObj) throws Exception {
        mTVShowTitle.setText(jObj.getString("id"));
        mTVDate.setText(jObj.getString("date"));
        mETEditTitle.setText(jObj.getString("title"));
        mETMessage.setText(jObj.getString("details"));
    }

    private void initView() {
        mTVShowTitle = findViewById(R.id.tv_show_title);
        mETEditTitle = findViewById(R.id.et_edit_title);
        mTVDate = findViewById(R.id.tv_edit_date);
        ImageView mIVSave = findViewById(R.id.iv_edit_save);
        ImageView mIVQuite = findViewById(R.id.iv_edit_cancel);
        LinearLayout mLL = findViewById(R.id.ll_edit);
        mETMessage = findViewById(R.id.et_edit_message);

        mLL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mETMessage.setFocusable(true);
                mETMessage.setFocusableInTouchMode(true);
                mETMessage.requestFocus();
                InputMethodManager inputMethodManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManger.showSoftInput(mETMessage, InputMethodManager.SHOW_FORCED);
            }
        });

        mIVSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String title, details;
                title = mETEditTitle.getText().toString();
                details = mETMessage.getText().toString();
                if (title != null || details != null) {
                    saveNote();
                }
            }
        });

        mIVQuite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!mETMessage.getText().toString().equals("")) {
                    new AlertDialog.Builder(ActivityEditNote.this).setTitle(R.string.hint).setMessage(R.string.hint_save).setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveNote();
                        }
                    }).setNegativeButton(R.string.cancal, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
                } else {
                    finish();
                }
            }
        });
    }

    //保存笔记操作
    private void saveNote() {
        SharedPreferences sP = getSharedPreferences(Config.CONFIG, MODE_PRIVATE);
        JSONObject jsonNote = new JSONObject();
        Date date = new Date();
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        try {
            jsonNote.put("title", mETEditTitle.getText().toString());
            jsonNote.put("details", mETMessage.getText().toString());
            jsonNote.put("date", sDate.format(date));
            if (!newNote) {
                jsonNote.put("id", mTVShowTitle.getText().toString());
            }
            String note = jsonNote.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SaveNote(sP.getString(Config.USERNAME, null), sP.getString(Config.TOKEN, null), jsonNote.toString(), new Config.SuccessCallback() {
            public void successDo(String msg) {
                String status = Config.getParam(".*" + Config.STATUS + "=", msg);
                if (1 != Integer.valueOf(status)) {
                    return;
                }
                finish();
            }
        }, null);
    }
}
