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

public class FIXMessage {
    public static final String SOH = "\001";
    private static final int BUFFER_SIZE = 1024;
    private Map<Integer, Field<?>> header = new HashMap<>();// ID
    private Map<Integer, Field<?>> body = new HashMap<>(); // MsgType, Instrument, Quantity, Price,
                                                           // Market, BrokerID
    private Map<Integer, Field<?>> trailer = new HashMap<>(); // Checksum

    public FIXMessage(ByteBuffer buffer) throws IllegalArgumentException {
        String s = new String(buffer.array()).trim();
        String[] fields = s.split(SOH);

        for (String field : fields) {
            String[] tagValue = field.split("=");
            int tag = Integer.parseInt(tagValue[0]);
            String value = tagValue[1];

            switch (tag) {
                case FIXID.TAG:
                    this.header.put(FIXID.TAG, new FIXID(value));
                    break;
                case FIXMsgType.TAG:
                    this.body.put(FIXMsgType.TAG, new FIXMsgType(value));
                    break;
                case FIXInstrument.TAG:
                    this.body.put(FIXInstrument.TAG, new FIXInstrument(value));
                    break;
                case FIXQuantity.TAG:
                    this.body.put(FIXQuantity.TAG, new FIXQuantity(Integer.parseInt(value)));
                    break;
                case FIXPrice.TAG:
                    this.body.put(FIXPrice.TAG, new FIXPrice(Integer.parseInt(value)));
                    break;
                case FIXMarket.TAG:
                    this.body.put(FIXMarket.TAG, new FIXMarket(value));
                    break;
                case FIXBrokerID.TAG:
                    this.body.put(FIXBrokerID.TAG, new FIXBrokerID(value));
                    break;
                case FIXChecksum.TAG:
                    this.trailer.put(FIXChecksum.TAG, new FIXChecksum(value));
                    break;
            }
        }
    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        buffer.put(this.header.get(FIXID.TAG).getTagValue().getBytes());
        this.body.keySet().forEach(key -> {
            buffer.put(this.body.get(key).getTagValue().getBytes());
        });
        buffer.put(this.trailer.get(FIXChecksum.TAG).getTagValue().getBytes());

        return buffer;
    }

    private FIXMessage() {}


    public static FIXMessageBuilder builder() {
        return new FIXMessageBuilder();
    }

    public static class FIXMessageBuilder {
        private FIXMessage fixMessage = new FIXMessage();

        public FIXMessageBuilder id(String id) {
            this.fixMessage.header.put(FIXID.TAG, new FIXID(id));
            return this;
        }

        public FIXMessageBuilder msgType(String value) {
            this.fixMessage.body.put(FIXMsgType.TAG, new FIXMsgType(value));
            return this;
        }

        public FIXMessageBuilder instrument(String instrument) {
            this.fixMessage.body.put(FIXInstrument.TAG, new FIXInstrument(instrument));
            return this;
        }

        public FIXMessageBuilder quantity(int quantity) {
            this.fixMessage.body.put(FIXQuantity.TAG, new FIXQuantity(quantity));
            return this;
        }

        public FIXMessageBuilder price(int price) {
            this.fixMessage.body.put(FIXPrice.TAG, new FIXPrice(price));
            return this;
        }

        public FIXMessageBuilder market(String market) {
            this.fixMessage.body.put(FIXMarket.TAG, new FIXMarket(market));
            return this;
        }

        public FIXMessageBuilder brokerID(String brokerID) {
            this.fixMessage.body.put(FIXBrokerID.TAG, new FIXBrokerID(brokerID));
            return this;
        }

        public FIXMessage build() {
            String checksum = String.format("%03d", this.calcChecksum());
            this.fixMessage.trailer.put(FIXChecksum.TAG, new FIXChecksum(checksum));
            return this.fixMessage;
        }

        private int calcChecksum() {
            int sum = 0;

            for (Integer key : this.fixMessage.header.keySet()) {
                String tagValue = this.fixMessage.header.get(key).getTagValue();
                for (int c : tagValue.toCharArray()) {
                    sum += c;
                }
            }
            for (Integer key : this.fixMessage.body.keySet()) {
                String tagValue = this.fixMessage.body.get(key).getTagValue();
                for (int c : tagValue.toCharArray()) {
                    sum += c;
                }
            }

            return sum % 256;
        }
    }

}
