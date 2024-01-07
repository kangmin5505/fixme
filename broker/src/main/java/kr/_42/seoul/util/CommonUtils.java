package kr._42.seoul.util;

public abstract class CommonUtils {
    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception ignored) {}
            }
        }
    }
}
