package kr._42.seoul.server;

import kr._42.seoul.IOUtils;
import kr._42.seoul.idgenerator.IDGenerator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class BrokerServer extends ServerMultiplexer {
    private static final IDGenerator idGenerator = new IDGenerator();

    public BrokerServer(int port) throws IOException {
        super(port);
        Thread.currentThread().setName("BrokerServer");

        logger.debug("Success to create broker server");
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
//                FIXMessage fixMessage = new FIXMessage(buffer);
                // thread
                this.executorService.submit(() -> {
                    // 1. validate
                    // 2. identify
                    // 3. forward
                    try {
                        logger.debug("Try to forward to market server");
                        SocketChannel socketChannel = MarketServer.getSocketChannel("000000");
                        logger.debug("{}", socketChannel);
                        buffer.flip();
                        socketChannel.write(buffer);
                        logger.debug("Success to forward to market server");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            logger.error("Fail to read from client ({}).", client);
            logger.error(e.getMessage());
            this.disconnect(key);
        }
    }
}
