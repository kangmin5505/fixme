package kr._42.seoul.broker;

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
import kr._42.seoul.common.RouterMediator;
import kr._42.seoul.common.ServerSocket;

public class BrokerRouter extends ServerSocket {
    private final static Logger logger = LoggerFactory.getLogger(BrokerRouter.class);
    private final static IDGenerator idGenerator = new IDGenerator();
    private final static Map<String, SocketChannel> brokerClients = new HashMap<>();
    private RouterMediator routerMediator;

    public static boolean isExistBrokerClient(String brokerID) {
        return brokerClients.containsKey(brokerID);
    }

    protected void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        Object attachment = key.attachment();
        byte[] bytes = (byte[]) attachment;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        channel.write(byteBuffer);
        key.interestOps(SelectionKey.OP_READ);

        logger.info("Sending to broker client: {}", new String(bytes));
    }

    protected void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        this.buffer.clear();

        int readByte = channel.read(this.buffer);
        if (readByte == -1) {
            channel.close();
            key.cancel();
            brokerClients.values().remove(channel);
            return;
        }

        ByteBuffer copyByteBuffer = ByteBufferHelper.deepCopy(this.buffer);
        this.routerMediator.sendToMarketRouter(copyByteBuffer);
    }

    protected void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        SelectionKey clientKey = client.register(this.selector, SelectionKey.OP_WRITE);

        String clientID = idGenerator.generateID();
        brokerClients.put(clientID, client);
        clientKey.attach(clientID.getBytes());
    }

    public void registerRouterMediator(RouterMediator routerMediator) {
        this.routerMediator = routerMediator;
    }

    public void sendToBroker(ByteBuffer byteBuffer, String brokerID) {
        SocketChannel brokerClientSocket = brokerClients.get(brokerID);
        byte[] bytes = byteBuffer.array();

        try {
            SelectionKey selectionKey =
                    brokerClientSocket.register(this.selector, SelectionKey.OP_WRITE);
            selectionKey.attach(bytes);
        } catch (ClosedChannelException e) {
            logger.error("Failed to register channel with selector", e);
        }

        this.selector.wakeup();
    }
}
