package kr._42.seoul.field;

public class FIXMsgType extends Field<String> {
    public FIXMsgType(String value) {
        super(Tag.MSG_TYPE, value);
    }
}
