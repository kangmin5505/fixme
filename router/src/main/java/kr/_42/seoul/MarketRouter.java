package kr._42.seoul;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketRouter extends ServerSocket {
    private final Logger logger = LoggerFactory.getLogger(MarketRouter.class);

    protected void write(SelectionKey key) throws IOException {
        logger.debug("Writing to client");

        SocketChannel client = (SocketChannel) key.channel();

        client.write(ByteBuffer.wrap("Pong".getBytes()));
    }

    protected void read(SelectionKey key) throws IOException {
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

    protected void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(this.selector, SelectionKey.OP_READ);
    }
}
