package xin.shengnan.helper.net;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import xin.shengnan.helper.local.Config;

/**
 * Created by Administrator on 2018/2/2.
 * */

public class Register {

    public Register(final String username, final String password, final Config.SuccessCallback success, final Config.FailCallback fail) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL mURL = new URL(Config.API);
                    HttpURLConnection mConnect = (HttpURLConnection) mURL.openConnection();

                    Config.setConnect(mConnect);

                    String msg = Config.ACTION + "=" + Config.ACTION_REGISTER + "&" + Config.USERNAME + "=" + username + "&" + Config.PASSWORD + "=" + password;

                    OutputStream out = mConnect.getOutputStream();
                    out.write(msg.getBytes());
                    out.flush();
                    out.close();

                    mConnect.connect();

                    Config.connectResult(mConnect, success, fail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
