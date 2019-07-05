package clink.net.qiujuer.clink.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by asus on 2019/7/4.
 */
public class CloseUtils {
    public static void close (Closeable ...closeables) {
        if(closeables == null) {
            return;
        }
        for(Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
