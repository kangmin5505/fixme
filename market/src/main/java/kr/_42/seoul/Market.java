package kr._42.seoul;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.field.Tag;

public class Market extends ClientSocket {
    private final Logger logger = LoggerFactory.getLogger(Market.class);
    private final Repository repository;
    private final Set<String> instruments;

    public Market(Set<String> instruments, Repository repository) {
        this.instruments = instruments;
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
        SocketChannel channel = (SocketChannel) key.channel();
        this.buffer.clear();

        int readByte = channel.read(this.buffer);
        if (readByte == -1) {
            this.close();
            logger.error("Connection closed by router");
            System.exit(1);
        }

        FIXMessage receivedMessage = new FIXMessage(this.buffer);

        this.handleMessage(key, receivedMessage);
    }

    private void handleMessage(SelectionKey key, FIXMessage receivedMessage) {
        try {
            String brokerID = (String) receivedMessage.get(Tag.ID).getValue();
            String msgType = (String) receivedMessage.get(Tag.MSG_TYPE).getValue();
            String instrument = (String) receivedMessage.get(Tag.INSTRUMENT).getValue();
            int quantity = (int) receivedMessage.get(Tag.QUANTITY).getValue();
            int price = (int) receivedMessage.get(Tag.PRICE).getValue();

            if (this.instruments.contains(instrument) == false) {
                this.sendInvalidMessage(key, brokerID);
                return;
            }

            if (msgType.equals(MarketMsgType.BUY.toString())) {
                // this.repository.buy(instrument, quantity, price, brokerID);
            } else if (msgType.equals(MarketMsgType.SELL.toString())) {
                // this.repository.sell(instrument, quantity, price, brokerID);
            } else {
                this.sendInvalidMessage(key, brokerID);
            }

        } catch (Exception e) {
            String brokerID = (String) receivedMessage.get(Tag.ID).getValue();
            this.sendInvalidMessage(key, brokerID);
        }
        // FIXMessage sendMessage =
        // FIXMessage.builder().id(id).msgType(MarketMsgType.EXECUTED.toString())
        // .instrument(instrument).quantity(quantity).price(price).brokerID(brokerID).build();
    }

    private void sendInvalidMessage(SelectionKey key, String brokerID) {
        FIXMessage fixMessage = FIXMessage.builder().id(id)
                .msgType(MarketMsgType.REJECTED.toString()).brokerID(brokerID).build();
        key.attach(fixMessage.toByteBuffer().array());
        key.interestOps(SelectionKey.OP_WRITE);
    }
}
