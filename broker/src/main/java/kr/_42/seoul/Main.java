package kr._42.seoul;

import java.util.concurrent.ExecutorService;
import kr._42.seoul.client.BrokerClient;
import kr._42.seoul.client.ConsoleRequestHandler;
import kr._42.seoul.client.ConsoleResponseHandler;
import kr._42.seoul.client.RequestHandler;
import kr._42.seoul.client.ResponseHandler;
import kr._42.seoul.common.ThreadPool;
import kr._42.seoul.server.BrokerServer;
import kr._42.seoul.server.DefaultBrokerServer;
import kr._42.seoul.server.repository.MemoryRepository;
import kr._42.seoul.server.repository.Repository;

public class Main {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        Repository repository = MemoryRepository.getInstance();
        // Repository repository = new DBRepository();
        BrokerServer brokerServer = new DefaultBrokerServer(HOSTNAME, PORT, repository);

        RequestHandler requestHandler = new ConsoleRequestHandler();
        ResponseHandler responseHandler = new ConsoleResponseHandler();
        BrokerClient brokerClient = new BrokerClient(brokerServer, requestHandler, responseHandler);

        ExecutorService executorService = ThreadPool.getExecutorService();

        executorService.submit(() -> {
            try {
                brokerClient.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // executorService.submit(() -> {
        // while (true) {
        // Thread.sleep(2000);
        // System.out.println("BrokerServer is running");
        // }
        // });

        executorService.shutdown();
    }
}
