package kr._42.seoul.server;

import kr._42.seoul.IOUtils;
import kr._42.seoul.idgenerator.BrokerIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class BrokerServer extends Server {
    private static final Logger logger = LoggerFactory.getLogger(BrokerServer.class);
    private static final int PORT = 5000;

    private BrokerServer() {
        super(PORT);
    }

    public static <T extends Server> T open(Class<T> clazz) {
        logger.debug("Try to open broker server");

        T server = Server.open(clazz);

        logger.debug("Success to open broker server");
        return server;
    }

    protected void setup() {
        Thread.currentThread().setName("BrokerServer");

        logger.debug("Try to setup broker server");

        super.setup();

        logger.debug("Success to setup broker server");
    }

    @Override
    public void close() {
        logger.debug("Try to close broker server");

        super.close();

        logger.debug("Success to close broker server");
    }

    @Override
    public void run() {
        logger.debug("Running broker server");

        super.run();
    }

    @Override
    protected void read(SelectionKey key) {
        logger.debug("Try to read data from client");

        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

        try {
            int readBytes = client.read(buffer);

            if (readBytes == IOUtils.EOF) {
                client.close();
            } else if (readBytes > 0) {
                logger.debug("Read {} bytes from {} - {}", readBytes, client, new String(buffer.array()).trim());

                buffer.flip();
                client.write(buffer);
                buffer.clear();
            }

            logger.debug("Success to read data from client");
        } catch (IOException e) {
            System.err.println("Fail to read data from client");
        }
    }

    @Override
    protected void accept(SelectionKey key) {
        logger.debug("Try to accept connection");

        try {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();

            logger.debug("Success to accept connection from {}", client);

            this.setupClient(client);

            // Generate Broker ID
            String id = BrokerIDGenerator.generate();
            ByteBuffer buffer = ByteBuffer.wrap(id.getBytes());
            client.write(buffer);
        } catch (IOException e) {
            logger.error("Fail to accept connection");
            throw new RuntimeException(e);
        }

    }

    private void setupClient(SocketChannel client) {
        logger.debug("Try to setup client");

        try {
            client.configureBlocking(false);
            client.register(this.selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            logger.error("Fail to setup client");
            throw new RuntimeException(e);
        }

        logger.debug("Success to setup client");
    }
}
