package kr._42.seoul;

import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.client.BrokerClient;
import kr._42.seoul.common.Request;
import kr._42.seoul.common.Response;
import kr._42.seoul.server.BrokerServer;

public class BrokerMediator {
    private static final ExecutorService executorService = ThreadPool.getExecutorService();
    private final Logger logger = LoggerFactory.getLogger(BrokerMediator.class);
    private BrokerClient brokerClient;
    private BrokerServer brokerServer;

    public void registerBrokerClient(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
        brokerClient.registerBrokerMediator(this);
    }

    public void registerBrokerServer(BrokerServer brokerServer) {
        this.brokerServer = brokerServer;
        brokerServer.registerBrokerMediator(this);
    }

    public void sendToBrokerServer(Request request) {
        executorService.submit(() -> {
            this.brokerServer.order(request);
            logger.info("Order request: {}", request.toString());
        });
    }

    public void sendToBrokerClient(Response response) {
        executorService.submit(() -> {
            this.brokerClient.receive(response);
            logger.info("Received response");
        });
    }
}
