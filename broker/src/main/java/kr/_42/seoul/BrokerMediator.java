package kr._42.seoul;

import kr._42.seoul.client.BrokerClient;
import kr._42.seoul.common.Request;
import kr._42.seoul.common.Response;
import kr._42.seoul.server.BrokerServer;

public class BrokerMediator {

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
        this.brokerServer.order(request);
    }

    public void sendToBrokerClient(Response response) {
        this.brokerClient.receive(response);
    }
}
