package kr._42.seoul.validator;

import java.nio.ByteBuffer;
import java.util.Arrays;
import kr._42.seoul.field.Tag;
import kr._42.seoul.market.MarketRouter;

public class BrokerRoutingValidator extends BaseValidator {

    @Override
    protected boolean validateImpl(ByteBuffer byteBuffer) {
        String marketIDTag = Tag.MARKET.toString();
        String str = new String(byteBuffer.array());
        String[] fields = str.split("\001");
        
        String marketID = Arrays.stream(fields)
                .filter(field -> field.startsWith(marketIDTag + "="))
                .map(field -> field.split("=")[1])
                .findFirst()
                .orElse(null);
                
        return MarketRouter.isExistMarketClient(marketID);
    }

}
