package xin.shengnan.helper.net;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import xin.shengnan.helper.local.Config;

/**
 * Created by Administrator on 2018/2/3.
 * */

public class SaveNote {
    public SaveNote(final String username, final String token, final String note, final Config.SuccessCallback success, final Config.FailCallback fail) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL mURL = new URL(Config.API);
                    HttpURLConnection mConnect = (HttpURLConnection) mURL.openConnection();

                    Config.setConnect(mConnect);

                    String msg = Config.ACTION + "=" + Config.ACTION_SAVE_NOTE + "&" + Config.TOKEN + "=" + token + "&" + Config.USERNAME + "=" + username + "&note=" + note;

                    OutputStream out = mConnect.getOutputStream();
                    out.write(msg.getBytes(Config.UTF_8));
                    out.flush();
                    out.close();

                    mConnect.connect();

                    Config.connectResult(mConnect, success, fail);

                } catch (Exception e) {

                }
            }
        }).start();
    }
}
