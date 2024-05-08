package kr._42.seoul.validator;

import java.nio.ByteBuffer;
import java.util.Arrays;
import kr._42.seoul.broker.BrokerRouter;
import kr._42.seoul.field.Tag;

public class MarketRoutingValidator extends BaseValidator {

    @Override
    protected boolean validateImpl(ByteBuffer byteBuffer) {
        String brokerIDTag = Tag.BROKER_ID.toString();
        String str = new String(byteBuffer.array());
        String[] fields = str.split("\001");
        
        String brokerID = Arrays.stream(fields)
                .filter(field -> field.startsWith(brokerIDTag + "="))
                .map(field -> field.split("=")[1])
                .findFirst()
                .orElse(null);

        return BrokerRouter.isExistBrokerClient(brokerID);
    }
}
