package kr._42.seoul.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

public class ValidatorTest {

    @Test
    public void testOkChecksumValidator() {
        Validator validator = new ChecksumValidator();

        String str = "1=000000\0013=1\0015=1\0014=1\0016=1\0012=SELL\0018=189\001";
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());

        assertEquals(validator.validate(byteBuffer), true);
    }

    @Test
    public void testFailChecksumValidator() {
        Validator validator = new ChecksumValidator();

        String str = "1=000000\0013=1\0015=1\0014=1\0016=1\0012=SELL\0018=188\001";
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());

        assertEquals(validator.validate(byteBuffer), false);
    }
}
