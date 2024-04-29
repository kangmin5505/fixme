package kr._42.seoul;

public class DefaultBrokerServer implements BrokerServer {
    private final String hostname;
    private final int port;

    public DefaultBrokerServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
}
