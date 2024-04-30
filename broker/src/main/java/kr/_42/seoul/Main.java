package kr._42.seoul;

import java.util.concurrent.ExecutorService;
import kr._42.seoul.client.BrokerClient;
import kr._42.seoul.client.ConsoleUserRequest;
import kr._42.seoul.client.UserRequest;
import kr._42.seoul.common.ThreadPool;
import kr._42.seoul.server.BrokerServer;
import kr._42.seoul.server.DefaultBrokerServer;

public class Main {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        BrokerServer brokerServer = new DefaultBrokerServer(HOSTNAME, PORT);

        UserRequest userRequest = new ConsoleUserRequest();
        BrokerClient brokerClient = new BrokerClient(brokerServer, userRequest);

        ExecutorService executorService = ThreadPool.getExecutorService();

        executorService.submit(() -> {
            try {
                brokerClient.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            while (true) {
                Thread.sleep(2000);
                System.out.println("BrokerServer is running");
            }
        });

        executorService.shutdown();
    }
}
