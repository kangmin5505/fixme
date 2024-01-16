package kr._42.seoul.fix;

import kr._42.seoul.fix.field.*;

import java.nio.ByteBuffer;

public class FIXMessage {
    private Header header;
    private Body body;
    private Trailer trailer;
    private ByteBuffer buffer;
    public static final String SOH = "\001";
    private static final int CHECKSUM_MODULAR = 256;

    public FIXMessage (ByteBuffer buffer) {
        this.buffer = buffer;
        this.header = new Header();
        this.body = new Body();
        this.trailer = new Trailer();

        this.parseByteBuffer();
    }

    // TODO: refactor
    private void parseByteBuffer() {
        String s = new String(this.buffer.array());
        String[] fields = s.split(SOH);

        try {
            for (String field : fields) {
                String[] kv = field.split("=");
                int tag = Integer.parseInt(kv[0]);
                String value = kv[1];

                switch (tag) {
                    case SenderCompID.TAG:
                        this.header.setSenderCompID(new SenderCompID(value));
                        break;
                    case TargetCompID.TAG:
                        this.header.setTargetCompID(new TargetCompID(value));
                        break;
                    case MsgType.TAG:
                        this.header.setMsgType(new MsgType(value));
                        break;
                    case Symbol.TAG:
                        this.body.setSymbol(new Symbol(value));
                        break;
                    case OrderQty.TAG:
                        this.body.setOrderQty(new OrderQty(Integer.parseInt(value)));
                        break;
                    case Price.TAG:
                        this.body.setPrice(new Price(Integer.parseInt(value)));
                        break;
                    case CheckSum.TAG:
                        this.trailer.setCheckSum(new CheckSum(value));
                        break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse byte buffer");
        }
    }

    public static FIXMessageBuilder builder() {
        return new FIXMessageBuilder();
    }

    public ByteBuffer getByteBuffer() {
        return this.buffer;
    }

    private void createByteBuffer() {
        StringBuilder sb = new StringBuilder();

        this.header.toString(sb);
        this.body.toString(sb);

        String checkSum = CheckSum.createField(sb.length() % CHECKSUM_MODULAR);
        this.trailer.setCheckSum(new CheckSum(checkSum));

        this.trailer.toString(sb);

        this.buffer = ByteBuffer.wrap(sb.toString().getBytes()).asReadOnlyBuffer();
    }

    @Override
    public String toString() {
        String s = new String(this.buffer.array());
        return s.trim().replace(SOH, "|");
    }

    public boolean validateChecksum() {
        int checksum = 0;

        checksum += this.header.getStringFieldsLength();
        checksum += this.body.getStringFieldsLength();

        String stringChecksum = String.format("%03d", checksum % CHECKSUM_MODULAR);

        return stringChecksum.equals(this.trailer.getCheckSum().getField());
    }

    private static class Header extends FieldMap {
        private SenderCompID senderCompID;
        private TargetCompID targetCompID;
        private MsgType msgType;

        public void setSenderCompID(SenderCompID senderCompID) {
            this.senderCompID = senderCompID;
            this.setField(this.senderCompID);
        }

        public void setTargetCompID(TargetCompID targetCompID) {
            this.targetCompID = targetCompID;
            this.setField(this.targetCompID);
        }

        public void setMsgType(MsgType msgType) {
            this.msgType = msgType;
            this.setField(this.msgType);
        }
    }

    private static class Body extends FieldMap {
        private Symbol symbol;
        private OrderQty orderQty;
        private Price price;

        public void setSymbol(Symbol symbol) {
            this.symbol = symbol;
            this.setField(this.symbol);
        }

        public void setOrderQty(OrderQty orderQty) {
            this.orderQty = orderQty;
            this.setField(this.orderQty);
        }

        public void setPrice(Price price) {
            this.price = price;
            this.setField(this.price);
        }
    }

    private static class Trailer extends FieldMap {
        private CheckSum checkSum;

        public void setCheckSum(CheckSum checkSum) {
            this.checkSum = checkSum;
            this.setField(this.checkSum);
        }

        public CheckSum getCheckSum() {
            return checkSum;
        }
    }

    public static class FIXMessageBuilder {
        private final Header header = new Header();
        private final Body body = new Body();
        private final Trailer trailer = new Trailer();

        public FIXMessageBuilder MsgType(String msgType) {
            if (!MsgType.contains(msgType)) {
                throw new IllegalArgumentException("msgType is not valid");
            }

            this.header.setMsgType(new MsgType(msgType));
            return this;
        }

        public FIXMessageBuilder SenderCompID(String senderCompId) {
            this.header.setSenderCompID(new SenderCompID(senderCompId));
            return this;
        }

        public FIXMessageBuilder TargetCompID(String targetCompID) {
            this.header.setTargetCompID(new TargetCompID(targetCompID));
            return this;
        }

        public FIXMessageBuilder Symbol(String symbol) {
            this.body.setSymbol(new Symbol(symbol));
            return this;
        }

        public FIXMessageBuilder OrderQty(Integer quantity) {
            this.body.setOrderQty(new OrderQty(quantity));
            return this;
        }

        public FIXMessageBuilder Price(Integer price) {
            this.body.setPrice(new Price(price));
            return this;
        }

        public FIXMessage build() {
            FIXMessage fixMessage = new FIXMessage();
            fixMessage.header = this.header;
            fixMessage.body = this.body;
            fixMessage.trailer = this.trailer;

            fixMessage.createByteBuffer();
            return fixMessage;
        }
    }
}
