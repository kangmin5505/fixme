package kr._42.seoul.client;

import kr._42.seoul.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BrokerClient implements AutoCloseable {
    private static final int BUFFER_CAPACITY = 1024;
    private static final Logger logger = LoggerFactory.getLogger(BrokerClient.class);
    private SocketChannel socket;
    private ByteBuffer buffer;
    private String id;

    private BrokerClient() {}


    public static BrokerClient open(String hostname, int port) {
        logger.debug("Try to connect server({}:{})", hostname, port);

        BrokerClient brokerClient = new BrokerClient();
        try {
            brokerClient.socket = SocketChannel.open(new InetSocketAddress(hostname, port));

            logger.debug("Success to connect server({})", brokerClient.socket);
            return brokerClient;
        } catch (IOException e) {
            throw new RuntimeException("Fail to open socket channel");
        }
    }


    @Override
    public void close() {
        logger.debug("Try to close broker client");

        CommonUtils.close(this.socket);
    }

    public void setup() {
        logger.debug("Try to setup for request");

        try {
            this.buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
            this.socket.read(this.buffer);
            this.id = new String(this.buffer.array()).trim();
            this.buffer.clear();

            logger.debug("Success to setup for request with id {}", this.id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String request() {
        logger.debug("Try to request message");

        try {
            // request
            this.buffer = ByteBuffer.wrap(this.id.getBytes());
            this.socket.write(this.buffer);
            this.buffer.clear();

            // response
            this.socket.read(this.buffer);
            return new String(this.buffer.array()).trim();
        } catch (IOException e) {
            throw new RuntimeException("Fail to receive response from server");
        }
    }
}
