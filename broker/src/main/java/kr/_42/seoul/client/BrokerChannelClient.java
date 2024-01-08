package kr._42.seoul.client;

import kr._42.seoul.SocketChannelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class BrokerChannelClient extends SocketChannelClient {
    private static final Logger logger = LoggerFactory.getLogger(BrokerChannelClient.class);

    private BrokerChannelClient() {}

    @Override
    public void close() {
        logger.debug("Try to close broker client");

        super.close();

        logger.debug("Success to close broker client");
    }

    @Override
    protected void setup() {
        logger.debug("Try to setup");

        super.setup();

        logger.debug("Success to setup with id {}", this.id);
    }


    @Override
    protected void read(SelectionKey key) {
        logger.debug("Try to read data from client");

        super.read(key);

        logger.debug("Success to read data from client");
    }

    public void request(String message) {
        logger.debug("Try to request message");

        try {
            // request
            this.buffer = ByteBuffer.wrap(message.getBytes());
            this.client.write(this.buffer);
            this.buffer.clear();

            // response
            this.client.read(this.buffer);
            logger.debug("Response from server: {}", new String(this.buffer.array()).trim());
        } catch (IOException e) {
            throw new RuntimeException("Fail to receive response from server");
        }
    }
}
