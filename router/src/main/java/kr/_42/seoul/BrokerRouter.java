package kr._42.seoul;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokerRouter {
    private final Logger logger = LoggerFactory.getLogger(BrokerRouter.class);
    private final int port;
    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;

    // TODO: close
    public BrokerRouter(int brokerPort) throws IOException {
        this.port = brokerPort;
        this.serverSocketChannel = ServerSocketChannel.open();
        this.selector = Selector.open();
    }

    public void run() throws IOException {
        this.logger.debug("BrokerRouter is running");

        setup();

        while (true) {
            try {
                this.selector.select();

                for (SelectionKey key : this.selector.selectedKeys()) {
                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    } else if (key.isWritable()) {
                        this.write(key);
                    }
                }

                this.selector.selectedKeys().clear();
            } catch (IOException e) {
                this.logger.error("Error occurred while selecting keys", e);
                e.printStackTrace();
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        logger.debug("Writing to client");

        SocketChannel client = (SocketChannel) key.channel();

        client.write(ByteBuffer.wrap("Pong".getBytes()));
    }

    private void read(SelectionKey key) throws IOException {
        this.logger.debug("Reading from client");

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        SocketChannel client = (SocketChannel) key.channel();

        int readByte = client.read(buffer);
        if (readByte == -1) {
            client.close();
            key.cancel();
            return;
        }

        buffer.flip();
        logger.debug(new String(buffer.array()));
        client.write(ByteBuffer.wrap("Pong".getBytes()));
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(this.selector, SelectionKey.OP_READ);
    }

    private void setup() throws IOException {
        this.serverSocketChannel.bind(new InetSocketAddress(this.port));
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
    }

}
