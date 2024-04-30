package kr._42.seoul.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.common.Request;
import kr._42.seoul.common.Response;
import kr._42.seoul.server.BrokerServer;

public class BrokerClient {
    private static final Logger logger = LoggerFactory.getLogger(BrokerClient.class);
    private final BrokerServer brokerServer;
    private final RequestHandler requestHandler;
    private final ResponseHandler responseHandler;

    public BrokerClient(BrokerServer brokerServer, RequestHandler requestHandler,
            ResponseHandler responseHandler) {
        this.brokerServer = brokerServer;
        this.requestHandler = requestHandler;
        this.responseHandler = responseHandler;
        Thread.currentThread().setName("BrokerClientThread");
    }

    public void run() {
        logger.debug("BrokerClient is running");

        while (true) {
            try {
                Request request = requestHandler.getRequest();

                logger.debug(request.toString());

                switch (request.getCommand()) {
                    case ORDER:
                        brokerServer.order(request);
                        break;
                    case QUERY:
                        Response response = brokerServer.query(request);
                        responseHandler.handle(response);
                        break;
                    case EXIT:
                        System.exit(0);
                }
            } catch (Exception e) {
                logger.error("Error occurred while getting user request", e);
                continue;
            }
        }
    }
}
