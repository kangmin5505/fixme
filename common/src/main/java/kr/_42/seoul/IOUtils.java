package kr._42.seoul;

public abstract class IOUtils {
    public static final int EOF = -1;
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
