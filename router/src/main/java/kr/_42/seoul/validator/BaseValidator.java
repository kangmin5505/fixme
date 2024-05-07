package kr._42.seoul.validator;

import java.nio.ByteBuffer;

public abstract class BaseValidator implements Validator {
    Validator nextValidator;

    public void setNextValidator(Validator nextValidator) {
        this.nextValidator = nextValidator;
    }

    public boolean validate(ByteBuffer byteBuffer) {
        if (this.validateImpl(byteBuffer)) {
            return this.nextValidator != null ? this.nextValidator.validate(byteBuffer) : true;
        }
        return false;
    }

    abstract protected boolean validateImpl(ByteBuffer byteBuffer);
}
