package kr._42.seoul.server;

import kr._42.seoul.IOUtils;
import kr._42.seoul.idgenerator.IDGenerator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class MarketServer extends ServerMultiplexer {
    public MarketServer(int port) throws IOException {
        super(port, new IDGenerator());

        Thread.currentThread().setName("MarketServer");
        logger.debug("Success to create market server");
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
