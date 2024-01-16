package kr._42.seoul.server;

import kr._42.seoul.IOUtils;
import kr._42.seoul.fix.FIXMessage;
import kr._42.seoul.idgenerator.IDGenerator;
import kr._42.seoul.server.handler.ForwardHandler;
import kr._42.seoul.server.handler.Handler;
import kr._42.seoul.server.handler.MarketIdentifyHandler;
import kr._42.seoul.server.handler.ValidateHandler;

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
                this.executorService.submit(new Routing(buffer, client));
            }
        } catch (IOException e) {
            logger.error("Fail to read from client ({}).", client);
            logger.error(e.getMessage());
            this.disconnect(key);
        }
    }

    private static class Routing implements Runnable {
        private final FIXMessage fixMessage;
        private final SocketChannel client;
        private final ByteBuffer buffer;
        private static final Handler handler;
        static {
            handler = new ValidateHandler();
            Handler identifier = new MarketIdentifyHandler();
            Handler forwarder = new ForwardHandler();

            handler.setNext(identifier);
            identifier.setNext(forwarder);
        }


        public Routing(ByteBuffer buffer, SocketChannel client) {
            buffer.flip();

            this.fixMessage = new FIXMessage(buffer);
            this.buffer = buffer;
            this.client = client;
        }

        @Override
        public void run() {
            logger.debug("Received from client - ({})", fixMessage);

            if (!handler.handle(this.client, this.fixMessage)) {
                // fail fix message
                logger.debug("Fail to validate");
                return;
            }

            // 3. forward
//            logger.debug("Try to forward to market client");
//            try {
//                SocketChannel socketChannel = MarketServer.getSocketChannel("000000");
//
//                socketChannel.write(buffer);
//                client.write(ByteBuffer.wrap("Success".getBytes()));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        }
    }
}
