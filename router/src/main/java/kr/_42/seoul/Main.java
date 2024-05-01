package kr._42.seoul;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int BROKER_PORT = 5000;
    private static final int MARKET_PORT = 5001;

    public static void main(String[] args) {
        try {
            BrokerRouter brokerRouter = new BrokerRouter(BROKER_PORT);
            MarketRouter marketRouter = new MarketRouter(MARKET_PORT);

            ExecutorService executorService = ThreadPool.getExecutorService();

            executorService.submit(() -> {
                try {
                    brokerRouter.run();
                } catch (Exception e) {
                    logger.error("Error occurred while running broker router", e);
                    System.exit(1);
                }
            });

            executorService.submit(() -> {
                try {
                    marketRouter.run();
                } catch (Exception e) {
                    logger.error("Error occurred while running market router", e);
                    System.exit(1);
                }
            });
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mediator mediator = new Mediator(brokerRouter, marketRouter);
        // executorService.submit(() -> {
        // mediator.run();
        // });

    }
}
