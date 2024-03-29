package kr._42.seoul;

import kr._42.seoul.server.BrokerServer;
import kr._42.seoul.server.MarketServer;
import kr._42.seoul.server.ServerMultiplexer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {
    public static final int THREAD_POOL_SIZE = 2;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static final int BROKER_PORT = 5000;
    private static final int MARKET_PORT = 5001;
    private static final int EXIT_STATUS = 2;

    public static void main(String[] args) {
        executorService.submit(() -> {
            try (ServerMultiplexer brokerServer = new BrokerServer(BROKER_PORT)) {
                brokerServer.run();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
        executorService.submit(() -> {
            try (ServerMultiplexer marketServer = new MarketServer(MARKET_PORT)) {
                marketServer.run();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });

        executorService.shutdown();
    }

}
