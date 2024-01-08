package kr._42.seoul.client;

import kr._42.seoul.SocketChannelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;

public class MarketChannelClient extends SocketChannelClient {
    private static final Logger logger = LoggerFactory.getLogger(MarketChannelClient.class);

    private MarketChannelClient() {}

    @Override
    public void close() {
        logger.debug("Try to close market client");

        super.close();

        logger.debug("Success to close market client");
    }

    @Override
    protected void read(SelectionKey key) {
        logger.debug("Try to read data from client");

        super.read(key);

        logger.debug("Success to read data from client");

    }

    @Override
    protected void setup() {
        logger.debug("Try to setup");

        super.setup();

        logger.debug("Success to setup with id {}", this.id);
    }
}
