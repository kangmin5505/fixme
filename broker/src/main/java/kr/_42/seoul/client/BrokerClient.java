package kr._42.seoul.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.enums.BrokerCommand;
import kr._42.seoul.server.BrokerServer;

public class BrokerClient {
    private static final Logger logger = LoggerFactory.getLogger(BrokerClient.class);
    private final BrokerServer brokerServer;
    private final UserRequest userRequest;

    public BrokerClient(BrokerServer brokerServer, UserRequest userRequest) {
        this.brokerServer = brokerServer;
        this.userRequest = userRequest;
    }

    public void run() {
        logger.debug("BrokerClient is running");

        while (true) {
            try {
                Request request = userRequest.getUserRequest();

                // 3. send request to broker server

                logger.debug(request.toString());
                if (request.getCommand() == BrokerCommand.EXIT) {
                    System.exit(0);
                }
            } catch (Exception e) {
                logger.error("Error occurred while getting user request", e);
                continue;
            }
        }
    }
}
