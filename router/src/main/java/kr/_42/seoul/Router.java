package kr._42.seoul;

import kr._42.seoul.server.BrokerServer;
import kr._42.seoul.server.MarketServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(BrokerServer.open());
        executorService.submit(MarketServer.open());

        executorService.shutdown();
    }

}
