package kr._42.seoul.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class OldBrokerClient implements AutoCloseable {
    private final static Logger logger = LoggerFactory.getLogger(OldBrokerClient.class);
    private static final int BUFFER_CAPACITY = 1024;
    private Selector selector;
    private SocketChannel socketChannel;
    private ByteBuffer buffer;

    private OldBrokerClient() {
    }

    public static OldBrokerClient open(String hostname, int port) {
        logger.debug("Try to connect server({}:{})", hostname, port);

        try {
            OldBrokerClient oldBrokerClient = new OldBrokerClient();
            oldBrokerClient.selector = Selector.open();
            oldBrokerClient.socketChannel = SocketChannel.open(new InetSocketAddress(hostname, port));

            return oldBrokerClient;
        } catch (IOException e) {

            throw new RuntimeException(String.format("Fail to connect server(%s:%d)", hostname, port));
        }
    }

    public void setupForSelector() {
        logger.debug("Try to setup for send");

        try {
            this.socketChannel.configureBlocking(false);
            this.socketChannel.register(this.selector, SelectionKey.OP_READ);
            this.buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        } catch (IOException e) {
            throw new RuntimeException("Fail to setup for send");
        }
    }

    public void process() {
        while (true) {
            try {
                this.selector.select();

                this.selector.selectedKeys().forEach(key -> {
                    if (key.isReadable()) {

                    } else if (key.isWritable()) {

                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() {
        logger.debug("Try to close broker client");

        try {
            if (this.selector != null) {
                this.selector.close();
            }
        } catch (IOException ignored) {}

        try {
            if (this.socketChannel != null) {
                this.socketChannel.close();
            }
        } catch (IOException ignored) {}

    }
}
