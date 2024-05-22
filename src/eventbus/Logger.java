package eventbus;

import java.sql.Statement;

public class Logger {
    private static final String TAG = "EventBus";
    // 加一行注释
    public static void v(String text) {
        System.out.println(TAG + "+" + text);
    }

    // 再加一行注释
    public static void i(String text) {
        System.out.println(TAG + "+" + text);
    }
}
