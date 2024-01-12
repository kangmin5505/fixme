package kr._42.seoul.server;

import kr._42.seoul.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public abstract class ServerMultiplexer implements AutoCloseable {
    protected static final Logger logger = LoggerFactory.getLogger(ServerMultiplexer.class);
    protected static final int BUFFER_CAPACITY = 1024;
    protected ByteBuffer buffer;
    protected Selector selector;
    protected ServerSocketChannel serverSocket;
    protected final int port;

    protected ServerMultiplexer(int port) throws IOException {
        this.port = port;
        this.selector = Selector.open();
        this.serverSocket = ServerSocketChannel.open();
    }

    protected void setup() {
        logger.debug("Try to setup");

        try {
            this.buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

            this.serverSocket.bind(new InetSocketAddress(this.port));
            this.serverSocket.configureBlocking(false);
            this.serverSocket.register(this.selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException("Fail to setup market server");
        }

        logger.debug("Success to setup");
    }

    public void run() {
        this.setup();

        implRun();
    }

    public abstract void implRun();

    @Override
    public void close() {
        logger.debug("Try to close");

        IOUtils.close(this.serverSocket, this.selector);

        logger.debug("Success to close");
    }

    protected void disconnect(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        logger.debug("Try to disconnect client ({})", client);

        try {
            key.cancel();
            client.close();
            logger.debug("Success to disconnect client ({})", client);
        } catch (IOException ignored) {
            logger.error("Fail to disconnect client ({})", client);
        }
    }

    public abstract void accept(SelectionKey key);
    public abstract void read(SelectionKey key);

}
