package kr._42.seoul.server;

import kr._42.seoul.IOUtils;
import kr._42.seoul.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ServerMultiplexer implements AutoCloseable {
    protected static final Logger logger = LoggerFactory.getLogger(ServerMultiplexer.class);
    protected final ExecutorService executorService;
    protected static final int BUFFER_CAPACITY = 1024;
    protected Selector selector;
    protected ServerSocketChannel serverSocket;
    protected final int port;

    protected ServerMultiplexer(int port) throws IOException {
        int nThreads = (Runtime.getRuntime().availableProcessors() - Router.ROUTER_THREAD_POOL_SIZE) / 2;

        this.executorService = Executors.newFixedThreadPool(nThreads);
        this.port = port;
        this.selector = Selector.open();
        this.serverSocket = ServerSocketChannel.open();
    }

    protected void setup() {
        logger.debug("Try to setup");

        try {
            this.serverSocket.bind(new InetSocketAddress(this.port));
            this.serverSocket.configureBlocking(false);
            this.serverSocket.register(this.selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException("Fail to setup market server");
        }

        logger.debug("Success to setup");
    }

    public void run() {
        logger.debug("Running server");

        this.setup();

        while (true) {
            try {
                this.selector.select();

                Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                selectionKeys.stream()
                             .filter(SelectionKey::isValid)
                             .forEach(key -> {
                                 if (key.isAcceptable()) {
                                     this.accept(key);
                                 } else if (key.isReadable()) {
                                     this.read(key);
                                 } else if (key.isWritable()) {
                                     this.write(key);
                                 }
                             });

                selectionKeys.clear();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                break;
            }
        }
    }

    @Override
    public void close() {
        logger.debug("Try to close");

        IOUtils.close(this.serverSocket, this.selector);

        logger.debug("Success to close");
    }

    protected void disconnect(SelectionKey key) {
        logger.debug("Try to disconnect client");

        SocketChannel client = (SocketChannel) key.channel();
        try {
            key.cancel();
            client.close();

            logger.debug("Success to disconnect client ({})", client);
        } catch (IOException ignored) {
            logger.error("Fail to disconnect client ({})", client);
        }
    }

    protected void write(SelectionKey key) {
        logger.debug("Try to write to client");

        SocketChannel client = (SocketChannel) key.channel();
        Attachment attachment = (Attachment) key.attachment();
        ByteBuffer buffer = attachment.getBuffer();

        try {
            client.write(buffer);
            key.interestOps(SelectionKey.OP_READ);

            logger.debug("Success to write to client ({})", client);
        } catch (IOException e) {
        }
    }

    protected abstract void accept(SelectionKey key);
    protected abstract void read(SelectionKey key);

    protected class Attachment {
        private final ByteBuffer buffer;
        private final String id;

        public Attachment(int bufferCapacity, String id) {
            this.buffer = ByteBuffer.allocate(bufferCapacity);
            this.id = id;
            this.buffer.put(this.id.getBytes());
            this.buffer.flip();
        }
        public ByteBuffer getBuffer() {
            return this.buffer;
        }

        public String getId() {
            return this.id;
        }
    }
}
