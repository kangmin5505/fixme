package kr._42.seoul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public abstract class AsynchronousClient implements AutoCloseable {
    protected static final Logger logger = LoggerFactory.getLogger(AsynchronousClient.class);
    protected final int bufferCapacity;
    protected AsynchronousSocketChannel client;
    private final String hostname;
    private final int port;
    private String id;


    protected AsynchronousClient(String hostname, int port) throws IOException {
        this.client = AsynchronousSocketChannel.open();
        this.hostname = hostname;
        this.port = port;
        this.bufferCapacity = 1024;
    }

    protected void init() {
        logger.debug("Try to init client");

        try {
            this.client.connect(new InetSocketAddress(this.hostname, this.port)).get();

            logger.debug("Success to init client");
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Fail to connect to server");
        }
    }

    protected void run() {
        logger.debug("Running client");

        this.init();
        this.receiveID();

        runImpl();
    }

    protected void receiveID() {
        logger.debug("Try to get ID");

        ByteBuffer buffer = ByteBuffer.allocate(this.bufferCapacity);
        try {
            this.client.read(buffer).get();
            this.id = new String(buffer.array()).trim();

            logger.debug("Success to get ID({})", this.id);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Fail to get ID");
        }
    }

    protected abstract void runImpl();

    @Override
    public void close() {
        IOUtils.close(this.client);
    }

    public String getId() {
        return this.id;
    }

    public static class Attachment {
        private final AsynchronousSocketChannel channel;
        private final ByteBuffer buffer;

        public Attachment(AsynchronousSocketChannel channel, int bufferCapacity) {
            this.channel = channel;
            this.buffer = ByteBuffer.allocate(bufferCapacity);
        }

        public AsynchronousSocketChannel getChannel() {
            return this.channel;
        }

        public ByteBuffer getBuffer() {
            return this.buffer;
        }
    }
}