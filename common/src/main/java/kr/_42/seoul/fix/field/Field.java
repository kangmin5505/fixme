package kr._42.seoul.fix.field;

import static kr._42.seoul.fix.FIXMessage.SOH;

public abstract class Field<T> {
    private final int tag;
    private final T field;
    private final String stringField;

    public Field(int tag, T field) {
        this.tag = tag;
        this.field = field;
        this.stringField = String.format("%s=%s%s", tag, field, SOH);
    }

    public int getTag() {
        return this.tag;
    }

    public T getField() {
        return this.field;
    }

    public void toString(StringBuilder sb) {
        sb.append(this.stringField);
    }

    public int getStringFieldLength() {
        return this.stringField.length();
    }
}
