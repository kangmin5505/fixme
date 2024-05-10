package kr._42.seoul.validator;

import java.nio.ByteBuffer;
import kr._42.seoul.FIXMessage;
import kr._42.seoul.enums.MsgType;
import kr._42.seoul.field.Tag;
import kr._42.seoul.market.Market;

public class TagValidator extends BaseValidator {

    @Override
    protected boolean validateImpl(ByteBuffer byteBuffer) {
        FIXMessage message = new FIXMessage(byteBuffer);

        try {
            String brokerID = (String) message.get(Tag.ID).getValue();
            String msgType = (String) message.get(Tag.MSG_TYPE).getValue();
            String instrument = (String) message.get(Tag.INSTRUMENT).getValue();
            int quantity = (int) message.get(Tag.QUANTITY).getValue();
            int price = (int) message.get(Tag.PRICE).getValue();

            return validateMsgType(msgType) && validateInstrument(instrument)
                    && validateQuantity(quantity) && validatePrice(price);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateMsgType(String msgType) {
        return msgType.equals(MsgType.BUY.toString()) || msgType.equals(MsgType.SELL.toString());
    }

    private boolean validateInstrument(String instrument) {
        return Market.isExistInstrument(instrument);
    }

    private boolean validateQuantity(int quantity) {
        return quantity > 0;
    }

    private boolean validatePrice(int price) {
        return price > 0;
    }
}
