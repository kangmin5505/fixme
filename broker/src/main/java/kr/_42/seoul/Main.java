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
import kr._42.seoul.server.DefaultBrokerServer;
import kr._42.seoul.server.repository.MemoryRepository;
import kr._42.seoul.server.repository.Repository;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        Repository repository = MemoryRepository.getInstance();
        // Repository repository = new DBRepository();
        try {
            BrokerServer brokerServer = new DefaultBrokerServer(HOSTNAME, PORT, repository);
            BrokerClient brokerClient =
                    new BrokerClient(brokerServer, new ConsoleRequestHandler(), new ConsoleResponseHandler());
            ExecutorService executorService = ThreadPool.getExecutorService();

            executorService.submit(() -> {
                try {
                    brokerServer.run();
                } catch (IOException e) {
                    logger.error("Error occurred while running broker server", e);
                    System.exit(1);
                }
            });
            
            executorService.submit(() -> {
                brokerClient.run();
            });
            
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
