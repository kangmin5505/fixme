package kr._42.seoul.field;

public class FIXInstrument extends Field<String> {
    public static final int TAG = 3;

    public FIXInstrument(String value) {
        super(TAG, value);
    }
}