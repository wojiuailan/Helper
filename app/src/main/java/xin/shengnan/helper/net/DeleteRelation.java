package xin.shengnan.helper.net;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import xin.shengnan.helper.local.Config;

/**
 * Created by Administrator on 2018/2/3.
 * */

public class DeleteRelation {
    public DeleteRelation(final String username, final String token, final String familyName, final Config.SuccessCallback success, final Config.FailCallback fial) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL mURL = new URL(Config.API);
                    HttpURLConnection mConnect = (HttpURLConnection)mURL.openConnection();

                    Config.setConnect(mConnect);

                    String msg = Config.ACTION + "=" + Config.ACTION_DELETE_RELATION + "&" + Config.USERNAME + "=" + username + "&" + Config.TOKEN + "=" + token + "&" + Config.FAMILY_NAME + "=" + familyName;

                    OutputStream out = mConnect.getOutputStream();
                    out.write(msg.getBytes());
                    out.flush();
                    out.close();

                    mConnect.connect();

                    Config.connectResult(mConnect, success, fial);
                } catch (Exception e) {}
            }
        }).start();
    }
}
