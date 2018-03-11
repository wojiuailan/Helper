package xin.shengnan.helper.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import xin.shengnan.helper.R;
import xin.shengnan.helper.aty.ActivityEditNote;
import xin.shengnan.helper.local.Config;
import xin.shengnan.helper.net.DeleteNote;

/**
 * Created by Administrator on 2018/2/6.
 */

public class AdapterNoteList extends RecyclerView.Adapter<AdapterNoteList.MyViewHolder> {

    private List<JSONObject> mList;
    private Context mContext;

    private static final int DELETE_SUCCESS = 3;
    private static final int DELETE_FAIL = 2;

    private MyHandler myHandler = new MyHandler();


    public void setList(List<JSONObject> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public AdapterNoteList(Context context, List<JSONObject> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new MyViewHolder(inflater.inflate(R.layout.item_note_list, null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JSONObject jObj = mList.get(position);
        try {
            holder.mTVTitle.setText(jObj.getString("title"));
            holder.mTVMessage.setText(jObj.getString("details"));
            holder.mTVDate.setText(jObj.getString("date"));
            holder.itemView.setTag(position);

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(final View v) {
                    new AlertDialog.Builder(mContext).setTitle(R.string.hint).setMessage(R.string.hint_delete).setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int noteID = 0;
                            try {
                                noteID = mList.get( (int) v.getTag()).getInt("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SharedPreferences sP = mContext.getSharedPreferences(Config.CONFIG, Context.MODE_PRIVATE);
                            new DeleteNote(sP.getString(Config.USERNAME, null), sP.getString(Config.TOKEN, null), noteID, new Config.SuccessCallback() {
                                public void successDo(String message) {
                                    String status = Config.getParam(".*" + Config.STATUS + "=", message);
                                    myHandler.sendEmptyMessage(Integer.valueOf(status) + 2);
                                }
                            }, null);
                        }
                    }).setNegativeButton(R.string.cancal,null).show();
                    return true;
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(mContext, ActivityEditNote.class);
                    JSONObject jObj = mList.get((int) v.getTag());
                    mIntent.putExtra("Message", jObj.toString());
                    mContext.startActivity(mIntent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTVTitle, mTVDate, mTVMessage;

        public MyViewHolder(View itemView) {
            super(itemView);

            mTVTitle = itemView.findViewById(R.id.tv_item_title);
            mTVDate = itemView.findViewById(R.id.tv_item_date);
            mTVMessage = itemView.findViewById(R.id.tv_item_message);
        }
    }

    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case DELETE_SUCCESS:
                    Toast.makeText(mContext, R.string.delete_note_success, Toast.LENGTH_SHORT).show();
                    break;
                case DELETE_FAIL:
                    Toast.makeText(mContext, R.string.delete_note_fail, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
