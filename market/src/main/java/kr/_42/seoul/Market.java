package kr._42.seoul;

import kr._42.seoul.client.MarketClient;

import java.io.IOException;

public class Market {
    private static int FAIL_STATUS = 2;
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5001;

    public static void main(String[] args) {
        try (ClientMultiplexer marketClient = new MarketClient(HOSTNAME, PORT)) {
            marketClient.run();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(FAIL_STATUS);
        }
    }
}
