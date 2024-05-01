package kr._42.seoul;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Market {
    private final Logger logger = LoggerFactory.getLogger(Market.class);
    private String hostname;
    private int port;
    private Repository repository;
    private SocketChannel socketChannel;
    private Selector selector;

    public Market(String hostname, int port, Repository repository) throws IOException {
        this.hostname = hostname;
        this.port = port;
        this.repository = repository;
        this.socketChannel = SocketChannel.open();
        this.selector = Selector.open();
    }

    public void run() throws IOException {

        this.setup();

        while (true) {
            try {
                this.selector.select();

                for (SelectionKey key : this.selector.selectedKeys()) {
                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isWritable()) {
                        this.write(key);
                    } else if (key.isReadable()) {
                        this.read(key);
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
        logger.debug("Writing Ping to client");

        SocketChannel client = (SocketChannel) key.channel();

        client.write(ByteBuffer.wrap("Ping".getBytes()));
        key.interestOps(SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) {
        logger.debug("Reading from client");

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        SocketChannel client = (SocketChannel) key.channel();

        try {
            client.read(buffer);
            buffer.flip();

            logger.debug(new String(buffer.array()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setup() throws IOException {
        this.socketChannel.connect(new InetSocketAddress(this.hostname, this.port));
        this.socketChannel.configureBlocking(false);
        this.socketChannel.register(this.selector, SelectionKey.OP_WRITE);
    }

}
