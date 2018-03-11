package xin.shengnan.helper.local;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by Administrator on 2018/1/31.
 * */

public class Config {

    public static final String API = "http://39.106.172.192:8080/note/io";
//    public static final String API = "http://192.168.0.107:8080/note/io";

    public static final String FAMILY_NAME = "family_name";
    public static final String AUTO_LOGIN = "autoLogin";
    public static final String AUTO_UPDATE_GPS = "autoUpdateGPS";
    public static final String AUTO_UPDATE_GPS_DELAY_TIME = "autoUpdateGPSDelayTime";
    public static final String FIRST_OPEN = "firstOpen";
    public static final String CONFIG = "config";
    public static final String ACTION = "action";
    public static final String UTF_8 = "utf-8";
    private static final String POST = "POST";

    public static final String TOKEN = "token";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String LOCATION = "location";
    public static final String STATUS = "status";

    public static final String ACTION_GET_RELATION_TO_OTHER = "get_relation_to_other";
    public static final String ACTION_GET_RELATION_TO_SELF = "get_relation_to_self";
    public static final String ACTION_CREATE_RELATION = "create_relation";
    public static final String ACTION_DELETE_RELATION = "delete_relation";
    public static final String ACTION_DELETE_NOTE= "delete_note";
    public static final String ACTION_SAVE_NOTE = "save_note";
    public static final String ACTION_GET_NOTE = "get_note";
    public static final String ACTION_SAVE_GPS = "save_GPS";
    public static final String ACTION_REGISTER = "register";
    public static final String ACTION_GET_GPS = "get_GPS";
    public static final String ACTION_LOGIN = "login";

    private static final int CONECT_SUCCESS = 200;

    public static String getParam(String regex, String str) {

        str = str.replaceFirst(regex, "");//去掉之前的字符
        str = str.replaceFirst("&.+", "");  //去掉剩余的参数。
        return str;
    }
    public interface SuccessCallback{
        void successDo(String message);
    }

    public interface FailCallback{
        void failDo();
    }

    public static void setConnect(HttpURLConnection mConnect) throws Exception {
        mConnect.setRequestMethod(Config.POST);
        mConnect.setReadTimeout(5000);
        mConnect.setConnectTimeout(5000);
        mConnect.setDoInput(true);
        mConnect.setDoOutput(true);
        mConnect.setUseCaches(false);
    }

    private static String getConnectResult(HttpURLConnection mConnect) throws Exception {
        String result;
        InputStream in = mConnect.getInputStream();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        int len;
        byte[] flush = new byte[10240];

        while ((len = in.read(flush)) != -1) {
            bOut.write(flush, 0, len);
        }
        result = bOut.toString(Config.UTF_8);
        return result;
    }

    public static void connectResult(HttpURLConnection mConnect, SuccessCallback success, FailCallback fail) throws Exception {
        if (Config.CONECT_SUCCESS == mConnect.getResponseCode()) {
            String s = Config.getConnectResult(mConnect);

            if (success != null) {
                success.successDo(s);
            }
        } else {
            if (fail != null) {
                fail.failDo();
            }
        }
    }
}
