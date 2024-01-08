package kr._42.seoul.server;

import kr._42.seoul.IOUtils;
import kr._42.seoul.idgenerator.MarketIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class MarketServer extends Server {
    private static final Logger logger = LoggerFactory.getLogger(MarketServer.class);
    private static final IDGenerator idGenerator = new MarketIDGenerator();
    private static final int MARKET_PORT = 5001;

    private MarketServer() {
    }

    public static Server open() {
        logger.debug("Try to open market server");

        Server server = Server.open(new MarketServer());

        logger.debug("Success to open market server");
        return server;
    }

    private void setup() {
        Thread.currentThread().setName("MarketServer");

        logger.debug("Try to setup market server");
        super.setup(MARKET_PORT);

        logger.debug("Success to setup market server");
    }

    @Override
    public void close() {
        logger.debug("Try to close market server");
        // Utils 사용

        logger.debug("Success to close market server");
    }

    // refactor
    @Override
    public void run() {
        this.setup();

        logger.debug("Running market server");

        while (true) {
            try {
                this.selector.select();

                Set<SelectionKey> selectedKeys = this.selector.selectedKeys();
                for (SelectionKey key : selectedKeys) {
                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    }
                }
                selectedKeys.clear();
            } catch (IOException e) {
                logger.debug("Error occurred while server is running");
                break;
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        logger.debug("Try to read data from client");

        // Read data from the client
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
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
    }

    // refactor accept(SelectionKey key, IDGenerator iDGenerator)
    private void accept(SelectionKey key) {
        logger.debug("Try to accept connection");

        try {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();

            logger.debug("Success to accept connection from {}", client);

            this.setupClient(client);

            String id = idGenerator.generate();
            ByteBuffer buffer = ByteBuffer.wrap(id.getBytes());
            client.write(buffer);
        } catch (IOException e) {
            logger.error("Fail to accept connection");
            throw new RuntimeException(e);
        }

    }

    // refactor
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
