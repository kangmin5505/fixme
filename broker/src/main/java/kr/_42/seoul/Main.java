package kr._42.seoul;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.client.BrokerClient;
import kr._42.seoul.client.ConsoleRequestHandler;
import kr._42.seoul.client.ConsoleResponseHandler;
import kr._42.seoul.common.ThreadPool;
import kr._42.seoul.server.BrokerServer;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        BrokerServer brokerServer = new BrokerServer();
        BrokerClient brokerClient = new BrokerClient(new ConsoleRequestHandler(),
                new ConsoleResponseHandler());
        BrokerMediator brokerMediator = new BrokerMediator();
        brokerMediator.registerBrokerClient(brokerClient);
        brokerMediator.registerBrokerServer(brokerServer);

        try {
            brokerServer.open();
            brokerServer.connect(HOSTNAME, PORT);
        } catch (IOException e) {
            logger.error("Failed to start Broker Server", e.getMessage());
            System.exit(1);
        }

        ExecutorService executorService = ThreadPool.getExecutorService();
        executorService.submit(() -> {
            Thread.currentThread().setName("BrokerServer");
            brokerServer.run();
        });

        executorService.submit(() -> {
            Thread.currentThread().setName("BrokerClient");
            brokerClient.run();
        });

        executorService.shutdown();
    }
}
