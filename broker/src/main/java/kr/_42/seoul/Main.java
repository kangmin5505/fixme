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
import kr._42.seoul.server.repository.MemoryRepository;
import kr._42.seoul.server.repository.Repository;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        Repository repository = MemoryRepository.getInstance();
        // Repository repository = new DBRepository();

        BrokerServer brokerServer = new BrokerServer(repository);

        BrokerClient brokerClient = new BrokerClient(brokerServer, new ConsoleRequestHandler(),
                new ConsoleResponseHandler());
        ExecutorService executorService = ThreadPool.getExecutorService();

        try {
            brokerServer.open();
            brokerServer.connect(HOSTNAME, PORT);
        } catch (IOException e) {
            logger.error("Failed to start Market Server", e.getMessage());
            System.exit(1);
        }

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
