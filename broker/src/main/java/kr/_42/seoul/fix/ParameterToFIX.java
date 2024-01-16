package kr._42.seoul.fix;

import kr._42.seoul.parser.ParameterParser;

import java.nio.ByteBuffer;

public class ParameterToFIX {

    private ParameterToFIX() {}

    public static ByteBuffer convert(String id, ParameterParser parser) {
        // ex. 8=FIX.4.4|9=123|35=D|49=SENDER|56=TARGET|34=1|52=20190601-00:00:00.000|11=CLORDID|21=1|55=MSFT|54=1|60=20190601-00:00:00.000|38=100|40=1|44=100|10=123|

        FIXMessage fixMessage = FIXMessage.builder()
                                          .SenderCompID(id)
                                          .TargetCompID(parser.getMarket())
                                          .MsgType(parser.getCommand().toString())
                                          .Symbol(parser.getInstrument())
                                          .OrderQty(parser.getQuantity())
                                          .Price(parser.getPrice())
                                          .build();
        return fixMessage.getByteBuffer();
    }
}
