package kr._42.seoul;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import kr._42.seoul.enums.MsgType;

public class FIXMessageTest {

    @Test
    public void testFIXMessage() {
        String s =
                "1=123456\0012=SELL\0013=instrument\0014=100\0015=1000\0016=KRX\0017=broker\0018=168\001";
        FIXMessage fixMessage = new FIXMessage(ByteBuffer.wrap(s.getBytes()));
        String message = new String(fixMessage.toByteBuffer().array());

        FIXMessage fixMessage2 =
                FIXMessage.builder().id("123456").msgType(MsgType.SELL).instrument("instrument")
                        .quantity(100).price(1000).market("KRX").brokerID("broker").build();

        String message2 = new String(fixMessage2.toByteBuffer().array());

        assertEquals(message, message2);
    }
}
