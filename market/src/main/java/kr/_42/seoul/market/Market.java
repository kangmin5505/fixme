package kr._42.seoul.market;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.ByteBufferHelper;
import kr._42.seoul.ClientSocket;
import kr._42.seoul.FIXMessage;
import kr._42.seoul.enums.MsgType;
import kr._42.seoul.field.Tag;
import kr._42.seoul.repository.Order;
import kr._42.seoul.repository.Repository;
import kr._42.seoul.validator.TagValidator;
import kr._42.seoul.validator.Validator;

public class Market extends ClientSocket {
    private final Logger logger = LoggerFactory.getLogger(Market.class);
    private final Repository repository;
    private static final Set<String> instruments = new HashSet<>();
    private Validator validator;

    public static boolean isExistInstrument(String instrument) {
        return instruments.contains(instrument);
    }

    public Market(Set<String> instrumentsSet, Repository repository) {
        instrumentsSet.forEach(instruments::add);
        this.repository = repository;
        this.validator = new TagValidator();
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

        ByteBuffer copy = ByteBufferHelper.deepCopy(this.buffer);
        this.handleMessage(key, copy);
    }

    private void handleMessage(SelectionKey key, ByteBuffer byteBuffer) {
        if (this.validator.validate(byteBuffer) == false) {
            this.sendInvalidMessage(key, byteBuffer);
            return;
        }

        FIXMessage message = new FIXMessage(byteBuffer);
        String msgType = (String) message.get(Tag.MSG_TYPE).getValue();

        if (MsgType.BUY.toString().equals(msgType)) {
            this.handleBuy(key, message);
        } else {
            this.handleSell(key, message);
        }
    }

    private void handleBuy(SelectionKey key, FIXMessage message) {
        String instrument = (String) message.get(Tag.INSTRUMENT).getValue();
        int quantity = (int) message.get(Tag.QUANTITY).getValue();
        int price = (int) message.get(Tag.PRICE).getValue();

        try {       
            List<Order> orders = this.repository.findOrdersByInstrumentAndPrice(instrument, price);

            for (Order order : orders) {
                if (order.getQuantity() >= quantity) {
                    int remianQuantity = order.getQuantity() - quantity;
                    order.updateQuantity(remianQuantity);
                    
                    if (remianQuantity == 0) {
                        this.repository.deleteOrder(order);
                    } else {
                        this.repository.updateOrder(order);
                    }

                    FIXMessage response = FIXMessage.builder().id(id).msgType(MsgType.EXECUTED)
                            .brokerID(order.getBrokerID()).instrument(instrument).quantity(quantity).price(price).build();
                    key.attach(response.toByteBuffer().array());
                    key.interestOps(SelectionKey.OP_WRITE);
                    return;
                }
            }
            this.sendInvalidMessage(key, this.buffer);
        } catch (SQLException e) {
            this.sendInvalidMessage(key, this.buffer);            
        }
    }

    private void handleSell(SelectionKey key, FIXMessage message) {
        String brokerID = (String) message.get(Tag.ID).getValue();
        String instrument = (String) message.get(Tag.INSTRUMENT).getValue();
        int quantity = (int) message.get(Tag.QUANTITY).getValue();
        int price = (int) message.get(Tag.PRICE).getValue();

        try {
            this.repository.addOrder(Order.builder().brokerID(brokerID).instrument(instrument)
            .quantity(quantity).price(price).build());
        } catch (SQLException e) {
            logger.error("Failed to add order: {}", e.getMessage());
            this.sendInvalidMessage(key, buffer);
        }

    }

    private void sendInvalidMessage(SelectionKey key, ByteBuffer byteBuffer) {
        FIXMessage message = new FIXMessage(byteBuffer);
        String brokerID = (String) message.get(Tag.ID).getValue();
        FIXMessage invalidMessage = FIXMessage.builder().id(id)
                .msgType(MsgType.REJECTED).brokerID(brokerID).build();

        key.attach(invalidMessage.toByteBuffer().array());
        key.interestOps(SelectionKey.OP_WRITE);
    }
}
