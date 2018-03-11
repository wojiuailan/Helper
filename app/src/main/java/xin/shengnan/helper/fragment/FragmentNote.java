package xin.shengnan.helper.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import xin.shengnan.helper.R;
import xin.shengnan.helper.adapter.AdapterNoteList;
import xin.shengnan.helper.aty.ActivityEditNote;
import xin.shengnan.helper.local.Config;
import xin.shengnan.helper.net.GetNote;

public class FragmentNote extends Fragment {

    private AdapterNoteList mAdapter;
    private List<JSONObject> mList;
    private Context mContext;

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton mIBAddNote = view.findViewById(R.id.ib_add_note);
        RecyclerView mRCNoteList = view.findViewById(R.id.rv_note_list);

        mRCNoteList.setLayoutManager(new LinearLayoutManager(mContext));
        mList = new ArrayList<JSONObject>();
        mAdapter = new AdapterNoteList(mContext, mList);
        mRCNoteList.setAdapter(mAdapter);

        mIBAddNote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().startActivity(new Intent(mContext, ActivityEditNote.class));
            }
        });
    }

    //更新下载Note信息
    private void flushList() {

        mList.clear();

        SharedPreferences sP = mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE);
        new GetNote(sP.getString(Config.USERNAME, null), sP.getString(Config.TOKEN, null), 20, new Config.SuccessCallback() {
            public void successDo(String message) {
                String status = Config.getParam(".*" + Config.STATUS + "=", message);
                if (1 != Integer.valueOf(status)) {
                    return;
                }
                try {
                    JSONArray jArr = new JSONArray(Config.getParam(".*notes=", message));
                    for (int i = 0; i < jArr.length(); i++) {
                        mList.add(jArr.getJSONObject(i));
                    }

                    myHandler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    private MyHandler myHandler = new MyHandler();

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(1 == msg.what) {
                mAdapter.setList(mList);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        flushList();
    }
}
