package kr._42.seoul.common;

import java.nio.ByteBuffer;

public abstract class ByteBufferHelper {
    public static ByteBuffer deepCopy(ByteBuffer buffer) {
        ByteBuffer copy = ByteBuffer.allocate(buffer.capacity());
        buffer.rewind();
        copy.put(buffer);
        copy.flip();
        return copy;
    }
}
