package eventbus;

import java.sql.Statement;

public class Logger {
    private static final String TAG = "EventBus";

    public static void v(String text) {
        System.out.println(TAG + "+" + text);
    }
    public static void i(String text) {
        System.out.println(TAG + "+" + text);
    }
}
