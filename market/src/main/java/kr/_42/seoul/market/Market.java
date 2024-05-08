package kr._42.seoul.market;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kr._42.seoul.ByteBufferHelper;
import kr._42.seoul.ClientSocket;
import kr._42.seoul.FIXMessage;
import kr._42.seoul.enums.MarketMsgType;
import kr._42.seoul.field.Tag;
import kr._42.seoul.repository.Repository;
import kr._42.seoul.validator.TagValidator;
import kr._42.seoul.validator.Validator;

public class Market extends ClientSocket {
    private final Logger logger = LoggerFactory.getLogger(Market.class);
    private final Repository repository;
    private static final Set<String> instruments = new HashSet<>();
    private Validator validator;

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

    public static boolean isExistInstrument(String instrument) {
        return instruments.contains(instrument);
    }

    private void handleMessage(SelectionKey key, ByteBuffer byteBuffer) {
        if (this.validator.validate(byteBuffer) == false) {
            this.sendInvalidMessage(key, byteBuffer);
            return;
        }
 
        // FIXMessage sendMessage =
        // FIXMessage.builder().id(id).msgType(MarketMsgType.EXECUTED.toString())
        // .instrument(instrument).quantity(quantity).price(price).brokerID(brokerID).build();
    }

    private void sendInvalidMessage(SelectionKey key, ByteBuffer byteBuffer) {
        FIXMessage message = new FIXMessage(byteBuffer);
        String brokerID = (String) message.get(Tag.ID).getValue();
        FIXMessage invalidMessage = FIXMessage.builder().id(id)
                .msgType(MarketMsgType.REJECTED.toString()).brokerID(brokerID).build();

        key.attach(invalidMessage.toByteBuffer().array());
        key.interestOps(SelectionKey.OP_WRITE);
    }
}
