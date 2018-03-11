package xin.shengnan.helper.net;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import xin.shengnan.helper.local.Config;

public class Login {

    public static final int MODE_TOKEN = 1;
    public static final int MODE_PASSWORD = 2;


    public Login(final String username, final String arg1, final int mode, final Config.SuccessCallback success, final Config.FailCallback fail) {
        Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        URL mURL = new URL(Config.API);
                        HttpURLConnection mConnect = (HttpURLConnection) mURL.openConnection();

                        Config.setConnect(mConnect);

                        String msg;

                        if (MODE_TOKEN == mode) {
                            msg = Config.TOKEN;
                        } else if (MODE_PASSWORD == mode) {
                            msg = Config.PASSWORD;
                        } else {
                            throw new Exception("Error MODE");
                        }

                        msg += "=" + arg1 + "&" + Config.ACTION + "=" + Config.ACTION_LOGIN + "&" + Config.USERNAME + "=" + username;

                        OutputStream mOut = mConnect.getOutputStream();
                        mOut.write(msg.getBytes());
                        mOut.flush();
                        mOut.close();

                        mConnect.connect();

                        Config.connectResult(mConnect, success, fail);
                    } catch (Exception e) {
//                        mFail.failDo();
                    }
                }
            };


        new Thread(mRunnable).start();
    }
}
