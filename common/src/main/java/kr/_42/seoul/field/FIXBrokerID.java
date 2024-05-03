package kr._42.seoul.field;

public class FIXBrokerID extends Field<String> {
    public static final int TAG = 7;

    public FIXBrokerID(String value) {
        super(TAG, value);
    }
}
