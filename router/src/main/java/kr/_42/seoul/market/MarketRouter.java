package kr._42.seoul.market;

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
import kr._42.seoul.ByteBufferHelper;
import kr._42.seoul.common.IDGenerator;
import kr._42.seoul.common.Mediator;
import kr._42.seoul.common.ServerSocket;

public class MarketRouter extends ServerSocket {
    private final Logger logger = LoggerFactory.getLogger(MarketRouter.class);
    private final static IDGenerator idGenerator = new IDGenerator();
    private final static Map<String, SocketChannel> marketClients = new HashMap<>();
    private Mediator mediator;

    public static boolean isExistMarketClient(String marketID) {
        return marketClients.containsKey(marketID);
    }

    protected void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        Object attachment = key.attachment();
        byte[] bytes = (byte[]) attachment;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        channel.write(byteBuffer);
        key.interestOps(SelectionKey.OP_READ);

        logger.info("Sending to market client: {}", new String(bytes));
    }

    protected void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        this.buffer.clear();

        int readByte = channel.read(this.buffer);

        if (readByte == -1) {
            channel.close();
            key.cancel();
            marketClients.values().remove(channel);
            return;
        }

        ByteBuffer copyByteBuffer = ByteBufferHelper.deepCopy(this.buffer);
        this.mediator.sendToBrokerRouter(copyByteBuffer);

        logger.info("Forwarding to BrokerRouter: {}",
                new String(copyByteBuffer.array()));
    }

    protected void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        SelectionKey clientKey = client.register(this.selector, SelectionKey.OP_WRITE);

        String clientID = idGenerator.generateID();
        marketClients.put(clientID, client);
        clientKey.attach(clientID.getBytes());
    }

    public void registerMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    public void sendToMarket(ByteBuffer byteBuffer, String marketID) {
        SocketChannel marketClientSocket = marketClients.get(marketID);
        byte[] bytes = byteBuffer.array();

        try {
            SelectionKey selectionKey = marketClientSocket.register(this.selector, SelectionKey.OP_WRITE);
            selectionKey.attach(bytes);
        } catch (ClosedChannelException e) {
            logger.error("Failed to register channel with selector", e);
        }

        this.selector.wakeup();
    }
}
