package kr._42.seoul.common;

import java.nio.ByteBuffer;
import kr._42.seoul.ByteBufferHelper;
import kr._42.seoul.FIXMessage;

public class Response {
    private final ByteBuffer byteBuffer;

    public Response(ByteBuffer buffer) {
        this.byteBuffer = ByteBufferHelper.deepCopy(buffer);
    }

    public FIXMessage getFixMessage() {
        return new FIXMessage(this.byteBuffer);
    }
}
