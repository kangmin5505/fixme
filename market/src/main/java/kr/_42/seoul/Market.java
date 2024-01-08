package kr._42.seoul;

import kr._42.seoul.client.MarketChannelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Market {
    private static final Logger logger = LoggerFactory.getLogger(Market.class);
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5001;


    public static void main( String[] args ) {
        logger.debug("Start market");

        try (MarketChannelClient marketClient = SocketChannelClient.open(HOSTNAME, PORT, MarketChannelClient.class)) {
            marketClient.run();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }

    }
}
