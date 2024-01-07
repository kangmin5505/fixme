package kr._42.seoul;

import kr._42.seoul.server.BrokerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {
    private static final Logger logger = LoggerFactory.getLogger(Router.class);

    private static final int BROKER_PORT = 5000;
    private static final int MARKET_PORT = 5001;
    private static int FAIL_STATUS = 2;
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> {
            try (BrokerServer brokerServer = BrokerServer.open()) {
                brokerServer.setup(BROKER_PORT);
            } catch (RuntimeException e) {
                error(e.getMessage());
            }

        });

        executorService.shutdown();
    }

    private static void error(String message) {
        System.err.println(message);
        exit();
    }

    private static void exit() {
        System.exit(FAIL_STATUS);
    }
}
