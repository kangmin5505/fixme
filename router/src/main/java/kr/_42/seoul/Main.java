package kr._42.seoul;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.broker.BrokerRouter;
import kr._42.seoul.common.RouterMediator;
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

        try {
            brokerRouter.open();
            brokerRouter.bind(BROKER_PORT);
            marketRouter.open();
            marketRouter.bind(MARKET_PORT);
        } catch (IOException e) {
            logger.error("Failed to start routers", e);
            System.exit(1);
        }

        ExecutorService executorService = ThreadPool.getExecutorService();
        List<Callable<Object>> tasks = new ArrayList<>();
        Callable<Object> brokerRouterTask = Executors.callable(() -> {
            Thread.currentThread().setName("BrokerRouter");
            brokerRouter.run();
        });
        Callable<Object> marketRouterTask = Executors.callable(() -> {
            Thread.currentThread().setName("MarketRouter");
            marketRouter.run();
        });

        tasks.add(brokerRouterTask);
        tasks.add(marketRouterTask);

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            logger.error("Interrupted while running: ", e);
            System.exit(1);
        }
    }
}
