package kr._42.seoul;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Market extends ClientSocket {
    private final Logger logger = LoggerFactory.getLogger(Market.class);
    private final Repository repository;

    public Market(Repository repository) {
        this.repository = repository;
    }

    protected void write(SelectionKey key) throws IOException {
        logger.debug("Writing Ping to client");

        SocketChannel client = (SocketChannel) key.channel();

        client.write(ByteBuffer.wrap("Ping".getBytes()));
        key.interestOps(SelectionKey.OP_READ);
    }

protected void read(SelectionKey key) throws IOException {
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
}
