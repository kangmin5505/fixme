package kr._42.seoul.server;

import kr._42.seoul.IOUtils;
import kr._42.seoul.idgenerator.MarketIDGenerator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MarketServer extends ServerMultiplexer {
    public MarketServer(int port) throws IOException {
        super(port);

        Thread.currentThread().setName("MarketServer");
        logger.debug("Success to create market server");
    }

    @Override
    public void implRun() {
        while (true) {
            try {
                this.selector.select();

                Set<SelectionKey> selectionKeys = this.selector.selectedKeys();

                selectionKeys.stream()
                             .filter(SelectionKey::isValid)
                             .forEach(key -> {
                                 if (key.isAcceptable()) {
                                     this.accept(key);
                                 } else if (key.isReadable()) {
                                     this.read(key);
                                 }
                             });

                selectionKeys.clear();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                break;
            }
        }
    }

    @Override
    public void accept(SelectionKey key) {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();

        try {
            SocketChannel client = serverSocket.accept();
            logger.debug("Try to accept client ({})", client);

            client.configureBlocking(false);
            client.register(this.selector, SelectionKey.OP_READ);

            logger.debug("Try to send id to client");
            // id 생성 후 전송
            String id = MarketIDGenerator.generate();
            buffer = ByteBuffer.wrap(id.getBytes(UTF_8));
            client.write(buffer);

            logger.debug("Success to send id to client. (id = {})", id);
            logger.debug("Success to accept client ({})", client);
        } catch (IOException e) {
            throw new RuntimeException("Fail to accept client");
        }
    }

    @Override
    public void read(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();

        logger.debug("Try to read from client ({})", client);

        try {
            buffer.clear();
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
