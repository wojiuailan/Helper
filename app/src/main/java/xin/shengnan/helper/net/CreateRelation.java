package xin.shengnan.helper.net;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import xin.shengnan.helper.local.Config;

/**
 * Created by Administrator on 2018/2/3.
 * */

public class CreateRelation {
    public CreateRelation(final String username, final String token, final String familyName, final Config.SuccessCallback success, final Config.FailCallback fail) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL mURL = new URL(Config.API);
                    HttpURLConnection mConnect = (HttpURLConnection) mURL.openConnection();

                    Config.setConnect(mConnect);

                    String msg = Config.ACTION + "=" + Config.ACTION_CREATE_RELATION + "&" + Config.TOKEN + "=" + token + "&" + Config.USERNAME + "=" + username + "&" + Config.FAMILY_NAME + "=" + familyName;

                    OutputStream out = mConnect.getOutputStream();
                    out.write(msg.getBytes());
                    out.flush();
                    out.close();

                    mConnect.connect();

                    Config.connectResult(mConnect, success, fail);
                } catch (Exception e) {}
            }
        }).start();
    }
}
