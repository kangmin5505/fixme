package kr._42.seoul.fix;

import java.nio.ByteBuffer;

public class FIXMessage {
    private Header header;
    private Body body;
    private Trailer trailer;
    private ByteBuffer buffer;

    public FIXMessage(ByteBuffer buffer) {
        // parse message
        // ex. 8=FIX.4.4|9=123|35=D|49=SENDER|56=TARGET|34=1|52=20190601-00:00:00.000|11=CLORDID|21=1|55=MSFT|54=1|60=20190601-00:00:00.000|38=100|40=1|44=100|10=123|
        this.buffer = buffer;
    }

    private class Header {
        // header fields enum
    }

    private class Body {
        // body fields enum
    }

    private class Trailer {
        // trailer fields enum
    }
}
