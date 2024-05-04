package kr._42.seoul;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokerRouter extends ServerSocket {
    private final static Logger logger = LoggerFactory.getLogger(BrokerRouter.class);
    private final static IDGenerator idGenerator = new IDGenerator();

    protected void write(SelectionKey key) throws IOException {
        logger.debug("Writing to client");

        SocketChannel client = (SocketChannel) key.channel();
        Object attachment = key.attachment();

        client.write(ByteBuffer.wrap(attachment.toString().getBytes()));
        key.interestOps(SelectionKey.OP_READ);
    }

    protected void read(SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        SocketChannel client = (SocketChannel) key.channel();

        int readByte = client.read(buffer);
        if (readByte == -1) {
            client.close();
            key.cancel();
            return;
        }

        buffer.flip();
        FIXMessage message = new FIXMessage(buffer);

        logger.info(new String(message.toByteBuffer().array()));
    }

    protected void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        SelectionKey clientKey = client.register(this.selector, SelectionKey.OP_WRITE);

        String clientID = idGenerator.generateID();
        clientKey.attach(clientID);
    }
}
