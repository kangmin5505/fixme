package kr._42.seoul;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.field.Tag;

public class MarketRouter extends ServerSocket {
    private final static IDGenerator idGenerator = new IDGenerator();
    private final Map<String, SocketChannel> clients = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(MarketRouter.class);
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
        client.write(ByteBuffer.wrap("Pong".getBytes()));
    }

    protected void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        SelectionKey clientKey = client.register(this.selector, SelectionKey.OP_WRITE);

        String clientID = idGenerator.generateID();
        this.clients.put(clientID, client);
        clientKey.attach(clientID.getBytes());
    }

    public void registerMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    public void sendToMarket(FIXMessage message) {
        String marketID = (String)message.get(Tag.ID).getValue();
        // TODO: handle null
        SocketChannel marketClientSocket = this.clients.get(marketID);

        try {
            SelectionKey selectionKey = marketClientSocket.register(this.selector, SelectionKey.OP_WRITE);
            byte[] bytes = message.toByteBuffer().array();
            selectionKey.attach(bytes);
        } catch (ClosedChannelException e) {
            logger.error("Failed to register channel with selector", e);
        }

        this.selector.wakeup();

        logger.info("Success to send to market: {}", new String(message.toByteBuffer().array()));
    }
}
