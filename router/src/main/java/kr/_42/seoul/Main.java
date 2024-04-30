package kr._42.seoul;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class Main {
    private static final int BROKER_PORT = 5000;
    private static final int MARKET_PORT = 5001;

    public static void main(String[] args) {
        try {
            BrokerRouter brokerRouter = new BrokerRouter(BROKER_PORT);
            MarketRouter marketRouter = new MarketRouter(MARKET_PORT);

            ExecutorService executorService = ThreadPool.getExecutorService();

            executorService.submit(() -> {
                brokerRouter.run();
            });
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mediator mediator = new Mediator(brokerRouter, marketRouter);

        // executorService.submit(() -> {
        // marketRouter.run();
        // });
        // executorService.submit(() -> {
        // mediator.run();
        // });

    }
}
