package kr._42.seoul;

import java.io.IOException;
import java.nio.ByteBuffer;
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
        Object attachment = key.attachment();
        byte[] bytes = (byte[]) attachment;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        client.write(byteBuffer);
        key.interestOps(SelectionKey.OP_READ);

        logger.info("Sending to router: {}", new String(bytes));
    }

    protected void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        this.buffer.clear();
        client.read(this.buffer);
        this.buffer.flip();

        FIXMessage receivedMessage = new FIXMessage(this.buffer);

        String brokerID = (String) receivedMessage.get(Tag.ID).getValue();
        String msgType = (String) receivedMessage.get(Tag.MSG_TYPE).getValue();
        String instrument = (String) receivedMessage.get(Tag.INSTRUMENT).getValue();
        int quantity = (int) receivedMessage.get(Tag.QUANTITY).getValue();
        int price = (int) receivedMessage.get(Tag.PRICE).getValue();

        // TODO: add repository
        logger.info(
                "Received FIX Message: ID: {}, MsgType: {}, Instrument: {}, Quantity: {}, Price: {}",
                brokerID, msgType, instrument, quantity, price);

        FIXMessage sendMessage = FIXMessage.builder().id(id).msgType(MarketMsgType.EXECUTED.toString())
                .instrument(instrument).quantity(quantity).price(price).brokerID(brokerID).build();

        key.attach(sendMessage.toByteBuffer().array());
        key.interestOps(SelectionKey.OP_WRITE);
    }
}
