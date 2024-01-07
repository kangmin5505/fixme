package kr._42.seoul.server;

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

public class BrokerServer implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(BrokerServer.class);
    private Selector selector;
    private ServerSocketChannel serverSocket;

    private BrokerServer() {}

    public static BrokerServer open() {
        logger.debug("Try to open broker server");

        BrokerServer brokerServer = new BrokerServer();
        try {
            brokerServer.selector = Selector.open();
            brokerServer.serverSocket = ServerSocketChannel.open();

            return brokerServer;
        } catch (IOException e) {
            throw new RuntimeException("Fail to open broker server");
        }
    }

    public void setup(int port) {
        try {
            this.serverSocket.bind(new InetSocketAddress(port));
            this.serverSocket.configureBlocking(false);
            this.serverSocket.register(this.selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException("Fail to setup broker server");
        }
    }

    public void start() {
        logger.debug("Try to start broker server");

        while (true) {
            try {
                this.selector.select();

                Set<SelectionKey> selectedKeys = this.selector.selectedKeys();

                selectedKeys.forEach(key -> {
                    try {
                        if (key.isAcceptable()) {
                            this.accept(key);
                        } else if (key.isReadable()) {
                            // Read data from the client
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int bytesRead = client.read(buffer);
                            logger.debug("Read {} bytes from {} : ({})", bytesRead, client, new String(buffer.array()).trim());

                            if (bytesRead == -1) {
                                // Client has closed the connection
                                client.close();
                            } else if (bytesRead > 0) {
                                // Make buffer ready for reading
                                buffer.flip();

                                // Echo the data back to the client
                                client.write(buffer);

                                // Make buffer ready for writing again
                                buffer.clear();
                            }
                        }
                    }
                });
                selectedKeys.clear();
            } catch (IOException e) {
                throw new RuntimeException("Error occurred while server is running");
            }
        }
    }

    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();

            logger.debug("Accept the connection from {}", client);

            String welcomeMessage = "01024855505";
            ByteBuffer buffer = ByteBuffer.wrap(welcomeMessage.getBytes());
            client.write(buffer);

            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new RuntimeException("Fail to accept connection");
        }
    }

    @Override
    public void close() throws Exception {
        logger.debug("Try to close broker server");

    }
}
