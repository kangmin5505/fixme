package kr._42.seoul.server;

import kr._42.seoul.IOUtils;
import kr._42.seoul.idgenerator.BrokerIDGenerator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BrokerServer extends ServerMultiplexer {
    public BrokerServer(int port) throws IOException {
        super(port);
        Thread.currentThread().setName("BrokerServer");

        logger.debug("Success to create broker server");
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

            String id = BrokerIDGenerator.generate();
            buffer = ByteBuffer.wrap(id.getBytes(UTF_8));
            buffer.flip();
            int write = 0;
            while (write == 0) {
                logger.debug("Try to write ({}).", new String(buffer.array()));
                Thread.sleep(500);
                write = client.write(buffer);
            }

            logger.debug("Success to write ({}). result : {}", new String(buffer.array()), write);
            logger.debug("Success to accept client ({})", client);
        } catch (IOException e) {
            throw new RuntimeException("Fail to accept client");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
