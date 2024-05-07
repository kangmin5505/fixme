package kr._42.seoul.validator;

import java.nio.ByteBuffer;

public interface Validator {
    boolean validate(ByteBuffer byteBuffer);
    void setNextValidator(Validator nextValidator);
}
