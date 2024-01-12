package kr._42.seoul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class ClientMultiplexer implements AutoCloseable {
    protected static final Logger logger = LoggerFactory.getLogger(ClientMultiplexer.class);
    protected static final int BUFFER_CAPACITY = 1024;
    protected Selector selector;
    protected SocketChannel socket;
    protected ByteBuffer buffer;
    private String id;

    protected ClientMultiplexer(String hostname, int port) throws IOException {
        logger.debug("Try to create client multiplexer");

        this.selector = Selector.open();
        this.socket = SocketChannel.open(new InetSocketAddress(hostname, port));

        logger.debug("Success to create client multiplexer");
    }

    @Override
    public void close() {
        logger.debug("Try to close client multiplexer");

        IOUtils.close(this.socket, this.selector);

        logger.debug("Success to close client multiplexer");
    }

    protected void setup() {
        logger.debug("Try to setup client multiplexer");

        try {
            this.buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
            this.socket.read(this.buffer);
            this.id = new String(this.buffer.array()).trim();

            this.socket.configureBlocking(false);
            this.socket.register(this.selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new RuntimeException("Fail to setup");
        }

        logger.debug("Success to setup client multiplexer");
    }

    protected void run() {
        logger.debug("Start running");
        this.setup();

        implRun();
    }

    protected abstract void implRun();

    protected void read(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();

        try {
            this.buffer.clear();
            int readBytes = client.read(this.buffer);

            if (readBytes == IOUtils.EOF) {
                client.close();
            } else {
                // print result
                this.buffer.flip();
                client.write(this.buffer);
                this.buffer.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException("Fail to read data from client");
        }

    }
}
