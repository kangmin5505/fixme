package kr._42.seoul.client;

import java.util.NoSuchElementException;
import kr._42.seoul.common.Request;
import kr._42.seoul.common.Response;
import kr._42.seoul.server.BrokerServer;

public class BrokerClient {
    private final BrokerServer brokerServer;
    private final RequestHandler requestHandler;
    private final ResponseHandler responseHandler;

    public BrokerClient(BrokerServer brokerServer, RequestHandler requestHandler,
            ResponseHandler responseHandler) {
        this.brokerServer = brokerServer;
        this.requestHandler = requestHandler;
        this.responseHandler = responseHandler;
    }

    public void run() {
        while (true) {
            try {
                Request request = requestHandler.getRequest();

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
            } catch (NoSuchElementException e) { // EOF
                System.exit(0);
            } catch (Exception e) {
                responseHandler.error("Fail to request: " + e.getMessage());
                continue;
            }
        }
    }
}
