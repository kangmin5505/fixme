package kr._42.seoul.field;

import kr._42.seoul.FIXMessage;

public abstract class Field<T> {
    private final int tag;
    private final T value;
    private final String tagValue;

    public Field(int tag, T value) {
        this.tag = tag;
        this.value = value;
        this.tagValue = String.format("%s=%s%s", tag, value, FIXMessage.SOH);
    }

    public String getTagValue() {
        return this.tagValue;
    }

    public T getValue() {
        return this.value;
    }
}
