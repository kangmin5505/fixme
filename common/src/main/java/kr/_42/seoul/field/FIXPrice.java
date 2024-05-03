package kr._42.seoul.field;

public class FIXPrice extends Field<Integer> {
    public static final int TAG = 5;

    public FIXPrice(Integer value) {
        super(TAG, value);
    }
}
