package kr._42.seoul.server;

import kr._42.seoul.IOUtils;
import kr._42.seoul.idgenerator.IDGenerator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class MarketServer extends ServerMultiplexer {
    private static final IDGenerator idGenerator = new IDGenerator();
    private static final Map<String, SocketChannel> map = new HashMap<>();

    public MarketServer(int port) throws IOException {
        super(port);
        Thread.currentThread().setName("MarketServer");

        logger.debug("Success to create market server");
    }

    public static SocketChannel getSocketChannel(String id) {
        return map.get(id);
    }

    @Override
    protected void accept(SelectionKey key) {
        logger.debug("Try to accept client");

        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        try {
            SocketChannel client = serverSocket.accept();

            Attachment attachment = new Attachment(BUFFER_CAPACITY, idGenerator.generate());
            client.configureBlocking(false);
            client.register(this.selector, SelectionKey.OP_WRITE, attachment);

            map.put(attachment.getId(), client);

            logger.debug("Success to accept client ({})", client);
        } catch (IOException e) {
            throw new RuntimeException("Fail to accept client");
        }
    }

    @Override
    public void read(SelectionKey key) {
        logger.debug("Try to read from client");

        SocketChannel client = (SocketChannel) key.channel();
        Attachment attachment = (Attachment) key.attachment();
        ByteBuffer buffer = attachment.getBuffer();
        buffer.clear();

        try {
            int readByte = client.read(buffer);

            if (readByte == IOUtils.EOF) {
                this.disconnect(key);
            } else {
                buffer.flip();
                String message = new String(buffer.array()).trim();
                logger.debug("Read message from client ({}), ({})", client, message);
            }
        } catch (IOException e) {
            logger.error("Fail to read from client ({}).", client);
            logger.error(e.getMessage());
            this.disconnect(key);
        }
    }
}
