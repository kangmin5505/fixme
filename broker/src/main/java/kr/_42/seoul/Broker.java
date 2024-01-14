package kr._42.seoul;

import kr._42.seoul.client.AsynchronousBrokerClient;

import java.io.IOException;


public class Broker {
    public static int FAIL_STATUS = 2;
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {

        try (AsynchronousClient broker = new AsynchronousBrokerClient(HOSTNAME, PORT)) {
            broker.run();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(FAIL_STATUS);
        }
    }
}
