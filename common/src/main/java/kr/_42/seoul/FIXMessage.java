package kr._42.seoul;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import kr._42.seoul.field.FIXBrokerID;
import kr._42.seoul.field.FIXChecksum;
import kr._42.seoul.field.FIXID;
import kr._42.seoul.field.FIXInstrument;
import kr._42.seoul.field.FIXMarket;
import kr._42.seoul.field.FIXMsgType;
import kr._42.seoul.field.FIXPrice;
import kr._42.seoul.field.FIXQuantity;
import kr._42.seoul.field.Field;
import kr._42.seoul.field.Tag;

public class FIXMessage {
    public static final String SOH = "\001";
    private static final int BUFFER_SIZE = 1024;
    private Map<Tag, Field<?>> header = new HashMap<>();// ID
    private Map<Tag, Field<?>> body = new HashMap<>(); // MsgType, Instrument, Quantity, Price,
                                                       // Market, BrokerID
    private Map<Tag, Field<?>> trailer = new HashMap<>(); // Checksum

    public FIXMessage(ByteBuffer buffer) throws IllegalArgumentException {
        String s = new String(buffer.array()).trim();
        String[] fields = s.split(SOH);

        for (String field : fields) {
            String[] tagValue = field.split("=");
            Tag tag = Tag.toTag(tagValue[0]);
            String value = tagValue[1];

            switch (tag) {
                case ID:
                    this.header.put(Tag.ID, new FIXID(value));
                    break;
                case MSG_TYPE:
                    this.body.put(Tag.MSG_TYPE, new FIXMsgType(value));
                    break;
                case INSTRUMENT:
                    this.body.put(Tag.INSTRUMENT, new FIXInstrument(value));
                    break;
                case QUANTITY:
                    this.body.put(Tag.QUANTITY, new FIXQuantity(Integer.parseInt(value)));
                    break;
                case PRICE:
                    this.body.put(Tag.PRICE, new FIXPrice(Integer.parseInt(value)));
                    break;
                case MARKET:
                    this.body.put(Tag.MARKET, new FIXMarket(value));
                    break;
                case BROKER_ID:
                    this.body.put(Tag.BROKER_ID, new FIXBrokerID(value));
                    break;
                case CHECKSUM:
                    this.trailer.put(Tag.CHECKSUM, new FIXChecksum(value));
                    break;
            }
        }
    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        buffer.put(this.header.get(Tag.ID).getTagValue().getBytes());
        this.body.keySet().forEach(key -> {
            buffer.put(this.body.get(key).getTagValue().getBytes());
        });
        buffer.put(this.trailer.get(Tag.CHECKSUM).getTagValue().getBytes());

        return buffer;
    }

    private FIXMessage() {}


    public static FIXMessageBuilder builder() {
        return new FIXMessageBuilder();
    }

    public Field<?> get(Tag tag) throws IllegalArgumentException {
        if (this.header.containsKey(tag)) {
            return this.header.get(tag);
        } else if (this.body.containsKey(tag)) {
            return this.body.get(tag);
        } else if (this.trailer.containsKey(tag)) {
            return this.trailer.get(tag);
        }

        throw new IllegalArgumentException("Invalid tag: " + tag);
    }

    public static class FIXMessageBuilder {
        private FIXMessage fixMessage = new FIXMessage();

        public FIXMessageBuilder id(String id) {
            this.fixMessage.header.put(Tag.ID, new FIXID(id));
            return this;
        }

        public FIXMessageBuilder msgType(String value) {
            this.fixMessage.body.put(Tag.MSG_TYPE, new FIXMsgType(value));
            return this;
        }

        public FIXMessageBuilder instrument(String instrument) {
            this.fixMessage.body.put(Tag.INSTRUMENT, new FIXInstrument(instrument));
            return this;
        }

        public FIXMessageBuilder quantity(int quantity) {
            this.fixMessage.body.put(Tag.QUANTITY, new FIXQuantity(quantity));
            return this;
        }

        public FIXMessageBuilder price(int price) {
            this.fixMessage.body.put(Tag.PRICE, new FIXPrice(price));
            return this;
        }

        public FIXMessageBuilder market(String market) {
            this.fixMessage.body.put(Tag.MARKET, new FIXMarket(market));
            return this;
        }

        public FIXMessageBuilder brokerID(String brokerID) {
            this.fixMessage.body.put(Tag.BROKER_ID, new FIXBrokerID(brokerID));
            return this;
        }

        public FIXMessage build() {
            String checksum = String.format("%03d", this.calcChecksum());
            this.fixMessage.trailer.put(Tag.CHECKSUM, new FIXChecksum(checksum));
            return this.fixMessage;
        }

        private int calcChecksum() {
            int sum = 0;

            for (Tag key : this.fixMessage.header.keySet()) {
                String tagValue = this.fixMessage.header.get(key).getTagValue();
                for (int c : tagValue.toCharArray()) {
                    sum += c;
                }
            }
            for (Tag key : this.fixMessage.body.keySet()) {
                String tagValue = this.fixMessage.body.get(key).getTagValue();
                for (int c : tagValue.toCharArray()) {
                    sum += c;
                }
            }

            return sum % 256;
        }
    }
}
