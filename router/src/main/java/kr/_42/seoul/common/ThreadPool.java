package kr._42.seoul.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private static final int MAX_THREAD_SIZE = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executorService =
            Executors.newFixedThreadPool(MAX_THREAD_SIZE);

    public static ExecutorService getExecutorService() {
        return executorService;
    }

}

