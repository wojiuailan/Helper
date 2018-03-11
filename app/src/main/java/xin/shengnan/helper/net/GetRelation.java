package xin.shengnan.helper.net;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import xin.shengnan.helper.local.Config;

/**
 * Created by Administrator on 2018/2/3.
 * */

public class GetRelation {

    public static final int TOSELF = 1;
    public static final int TOOTHER = 2;

    public GetRelation(final String username, final String token, final int mode, final Config.SuccessCallback success, final Config.FailCallback fail) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL mURL = new URL(Config.API);
                    HttpURLConnection mConnect = (HttpURLConnection)mURL.openConnection();

                    Config.setConnect(mConnect);

                    String msg ;

                    if (TOSELF == mode) {
                        msg = Config.ACTION + "=" + Config.ACTION_GET_RELATION_TO_SELF;
                    } else if (TOOTHER == mode) {
                        msg = Config.ACTION + "=" + Config.ACTION_GET_RELATION_TO_OTHER;
                    } else {
                        throw new Exception("mode error");
                    }
                    msg += "&" + Config.USERNAME + "=" + username + "&" + Config.TOKEN + "=" + token;

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
