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
    private Mediator mediator;

    protected void write(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        Object attachment = key.attachment();
        byte[] bytes = (byte[]) attachment;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        client.write(byteBuffer);
        key.interestOps(SelectionKey.OP_READ);

        logger.debug("Success to write to client: {}", new String(bytes));
    }

    protected void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        int readByte = client.read(this.buffer);
        if (readByte == -1) {
            client.close();
            key.cancel();
            return;
        }

        this.buffer.flip();
        FIXMessage message = new FIXMessage(this.buffer);

        this.mediator.sendMarketRouter(message);

        logger.info("Success to send to marketRouter: {}", new String(message.toByteBuffer().array()));
    }

    protected void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        SelectionKey clientKey = client.register(this.selector, SelectionKey.OP_WRITE);

        String clientID = idGenerator.generateID();
        clientKey.attach(clientID.getBytes());
    }

    public void registerMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
