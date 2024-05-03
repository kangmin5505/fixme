package kr._42.seoul.field;

public class FIXChecksum extends Field<String> {
    public static final int TAG = 8;

    public FIXChecksum(String value) {
        super(TAG, value);
    }
}
