package kr._42.seoul;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.field.Tag;

public class Market extends ClientSocket {
    private final Logger logger = LoggerFactory.getLogger(Market.class);
    private final Repository repository;

    public Market(Repository repository) {
        this.repository = repository;
    }

    protected void write(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        key.interestOps(SelectionKey.OP_READ);
    }

    protected void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        this.buffer.clear();
        client.read(this.buffer);
        this.buffer.flip();

        FIXMessage fixMessage = new FIXMessage(this.buffer);

        String id = (String) fixMessage.get(Tag.ID).getValue();
        String msgType = (String) fixMessage.get(Tag.MSG_TYPE).getValue();
        String instrument = (String) fixMessage.get(Tag.INSTRUMENT).getValue();
        int quantity = (int) fixMessage.get(Tag.QUANTITY).getValue();
        int price = (int) fixMessage.get(Tag.PRICE).getValue();

        // TODO: add repository
        logger.info("Received FIX Message: ID: {}, MsgType: {}, Instrument: {}, Quantity: {}, Price: {}",
                id, msgType, instrument, quantity, price);
    }
}
