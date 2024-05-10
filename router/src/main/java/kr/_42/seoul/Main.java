package kr._42.seoul;

import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.broker.BrokerRouter;
import kr._42.seoul.common.RouterMediator;
import kr._42.seoul.common.ThreadPool;
import kr._42.seoul.market.MarketRouter;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int BROKER_PORT = 5000;
    private static final int MARKET_PORT = 5001;

    public static void main(String[] args) {
        BrokerRouter brokerRouter = new BrokerRouter();
        MarketRouter marketRouter = new MarketRouter();
        RouterMediator routerMediator = new RouterMediator();
        routerMediator.registerBrokerRouter(brokerRouter);
        routerMediator.registerMarketRouter(marketRouter);

        ExecutorService executorService = ThreadPool.getExecutorService();
        executorService.submit(() -> {
            Thread.currentThread().setName("BrokerRouter");
            try {
                brokerRouter.open();
                brokerRouter.bind(BROKER_PORT);
                brokerRouter.run();
            } catch (Exception e) {
                logger.error("Error occurred while running broker router", e);
                System.exit(1);
            }
        });

        executorService.submit(() -> {
            Thread.currentThread().setName("MarketRouter");
            try {
                marketRouter.open();
                marketRouter.bind(MARKET_PORT);
                marketRouter.run();
            } catch (Exception e) {
                logger.error("Error occurred while running market router", e);
                System.exit(1);
            }
        });

        executorService.shutdown();
    }
}
